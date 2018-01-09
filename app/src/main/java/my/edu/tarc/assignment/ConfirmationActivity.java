package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class ConfirmationActivity extends AppCompatActivity {
    public static final String TAG = "my.edu.tarc.assignment";
    private TextView textViewStoreName, textViewAmount;
    private ImageView imageViewStore;
    private Pinview pinviewConfirmPIN;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private User user;
    private Bitmap bitmap;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        pDialog = new ProgressDialog(this);
        textViewAmount = (TextView)findViewById(R.id.textViewAmount);
        textViewStoreName = (TextView)findViewById(R.id.textViewTarget);
        pinviewConfirmPIN = (Pinview)findViewById(R.id.pinviewConfirmPIN);
        imageViewStore = (ImageView)findViewById(R.id.imageViewStore);
        //Get QR code values
        Intent intent = getIntent();
        String storeName = intent.getStringExtra(QRCodeScannerActivity.STORE_NAME);
        amount = intent.getDoubleExtra(QRCodeScannerActivity.PAYMENT_AMOUNT, 0);
        textViewStoreName.setText(storeName);
        textViewAmount.setText(getString(R.string.balance) + String.format("%.2f", amount));
        //Check Store name
        if (storeName.equalsIgnoreCase("7-eleven")){
            bitmap = getBitmapFromVectorDrawable(this, R.drawable.icon_7eleven);
            imageViewStore.setImageResource(R.drawable.icon_7eleven);
        } else if (storeName.equalsIgnoreCase("KK Super Mart")){
            bitmap = getBitmapFromVectorDrawable(this, R.drawable.icon_kk_super_mart);
            imageViewStore.setImageResource(R.drawable.icon_kk_super_mart);
        } else {
            bitmap = getBitmapFromVectorDrawable(this, R.drawable.ic_terrain_black_24dp);
            imageViewStore.setImageResource(R.drawable.ic_terrain_black_24dp);
        }
        //Confirm payment
        Button buttonConfirm = (Button)findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pinviewConfirmPIN.getValue().length() < 6){//Invalid pin number
                    Toast.makeText(getApplicationContext(),"Please fill up 6-Digit PIN.",Toast.LENGTH_SHORT).show();
                } else if (amount <= 0) {//Check payment amount
                    Toast.makeText(getApplicationContext(),"Error: payment amount must greater than 0.",Toast.LENGTH_LONG).show();
                } else {
                    //Process payment
                    processPayment();
                }
            }
        });
    }

    private void processPayment() {
        int pinInput = Integer.parseInt(pinviewConfirmPIN.getValue());
        validatePIN(getApplicationContext(), getString(R.string.get_user_url), pinInput);
    }
    //Validate pin number
    private void validatePIN(final Context context, String url, final int pinInput){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Payment processing...");
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
                                if (user.getBalance() >= amount){
                                    checkStore(context, getString(R.string.get_store_url));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Payment failed. failed, insufficient balance.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Payment failed. failed, incorrect PIN number.", Toast.LENGTH_LONG).show();
                            }
                            if ((pDialog.isShowing() && !isValidPin) || (pDialog.isShowing() && user.getBalance() < amount))
                                pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                            if (pDialog.isShowing())
                                pDialog.dismiss();
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
    //Check Valid Store
    private void checkStore(final Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url + textViewStoreName.getText().toString().replace(" ", "%20"),null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            int success = response.getInt("success");
                            if (success == 1){
                                updateBalance(context, getString(R.string.update_balance_url), user);
                            } else {
                                Toast.makeText(getApplicationContext(), "Store not found", Toast.LENGTH_LONG).show();
                            }
                            if (pDialog.isShowing() && success == 0)
                                pDialog.dismiss();
                        } catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                            if (pDialog.isShowing())
                                pDialog.dismiss();
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
                                    Toast.makeText(getApplicationContext(), "Payment failed.", Toast.LENGTH_LONG).show();
                                }else{
                                    //Telco image to string
                                    String image = getStringImage(bitmap);

                                    SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                                    String loginUsername = pref.getString("username", "");

                                    Transaction transaction = new Transaction(image, textViewStoreName.getText().toString(), -amount, loginUsername);
                                    recordTransaction(context, getString(R.string.insert_transaction_url), transaction);
                                }
                                if (pDialog.isShowing() && success == 0)
                                    pDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("balance", String.valueOf(user.getBalance()-amount));
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
    //Record payment transaction
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
                                    intent.putExtra(MainActivity.PAYMENT_TITLE, getString(R.string.balance) + String.format("%.2f", amount) +
                                            " has been paid to");
                                    intent.putExtra(MainActivity.PAYMENT_IMAGE, transaction.getImageMerchant());
                                    intent.putExtra(MainActivity.PAYMENT_TARGET, transaction.getTitle());
                                    TaskStackBuilder.create(ConfirmationActivity.this)//Create a new stack of activities
                                            .addNextIntentWithParentStack(new Intent(ConfirmationActivity.this, MainActivity.class))
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
                            if (pDialog.isShowing())
                                pDialog.dismiss();
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
