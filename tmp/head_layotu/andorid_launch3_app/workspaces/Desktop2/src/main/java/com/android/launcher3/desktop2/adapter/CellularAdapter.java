package com.android.launcher3.desktop2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.widget.CircleImageView;
import com.android.launcher3.desktop2.R;

import java.util.ArrayList;

public class CellularAdapter extends ArrayAdapter<AppBean> {
    private Context context;

    public CellularAdapter(final Context context, final ArrayList<AppBean> contacts) {
        super(context, 0, contacts);
        this.context = context;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.cellular_item, null);
        }
        RelativeLayout menu_ll = view.findViewById(R.id.fd22_menu_ll);
        TextView menu_tv = view.findViewById(R.id.fd22_menu_tv);

        CircleImageView itemRound = (CircleImageView) view.findViewById(R.id.item_round);
        itemRound.setImageDrawable(getItem(position).getIcon());
        menu_ll.setVisibility(View.GONE);
        itemRound.setVisibility(View.GONE);
        if(getItem(position).getFlag()==7){
            itemRound.setVisibility(View.GONE);
            menu_ll.setVisibility(View.VISIBLE);
            menu_tv.setText(context.getString(R.string.menu_title2));
        }else{
            itemRound.setVisibility(View.VISIBLE);
            menu_ll.setVisibility(View.GONE);
        }
        return view;
    }


}
