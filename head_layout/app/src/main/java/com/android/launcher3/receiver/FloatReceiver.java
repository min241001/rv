package com.android.launcher3.receiver;

import static android.content.Context.MODE_PRIVATE;
import static com.android.launcher3.common.constant.SettingsConstant.LS_FLOAT_MENU_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.android.launcher3.moudle.launcher.view.LauncherActivity;
import com.android.launcher3.moudle.menufloat.MenuFloat;
import com.android.launcher3.moudle.touchup.bean.AppInfoBean;
import com.lzf.easyfloat.EasyFloat;
import java.util.ArrayList;
import cn.hutool.core.collection.CollectionUtil;


public class FloatReceiver extends BroadcastReceiver {
    public static String APP_FLOAT = "APP_FLOAT";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LS_FLOAT_MENU_ACTION.equals(intent.getAction())) {
            int ls_land_mode = intent.getIntExtra("mode", 0);
            int menu_change = intent.getIntExtra("menu_change", 0);
            String FLOAT_0 = intent.getStringExtra(MenuFloat.FLOAT_0);
            String FLOAT_1 = intent.getStringExtra(MenuFloat.FLOAT_1);
            String FLOAT_2 = intent.getStringExtra(MenuFloat.FLOAT_2);
            if (MenuFloat.getInstance().getContext() == null && context instanceof LauncherActivity) {
                MenuFloat.getInstance().setContext((LauncherActivity) context);
            }

            //设置悬浮窗开关打开
            if (ls_land_mode == 1) {
                if (menu_change == 1) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(APP_FLOAT, MODE_PRIVATE);
                    if (!TextUtils.isEmpty(FLOAT_0) && !TextUtils.isEmpty(FLOAT_0) && !TextUtils.isEmpty(FLOAT_2)) {
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(MenuFloat.FLOAT_0, FLOAT_0);
                        edit.putString(MenuFloat.FLOAT_1, FLOAT_1);
                        edit.putString(MenuFloat.FLOAT_2, FLOAT_2);
                        edit.apply();
                    }

                    ArrayList<AppInfoBean> data = new ArrayList<>();
                    AppInfoBean bean0 = new AppInfoBean();
                    bean0.setResourceId(MenuFloat.getResourceId(FLOAT_0));
                    bean0.setApp_name(FLOAT_0);
                    AppInfoBean bean1 = new AppInfoBean();
                    bean1.setResourceId(MenuFloat.getResourceId(FLOAT_1));
                    bean1.setApp_name(FLOAT_1);
                    AppInfoBean bean2 = new AppInfoBean();
                    bean2.setResourceId(MenuFloat.getResourceId(FLOAT_2));
                    bean2.setApp_name(FLOAT_2);
                    data.add(bean0);
                    data.add(bean1);
                    data.add(bean2);
                    if (MenuFloat.getInstance().getContext() != null) {
                        MenuFloat.getInstance().updateMenuFloat(data);
                    }
                } else {
                    if (MenuFloat.getInstance().getContext() != null && !EasyFloat.isShow(MenuFloat.MENU_FLOAT)) {
                        if (CollectionUtil.isNotEmpty(MenuFloat.getInstance().getMenuData())) {
                            MenuFloat.getInstance().showMenuFloat(MenuFloat.getInstance().getContext());
                        } else {
                            MenuFloat.getInstance().updateMenuFloat(MenuFloat.getInstance().initData());
                            MenuFloat.getInstance().showMenuFloat(context);
                        }
                    }
                }

            } else {
                //设置悬浮窗开关关闭
                MenuFloat.getInstance().disMissAllFloat();
            }

        }
    }

}
