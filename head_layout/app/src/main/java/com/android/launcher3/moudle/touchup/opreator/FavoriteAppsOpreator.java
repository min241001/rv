package com.android.launcher3.moudle.touchup.opreator;

import android.content.Context;
import android.widget.Toast;

import com.android.launcher3.R;
import com.android.launcher3.moudle.touchup.adapter.FavoriteAdapter;
import com.android.launcher3.moudle.touchup.bean.AppInfoBean;

import java.util.List;

/**
 * Create by pengmin on 2024/9/4 .
 */
public class FavoriteAppsOpreator {
    public static void DelItem(List<AppInfoBean> beans, int position, FavoriteAdapter a) {
        beans.remove(position);
        AppInfoBean bean3 = new AppInfoBean();
        bean3.setType(3);//加item
        bean3.setHide(false);
        beans.add(position, bean3);
        a.notifyItemRangeChanged(position, beans.size());
    }

    public static void AddItem(List<AppInfoBean> beans, AppInfoBean bean, int position, FavoriteAdapter a) {
        beans.remove(position);
        beans.add(position, bean);
        if (a != null) {
            //a.notifyItemInserted(position);
            a.notifyItemRangeChanged(position, beans.size());
        }

    }

    public static void AddItem_CheckDuplication(List<AppInfoBean> beans, AppInfoBean bean, int position, FavoriteAdapter a, Context context) {
        if (!beans.contains(bean)) {
            beans.remove(position);
            beans.add(position, bean);
            if (a != null) {
                //a.notifyItemInserted(position);
                a.notifyItemRangeChanged(position, beans.size());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.app_duplication), Toast.LENGTH_SHORT).show();
        }

    }

    public static void Favorite_UnEdit(List<AppInfoBean> beans, FavoriteAdapter fa) {
        for (int i = 0; i < beans.size(); i++) {
            beans.get(i).setEditMode(false);
            if (fa != null) {
                fa.notifyItemChanged(i);
                fa.notifyItemRangeChanged(i, beans.size());
            }
        }
    }

    //少于3个item填充+号
    public static void CheckFavoriteBeans(List<AppInfoBean> fav_beans) {
        if (fav_beans.size() > 3) {
            fav_beans = fav_beans.subList(0, 3);
        }
        for (int i = 0; i < 3 - fav_beans.size(); i++) {
            AppInfoBean bean3 = new AppInfoBean();
            bean3.setType(3);//加item
            bean3.setHide(false);
            fav_beans.add(0, bean3);
        }

    }
}
