package my.edu.tarc.assignment;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import my.edu.tarc.assignment.Model.Transaction;

public class PaymentSuccessfulActivity extends AppCompatActivity {
    private TextView textViewPaymentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);

        textViewPaymentTitle = (TextView)findViewById(R.id.textViewPaymentTitle);

        final Intent intent = getIntent();
        textViewPaymentTitle.setText(intent.getStringExtra(MainActivity.PAYMENT_TITLE));

        Button buttonViewTransactionHistory = (Button)findViewById(R.id.buttonViewTransactionHistory);
        buttonViewTransactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskStackBuilder.create(PaymentSuccessfulActivity.this)
                        .addNextIntentWithParentStack(new Intent(PaymentSuccessfulActivity.this, MainActivity.class))
                        .addNextIntentWithParentStack(new Intent(PaymentSuccessfulActivity.this, WalletActivity.class))
                        .addNextIntentWithParentStack(new Intent(PaymentSuccessfulActivity.this, TransactionHistoryActivity.class))
                        .startActivities();

            }
        });
    }
}
