package my.edu.tarc.assignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodiebag.pinview.Pinview;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.assignment.Model.User;

public class PINSetupActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinsetup);

        Button buttonnconfirm=(Button)findViewById(R.id.buttonConfirm);

        buttonnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pinview PIN=(Pinview)findViewById(R.id.pinviewTopUpPIN);
            if(PIN.getValue().length()<6){
                Toast.makeText(getApplicationContext(),"Error:"+"Please fill up 6-Digit PIN.",Toast.LENGTH_SHORT).show();
            }else{
                String username,password,phoneNo,email,name,transactionPIN;
                Intent intent=getIntent();
                username=intent.getStringExtra(RegisterActivity.USER_USERNAME);
                password=intent.getStringExtra(RegisterActivity.USER_PASSWORD);
                email=intent.getStringExtra(RegisterActivity.USER_EMAIL);
                phoneNo=intent.getStringExtra(RegisterActivity.USER_PHONENO);
                name=intent.getStringExtra(RegisterActivity.USER_NAME);
                transactionPIN=PIN.getValue().toString();
                User user=new User(username,password,name,phoneNo,email,Integer.parseInt(transactionPIN),0.0);

                try {
                    InsertUser(PINSetupActivity.this, getString(R.string.insert_user_url), user);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            }
        });


    }
    public void InsertUser(Context context,String url,final User user){
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
                                    Intent intent = new Intent(PINSetupActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
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
                    params.put("username",user.getUsername());
                    params.put("password",user.getPassword());
                    params.put("name", user.getName());
                    params.put("phoneNo", user.getPhoneNo());
                    params.put("email", user.getEmail());
                    params.put("pin",String.valueOf(user.getPin()));
                    params.put("balance",String.valueOf(user.getBalance()));

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
