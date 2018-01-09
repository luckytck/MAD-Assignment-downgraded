package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import my.edu.tarc.assignment.Model.Card;
import my.edu.tarc.assignment.Model.Transaction;
import my.edu.tarc.assignment.Model.TransactionAdapter;

public class TransactionHistoryActivity extends AppCompatActivity {
    public static final String TAG = "my.edu.tarc.assignment";
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private ListView listViewTransaction;
    private List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        listViewTransaction = (ListView)findViewById(R.id.listViewTransaction);
        pDialog = new ProgressDialog(this);
        transactionList = new ArrayList<>();
        //Get transaction records
        downloadTransaction(getApplicationContext(), getString(R.string.get_transaction_url));
    }

    private void downloadTransaction(Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            transactionList.clear();
                            SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                            String loginUsername = pref.getString("username", "");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                //String id = userResponse.getString("id");
                                String imageMerchant = userResponse.getString("imageMerchant");
                                String title = userResponse.getString("title");
                                double amount = userResponse.getDouble("amount");
                                //String transactionDate = userResponse.getString("transactionDate");
                                String username = userResponse.getString("username");
                                if (username.equalsIgnoreCase(loginUsername)) {
                                    Transaction transaction = new Transaction(imageMerchant, title, amount, username);
                                    transactionList.add(transaction);
                                }
                            }
                            loadListViewTransaction();
                            if (pDialog.isShowing())
                                pDialog.dismiss();
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
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
    //Show transaction records from latest to oldest
    private void loadListViewTransaction() {
        if (transactionList.size()>0){
            TransactionAdapter transactionAdapter = new TransactionAdapter(this, transactionList);
            listViewTransaction.setAdapter(transactionAdapter);
        }
    }
}
