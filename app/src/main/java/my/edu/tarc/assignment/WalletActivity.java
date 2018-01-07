package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import my.edu.tarc.assignment.Model.Card;
import my.edu.tarc.assignment.Model.CardAdapter;
import my.edu.tarc.assignment.Model.User;

public class WalletActivity extends AppCompatActivity {
    public static final String TAG = "my.edu.tarc.assignment";
    private TextView textViewBalance;
    private ListView listViewCard;
    private List<Card> cardList;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarWallet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textViewBalance = (TextView)findViewById(R.id.textViewBalance);
        pDialog = new ProgressDialog(this);
        cardList = new ArrayList<>();
        listViewCard = (ListView)findViewById(R.id.listViewCard);
        user = new User();

        downloadCard(getApplicationContext(),getString(R.string.get_card_url));
        retrieveBalance(getApplicationContext(), getString(R.string.get_balance_url));
    }

    private void downloadCard(Context context, String url){
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
                            cardList.clear();
                            SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                            String loginUsername = pref.getString("username", "");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                String cardNumber = userResponse.getString("cardNumber");
                                String cardHolderName = userResponse.getString("cardHolderName");
                                int expiryMonth = userResponse.getInt("expiryMonth");
                                int expiryYear = userResponse.getInt("expiryYear");
                                int cvv = userResponse.getInt("cvv");
                                String username = userResponse.getString("username");
                                if (username.equalsIgnoreCase(loginUsername)) {
                                    Card card = new Card(cardHolderName, cardNumber, expiryMonth, expiryYear, cvv, username);
                                    cardList.add(card);
                                }
                            }
                            loadListViewCard();
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

    private void loadListViewCard() {
        CardAdapter cardAdapter = new CardAdapter(this,cardList);
        listViewCard.setAdapter(cardAdapter);
    }

    private void retrieveBalance(Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        String username = pref.getString("username", "");

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url + username,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            int success = response.getInt("success");
                            if (success == 1){
                                double balance = response.getDouble("balance");
                                user.setBalance(balance);
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                            loadBalance();
                        } catch (Exception e){
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
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void loadBalance(){
        textViewBalance.setText(getString(R.string.balance) + String.format("%.2f", user.getBalance()));
    }

    public static void reloadListViewCard(Context context){
        WalletActivity activity = (WalletActivity)context;
        activity.downloadCard(context, context.getString(R.string.get_card_url));
    }

    public void addNewCard(View view){
        if (cardList.size() >= 3){
            Toast.makeText(getApplicationContext(), "Sorry, you have reach the maximum number of cards can be added.", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, AddNewCardActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        downloadCard(getApplicationContext(),getString(R.string.get_card_url));
        retrieveBalance(getApplicationContext(), getString(R.string.get_balance_url));
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wallet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_history || id == R.id.action_history_icon) {
            Intent intent = new Intent(this, TransactionHistoryActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
