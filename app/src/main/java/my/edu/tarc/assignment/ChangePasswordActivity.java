package my.edu.tarc.assignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.assignment.Model.User;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText editTextOldPassword,editTextNewPassword,editTextConfirmNewPassword;
    private static final String TAG = "my.edu.tarc.assignment";

    private List<User> userList;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        userList = new ArrayList<>();
        editTextOldPassword=(EditText)findViewById(R.id.editTextOldPassword);
        editTextNewPassword=(EditText)findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword=(EditText)findViewById(R.id.editTextConfirmNewPassword);
        downloadUser(getApplicationContext(), getString(R.string.get_user_url));
        Button buttonSave=(Button)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                String username = pref.getString("username", "");
                String oldpassword = editTextOldPassword.getText().toString();
                String newpassword = editTextNewPassword.getText().toString();
                String confirmnewpassword = editTextConfirmNewPassword.getText().toString();
                if(oldpassword.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter Old Password",Toast.LENGTH_SHORT).show();
                }else if(newpassword.isEmpty() ){
                    Toast.makeText(getApplicationContext(),"Please enter New Password",Toast.LENGTH_SHORT).show();
                }else if(confirmnewpassword.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter Confirm New Password",Toast.LENGTH_SHORT).show();
                }else {
                    if (!newpassword.equals(confirmnewpassword)) {
                        Toast.makeText(getApplicationContext(), "New Password and Confirm New Password must be same.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkPassword(username, oldpassword)) {
                            User user = new User();
                            user.setUsername(username);
                            user.setPassword(newpassword);

                            try {
                                updatePassword(ChangePasswordActivity.this, getString(R.string.update_user_password), user);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                        }

                    }
                }


            }
        });

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
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
    private boolean checkPassword(String username,String password){
        boolean isValid=false;

        for (int i = 0; i < userList.size(); i++){
            if (username.equalsIgnoreCase(userList.get(i).getUsername()) &&
                    password.equals(userList.get(i).getPassword())){
                isValid = true;

                break;
            }
        }
        return isValid;
    }
    public void updatePassword(Context context, String url, final User user) {
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
                    params.put("password", user.getPassword());
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
