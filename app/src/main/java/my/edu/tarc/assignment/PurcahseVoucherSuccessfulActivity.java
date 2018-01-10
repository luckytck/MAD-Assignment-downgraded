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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purcahse_voucher_successful);
        String vouchertype,voucheramount,vouchercode,expirydate;
        String redeemMethod="";
        textViewVoucherInfo=(TextView)findViewById(R.id.textViewVoucherInfo);
        textViewVoucherCode=(TextView)findViewById(R.id.textViewVoucherCode);
        textViewVoucherExpiryDate=(TextView)findViewById(R.id.textViewVoucherExpiryDate);
        textViewRedeemMethod=(TextView)findViewById(R.id.textViewRedeemMethod);
        imageViewVoucherLogo= (ImageView)findViewById(R.id.imageViewVoucherLogo);

        Intent intent=getIntent();
        vouchertype=intent.getStringExtra(MainActivity.VOUCHER_TYPE);
        voucheramount=intent.getStringExtra(MainActivity.VOUCHER_AMOUNT);
        vouchercode=intent.getStringExtra(MainActivity.VOUCHER_CODE);
        expirydate=intent.getStringExtra(MainActivity.VOUCHER_EXPIRYDATE);
        textViewVoucherCode.setText(vouchercode);
        textViewVoucherInfo.setText(voucheramount+" "+vouchertype);
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

}
