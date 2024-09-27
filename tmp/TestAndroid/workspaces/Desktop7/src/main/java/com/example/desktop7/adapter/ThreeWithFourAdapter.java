package com.example.desktop7.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.widget.CircleImageView;
import com.android.launcher3.desktop7.R;

import java.util.ArrayList;

public class ThreeWithFourAdapter extends ArrayAdapter<AppBean> {

    public ThreeWithFourAdapter(final Context context, final ArrayList<AppBean> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.three_with_four_item, null);
        }
        final CircleImageView itemRound = (CircleImageView) view.findViewById(R.id.checker_board_item_imageView);
        itemRound.setImageDrawable(getItem(position).getIcon());
        return view;
    }

}
