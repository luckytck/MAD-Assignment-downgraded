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
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.assignment.Model.Transaction;
import my.edu.tarc.assignment.Model.User;
import my.edu.tarc.assignment.Model.Voucher;
import my.edu.tarc.assignment.Model.VoucherOrder;

public class PurchaseVoucherActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner spinnerPurchaseAmount;
    private Pinview pinviewTransactionPIN;
    private ImageView imageViewVoucherLogo;
    private String vouchertype;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private User user;
    private List<Voucher> voucherList;
    private List<VoucherOrder> voucherOrderList;
    private int pinInput;
    private double purchaseAmount;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_voucher);

        spinnerPurchaseAmount=(Spinner)findViewById(R.id.spinnerPurchaseAmount);
        pinviewTransactionPIN = (Pinview)findViewById(R.id.pinviewPurchasePIN);
        imageViewVoucherLogo=(ImageView)findViewById(R.id.imageViewVoucherLogo);
        pDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        voucherList=new ArrayList<>();
        //voucherOrderList= new ArrayList<>();
        vouchertype = intent.getStringExtra(MainActivity.VOUCHER_TYPE);

        if (vouchertype.equalsIgnoreCase("Garena Shells")){
            imageViewVoucherLogo.setImageResource(R.drawable.logo_garena);
            bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.logo_garena);
        } else if (vouchertype.equalsIgnoreCase("Steam Wallet Code")){
            imageViewVoucherLogo.setImageResource(R.drawable.logo_steam);
            bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.logo_steam);
        } else if (vouchertype.equalsIgnoreCase("PSD Digital Code")){
            imageViewVoucherLogo.setImageResource(R.drawable.logo_psd);
            bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.logo_psd);
        }
        //Create an adapter for spinner
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.purchase_amount,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPurchaseAmount.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        spinnerPurchaseAmount.setAdapter(adapter);

        Button buttonConfirm = (Button)findViewById(R.id.buttonConfirm5);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = spinnerPurchaseAmount.getSelectedItemPosition();
                if (pos == -1){
                    Toast.makeText(getApplicationContext(), "Please select purchase amount.", Toast.LENGTH_LONG).show();
                }  else if (pinviewTransactionPIN.getValue().length() < 6){
                    Toast.makeText(getApplicationContext(),"Please fill up 6-Digit PIN.",Toast.LENGTH_SHORT).show();
                } else {
                    pinInput = Integer.parseInt(pinviewTransactionPIN.getValue());
                    switch (pos){
                        case 0:
                            purchaseAmount = 10;
                            break;
                        case 1:
                            purchaseAmount = 20;
                            break;
                        case 2:
                            purchaseAmount = 50;
                            break;
                        case 3:
                            purchaseAmount = 100;
                            break;
                        default:
                            purchaseAmount = 0;
                    }
                    validatePIN(getApplicationContext(),getString(R.string.get_user_url));
                }
            }
        });




    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
        ((TextView) adapterView.getChildAt(0)).setTextSize(18);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void validatePIN(final Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Purchase processing...");
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
                                if (user.getBalance() >= purchaseAmount){
                                    getVoucher(context, getString(R.string.select_voucher));

                                } else {
                                    Toast.makeText(getApplicationContext(), "Purchase failed, insufficient balance.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Purchase failed, incorrect PIN number.", Toast.LENGTH_LONG).show();
                            }
                            if ((pDialog.isShowing() && !isValidPin) || (pDialog.isShowing() && user.getBalance() < purchaseAmount))
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
        jsonObjectRequest.setTag(LoginActivity.TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void getVoucher(final Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                                voucherList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                String voucherCode = userResponse.getString("voucherCode");
                                String voucherType = userResponse.getString("voucherType");
                                double amount = userResponse.getDouble("amount");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date expiryDate = formatter.parse(userResponse.getString("expiryDate"));
                                String status = userResponse.getString("status");

                               Voucher v=new Voucher(voucherCode,voucherType,amount,expiryDate,status);
                                if(voucherType.equalsIgnoreCase(vouchertype) && status.equalsIgnoreCase("available")
                                        && amount==purchaseAmount && System.currentTimeMillis() <expiryDate.getTime()){
                                    voucherList.add(v);
                                }

                            }
                            if(voucherList.size()>0){
                                voucherList.get(0).setStatus("unavailable");
                                updateVoucherStatus(getApplicationContext(),getString(R.string.update_voucher_status),voucherList.get(0));


                            }else{
                                Toast.makeText(getApplicationContext(), "This amount of voucher is sold out.", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
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
        jsonObjectRequest.setTag(LoginActivity.TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
   /* private void getVoucherOrder(final Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
voucherOrderList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                int id = userResponse.getInt("id");
                                String voucherCode = userResponse.getString("voucherCode");
                                  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date orderDate = formatter.parse(userResponse.getString("orderDate"));
                                String username = userResponse.getString("username");

                                VoucherOrder vo=new VoucherOrder(id,voucherCode,orderDate,username);
                               voucherOrderList.add(vo);

                            }



                        } catch (Exception e) {
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
        jsonObjectRequest.setTag(LoginActivity.TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }*/


    public void updateVoucherStatus(Context context, String url, final Voucher voucher) {
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
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                                    String loginUsername = pref.getString("username", "");
                                    VoucherOrder voucherOrder=new VoucherOrder();
                                    voucherOrder.setUsername(loginUsername);
                                    voucherOrder.setVoucherCode(voucherList.get(0).getVoucherCode());
                                    insertVoucherOrder(getApplicationContext(),getString(R.string.insert_voucherOrder),voucherOrder);




                                                                    }
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
                    params.put("voucherCode", voucher.getVoucherCode());
                    params.put("status", voucher.getStatus());
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
    public void insertVoucherOrder(Context context,String url,final VoucherOrder voucherOrder){
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
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    user.setBalance(user.getBalance()-purchaseAmount);
                                    updateBalance(getApplicationContext(),getString(R.string.update_balance_url),user);
                                                                   }
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

                    params.put("voucherCode",voucherOrder.getVoucherCode());
                    params.put("username", voucherOrder.getUsername());
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
                                    Toast.makeText(getApplicationContext(), "Purchase failed.", Toast.LENGTH_LONG).show();
                                }else{
                                    String image = getStringImage(bitmap);
                                    SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                                    String loginUsername = pref.getString("username", "");

                                    Transaction transaction = new Transaction(image, vouchertype + " Purchase", -purchaseAmount, loginUsername);
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
                    params.put("balance", String.valueOf(user.getBalance()));
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
                              //  String message = jsonObject.getString("message");
                                if (success==0) {
                                    Toast.makeText(getApplicationContext(), "Insert transaction failed.", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Purchase successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, PurcahseVoucherSuccessfulActivity.class);
                                    intent.putExtra(MainActivity.VOUCHER_AMOUNT,spinnerPurchaseAmount.getSelectedItem().toString());
                                    intent.putExtra(MainActivity.VOUCHER_TYPE,vouchertype);
                                    intent.putExtra(MainActivity.VOUCHER_CODE,voucherList.get(0).getVoucherCode());
                                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                                    intent.putExtra(MainActivity.VOUCHER_EXPIRYDATE,formatter.format(voucherList.get(0).getExpiryDate()));
                                    TaskStackBuilder.create(PurchaseVoucherActivity.this)//Create a new stack of activities
                                            .addNextIntentWithParentStack(new Intent(PurchaseVoucherActivity.this, MainActivity.class))
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
