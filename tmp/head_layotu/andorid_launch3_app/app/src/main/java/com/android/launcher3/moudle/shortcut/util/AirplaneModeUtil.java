package com.android.launcher3.moudle.shortcut.util;

import android.content.Context;
import android.provider.Settings;

/**
 * @Author: shensl
 * @Description：飞行模式工具类 注意：需要在AndroidManifest.xml文件中添加以下权限：android.permission.WRITE_SETTINGS 权限
 * @CreateDate：2024/1/4 13:40
 * @UpdateUser: shensl
 */
public class AirplaneModeUtil {

    // 是否飞行模式
    public static boolean isAirplaneMode(Context context) {
        int val = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        return val == 1 ? true : false;
    }

    // 打开飞行模式
    public static void enableAirplaneMode(Context context) {
        // 需要WRITE_SETTINGS权限才能执行
        if (Settings.System.canWrite(context)) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 1);
        }
    }

    // 关闭飞行模式
    public static void disableAirplaneMode(Context context) {
        // 需要WRITE_SETTINGS权限才能执行
        if (Settings.System.canWrite(context)) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        }
    }
}
