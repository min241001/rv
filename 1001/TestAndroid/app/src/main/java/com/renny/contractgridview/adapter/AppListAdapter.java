package com.renny.contractgridview.adapter;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.imageview.ShapeableImageView;
import com.renny.contractgridview.R;
import com.renny.contractgridview.bean.AppInfoBean;
import com.renny.contractgridview.utils.AppUtil;
import com.renny.contractgridview.view.rv.RVHolder;

import java.util.List;

/**
 * Create by pengmin on 2024/9/2 .
 */
public class AppListAdapter extends RVAdapter<List<AppInfoBean>, RVHolder> {
   // private List<AppInfoBean> bean;
    public AppListAdapter(Context context, List<AppInfoBean> beans, RecyclerView rv) {
        super(context, beans,rv, R.layout.app_list_rv_item);
    }

    @Override
    protected void bindData(@NonNull RVHolder holder, int position) {
        AppInfoBean bean = (AppInfoBean) beans.get(position);
        ShapeableImageView iv =  (ShapeableImageView)holder.findViewById(R.id.app_list_item_iv);
        TextView tv = (TextView) holder.findViewById(R.id.app_list_item_tv);
        iv.setImageDrawable(AppUtil.getAppIcon(context,bean.getPackage_name()));
        tv.setText(bean.getApp_name());
    }
}
