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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import my.edu.tarc.assignment.Model.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "my.edu.tarc.assignment";
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

        View header = navigationView.getHeaderView(0);
        textViewWelcome = (TextView)header.findViewById(R.id.textViewWelcome);
        SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        String username = pref.getString("username", "");
        textViewWelcome.setText(getString(R.string.welcome) + username.toUpperCase());

        //Image buttons for top up
        ImageButton imageButtonDigi = (ImageButton)findViewById(R.id.imageButtonDigi);
        imageButtonDigi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "testing", Snackbar.LENGTH_SHORT).setAction("test", null).show();
            }
        });

        ImageButton imageButtonUmobile = (ImageButton)findViewById(R.id.imageButtonUmobile);
        imageButtonUmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageButton imageButtonHotlink = (ImageButton)findViewById(R.id.imageButtonHotlink);
        imageButtonHotlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageButton imageButtonXpax = (ImageButton)findViewById(R.id.imageButtonXpax);
        imageButtonXpax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //Image button for voucher
        ImageButton imageButtonSteam = (ImageButton)findViewById(R.id.imageButtonSteam);
        imageButtonSteam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageButton imageButtonGarena = (ImageButton)findViewById(R.id.imageButtonGarena);
        imageButtonGarena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageButton imageButtonPlaystation = (ImageButton)findViewById(R.id.imageButtonPlaystation);
        imageButtonPlaystation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        if (id == R.id.action_wallet || id == R.id.action_balance) {
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

        if (id == R.id.nav_voucher) {
            // Handle the camera action
        } else if (id == R.id.nav_wallet) {
            Intent intent = new Intent(this,WalletActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences loginInfo = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.putBoolean("login_key", false);
            editor.apply();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        menu.getItem(0).setTitle(getString(R.string.balance)+ String.format("%.2f", user.getBalance()));
    }
}
