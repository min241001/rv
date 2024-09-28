package com.android.launcher3.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;

import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.android.launcher3.netty.schedule.RepeatTaskHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class BatteryChangeUtils {

    private static BatteryChangeUtils instance;

    private BatteryChangeUtils() {
        // 私有构造函数，防止外部实例化
    }

    public static BatteryChangeUtils getInstance() {
        if (instance == null) {
            synchronized (BatteryChangeUtils.class) {
                if (instance == null) {
                    instance = new BatteryChangeUtils();
                }
            }
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public void batteryChangeRecord(Context context) {
        //1.获取当前时间
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(calendar.getTime());
        LogUtil.d("打印参数一 当前时间: " + currentTime, LogUtil.TYPE_RELEASE);
        //2.获取当前电量
        int battery = 0;
        try {
            battery = AppLocalData.getInstance().getBattery();
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        LogUtil.d("打印参数二 当前电量: " + battery, LogUtil.TYPE_RELEASE);
        //3.获取心跳时间
        int heartBeet = NettyManager.SCREEN_ON_HB_TIME;
        LogUtil.d("打印参数三 : 获取心跳时间" + heartBeet, LogUtil.TYPE_RELEASE);
        //4.当前运行的apk
        String runningApp = getRunningApp(context);
        //5.获取定位频率
        Map<String, Long> map = SharedPreferencesUtils.getMap(context, RepeatTaskHelper.getInstance().getSpKey(AppLocalData.getInstance().getWatchId()));
        long location_frequence = 0;
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                if (XmxxCmdConstant.MOD_LOCATION_FRQ.equals(key)) {
                    long longValue = map.get(key).longValue();
                    long minutes = (longValue / 1000) / 60; // 将毫秒值转换为分钟
                    location_frequence = minutes;
                    System.out.println("毫秒值 " + longValue + " 转换为分钟为: " + minutes);
                    LogUtil.d("打印参数五 定位频率: " + minutes + "分钟", LogUtil.TYPE_RELEASE);
                }
            }
        }
        //6.流量开关
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        String NetworkInfo = "未知";
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null) {
                boolean isDataEnabled = info.isConnected();
                if (isDataEnabled) {
                    // 数据流量开关已打开
                    NetworkInfo = "是";
                    LogUtil.d("打印参数六 流量开关: 数据流量已开启", LogUtil.TYPE_RELEASE);
                } else {
                    // 数据流量开关已关闭
                    NetworkInfo = "否";
                    LogUtil.d("打印参数六 流量开关: 数据流量已关闭", LogUtil.TYPE_RELEASE);
                }
            }
        }
        //7.WIFi开关
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifiInfo = "未知";
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                // WiFi已打开
                wifiInfo = "是";
                LogUtil.d("打印参数七 WIFi开关: WiFi已打开", LogUtil.TYPE_RELEASE);
            } else {
                // WiFi已关闭
                wifiInfo = "否";
                LogUtil.d("打印参数七 WIFi开关: WiFi已关闭", LogUtil.TYPE_RELEASE);
            }
        }
        //8.蓝牙开关
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothInfo = "未知";
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                // 蓝牙已打开
                bluetoothInfo = "是";
                LogUtil.d("打印参数八 蓝牙开关: 蓝牙已打开", LogUtil.TYPE_RELEASE);
            } else {
                // 蓝牙已关闭
                bluetoothInfo = "否";
                LogUtil.d("打印参数八 蓝牙开关: 蓝牙已关闭", LogUtil.TYPE_RELEASE);
            }
        }
        //9.当前运行services
        String runningServices = getRunningServices(context);
        String message = "{\t当前时间:\t" + currentTime + "\t当前电量:\t" + battery + "\t定位频率(分钟):\t"
                + location_frequence + "\tnetty心跳时间(秒):\t" + heartBeet + "\t流量开关:\t"
                + NetworkInfo + "\twifi开关:\t" + wifiInfo + "\t蓝牙开关:\t" + bluetoothInfo
                + "\t当前运行的apk:\t" + runningApp + "\t当前运行的services:\t" + runningServices+"}";
        //日志写入本地文件
        writeLogToFile(message);
    }

    //android系统获取当前运行services
    public String getRunningServices(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices != null && !runningServices.isEmpty()) {
            String runningServiceStr = "";
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                LogUtil.d("打印参数九 Running Service   ++++++ Service Name: : " + service.service.getClassName(), LogUtil.TYPE_RELEASE);
                runningServiceStr += service.service.getClassName() + "\t\t";
            }
            return runningServiceStr;
        } else {
            return "没有任何正在运行的服务";
        }
    }

    //android系统获取当前正在运行的应用
    public String getRunningApp(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
        if (runningProcesses != null) {
            String processName = "";
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                LogUtil.d("打印参数四 Running App   ++++++ Process Name: : " + processInfo.processName, LogUtil.TYPE_RELEASE);
                processName += processInfo.processName + "\t\t";
            }
            return processName;
        } else {
            LogUtil.d("Running App   ++++++ Process Name:  当前无正在运行的APP", LogUtil.TYPE_RELEASE);
            return "没有任何正在运行的应用";
        }
    }

    //日志写入本地文件
    public void writeLogToFile(String logMessage) {
        File logFile = new File(Environment.getExternalStorageDirectory(), "batteryLogfile.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(logMessage);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
