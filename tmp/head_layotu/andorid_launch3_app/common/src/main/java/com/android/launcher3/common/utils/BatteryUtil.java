package com.android.launcher3.common.utils;

import android.content.Context;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;

/**
 * @Author: shensl
 * @Description：电量工具
 * @CreateDate：2024/1/16 21:14
 * @UpdateUser: shensl
 */
public class BatteryUtil {

    private static BatteryUtil instance;

    private BatteryUtil() {

    }

    public static BatteryUtil getInstance() {
        if (instance == null) {
            instance = new BatteryUtil();
        }
        return instance;
    }

    private BatteryManager batteryManager;

    public void init(Context context) {
        batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
    }

    // 电量大小
    public int getBattery() {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    // 是否正在充电
    public boolean isCharging() {
        return batteryManager.isCharging();
    }

}
