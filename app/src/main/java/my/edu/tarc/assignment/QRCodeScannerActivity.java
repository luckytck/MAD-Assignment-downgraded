package my.edu.tarc.assignment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import my.edu.tarc.assignment.Model.User;

public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public static final String STORE_NAME = "store name";
    public static final String PAYMENT_AMOUNT = "amount";
    private ZXingScannerView mScannerView;
    private List<User> userList;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }
    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here

        Log.v(LoginActivity.TAG, rawResult.getText()); // Prints scan results
        Log.v(LoginActivity.TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        String[] params = rawResult.getText().split(":");
        String[] storeName = null;
        String[] amount = null;
        boolean isValidQRcode = false;
        if (params.length == 2){
            storeName = params[0].split("=");
            amount = params[1].split("=");
            if (storeName[0].equalsIgnoreCase("storeName")
                    && amount[0].equalsIgnoreCase("amount")
                    && amount[1].matches("^\\-?([0-9]*)(\\.([0-9]*))?$")){
                isValidQRcode = true;
            }
        }
        //QR code Format : storeName=xxx&amount=xxx
        if (isValidQRcode){
            //Go payment confirmation page
            Intent intent = new Intent(this, ConfirmationActivity.class);
            intent.putExtra(STORE_NAME, storeName[1]);
            intent.putExtra(PAYMENT_AMOUNT, Double.parseDouble(amount[1]));
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(),"Invalid QR code format",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
