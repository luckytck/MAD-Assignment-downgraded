package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.edu.tarc.assignment.Model.Voucher;
import my.edu.tarc.assignment.Model.VoucherOrder;

public class ViewVoucherActivity extends AppCompatActivity {
    private ListView listViewVoucher;
    private List<Voucher> voucherList;
    private List<VoucherOrder> voucherOrderList;
    private RequestQueue queue;
    private ProgressDialog pDialog;
    private String loginUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_voucher);
     /*   pDialog = new ProgressDialog(this);
        voucherList = new ArrayList<>();
        SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        loginUsername = pref.getString("username", "");
        listViewVoucher = (ListView)findViewById(R.id.listViewVoucher);
        downloadVoucherOrder(getApplicationContext(),getString(R.string.select_voucherOrder));
        for(int i=0;i<voucherOrderList.size();++i){
            if(!voucherOrderList.get(i).getUsername().equalsIgnoreCase(loginUsername)){
                voucherOrderList.remove(i);
            }
        }
        downloadVoucher(getApplicationContext(),getString(R.string.select_voucher));
        List<Voucher> voucherUser=getVoucher();*/
        //VoucherAdapter voucherAdapter = new VoucherAdapter(getApplicationContext(),voucherUser);
        //listViewVoucher.setAdapter(voucherAdapter);



    }
    @Override
    protected void onResume() {
       /* downloadVoucherOrder(getApplicationContext(),getString(R.string.select_voucherOrder));
        for(int i=0;i<voucherOrderList.size();++i){
            if(!voucherOrderList.get(i).getUsername().equalsIgnoreCase(loginUsername)){
                voucherOrderList.remove(i);
            }
        }
        downloadVoucher(getApplicationContext(),getString(R.string.select_voucher));
        List<Voucher> voucherUser=getVoucher();
        VoucherAdapter voucherAdapter = new VoucherAdapter(getApplicationContext(),voucherList);
        listViewVoucher.setAdapter(voucherAdapter);*/
        super.onResume();
    }

    private List<Voucher> getVoucher(){
        List<Voucher> voucherUser=new ArrayList<>();
        for (int i=0;i<voucherList.size();++i){
            for(int j=0;j<voucherOrderList.size();++j){
                if(voucherList.get(i).getVoucherCode().equals(voucherOrderList.get(j).getVoucherCode())){
                    voucherUser.add(voucherList.get(i));
                }
            }
           }
           return voucherUser;
    }


    private void downloadVoucherOrder(Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        final JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            voucherOrderList.clear();
                             for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                int id = userResponse.getInt("id");
                                String voucherCode = userResponse.getString("voucherCode");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date orderDate = formatter.parse(userResponse.getString("orderDate"));
                                String username = userResponse.getString("username");
                                    VoucherOrder vOrder = new VoucherOrder(id, voucherCode, orderDate, username);
                                    voucherOrderList.add(vOrder);

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
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(LoginActivity.TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void downloadVoucher(Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        final JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                                 voucherList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                String voucherCode = userResponse.getString("voucherCode");
                                String voucherType = userResponse.getString("voucherType");
                                double amount = userResponse.getDouble("amount");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date expiryDate = formatter.parse(userResponse.getString("expiryDate"));
                                String status = userResponse.getString("status");
                                       Voucher v = new Voucher(voucherCode,voucherType,amount,expiryDate,status);
                                        voucherList.add(v);

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
