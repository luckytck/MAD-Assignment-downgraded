package my.edu.tarc.assignment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import my.edu.tarc.assignment.Model.User;

public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private List<User> userList;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }
    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here

      Log.v(LoginActivity.TAG, rawResult.getText()); // Prints scan results
       Log.v(LoginActivity.TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Proceed the payment?");
        builder.setMessage("Amount : RM "+rawResult.getText());
        builder.setIcon(R.drawable.payment);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                proceedPayment(Double.parseDouble(rawResult.getText()));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(QRCodeScannerActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
       builder.show();




    }
    public void proceedPayment(double amount){
        userList = new ArrayList<>();
        downloadUser(getApplicationContext(), getString(R.string.get_user_url));
        SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        String username = pref.getString("username", "");
        int index=-1;
        for (int i = 0; i < userList.size(); i++){
            if (username.equalsIgnoreCase(userList.get(i).getUsername())){
                index=i;
                break;
            }
        }
        User user=new User();
        user.setUsername(username);
        user.setBalance(userList.get(index).getBalance());
        if(user.getBalance()<amount){
            Toast.makeText(getBaseContext(),"Balance not enought,please top up first.",Toast.LENGTH_SHORT).show();
        }else{
            double tempbalance=user.getBalance()-amount;
            user.setBalance(tempbalance);
            updateBalance(QRCodeScannerActivity.this,"https://easy-app.000webhostapp.com/update_balance.php",user);
        }
    }
    private void downloadUser(Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            userList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                String username = userResponse.getString("username");
                                String password = userResponse.getString("password");
                                String name = userResponse.getString("name");
                                String phoneNo = userResponse.getString("phoneNo");
                                String email = userResponse.getString("email");
                                int pin = userResponse.getInt("pin");
                                double balance = userResponse.getDouble("balance");
                                User user = new User(username,password,name,phoneNo,email,pin, balance);
                                userList.add(user);
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
    public void updateBalance(Context context, String url, final User user) {
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
                                String message1 = "Transaction has proceeded";
                                String message2 = "Transaction failed to proceed";
                                if (success==0) {
                                    Toast.makeText(getBaseContext(), message1, Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getBaseContext(), message2, Toast.LENGTH_LONG).show();
                                    finish();
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



}
