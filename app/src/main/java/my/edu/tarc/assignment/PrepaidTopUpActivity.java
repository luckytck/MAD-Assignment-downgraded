package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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

public class PrepaidTopUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static final String TAG = "my.edu.tarc.assignment";
    private Spinner spinnerReloadAmount;
    private ImageView imageViewTelco;
    private EditText editTextPhoneNo;
    private Pinview pinviewTopUpPIN;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private int pinInput;
    private String telcoName;
    private double reloadAmount;
    private User user;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepaid_top_up);

        spinnerReloadAmount = (Spinner)findViewById(R.id.spinnerReloadAmount);
        imageViewTelco = (ImageView)findViewById(R.id.imageViewTelco);
        editTextPhoneNo = (EditText)findViewById(R.id.editTextPhoneNo);
        pinviewTopUpPIN = (Pinview) findViewById(R.id.pinviewTopUpPIN);
        pDialog = new ProgressDialog(this);
        //Get top up telco name
        Intent intent = getIntent();
        telcoName = intent.getStringExtra(MainActivity.TELCO_NAME);
        setTitle(telcoName + getString(R.string.prepaid_reload_title));
        if (telcoName.equalsIgnoreCase("Digi")){//Digi top up
            imageViewTelco.setImageResource(R.drawable.digi_banner);
            bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.digi);
        } else if (telcoName.equalsIgnoreCase("Umobile")){//Umobile top up
            imageViewTelco.setImageResource(R.drawable.umobile_banner);
            bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.umobile);
        } else if (telcoName.equalsIgnoreCase("Hotlink")){//Hotlink top up
            imageViewTelco.setImageResource(R.drawable.hotlink_banner);
            bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.hotlink);
        } else {//Xpax top up
            imageViewTelco.setImageResource(R.drawable.xpax_banner);
            bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.xpax);
        }

        //Create an adapter for spinner
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.reload_amount,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        //Set reload amount to spinner
        spinnerReloadAmount.setOnItemSelectedListener(this);
        spinnerReloadAmount.setAdapter(adapter);
        //Confirm top up
        Button buttonConfirm = (Button)findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = editTextPhoneNo.getText().toString();
                int pos = spinnerReloadAmount.getSelectedItemPosition();
                if (pos == -1){//No selected reload amount
                    Toast.makeText(getApplicationContext(), "Please select reload amount.", Toast.LENGTH_LONG).show();
                } else if (phoneNo.isEmpty()){//empty phone number
                    Toast.makeText(getApplicationContext(), "Please enter phone number you want to reload.", Toast.LENGTH_LONG).show();
                } else if (!phoneNo.matches("^01[0-9]{8,9}$")){//Invalid phone number
                    Toast.makeText(getApplicationContext(), "Invalid phone number, please enter a valid format: 01xxxxxxxx", Toast.LENGTH_LONG).show();
                } else if (pinviewTopUpPIN.getValue().length() < 6){//Invalid pin number
                    Toast.makeText(getApplicationContext(),"Please fill up 6-Digit PIN.",Toast.LENGTH_SHORT).show();
                } else {
                    pinInput = Integer.parseInt(pinviewTopUpPIN.getValue());
                    //Set reload amount
                    switch (pos){
                        case 0:
                            reloadAmount = 10;
                            break;
                        case 1:
                            reloadAmount = 30;
                            break;
                        case 2:
                            reloadAmount = 50;
                            break;
                        default:
                            reloadAmount = 0;
                    }
                    //Start top up
                    processTopUp();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Set selected item's text size and colour
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
        ((TextView) adapterView.getChildAt(0)).setTextSize(18);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void processTopUp() {
        validatePIN(getApplicationContext(), getString(R.string.get_user_url));
    }
    //Validate pin
    private void validatePIN(final Context context, String url){
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
                                if (user.getBalance() >= reloadAmount){
                                    checkStock(context, getString(R.string.get_top_up_stock_url));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Top Up failed, insufficient balance.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Top Up failed, incorrect PIN number.", Toast.LENGTH_LONG).show();
                            }
                            if ((pDialog.isShowing() && !isValidPin) || (pDialog.isShowing() && user.getBalance() < reloadAmount))
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
    //Check top up stock
    private void checkStock(final Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url + telcoName + "&reloadAmount=" + reloadAmount,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            int quantity = 0;
                            int success = response.getInt("success");
                            if (success == 1){
                                quantity = response.getInt("quantity");
                                if (quantity > 0){
                                    updateStock(context, getString(R.string.update_top_up_stock_url), quantity);
                                } else {
                                    Toast.makeText(getApplicationContext(), spinnerReloadAmount.getSelectedItem() + " top up out of Stock.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                            if (pDialog.isShowing() && quantity == 0)
                                pDialog.dismiss();
                        } catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
    //Update top up stock
    public void updateStock(final Context context, String url, final int quantity) {
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
                                    Toast.makeText(getApplicationContext(), "Update stock failed.", Toast.LENGTH_LONG).show();
                                }else{
                                    updateBalance(context, getString(R.string.update_balance_url), user);
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
                    params.put("telcoName", telcoName);
                    params.put("reloadAmount", String.valueOf(reloadAmount));
                    params.put("quantity", String.valueOf(quantity-1));
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
                                    String image = getStringImage(bitmap);

                                    SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                                    String loginUsername = pref.getString("username", "");

                                    Transaction transaction = new Transaction(image, telcoName + " Top Up", -reloadAmount, loginUsername);
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
                    params.put("balance", String.valueOf(user.getBalance()-reloadAmount));
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
    //Record top up transaction
    public void recordTransaction(final Context context, String url, final Transaction transaction) {
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
                                    Toast.makeText(getApplicationContext(), "Top up successful.", Toast.LENGTH_SHORT).show();
                                    //Go payment successful page
                                    Intent intent = new Intent(context, PaymentSuccessfulActivity.class);
                                    intent.putExtra(MainActivity.PAYMENT_TITLE, spinnerReloadAmount.getSelectedItem() +
                                            " has been top up to ");
                                    intent.putExtra(MainActivity.PAYMENT_IMAGE, transaction.getImageMerchant());
                                    intent.putExtra(MainActivity.PAYMENT_TARGET, editTextPhoneNo.getText().toString());
                                    TaskStackBuilder.create(PrepaidTopUpActivity.this)//Create a new stack of activities
                                            .addNextIntentWithParentStack(new Intent(PrepaidTopUpActivity.this, MainActivity.class))
                                            .addNextIntentWithParentStack(intent)
                                            .startActivities();
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
    //Convert bitmap image to string
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    //Get Bitmap image from drawable or vector image
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
