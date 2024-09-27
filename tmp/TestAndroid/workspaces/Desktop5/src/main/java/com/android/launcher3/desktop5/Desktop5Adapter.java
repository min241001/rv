package com.android.launcher3.desktop5;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.BaseRecyclerHolder;
import com.android.launcher3.common.bean.AppBean;

import java.util.ArrayList;

/**
 * @Author: zeckchan
 * @Description：桌面风格五适配器
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class Desktop5Adapter extends BaseRecyclerAdapter<AppBean> {

    public Desktop5Adapter(Context context) {
        super(context, new ArrayList<>(), R.layout.list_item_desktop5);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, AppBean item, int position, boolean isScrolling) {
        ImageView ivIcon = holder.getView(R.id.app_icon);
        Drawable icon = item.getIcon();
        if (icon == null) {
            ivIcon.setImageResource(R.drawable.ic_launcher);
        } else {
            ivIcon.setImageDrawable(icon);
        }
//        ImageUtil.displayImage(getContext(), item.getIcon(), holder.getView(R.id.app_icon), R.drawable.ic_launcher, getSize(80), getSize(80));
        holder.setText(R.id.app_name,item.getName());
    }
}