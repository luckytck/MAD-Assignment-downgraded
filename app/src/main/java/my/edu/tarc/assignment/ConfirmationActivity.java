package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ConfirmationActivity extends AppCompatActivity {
    private TextView textViewStoreName, textViewAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        textViewAmount = (TextView)findViewById(R.id.textViewAmount);
        textViewStoreName = (TextView)findViewById(R.id.textViewStoreName);

        Intent intent = getIntent();
        String storeName = intent.getStringExtra(QRCodeScannerActivity.STORE_NAME);
        double amount = Double.parseDouble(intent.getStringExtra(QRCodeScannerActivity.AMOUNT));
        textViewStoreName.setText(storeName);
        textViewAmount.setText(getString(R.string.balance) + String.format("%.2f", amount));
    }
}
