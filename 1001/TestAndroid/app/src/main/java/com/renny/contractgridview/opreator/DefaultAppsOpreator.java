package com.renny.contractgridview.opreator;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.renny.contractgridview.R;
import com.renny.contractgridview.adapter.FavoriteAdapter;
import com.renny.contractgridview.adapter.VerticalOverlayAdapter;
import com.renny.contractgridview.base.Constants;
import com.renny.contractgridview.bean.AppInfoBean;

import java.util.List;

/**
 * @Author: pengmin
 * @CreateDate：2024/8/30 17:44
 */
public class DefaultAppsOpreator {
    //插入一个添加按钮
    public static void InsertPlusItem(List<AppInfoBean> beans) {
        if (beans.get(0).getType() != 3) {
            AppInfoBean bean3 = new AppInfoBean();
            bean3.setType(3);//加item
            beans.add(0, bean3);
        }
    }

    public static void DelItem(int i, List<AppInfoBean> beans, VerticalOverlayAdapter a) {
        beans.remove(i);
        a.notifyItemRemoved(i);
        a.notifyItemRangeRemoved(i, beans.size() - 1);
    }

    public static void SetTopItem(int i, List<AppInfoBean> beans, VerticalOverlayAdapter a, Handler handler) {
        int index = 0;
        if (beans.get(0).getType() == 3) {
            index = 1;
        }
        AppInfoBean bean = beans.get(i);
        beans.remove(i);
        beans.add(index, bean);
        a.notifyItemRangeChanged(index, beans.size());
        SendMessage(index, Constants.msgWhat.UPDATE_RV_ITEM, handler);
    }

    public static void AddItem(int i, List<AppInfoBean> beans, AppInfoBean bean, VerticalOverlayAdapter a) {
        beans.add(i, bean);
        a.notifyItemInserted(i);
        a.notifyItemRangeInserted(i, beans.size() - 1);
    }

    //判断重复和插入到编辑模式下第二个位置，非编辑模式下第一个位置
    public static void AddItem_CheckDuplication(int i, List<AppInfoBean> beans, AppInfoBean bean, VerticalOverlayAdapter a, Context context) {
        if (!beans.contains(bean)) {
            int index = 0;
            if (beans.get(0).getType() == 3) {
                index = 1;
            }
            beans.add(index, bean);
            a.notifyItemInserted(index);
            a.notifyItemRangeInserted(index, beans.size() - 1);
        } else {
            Toast.makeText(context, context.getString(R.string.app_duplication), Toast.LENGTH_SHORT).show();
        }

    }

    public static void RemovePulsItem(List<AppInfoBean> beans, RecyclerView.Adapter a) {
        if (beans.get(0).getType() == 3) {
            beans.get(0).setHide(true);
            beans.remove(0);
            a.notifyItemRemoved(0);
            a.notifyItemRangeRemoved(0, beans.size() - 1);
        }

        for (int i = 0; i < beans.size(); i++) {
            if (i != (beans.size() - 1)) {
                beans.get(i).setEditMode(false);
                a.notifyItemChanged(i);
                a.notifyItemRangeChanged(i, beans.size() - 1);
            }
        }
    }

    public static void InsertPlusItem(List<AppInfoBean> beans, int position, VerticalOverlayAdapter a, Handler handler) {
        if (beans.get(0).getType() != 3) {
            AppInfoBean bean3 = new AppInfoBean();
            bean3.setType(3);//加item
            bean3.setHide(false);
            beans.add(0, bean3);
            a.notifyItemInserted(0);
            //a.notifyItemRangeInserted(0, beans.size() - 1);
            a.notifyItemRangeChanged(0, beans.size() - 1);
        }
        for (int i = 1; i < beans.size() - 1; i++) {
            if (i != (beans.size() - 1)) {
                beans.get(i).setEditMode(true);
                a.notifyItemChanged(i);
                a.notifyItemRangeChanged(i, beans.size() - 1);
            }

            //DefaultAppsOpreator.TryLongClickEvent(i, VerticalOverlayAdapter.this,mHandler);
        }
        SendMessage(position, Constants.msgWhat.OPEN_EDITMODE_STATUS, handler);
    }

    public static void SetFavoriteStatus(List<AppInfoBean> beans, int position, FavoriteAdapter a, Handler handler) {
        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getType() == 0) {
                beans.get(i).setEditMode(true);
                if (a != null) {
                    a.notifyItemRangeChanged(i, beans.size());
                }
            }
        }
    }

    public static void SendMessage(int position, int what, Handler handler) {
        //notifyDataSetChanged();
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = position;
        handler.sendMessageDelayed(msg, 100);
    }

}
