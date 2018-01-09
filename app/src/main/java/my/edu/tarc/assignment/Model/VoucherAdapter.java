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
 * Created by Han on 1/8/2018.
 */

public class VoucherAdapter extends BaseAdapter {
    private Context context;
    private List<Voucher> voucherList;


    public VoucherAdapter(Context context, List<Voucher> voucherList) {
        this.context = context;
        this.voucherList = voucherList;

    }

    @Override
    public int getCount() {
        return voucherList.size();
    }

    @Override
    public Object getItem(int position) {
        return voucherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return voucherList.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView imageViewVoucherIcon;
        TextView textViewTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.voucher_listview, null);
            holder = new ViewHolder();

            holder.imageViewVoucherIcon = (ImageView)convertView.findViewById(R.id.imageViewVoucherIcon);
            holder.textViewTitle = (TextView)convertView.findViewById(R.id.textViewVoucherTitle);


            final Voucher voucherItem = voucherList.get(position);



            String voucherTitle = "";
            if (voucherItem.getVoucherType().equalsIgnoreCase("Steam Wallet Code")){
                holder.imageViewVoucherIcon.setImageResource(R.drawable.steam_voucher_logo);
                voucherTitle = "Steam Wallet Code ";
            } else if (voucherItem.getVoucherType().equalsIgnoreCase("Ganera Shells")){
                holder.imageViewVoucherIcon.setImageResource(R.drawable.ganera_voucher_logo);
                voucherTitle = "Ganera Shells ";
            }else if(voucherItem.getVoucherType().equalsIgnoreCase("PSD Digital Code")){
                holder.imageViewVoucherIcon.setImageResource(R.drawable.psd_voucher_logo);
                voucherTitle ="PSD Digital Code ";
            }
            voucherTitle += String.format("%d",(long)voucherItem.getAmount());
            holder.textViewTitle.setText(voucherTitle);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }



}
