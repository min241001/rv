package com.android.launcher3.moudle.shortcut.util;

import android.content.Context;

import com.android.launcher3.common.utils.BluetoothUtil;
import com.android.launcher3.common.utils.MobileDataUtil;
import com.android.launcher3.common.utils.WifiUtil;

/**
 * wifi、蓝牙、数据流量等工具类
 */
public class ShutcutMenuUtil {

    private Context context;
    private static ShutcutMenuUtil instance;

    private ShutcutMenuUtil() {
    }

    public static ShutcutMenuUtil getInstance() {
        if (instance == null) {
            instance = new ShutcutMenuUtil();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    // 蓝牙是否开启
    public boolean isBluetoothEnabled() {
        return BluetoothUtil.isBluetoothEnabled();
    }

    // 打开蓝牙
    public void enableBluetooth() {
        BluetoothUtil.enableBluetooth();
    }

    // 关闭蓝牙
    public void disableBluetooth() {
        BluetoothUtil.disableBluetooth();
    }

    // wifi是否开启
    public boolean isWifiEnabled() {
        return WifiUtil.isWifiEnabled(context);
    }

    // 打开Wi-Fi
    public void enableWifi() {
        WifiUtil.enableWifi(context);
    }

    // 关闭Wi-Fi
    public void disableWifi() {
        WifiUtil.disableWifi(context);
    }

    // 打开飞行模式
    public boolean isAirplaneMode() {
        return AirplaneModeUtil.isAirplaneMode(context);
    }

    // 打开飞行模式
    public void enableAirplaneMode() {
        AirplaneModeUtil.enableAirplaneMode(context);
    }

    // 关闭飞行模式
    public void disableAirplaneMode() {
        AirplaneModeUtil.disableAirplaneMode(context);
    }

    // 网络是否开启
    public boolean isMobileDataEnabled() {
        return MobileDataUtil.INSTANCE.getDataEnabled(context);
    }

    // 打开蜂窝网络
    public void enableDataConnection() {
        MobileDataUtil.INSTANCE.setDataEnabled(context,true);
    }

    // 关闭蜂窝网络
    public void disableDataConnection() {
        MobileDataUtil.INSTANCE.setDataEnabled(context,false);
    }

    // 声音是否开启
    public boolean isSoundEnabled() {
        return SoundUtils.isSoundEnabled(context);
    }

    // 打开声音
    public void enableSound() {
        SoundUtils.enableSound(context);
    }

    // 关闭声音
    public void disableSound() {
        SoundUtils.disableSound(context);
    }
}
