package com.android.launcher3.moudle.shortcut.util;

import android.provider.Settings;

import com.android.launcher3.App;

/**
 * @Author: shensl
 * @Description：Volte工具
 * @CreateDate：2024/1/30 14:24
 * @UpdateUser: shensl
 */
public class VolteUtil {

    /**
     * 获取Volte的状态
     *
     * @return 0 是关，1是开
     */
    private static int getVolte() throws Settings.SettingNotFoundException {
        return Settings.Global.getInt(App.getInstance().getContentResolver(), "volte_vt_enabled");
    }

    /**
     * 获取Volte的状态
     * @return
     */
    public static String getVolteText() {
        int volte = 0;
        try {
            volte = getVolte();
        } catch (Settings.SettingNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            return volte == 0 ? "" : "HD";
        }
    }

}
