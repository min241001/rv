package com.android.launcher3.moudle.touchup.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.moudle.touchup.view.rv.RVHolder;
import com.android.launcher3.moudle.touchup.bean.AppInfoBean;
import com.android.launcher3.moudle.touchup.opreator.FavoriteAppsOpreator;
import com.android.launcher3.moudle.touchup.utils.AppUtil;
import com.android.launcher3.moudle.touchup.view.CircleProgress;

import java.util.List;

/**
 * Create by pengmin on 2024/9/2 .
 */
public class FavoriteAdapter extends RVAdapter<List<AppInfoBean>, RVHolder> {
    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView
     */
    public FavoriteAdapter(Context context, List<AppInfoBean> beans, RecyclerView rv) {
        super(context, beans, rv, R.layout.favritor_rv_item);
    }

    @Override
    protected void bindData(@NonNull RVHolder holder, int position) {
        // View v = holder.GetVRoot(getLayout_id());
        AppInfoBean bean = (AppInfoBean) beans.get(position);
        ImageView iv = (ImageView) holder.findViewById(R.id.fav_rv_item_iv);
        ImageView iv_del = (ImageView) holder.findViewById(R.id.fav_rv_item__del);
        CircleProgress fav_rv_item_circle_progress = (CircleProgress) holder.findViewById(R.id.fav_rv_item_circle_progress);
        iv.setImageDrawable(AppUtils.getAppIcon(context, bean.getPackage_name()));
        if (bean.getType() != 3) {
            fav_rv_item_circle_progress.setProgress((int) (Math.random() * 100));
        }
        if (bean.getEditMode()) {
            iv_del.setVisibility(View.VISIBLE);
        } else {
            iv_del.setVisibility(View.GONE);
        }

        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteAppsOpreator.DelItem(beans, position, FavoriteAdapter.this);
            }
        });

    }

    public void AddFavItem(int source_position, int target_position, Context context) {
        if (beans != null && AppUtil.apps.size() > target_position) {
            FavoriteAppsOpreator.AddItem_CheckDuplication(beans, AppUtil.apps.get(target_position), source_position, FavoriteAdapter.this, context);
            //notifyDataSetChanged();
        }
    }
}