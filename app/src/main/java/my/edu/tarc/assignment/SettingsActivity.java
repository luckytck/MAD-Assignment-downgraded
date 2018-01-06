package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import my.edu.tarc.assignment.Model.User;

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "my.edu.tarc.assignment";
    public static final String PROFILE_NAME = "name";
    public static final String PROFILE_CONTACT_NO = "contact_no";
    public static final String PROFILE_EMAIL = "email";
    private static final int PROFILE_UPDATE_REQUEST = 111;
    private User user = new User();
    private List<User> userList;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private TextView textViewUsername, textViewName, textViewContact, textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewName = (TextView)findViewById(R.id.textViewName);
        textViewContact = (TextView)findViewById(R.id.textViewContact);
        textViewEmail = (TextView)findViewById(R.id.textViewEmail);

        pDialog = new ProgressDialog(SettingsActivity.this);
        userList = new ArrayList<>();
        downloadUser(getApplicationContext(), getString(R.string.get_user_url));

        Button buttonEditProfile = (Button)findViewById(R.id.buttonEditProfile);
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                intent.putExtra(PROFILE_NAME, user.getName());
                intent.putExtra(PROFILE_CONTACT_NO, user.getPhoneNo());
                intent.putExtra(PROFILE_EMAIL, user.getEmail());
                startActivity(intent);
            }
        });
    }

    private void downloadUser(Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Loading...");
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
                            loadProfile();
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

    private void loadProfile(){
        SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        String username = pref.getString("username", "");
        for (int i = 0; i < userList.size(); i++){
            if (username.equalsIgnoreCase(userList.get(i).getUsername())){
                user = userList.get(i);
                textViewUsername.setText(getString(R.string.settings_username) + capitalize(user.getUsername()));
                textViewName.setText(getString(R.string.settings_name) + capitalize(user.getName()));
                textViewContact.setText(getString(R.string.settings_contact) + user.getPhoneNo());
                textViewEmail.setText(getString(R.string.settings_email) + capitalize(user.getEmail()));
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        downloadUser(getApplicationContext(), getString(R.string.get_user_url));
        super.onResume();
    }

    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z\\@\\.])([a-z\\@\\.]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }
}
