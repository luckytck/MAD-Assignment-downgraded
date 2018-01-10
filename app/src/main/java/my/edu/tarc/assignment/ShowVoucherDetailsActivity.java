package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowVoucherDetailsActivity extends AppCompatActivity {
    private TextView textViewVoucherInfo, textViewVoucherCode, textViewVoucherExpiryDate, textViewRedeemMethod;
    private ImageView imageViewVoucherLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_voucher_details);
        String vouchertype, voucheramount, vouchercode, expirydate;
        String redeemMethod = "";
        textViewVoucherInfo = (TextView) findViewById(R.id.textViewVoucherTitle);
        textViewVoucherCode = (TextView) findViewById(R.id.textViewVoucherCode);
        textViewVoucherExpiryDate = (TextView) findViewById(R.id.textViewVoucherExpiryDate);
        textViewRedeemMethod = (TextView) findViewById(R.id.textViewRedeemMethod);
        imageViewVoucherLogo = (ImageView) findViewById(R.id.imageViewVoucherLogo);
//to get the voucher info from last activity
        Intent intent = getIntent();
        vouchertype = intent.getStringExtra(MainActivity.VOUCHER_TYPE);
        voucheramount = intent.getStringExtra(MainActivity.VOUCHER_AMOUNT);
        vouchercode = intent.getStringExtra(MainActivity.VOUCHER_CODE);
        expirydate = intent.getStringExtra(MainActivity.VOUCHER_EXPIRYDATE);
        textViewVoucherCode.setText(vouchercode);
        textViewVoucherInfo.setText(getString(R.string.balance) + voucheramount + " " + vouchertype);
        textViewVoucherExpiryDate.setText("Expiry on " + expirydate);
//to get the correct voucher type
        if (vouchertype.equalsIgnoreCase("Garena Shells")) {
            imageViewVoucherLogo.setImageResource(R.drawable.logo_garena);
            redeemMethod = "1. Go to your Garena Account\n" +
                    "2. Click \"Add Shells\"\n" +
                    "3. Click \"Redeem a Garena Shells Code\"\n" +
                    "4. Enter your Garena Shells Code\n" +
                    "5. Click \"Continue\" ";
        } else if (vouchertype.equalsIgnoreCase("PSN Digital Code")) {
            imageViewVoucherLogo.setImageResource(R.drawable.logo_psn);
            redeemMethod = "1. Go to your PSD Account\n" +
                    "2. Click \"Add funds to your PSD Wallet\"\n" +
                    "3. Click \"Redeem a PSD Digital Code\"\n" +
                    "4. Enter your PSD Digital Code\n" +
                    "5. Click \"Continue\" ";
        } else if (vouchertype.equalsIgnoreCase("Steam Wallet Code")) {
            imageViewVoucherLogo.setImageResource(R.drawable.logo_steam);
            redeemMethod = "1. Go to your Steam Account\n" +
                    "2. Click \"Add funds to your Steam Wallet\"\n" +
                    "3. Click \"Redeem a Steam Wallet Code\"\n" +
                    "4. Enter your Steam Wallet Code\n" +
                    "5. Click \"Continue\" ";
        }
        textViewRedeemMethod.setText(redeemMethod);
    }
}
