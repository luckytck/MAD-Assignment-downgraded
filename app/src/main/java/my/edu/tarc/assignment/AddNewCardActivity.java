package my.edu.tarc.assignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.assignment.Model.Card;
import my.edu.tarc.assignment.Model.CardType;
import my.edu.tarc.assignment.Model.User;

public class AddNewCardActivity extends AppCompatActivity {
    private EditText editTextCardHolderName, editTextCardNumber, editTextExpiryDate, editTextCCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_card);

        editTextCardHolderName = (EditText) findViewById(R.id.editTextCardHolderName);
        editTextCardNumber = (EditText) findViewById(R.id.editTextCardNumber);
        editTextExpiryDate = (EditText) findViewById(R.id.editTextExpiryDate);
        editTextCCV = (EditText) findViewById(R.id.editTextCCV);

        Button buttonAddNewCard = (Button) findViewById(R.id.buttonAddNewCard);
        buttonAddNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardHolderName = editTextCardHolderName.getText().toString().trim();
                String cardNumber = editTextCardNumber.getText().toString().trim();
                String expiryDate = editTextExpiryDate.getText().toString().trim();
                String ccv = editTextCCV.getText().toString().trim();
                if (cardHolderName.isEmpty() || cardNumber.isEmpty() || expiryDate.isEmpty() || ccv.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields.", Toast.LENGTH_LONG).show();
                } else if (!(CardType.detect(cardNumber) == CardType.VISA || CardType.detect(cardNumber) == CardType.MASTERCARD)){
                    Toast.makeText(getApplicationContext(), "Invaid card number, please enter Visa/Mastercard number only.", Toast.LENGTH_LONG).show();
                } else if (!ccv.matches("^[0-9]{3}$")){
                    Toast.makeText(getApplicationContext(), "Invaid CCV, please enter a 3-digit code.", Toast.LENGTH_LONG).show();
                } else if (!expiryDate.matches("^[0-9]{1,2}/[0-9]{2}$")){
                    Toast.makeText(getApplicationContext(), "Invalid date, please enter a valid expiry date.", Toast.LENGTH_LONG).show();
                } else {
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
                    int expiryMonth = Integer.parseInt(expiryDate.split("/")[0]);
                    int expiryYear = Integer.parseInt(expiryDate.split("/")[1]) + 2000;
                    if (expiryMonth < 1 || expiryMonth > 12){
                        Toast.makeText(getApplicationContext(), "Invalid date, please enter a valid expiry date.", Toast.LENGTH_LONG).show();
                    } else if (expiryYear < currentYear || (expiryYear == currentYear && expiryMonth < currentMonth)){
                        Toast.makeText(getApplicationContext(), "Sorry, the card has expired.", Toast.LENGTH_LONG).show();
                    } else {
                        SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                        String username = pref.getString("username", "");
                        Card card = new Card(cardHolderName, cardNumber, expiryMonth, expiryYear, Integer.parseInt(ccv), username);
                        makeServiceCall(getApplicationContext(), getString(R.string.insert_card_url), card);
                    }
                }
            }
        });
    }

    public void makeServiceCall(Context context, String url, final Card card) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success==0) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("cardNumber", card.getCardNumber());
                    params.put("cardHolderName", card.getCardHolderName());
                    params.put("expiryMonth", String.valueOf(card.getExpiryMonth()));
                    params.put("expiryYear", String.valueOf(card.getExpiryYear()));
                    params.put("cvv", String.valueOf(card.getCvv()));
                    params.put("username", card.getUsername());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
