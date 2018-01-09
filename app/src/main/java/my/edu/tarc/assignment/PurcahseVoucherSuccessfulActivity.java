package my.edu.tarc.assignment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import my.edu.tarc.assignment.Model.Voucher;

public class PurcahseVoucherSuccessfulActivity extends AppCompatActivity {

    private TextView textViewVoucherInfo,textViewVoucherCode,textViewVoucherExpiryDate,textViewRedeemMethod;
    private ImageView imageViewVoucherLogo;
    private String vouchertype,voucheramount;
    private RequestQueue queue;

    private Voucher voucher;
    private String vouchercode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purcahse_voucher_successful);

        textViewVoucherInfo=(TextView)findViewById(R.id.textViewVoucherInfo);
        textViewVoucherCode=(TextView)findViewById(R.id.textViewVoucherCode);
        textViewVoucherExpiryDate=(TextView)findViewById(R.id.textViewVoucherExpiryDate);
        textViewRedeemMethod=(TextView)findViewById(R.id.textViewRedeemMethod);
        imageViewVoucherLogo= (ImageView)findViewById(R.id.imageViewVoucherLogo);

        Intent intent=getIntent();
        vouchertype=intent.getStringExtra(PurchaseVoucherActivity.VOUCHER_TYPE);
        voucheramount=intent.getStringExtra(PurchaseVoucherActivity.VOUCHER_AMOUNT);
        vouchercode=intent.getStringExtra(PurchaseVoucherActivity.VOUCHER_CODE);
        textViewVoucherCode.setText(vouchercode);
        textViewVoucherInfo.setText(voucheramount+" "+vouchertype);

        String redeemMethod="";
        getVouhcer(getApplicationContext(),getString(R.string.select_voucher));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String expirydate=formatter.format(voucher.getExpiryDate());
        textViewVoucherExpiryDate.setText("Expiry on "+expirydate);
        if(vouchertype.equalsIgnoreCase("Garena Shells")){
            imageViewVoucherLogo.setImageResource(R.drawable.logo_garena);
            redeemMethod="1.Go to your Garena Account\n 2.Click \"Add Shells\" \n " +
                    "3.Click \"Redeem a Garena Shells Code\"\n 4.Enter your Garena Shells Code\n" +
                    "5.Click \"Continue\" ";

        }else if(vouchertype.equalsIgnoreCase("PSD Digital Code")){
            imageViewVoucherLogo.setImageResource(R.drawable.logo_psd);
            redeemMethod="1.Go to your PSD Account\n 2.Click \"Add funds to your PSD Wallet\" \n " +
                    "3.Click \"Redeem a PSD Digital Code\"\n 4.Enter your PSD Digital Code\n" +
                    "5.Click \"Continue\" ";

        }else if(vouchertype.equalsIgnoreCase("Steam Wallet Code")){
            imageViewVoucherLogo.setImageResource(R.drawable.logo_steam);
            redeemMethod="1.Go to your Steam Account\n 2.Click \"Add funds to your Steam Wallet\" \n " +
                    "3.Click \"Redeem a Steam Wallet Code\"\n 4.Enter your Steam Wallet Code\n" +
                    "5.Click \"Continue\" ";

        }
        textViewRedeemMethod.setText(redeemMethod);



    }
    private void getVouhcer(final Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                String voucherCode = userResponse.getString("voucherCode");
                                String voucherType = userResponse.getString("voucherType");
                                double amount = userResponse.getDouble("amount");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date expiryDate = formatter.parse(userResponse.getString("expiryDate"));
                                String status = userResponse.getString("status");

                                if(voucherCode.equals(vouchercode)){
                                    voucher=new Voucher(voucherCode,voucherType,amount,expiryDate,status);
                                }

                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(LoginActivity.TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}
