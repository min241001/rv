package com.android.launcher3.moudle.touchup.utils;

import com.android.launcher3.moudle.touchup.bean.AppInfoBean;
import com.android.launcher3.moudle.touchup.bean.EventMessageBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//import org.greenrobot.eventbus.EventBus;

/**
 * @Author: pengmin
 * @CreateDate：2024/8/30 17:25
 */
public class StringUtil {

    public static void SendEventMsg(int what, String msg) {
        EventMessageBean bean = new EventMessageBean();
        bean.setWhat(what);
        bean.setMsg(msg);
       // EventBus.getDefault().post(bean);
    }

    public static AppInfoBean StringToBean(Gson gson, String ss){
        AppInfoBean bean = gson.fromJson(ss,new TypeToken<AppInfoBean>() {
        }.getType());
        return bean;

    }
    public static String BeanToString(Gson gson, AppInfoBean bean){
        return gson.toJson(bean);
    }
}
