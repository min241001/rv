package com.android.launcher3.desktop5;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        LinearLayout df_item_ll = holder.getView(R.id.fd55_item_ll);
        RelativeLayout menu_rl = holder.getView(R.id.fd55_menu_rl);
        TextView menu_tv = holder.getView(R.id.fd55_menu_tv);
        ImageView ivIcon = holder.getView(R.id.app_icon);
        menu_rl.setVisibility(View.GONE);
        df_item_ll.setVisibility(View.GONE);

        if(position==getList().size()-1){
            //df_item_ll.setVisibility(View.GONE);
           // menu_rl.setVisibility(View.VISIBLE);
            menu_tv.setText(context.getString(R.string.menu_title));
        }else{
            df_item_ll.setVisibility(View.VISIBLE);
            menu_rl.setVisibility(View.GONE);
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
}