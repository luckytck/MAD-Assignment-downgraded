package my.edu.tarc.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    EditText editTextUsername,editTextPassword,editTextConfirmPassword,editTextFullName,editTextPhoneNumber,editTextEmailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        editTextUsername=(EditText)findViewById(R.id.editTextUsername);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword=(EditText)findViewById(R.id.editTextConfirmPassword);
        editTextFullName=(EditText)findViewById(R.id.editTextFullName);
        editTextPhoneNumber=(EditText)findViewById(R.id.editTextPhoneNumber);
        editTextEmailAddress=(EditText)findViewById(R.id.editTextEmailAddress);
        editTextUsername.requestFocus();
        Button nextButton=(Button)findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {







                Intent intent=new Intent(RegisterActivity.this,PINSetupActivity.class);
                startActivity(intent);
            }
        });
    }
}
