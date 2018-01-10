package my.edu.tarc.assignment.Model;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import my.edu.tarc.assignment.R;

/**
 * Created by Han on 1/10/2018.
 */

public class VoucherAdapter  extends BaseAdapter {
    Context context;
    List<Voucher> voucherListList;

    public VoucherAdapter(Context context, List<Voucher> voucherListList) {
        this.context = context;
        this.voucherListList = voucherListList;
    }

    @Override
    public int getCount() {
        return voucherListList.size();
    }

    @Override
    public Object getItem(int position) {
        return voucherListList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return voucherListList.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView imageViewVoucherIcon;
        TextView textViewTitle;

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
            convertView = mInflater.inflate(R.layout.view_voucher_listview, null);
            holder = new ViewHolder();

            holder.imageViewVoucherIcon = (ImageView)convertView.findViewById(R.id.imageViewVoucherIcon3);
            holder.textViewTitle = (TextView)convertView.findViewById(R.id.textViewVoucherTitle);

            Voucher voucher = voucherListList.get(position);

            holder.textViewTitle.setText(context.getString(R.string.balance) + String.valueOf((int)voucher.getAmount())+" "+voucher.getVoucherType());
            if(voucher.getVoucherType().equalsIgnoreCase("Garena Shells")){
                holder.imageViewVoucherIcon.setImageResource(R.drawable.garena);
            }else if(voucher.getVoucherType().equalsIgnoreCase("Steam Wallet Code")){
                holder.imageViewVoucherIcon.setImageResource(R.drawable.steam);
            }else if(voucher.getVoucherType().equalsIgnoreCase("PSN Digital Code")){
                holder.imageViewVoucherIcon.setImageResource(R.drawable.playstation);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
}
