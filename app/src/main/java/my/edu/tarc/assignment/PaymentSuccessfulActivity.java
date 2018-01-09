package my.edu.tarc.assignment;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

import my.edu.tarc.assignment.Model.Transaction;

public class PaymentSuccessfulActivity extends AppCompatActivity {
    private TextView textViewPaymentTitle, textViewTarget;
    private ImageView imageViewPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);

        textViewPaymentTitle = (TextView)findViewById(R.id.textViewPaymentTitle);
        textViewTarget = (TextView)findViewById(R.id.textViewTarget);
        imageViewPayment = (ImageView)findViewById(R.id.imageViewPayment);
        //Get payment values
        final Intent intent = getIntent();
        textViewPaymentTitle.setText(intent.getStringExtra(MainActivity.PAYMENT_TITLE));
        textViewTarget.setText(intent.getStringExtra(MainActivity.PAYMENT_TARGET));

        //Convert string to bitmap
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //decode base64 string to image
        byte[] imageBytes = baos.toByteArray();
        imageBytes = Base64.decode(intent.getStringExtra(MainActivity.PAYMENT_IMAGE), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageViewPayment.setImageBitmap(decodedImage);
        //View transaction history
        Button buttonViewTransactionHistory = (Button)findViewById(R.id.buttonViewTransactionHistory);
        buttonViewTransactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskStackBuilder.create(PaymentSuccessfulActivity.this)//Create a new stack of activities
                        .addNextIntentWithParentStack(new Intent(PaymentSuccessfulActivity.this, MainActivity.class))
                        .addNextIntentWithParentStack(new Intent(PaymentSuccessfulActivity.this, WalletActivity.class))
                        .addNextIntentWithParentStack(new Intent(PaymentSuccessfulActivity.this, TransactionHistoryActivity.class))
                        .startActivities();

            }
        });
    }

    //Convert bitmap image to string
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
