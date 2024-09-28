package com.android.launcher3.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.launcher3.common.utils.LogUtil;

/**
 * Author : yanyong
 * Date : 2024/5/28
 * Details : 下拉控件状态变化监听
 */
public class WidgetStatusReceiver extends BroadcastReceiver {

    private static final String WIFI_STATE_ACTION = "com.android.launcher3.receiver.WidgetStatusReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WIFI_STATE_ACTION.equals(intent.getAction())) {
            int type = intent.getIntExtra("receiver_type", 0);
            boolean status = intent.getBooleanExtra("widget_status", false);
            LogUtil.i("onReceive: WIFI_STATE_ACTION status " + status + " type " + type, LogUtil.TYPE_RELEASE);
            switch (type) {
                case 1: // wifi
                    break;
                case 2: // 移动网络
                    break;
                case 3: // 蓝牙
                    break;
            }
        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    // 蓝牙已关闭
                    LogUtil.i("ACTION_STATE_CHANGED 123123 蓝牙已关闭。 state " + state, LogUtil.TYPE_RELEASE);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    LogUtil.i("ACTION_STATE_CHANGED 123123 蓝牙正在关闭。 state " + state, LogUtil.TYPE_RELEASE);
                    // 蓝牙正在关闭
                    break;
                case BluetoothAdapter.STATE_ON:
                    // 蓝牙已打开
                    LogUtil.i("ACTION_STATE_CHANGED 123123 蓝牙已打开。 state " + state, LogUtil.TYPE_RELEASE);
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    LogUtil.i("ACTION_STATE_CHANGED 123123 蓝牙正在打开。 state " + state, LogUtil.TYPE_RELEASE);
                    // 蓝牙正在打开
                    break;
            }
        } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())) {
            LogUtil.i("ACTION_ACL_CONNECTED 123123 蓝牙连接上了", LogUtil.TYPE_RELEASE);

        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
            LogUtil.i("ACTION_ACL_DISCONNECTED 123123 蓝牙断开了", LogUtil.TYPE_RELEASE);

        }
    }

    /**
     * 设置wifi、蓝牙、移动数据的状态
     *
     * @param context context
     * @param select  是否选中
     */
    private void setWidgetStatus(Context context, boolean select, int type) {

    }

    private WidgetInterface mWidgetInterface;

    public void setWidgetInterface(WidgetInterface widgetInterface) {
        this.mWidgetInterface = widgetInterface;
    }
}
