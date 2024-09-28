package com.android.launcher3.moudle.stopwatch.adapter;

import android.content.Context;
import android.util.Log;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.BaseRecyclerHolder;


import java.util.ArrayList;
import java.util.List;

public class CountTimeAdapter extends BaseRecyclerAdapter<String> {

    public CountTimeAdapter(Context context, List<String> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {
        holder.setText(R.id.tv_item_num, getResources().getString(R.string.count_time_text) + (position + 1));
        holder.setText(R.id.tv_time, item);
    }


}
