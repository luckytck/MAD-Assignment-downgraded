package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
import my.edu.tarc.assignment.Model.VoucherAdapter;
import my.edu.tarc.assignment.Model.VoucherOrder;

public class ViewVoucherActivity extends AppCompatActivity {
    private ListView listViewVoucher;
    private List<Voucher> voucherList;
    private List<VoucherOrder> voucherOrderList;
    private RequestQueue queue;


    private String loginUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_voucher);

        voucherList = new ArrayList<>();
        voucherOrderList = new ArrayList<>();
        listViewVoucher = (ListView)findViewById(R.id.listViewVoucherList);
        SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        loginUsername = pref.getString("username", "");
        downloadVoucher(getApplicationContext(),getString(R.string.select_voucher));

    }

    @Override
    protected void onResume() {
        super.onResume();
        downloadVoucher(getApplicationContext(),getString(R.string.select_voucher));

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
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date orderDate = formatter.parse(userResponse.getString("orderDate"));
                                String username = userResponse.getString("username");
                                    VoucherOrder vOrder = new VoucherOrder(id, voucherCode, orderDate, username);
                                    voucherOrderList.add(vOrder);

                           }
                            List<VoucherOrder> userVoucherOrderList=new ArrayList<>();
                            for(int i=0;i<voucherOrderList.size();++i){
                                if(voucherOrderList.get(i).getUsername().equalsIgnoreCase(loginUsername)){
                                    userVoucherOrderList.add(voucherOrderList.get(i));
                                }
                            }
                            List<Voucher> userVoucherList= new ArrayList<>();

                            for(int j=0;j<userVoucherOrderList.size();++j){
                                for(int i=0;i<voucherList.size();++i){
                                    if(userVoucherOrderList.get(j).getVoucherCode().equals(voucherList.get(i).getVoucherCode())){
                                        userVoucherList.add(voucherList.get(i));
                                    }
                                }

                            }
                            final VoucherAdapter voucherAdapter = new VoucherAdapter(getApplicationContext(),userVoucherList);
                            listViewVoucher.setAdapter(voucherAdapter);
                            listViewVoucher.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Voucher item = (Voucher) voucherAdapter.getItem(i);
                                    Intent intent = new Intent(ViewVoucherActivity.this,PurcahseVoucherSuccessfulActivity.class);
                                    intent.putExtra(MainActivity.VOUCHER_CODE,item.getVoucherCode());
                                    intent.putExtra(MainActivity.VOUCHER_AMOUNT,String.format("%d",(int)item.getAmount()));
                                    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
                                    intent.putExtra(MainActivity.VOUCHER_EXPIRYDATE,formatter.format(item.getExpiryDate()));
                                    intent.putExtra(MainActivity.VOUCHER_TYPE,item.getVoucherType());
                                    startActivity(intent);

                                }
                            });

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
                            downloadVoucherOrder(getApplicationContext(),getString(R.string.select_voucherOrder));
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
