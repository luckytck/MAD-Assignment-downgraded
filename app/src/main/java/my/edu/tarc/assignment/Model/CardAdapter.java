package my.edu.tarc.assignment.Model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.assignment.R;
import my.edu.tarc.assignment.WalletActivity;

/**
 * Created by ken_0 on 6/1/2018.
 */

public class CardAdapter extends BaseAdapter {
    private Context context;
    private List<Card> cardList;

    public CardAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public Object getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cardList.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView imageViewMerchant;
        TextView textViewCard;
        ImageButton imageButtonDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.credit_debit_card, null);
            holder = new ViewHolder();

            holder.imageViewMerchant = (ImageView)convertView.findViewById(R.id.imageViewMerchant);
            holder.textViewCard = (TextView)convertView.findViewById(R.id.textViewCard);
            holder.imageButtonDelete = (ImageButton) convertView.findViewById(R.id.imageButtonDelete);

            final Card cardItem = cardList.get(position);

            holder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(context);
                    dialogDelete.setMessage("Do you want to remove this card?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String url = context.getString(R.string.delete_card_url);
                                    makeServiceCall(context, url, cardItem);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alert = dialogDelete.create();
                    alert.setTitle("Confirmation");
                    alert.show();
                }
            });

            String cardTitle = "";
            if (CardType.detect(cardItem.getCardNumber()+ "") == CardType.MASTERCARD){
                holder.imageViewMerchant.setImageResource(R.drawable.mastercard_logo);
                cardTitle = "Mastercard **** ";
            } else if (CardType.detect(cardItem.getCardNumber()+ "") == CardType.VISA){
                holder.imageViewMerchant.setImageResource(R.drawable.visa_logo);
                cardTitle = "VisaCard **** ";
            }
            cardTitle += cardItem.getCardNumber().substring(12);
            holder.textViewCard.setText(cardTitle);
            holder.imageButtonDelete.setImageResource(R.drawable.ic_close_black_24dp);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private void makeServiceCall(final Context context, String url, final Card card) {
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
                                    Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    WalletActivity.reloadListViewCard(context);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context.getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("cardNumber", card.getCardNumber());
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
