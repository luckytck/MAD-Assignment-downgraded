package my.edu.tarc.assignment;

import android.content.Context;
import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.assignment.Model.User;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editTextName, editTextContactNo, editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextContactNo = (EditText)findViewById(R.id.editTextContactNo);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        //Show existing profile
        Intent intent = getIntent();
        editTextName.setText(intent.getStringExtra(SettingsActivity.PROFILE_NAME));
        editTextContactNo.setText(intent.getStringExtra(SettingsActivity.PROFILE_CONTACT_NO));
        editTextEmail.setText(intent.getStringExtra(SettingsActivity.PROFILE_EMAIL));
        //Save
        Button buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                String username = pref.getString("username", "");
                String name = editTextName.getText().toString();
                String contactNo = editTextContactNo.getText().toString();
                String email = editTextEmail.getText().toString();

                if (name.isEmpty() || contactNo.isEmpty() || email.isEmpty()){//Check empty fields
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                } else if (!contactNo.matches("^01[0-9]{1}\\-?[0-9]{7,8}$")){//Check contact no format
                    Toast.makeText(getApplicationContext(), "Invalid contact number, please enter a valid format: 01x-xxxxxxx", Toast.LENGTH_LONG).show();
                } else {//Update profile
                    User user = new User();
                    user.setUsername(username);
                    user.setName(name);
                    user.setEmail(email);
                    user.setPhoneNo(contactNo);

                    try {
                        updateProfile(EditProfileActivity.this, getString(R.string.update_profile_url), user);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void updateProfile(Context context, String url, final User user) {
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
                    params.put("name", user.getName());
                    params.put("phoneNo", user.getPhoneNo());
                    params.put("email", user.getEmail());
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
