package com.android.launcher3.desktop6;

import android.content.Context;

import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.BaseRecyclerHolder;
import com.android.launcher3.common.bean.AppBean;

import java.util.List;

public class Desktop6Adapter2 extends BaseRecyclerAdapter<AppBean> {
    public Desktop6Adapter2(Context context, List<AppBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, AppBean item, int position, boolean isScrolling) {
        holder.setText(R.id.app_name, item.getName());
        holder.setImageDrawable(R.id.app_icon, item.getIcon());
    }
}
