package com.android.launcher3.desktop4.cellular.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.widget.CircleImageView;
import com.android.launcher3.desktop4.R;

import java.util.ArrayList;

public class CheckerBoardAdapter extends ArrayAdapter<AppBean> {

    public CheckerBoardAdapter(final Context context, final ArrayList<AppBean> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.checker_board_item, null);
        }
        final CircleImageView itemRound = (CircleImageView) view.findViewById(R.id.checker_board_item_imageView);
        itemRound.setImageDrawable(getItem(position).getIcon());
        return view;
    }

}
