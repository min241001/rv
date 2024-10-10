package com.android.launcher3.moudle.touchup.adapter;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.moudle.touchup.view.rv.RVHolder;
import com.android.launcher3.moudle.touchup.bean.AppInfoBean;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * Create by pengmin on 2024/9/2 .
 */
public class AppListAdapter extends RVAdapter<List<AppInfoBean>, RVHolder> {
   // private List<AppInfoBean> bean;
    public AppListAdapter(Context context, List<AppInfoBean> beans, RecyclerView rv) {
        super(context, beans,rv,R.layout.app_list_rv_item);
    }

    @Override
    protected void bindData(@NonNull RVHolder holder, int position) {
        AppInfoBean bean = (AppInfoBean) beans.get(position);
        ShapeableImageView iv =  (ShapeableImageView)holder.findViewById(R.id.app_list_item_iv);
        TextView tv = (TextView) holder.findViewById(R.id.app_list_item_tv);
        iv.setImageDrawable(AppUtils.getAppIcon(context,bean.getPackage_name()));
        tv.setText(bean.getApp_name());
    }
}