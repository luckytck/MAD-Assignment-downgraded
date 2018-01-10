package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import my.edu.tarc.assignment.Model.User;

import static my.edu.tarc.assignment.R.id.editTextUsername;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "my.edu.tarc.assignment";
    private List<User> userList;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    public final static String USER_USERNAME="username";
    public final static String USER_PASSWORD="password";
    public final static String USER_NAME="name";
    public final static String USER_PHONENO="phoneNo";
    public final static String USER_EMAIL="email";

    private EditText editTextUsername2,editTextPassword,editTextConfirmPassword,editTextFullName,editTextPhoneNumber,editTextEmailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pDialog = new ProgressDialog(RegisterActivity.this);
        userList = new ArrayList<>();

        editTextUsername2=(EditText)findViewById(R.id.editTextUsername2);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword2);
        editTextConfirmPassword=(EditText)findViewById(R.id.editTextConfirmPassword);
        editTextFullName=(EditText)findViewById(R.id.editTextFullName);
        editTextPhoneNumber=(EditText)findViewById(R.id.editTextPhoneNumber);
        editTextEmailAddress=(EditText)findViewById(R.id.editTextEmailAddress);
        editTextUsername2.requestFocus();
        Button nextButton=(Button)findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isEmptyField()){
                    if(CheckPassword()){
                        getUser(getApplicationContext(), getString(R.string.get_user_url));
                    }
                }



            }
        });
    }
    public boolean isEmptyField(){
        boolean isEmpty=false;
        String username = editTextUsername2.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String email = editTextEmailAddress.getText().toString();
        String name = editTextFullName.getText().toString();
        String phoneNo = editTextPhoneNumber.getText().toString();
        if(username.isEmpty()){
            isEmpty=true;
            Toast.makeText(getApplicationContext(),"Error:"+"Username cannot be empty.",Toast.LENGTH_SHORT).show();

        } else if (password.isEmpty()) {
            isEmpty=true;
            Toast.makeText(getApplicationContext(),"Error:"+"Password cannot be empty.",Toast.LENGTH_SHORT).show();

        } else if (confirmPassword.isEmpty()) {
            isEmpty=true;
            Toast.makeText(getApplicationContext(),"Error:"+"Confirm Password cannot be empty.",Toast.LENGTH_SHORT).show();

        } else if (name.isEmpty()) {
                isEmpty = true;
                Toast.makeText(getApplicationContext(), "Error:" + "Name cannot be empty.", Toast.LENGTH_SHORT).show();

            } else if (phoneNo.isEmpty()) {
                isEmpty = true;
                Toast.makeText(getApplicationContext(), "Error:" + "Phone Number cannot be empty.", Toast.LENGTH_SHORT).show();

            } else if (email.isEmpty()) {
                isEmpty = true;
                Toast.makeText(getApplicationContext(), "Error:" + "Email cannot be empty.", Toast.LENGTH_SHORT).show();
        }
        return isEmpty;
    }

    public void getUser(Context context,String url){
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("processing...");
        pDialog.show();

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
                            CheckDuplicateUsername();
                            if (pDialog.isShowing())
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
    public void CheckDuplicateUsername(){
        String username = editTextUsername2.getText().toString();
        boolean isDuplicate=false;
            for (int i = 0; i < userList.size(); i++) {
                if (username.equalsIgnoreCase(userList.get(i).getUsername())) {

                    Toast.makeText(getApplicationContext(), "Error:" + "This username already exist.", Toast.LENGTH_SHORT).show();
                    isDuplicate=true;
                }
            }
        if(!isDuplicate){
            String password=editTextPassword.getText().toString();
            String phoneNo=editTextPhoneNumber.getText().toString();
            String email=editTextEmailAddress.getText().toString();
            String name=editTextFullName.getText().toString();
            if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                if(Pattern.matches("\\d{3,4}-\\d{7,8}",phoneNo)){
                    Intent intent = new Intent(RegisterActivity.this, PINSetupActivity.class);
                    intent.putExtra(USER_USERNAME,username);
                    intent.putExtra(USER_PASSWORD,password);
                    intent.putExtra(USER_NAME,name);
                    intent.putExtra(USER_EMAIL,email);
                    intent.putExtra(USER_PHONENO,phoneNo);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Error:PhoneNo format eg. xxx-xxxx xxxx",Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(getApplicationContext(),"Error: Invalid Email", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public boolean CheckPassword(){
        String password = editTextPassword.getText().toString();
        String confirmpassword = editTextConfirmPassword.getText().toString();
        boolean isValid = false;


            if(password.equals(confirmpassword)){
                isValid=true;
            }else{
                Toast.makeText(getApplicationContext(),"Error:"+"ConfirmPassword must same with Password.",Toast.LENGTH_SHORT).show();
            }
        return isValid;
    }



}
