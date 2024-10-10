package com.renny.contractgridview.opreator;

import android.os.Handler;
import android.os.Message;

import androidx.recyclerview.widget.RecyclerView;

import com.renny.contractgridview.adapter.VerticalOverlayAdapter;
import com.renny.contractgridview.base.Constants;
import com.renny.contractgridview.bean.AppInfoBean;
import com.renny.contractgridview.utils.AppUtils;

import java.util.List;

/**
 * @Author: pengmin
 * @CreateDate：2024/8/30 17:44
 */
public class AppListOpreator {
    //插入一个添加按钮
    public static void InsertPlusItem(List<AppInfoBean> beans) {
        if (beans.get(0).getType() != 3) {
            AppInfoBean bean3 = new AppInfoBean();
            bean3.setType(3);//加item
            beans.add(0, bean3);
        }
    }
    public void DelItem(int i,List<AppInfoBean> beans,VerticalOverlayAdapter a){
        beans.remove(i);
        a.notifyItemRemoved(i);
        a.notifyItemRangeRemoved(i, beans.size() - 1);
    }

    public void AddItem(int i,List<AppInfoBean> beans,AppInfoBean bean,VerticalOverlayAdapter a){
        beans.add(i,bean);
        a.notifyItemInserted(i);
        a.notifyItemRangeInserted(i, beans.size() - 1);
    }

    public static void RemovePulsItem(List<AppInfoBean> beans,RecyclerView.Adapter a){
        if(beans.get(0).getType()==3){
            beans.get(0).setHide(true);
            beans.remove(0);
            a.notifyItemRemoved(0);
            a.notifyItemRangeRemoved(0, beans.size() - 1);
        }

        for (int i = 0; i < beans.size() - 1; i++) {
            beans.get(i).setEditMode(false);
            a.notifyItemChanged(i);
            a.notifyItemRangeChanged(i, beans.size() - 1);
        }
    }
    public static void InsertPlusItem(List<AppInfoBean> beans,int position, VerticalOverlayAdapter a, Handler handler) {
        if (beans.get(0).getType() != 3) {
            AppInfoBean bean3 = new AppInfoBean();
            bean3.setType(3);//加item
            bean3.setHide(false);
            beans.add(0, bean3);
            a.notifyItemInserted(0);
            a.notifyItemRangeInserted(0, beans.size() - 1);
            //a.notifyItemRangeChanged(0, beans.size() - 1);
        }
        for (int i = 1; i < beans.size() - 1; i++) {
            beans.get(i).setEditMode(true);
            a.notifyItemChanged(i);
            a.notifyItemRangeChanged(i, beans.size() - 1);
            //AppListOpreator.TryLongClickEvent(i, VerticalOverlayAdapter.this,mHandler);
        }
        SendMessage(position,handler);
    }

    public static void SendMessage(int position,Handler handler) {
        //notifyDataSetChanged();
        //StringUtil.SendEventMsg(1, "show_puls_button");
        Message msg = new Message();
        msg.what = Constants.msgWhat.OPEN_EDITMODE_STATUS;
        msg.arg1 = position;
        handler.sendMessageDelayed(msg, 100);
    }
}
