package com.android.launcher3.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


public class WifiUtil {
    // wifi是否开启
    public static boolean isWifiEnabled(Context context) {
        if (context == null) {
            return false;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    // 打开WiFi
    public static void enableWifi(Context context) {
        if (context == null) {
            return;
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WiFi
    public static void disableWifi(Context context) {
        if (context == null) {
            return;
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    // 获取当前热点最新的信号强度
    public static int getCurrentNetworkRssi(Context context) {
        if (context == null) {
            return -101;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return isWifiConnected(context) ? wifiInfo.getRssi() : -100;
    }

    /**
     * wifi是否连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != networkInfo && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
