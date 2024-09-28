package com.android.launcher3.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;

import com.android.launcher3.App;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.shortcut.callback.BatteryCallBack;
import com.android.launcher3.moudle.shortcut.callback.CallBackManager;
import com.android.launcher3.moudle.shortcut.callback.SwitchChangeCallBack;
import com.android.launcher3.moudle.shortcut.callback.WifiSignalCallBack;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.common.utils.WifiUtil;
import com.android.launcher3.utils.BatteryChangeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description：状态变化广播
 */
public class StateChangeBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_RINGTONE = "android.media.RINGER_MODE_CHANGED";// 铃声
    public static final String ACTION_SIM = "android.intent.action.SIM_STATE_CHANGED";// 手机卡
    private static final String TAG = StateChangeBroadcastReceiver.class.getSimpleName() + "--->>>";
    private int oldBattery = 0;

    // 开关状态监听
    private void onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType type, boolean state) {
        SwitchChangeCallBack callBack = CallBackManager.getInstance().getCallBack(SwitchChangeCallBack.class);
        if (callBack != null) {
            callBack.onSwitchStateTypeChange(type, state);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.d(TAG + "action = " + action, LogUtil.TYPE_RELEASE);
        // 处理广播
        try {
            ReceiverManager.getInstance().onReceive(context, action, intent);
        }catch (Exception exception){
            LogUtil.e("onReceive: " + exception, LogUtil.TYPE_RELEASE);
        }

        switch (action) {
            case ConnectivityManager.CONNECTIVITY_ACTION:// 网络连接状态
                // 获取网络连接状态
                ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // 网络已连接
                    LogUtil.d(TAG + "网络已连接", LogUtil.TYPE_RELEASE);
                    onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.NETWORK, true);
                } else {
                    // 网络断开
                    LogUtil.d(TAG + "网络已断开", LogUtil.TYPE_RELEASE);
                    onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.NETWORK, false);
                }
                break;
            case Intent.ACTION_BATTERY_CHANGED:// 电量
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                // batteryPct为当前电量百分比，范围为0到100
                int batteryPct = (int) ((level / (float) scale) * 100);
                // 在这里处理电量信息
                LogUtil.d(TAG + "当前电量 = " + batteryPct, LogUtil.TYPE_RELEASE);
                AppLocalData.getInstance().setBattery(batteryPct);
                //电量变化进行日志打印
                if (AppLocalData.getInstance().getBatterySwitch() == 0){
                    if (batteryPct != oldBattery){
                        BatteryChangeUtils.getInstance().batteryChangeRecord(context);
                        oldBattery = batteryPct;
                    }
                }
                BatteryCallBack batteryCallBack = CallBackManager.getInstance().getCallBack(BatteryCallBack.class);
                if (batteryCallBack != null) {
                    batteryCallBack.onBatteryChange(batteryPct);
                }
                break;
//            case WifiManager.WIFI_STATE_CHANGED_ACTION:// WIFI开关状态改变
//                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
//                switch (wifiState) {
//                    case WifiManager.WIFI_STATE_ENABLING:
//                        LogUtil.d(TAG + "WIFI开关--->>>打开");
//                        onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.WIFI, true);
//                        break;
//                    case WifiManager.WIFI_STATE_DISABLED:
//                        LogUtil.d(TAG + "WIFI开关--->>>关闭");
//                        onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.WIFI, false);
//                        break;
//                }
//                break;
            case WifiManager.RSSI_CHANGED_ACTION:// WIFI信号强度改变
                int rssi = WifiUtil.getCurrentNetworkRssi(context);
                LogUtil.d(TAG + "WIFI信号强度改变，rssi = " + rssi, LogUtil.TYPE_RELEASE);
                int wifiLevel;
                if (rssi >= -55) {
                    wifiLevel = 3;
                } else if (rssi >= -66) {
                    wifiLevel = 2;
                } else if (rssi >= -77) {
                    wifiLevel = 1;
                } else {
                    wifiLevel = 0;
                }
                WifiSignalCallBack wifiSignalCallBack = CallBackManager.getInstance().getCallBack(WifiSignalCallBack.class);
                if (wifiSignalCallBack != null) {
                    wifiSignalCallBack.onSignalLevel(wifiLevel);
                }
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:// WIFI连接改变
                LogUtil.d(TAG + "WIFI连接改变", LogUtil.TYPE_RELEASE);
                if (WifiUtil.isWifiConnected(context)) {
                    LogUtil.d(TAG + "WIFI已连接", LogUtil.TYPE_RELEASE);
                    onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.WIFI, true);
                } else {
                    LogUtil.d(TAG + "WIFI未连接", LogUtil.TYPE_RELEASE);
                    onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.WIFI, false);
                }
                break;
            case ACTION_RINGTONE:// 声音
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int ringerMode = audioManager.getRingerMode();
                if (ringerMode == AudioManager.RINGER_MODE_NORMAL) {
                    // 声音开关已打开
                    LogUtil.d(TAG + "声音开关已打开", LogUtil.TYPE_RELEASE);
                    onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.SOUND, true);
                } else {
                    // 声音开关已关闭
                    LogUtil.d(TAG + "声音开关已关闭", LogUtil.TYPE_RELEASE);
                    onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.SOUND, false);
                }
                break;
            case ACTION_SIM: // 卡的状态
                onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.SIM, PhoneSIMCardUtil.getInstance().isSIMCardInserted());
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED:// 蓝牙
                int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // 蓝牙正在开启
                        LogUtil.d(TAG + "蓝牙正在开启", LogUtil.TYPE_DEBUG);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        // 蓝牙已开启
                        LogUtil.d(TAG + "蓝牙已开启", LogUtil.TYPE_RELEASE);
                        onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.BLUETOOTH, true);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        // 蓝牙正在关闭
                        LogUtil.d(TAG + "蓝牙正在关闭", LogUtil.TYPE_DEBUG);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        // 蓝牙已关闭
                        LogUtil.d(TAG + "蓝牙已关闭", LogUtil.TYPE_RELEASE);
                        onSwitchStateTypeChange(SwitchChangeCallBack.SwitchStateType.BLUETOOTH, false);
                        break;
                }
                break;
        }
    }

    public List<String> getActions() {
        List<String> actions = new ArrayList<>();
        actions.add(ConnectivityManager.CONNECTIVITY_ACTION);
        actions.add(Intent.ACTION_BATTERY_CHANGED);
        actions.add(WifiManager.RSSI_CHANGED_ACTION);
        //actions.add(WifiManager.WIFI_STATE_CHANGED_ACTION);
        actions.add(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        actions.add(BluetoothAdapter.ACTION_STATE_CHANGED);
        actions.add(StateChangeBroadcastReceiver.ACTION_RINGTONE);
        actions.add(StateChangeBroadcastReceiver.ACTION_SIM);
        actions.add(Intent.ACTION_SCREEN_ON);
        actions.add(Intent.ACTION_SCREEN_OFF);
        return actions;
    }

}
