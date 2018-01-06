package my.edu.tarc.assignment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.goodiebag.pinview.Pinview;

public class PINSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinsetup);
        Button btnconfirm=(Button)findViewById(R.id.buttonConfirm);
        final Pinview pinview=(Pinview)findViewById(R.id.pinviewTransactionPIN);
        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





            }
        });


    }
}
