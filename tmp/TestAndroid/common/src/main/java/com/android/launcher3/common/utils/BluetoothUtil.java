package com.android.launcher3.common.utils;

import android.bluetooth.BluetoothAdapter;

/**
 * 蓝牙工具类
 */
public class BluetoothUtil {

    // 蓝牙是否开启
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    // 打开蓝牙
    public static void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.enable();
        }
    }

    // 关闭蓝牙
    public static void disableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }
}
