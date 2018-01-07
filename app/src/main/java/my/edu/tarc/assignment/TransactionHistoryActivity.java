package my.edu.tarc.assignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import my.edu.tarc.assignment.Model.Transaction;
import my.edu.tarc.assignment.Model.TransactionAdapter;

public class TransactionHistoryActivity extends AppCompatActivity {
    private ListView listViewTransaction;
    private List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        listViewTransaction = (ListView)findViewById(R.id.listViewTransaction);
        transactionList = new ArrayList<>();

        Bitmap bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.digi);
        String encodedImage = getStringImage(bitmap);
        GregorianCalendar transactionDate = new GregorianCalendar();
        Transaction transaction = new Transaction(encodedImage, "Digi Top Up", -10, transactionDate);
        transactionList.add(transaction);
        bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_account_balance_wallet_grey_24dp);
        encodedImage = getStringImage(bitmap);
        Transaction transaction1 = new Transaction(encodedImage, "Wallet Top Up", 5, transactionDate);
        transactionList.add(transaction1);
        transactionList.add(transaction);
        transactionList.add(transaction1);
        transactionList.add(transaction);
        transactionList.add(transaction1);
        transactionList.add(transaction);
        transactionList.add(transaction1);
        transactionList.add(transaction);
        transactionList.add(transaction1);
        transactionList.add(transaction);
        transactionList.add(transaction1);
        transactionList.add(transaction);
        transactionList.add(transaction1);

        TransactionAdapter transactionAdapter = new TransactionAdapter(this, transactionList);
        listViewTransaction.setAdapter(transactionAdapter);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
