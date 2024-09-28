package com.android.launcher3.netty.command.monitor;

import android.content.Context;

import com.android.launcher3.common.bean.WatchAppBean;
import com.android.launcher3.common.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class WatchAppManager {

    /**
     * 保存修改后的应用列表
     */
    public static void saveAll(Context context,String key,List<WatchAppBean> list){
        List<WatchAppBean> receiveAppBeans = new ArrayList<>();
        for (WatchAppBean bean: list) {
            if ("2".equals(bean.getS03MType())){
                receiveAppBeans.add(bean);
            }
        }
        SharedPreferencesUtils.setList(context,key,receiveAppBeans);
    }

}
