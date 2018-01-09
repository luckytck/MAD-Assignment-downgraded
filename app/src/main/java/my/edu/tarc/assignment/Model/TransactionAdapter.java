package my.edu.tarc.assignment.Model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import my.edu.tarc.assignment.R;

/**
 * Created by ken_0 on 7/1/2018.
 */

public class TransactionAdapter extends BaseAdapter{
    Context context;
    List<Transaction> transactionListList;

    public TransactionAdapter(Context context, List<Transaction> transactionListList) {
        this.context = context;
        this.transactionListList = transactionListList;
    }

    @Override
    public int getCount() {
        return transactionListList.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionListList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return transactionListList.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView imageViewMerchant;
        TextView textViewTitle;
        TextView textViewAmount;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.transaction_item, null);
            holder = new ViewHolder();

            holder.imageViewMerchant = (ImageView)convertView.findViewById(R.id.imageViewMerchant);
            holder.textViewTitle = (TextView)convertView.findViewById(R.id.textViewTitle);
            holder.textViewAmount = (TextView)convertView.findViewById(R.id.textViewAmount);

            Transaction transaction = transactionListList.get(position);

            holder.textViewTitle.setText(transaction.getTitle());
            String amount = "";
            if (transaction.getAmount() > 0){
                holder.textViewAmount.setTextColor(Color.parseColor("#1B5E20"));
                amount = "+";
            } else {
                holder.textViewAmount.setTextColor(Color.parseColor("#F44336"));
                amount = "-";
            }
            //Currency currency = Currency.getInstance(Locale.getDefault());
            //String symbol = currency.getSymbol();
            amount += context.getString(R.string.balance) + String.format("%.2f", Math.abs(transaction.getAmount()));
            holder.textViewAmount.setText(amount);
            //Convert string to bitmap
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //decode base64 string to image
            byte[] imageBytes = baos.toByteArray();
            imageBytes = Base64.decode(transaction.getImageMerchant(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            holder.imageViewMerchant.setImageBitmap(decodedImage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
}
