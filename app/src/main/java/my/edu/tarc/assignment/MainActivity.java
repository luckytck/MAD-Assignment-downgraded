package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Currency;
import java.util.Locale;

import my.edu.tarc.assignment.Model.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "my.edu.tarc.assignment";
    public static final String TELCO_NAME = "telco name";
    public static final String PAYMENT_TITLE = "payment title";
    public static final String PAYMENT_IMAGE = "payment image";
    public static final String PAYMENT_TARGET = "payment target";
    private TextView textViewWelcome;
    private Menu menu;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pDialog = new ProgressDialog(this);
        user = new User();
        retrieveBalance(getApplicationContext(), getString(R.string.get_balance_url));
        //Set drawer welcome message
        View header = navigationView.getHeaderView(0);
        textViewWelcome = (TextView)header.findViewById(R.id.textViewWelcome);
        SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        String username = pref.getString("username", "");
        textViewWelcome.setText(getString(R.string.welcome) + username.toUpperCase());

        //Digi top up
        ImageButton imageButtonDigi = (ImageButton)findViewById(R.id.imageButtonDigi);
        imageButtonDigi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrepaidTopUpActivity.class);
                intent.putExtra(TELCO_NAME, "Digi");
                startActivity(intent);
            }
        });
        //Umobile top up
        ImageButton imageButtonUmobile = (ImageButton)findViewById(R.id.imageButtonUmobile);
        imageButtonUmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrepaidTopUpActivity.class);
                intent.putExtra(TELCO_NAME, "Umobile");
                startActivity(intent);
            }
        });
        //Hotlink top up
        ImageButton imageButtonHotlink = (ImageButton)findViewById(R.id.imageButtonHotlink);
        imageButtonHotlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrepaidTopUpActivity.class);
                intent.putExtra(TELCO_NAME, "Hotlink");
                startActivity(intent);
            }
        });
        //Xpax top up
        ImageButton imageButtonXpax = (ImageButton)findViewById(R.id.imageButtonXpax);
        imageButtonXpax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrepaidTopUpActivity.class);
                intent.putExtra(TELCO_NAME, "Xpax");
                startActivity(intent);
            }
        });
        //Buy Steam Credit
        ImageButton imageButtonSteam = (ImageButton)findViewById(R.id.imageButtonSteam);
        imageButtonSteam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //Buy Garena Shells
        ImageButton imageButtonGarena = (ImageButton)findViewById(R.id.imageButtonGarena);
        imageButtonGarena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //Buy Playstation Credit
        ImageButton imageButtonPlaystation = (ImageButton)findViewById(R.id.imageButtonPlaystation);
        imageButtonPlaystation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //Scan QR code
        Button buttonScan = (Button)findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,QRCodeScannerActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_wallet || id == R.id.action_balance) {//My wallet
            Intent intent = new Intent(this, WalletActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_voucher) {//View voucher
            Intent intent = new Intent(this,ViewVoucherActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_wallet) {//My wallet
            Intent intent = new Intent(this,WalletActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {//Settings
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {//Logout
            //Disable auto-login
            SharedPreferences loginInfo = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.putBoolean("login_key", false);
            editor.apply();
            //Login page
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Retrieve balance
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
    //Show balance on app bar
    private void loadBalance(){
        //Currency currency = Currency.getInstance(Locale.getDefault());
        //String symbol = currency.getSymbol();
        menu.getItem(0).setTitle(getString(R.string.balance) + String.format("%.2f", user.getBalance()));
    }

    @Override
    protected void onResume() {
        //Update balance
        retrieveBalance(getApplicationContext(), getString(R.string.get_balance_url));
        super.onPostResume();
        super.onResume();
    }
}
