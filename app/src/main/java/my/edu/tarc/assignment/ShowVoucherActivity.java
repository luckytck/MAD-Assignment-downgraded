package my.edu.tarc.assignment;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ShowVoucherActivity extends AppCompatActivity {

    private TextView textViewVoucherCode,textViewVouhcerExpiryDate,textViewVoucherTitle,textViewVoucherRedeemMethod;
    private ImageView imageViewVoucherLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_voucher);

        textViewVoucherCode=(TextView)findViewById(R.id.textViewVoucherCode3);
        textViewVoucherTitle=(TextView)findViewById(R.id.textViewVoucherTitle2);
        textViewVouhcerExpiryDate=(TextView)findViewById(R.id.textViewVoucherExpiryDate3);
        textViewVoucherRedeemMethod=(TextView)findViewById(R.id.textViewVoucherRedeemMethod2);
        imageViewVoucherLogo=(ImageView)findViewById(R.id.imageViewVoucherLogo4);

        Intent intent=getIntent();
        String voucherCode,title,expiryDate,type;
        String method="";
        voucherCode=intent.getStringExtra(ViewVoucherActivity.VOUCHER_CODE);
        textViewVoucherCode.setText(voucherCode);
        type=intent.getStringExtra(ViewVoucherActivity.VOUCHER_TYPE);
        title="RM "+intent.getStringExtra(ViewVoucherActivity.VOUCHER_AMOUNT)+" "+type;
        textViewVoucherTitle.setText(title);
        expiryDate="Expiry on "+intent.getStringExtra(ViewVoucherActivity.VOUCHER_EXPIRYDATE);
        textViewVouhcerExpiryDate.setText(expiryDate);
        if(type.equalsIgnoreCase("Garena Shells")){
            imageViewVoucherLogo.setImageResource(R.drawable.logo_garena);
            method="1.Go to your Garena Account\n 2.Click \"Add Shells\" \n " +
                    "3.Click \"Redeem a Garena Shells Code\"\n 4.Enter your Garena Shells Code\n" +
                    "5.Click \"Continue\" ";

        }else if(type.equalsIgnoreCase("PSD Digital Code")){
            imageViewVoucherLogo.setImageResource(R.drawable.logo_psd);
            method="1.Go to your PSD Account\n 2.Click \"Add funds to your PSD Wallet\" \n " +
                    "3.Click \"Redeem a PSD Digital Code\"\n 4.Enter your PSD Digital Code\n" +
                    "5.Click \"Continue\" ";

        }else if(type.equalsIgnoreCase("Steam Wallet Code")){
            imageViewVoucherLogo.setImageResource(R.drawable.logo_steam);
            method="1.Go to your Steam Account\n 2.Click \"Add funds to your Steam Wallet\" \n " +
                    "3.Click \"Redeem a Steam Wallet Code\"\n 4.Enter your Steam Wallet Code\n" +
                    "5.Click \"Continue\" ";

        }
        textViewVoucherRedeemMethod.setText(method);




    }
}
