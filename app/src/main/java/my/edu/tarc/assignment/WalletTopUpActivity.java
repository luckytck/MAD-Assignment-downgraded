package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.assignment.Model.Transaction;
import my.edu.tarc.assignment.Model.User;

public class WalletTopUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static final String TAG = "my.edu.tarc.assignment";
    private EditText editTextTopUpAmount;
    private double amount;
    private User user;
    private Spinner spinnerCard;
    private Pinview pinviewTopUpPin;
    private ProgressDialog pDialog;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_top_up);

        pDialog = new ProgressDialog(this);
        editTextTopUpAmount = (EditText)findViewById(R.id.editTextTopUpAmount);
        spinnerCard = (Spinner)findViewById(R.id.spinnerCard);
        pinviewTopUpPin = (Pinview)findViewById(R.id.pinviewTopUpPIN);
        //Get existing credit/debit card
        Intent intent = getIntent();
        String[] cardTitle = intent.getStringArrayExtra(WalletActivity.CARD_TITLE_ARRAY);
        //Show added card to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cardTitle);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCard.setOnItemSelectedListener(this);
        spinnerCard.setAdapter(adapter);
        //Top up wallet
        Button buttonConfirmTopUp = (Button)this.findViewById(R.id.buttonConfirmTopUp);
        buttonConfirmTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinnerCard.getCount() == 0){//No card added to account
                    Toast.makeText(getApplicationContext(), "Sorry, please add a credit/debit card first.", Toast.LENGTH_LONG).show();
                } else if (editTextTopUpAmount.getText().toString().isEmpty()) {//empty field
                    Toast.makeText(getApplicationContext(), "Please enter top up amount.", Toast.LENGTH_LONG).show();
                } else {
                    amount = Double.parseDouble(editTextTopUpAmount.getText().toString());
                    if (amount < 10) {//top up amount less than 10
                        Toast.makeText(getApplicationContext(), "Sorry, minimum top up amount is RM10.", Toast.LENGTH_LONG).show();
                    } else if (pinviewTopUpPin.getValue().length() < 6) {//Check 6-digit pin
                        Toast.makeText(getApplicationContext(),"Please fill up 6-Digit PIN.",Toast.LENGTH_SHORT).show();
                    } else {//Start top up wallet
                        processTopUp();
                    }
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Set selected spinner item's text size and colour;
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
        ((TextView) adapterView.getChildAt(0)).setTextSize(18);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void processTopUp(){
        int pinInput = Integer.parseInt(pinviewTopUpPin.getValue());
        validatePIN(getApplicationContext(), getString(R.string.get_user_url), pinInput);
    }
    //Validate pin number
    private void validatePIN(final Context context, String url, final int pinInput){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Top up processing...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            boolean isValidPin = false;
                            SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                            String loginUsername = pref.getString("username", "");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                String username = userResponse.getString("username");
                                String password = userResponse.getString("password");
                                String name = userResponse.getString("name");
                                String phoneNo = userResponse.getString("phoneNo");
                                String email = userResponse.getString("email");
                                int transactionPin = userResponse.getInt("pin");
                                double balance = userResponse.getDouble("balance");
                                if (username.equalsIgnoreCase(loginUsername) && transactionPin == pinInput){
                                    user = new User(username,password,name,phoneNo,email,pinInput, balance);
                                    isValidPin = true;
                                    break;
                                }
                            }
                            if (isValidPin){
                                updateBalance(context, getString(R.string.update_balance_url), user);
                            } else {
                                Toast.makeText(getApplicationContext(), "Top Up failed, incorrect PIN number.", Toast.LENGTH_LONG).show();
                            }
                            if (pDialog.isShowing() && !isValidPin)
                                pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
    //Update balance
    public void updateBalance(final Context context, String url, final User user) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success==0) {
                                    Toast.makeText(getApplicationContext(), "Top up failed.", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Top up successful.", Toast.LENGTH_SHORT).show();
                                    //Set wallet top up image to string
                                    Bitmap bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_account_balance_wallet_grey_24dp);
                                    String image = getStringImage(bitmap);

                                    SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                                    String loginUsername = pref.getString("username", "");

                                    Transaction transaction = new Transaction(image, getString(R.string.wallet_top_up), amount, loginUsername);
                                    recordTransaction(context, getString(R.string.insert_transaction_url), transaction);
                                }
                                if (pDialog.isShowing() && success == 0)
                                    pDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("balance", String.valueOf(user.getBalance()+amount));
                    params.put("username", user.getUsername());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Record wallet top up
    public void recordTransaction(Context context, String url, final Transaction transaction) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success==0) {
                                    Toast.makeText(getApplicationContext(), "Insert transaction failed.", Toast.LENGTH_LONG).show();
                                }else{
                                    finish();
                                }
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("imageMerchant", transaction.getImageMerchant());
                    params.put("title", transaction.getTitle());
                    params.put("amount", String.valueOf(transaction.getAmount()));
                    params.put("username", transaction.getUsername());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return getResizedBitmap(bitmap, 24);
    }
    //Compress Bitmap image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
