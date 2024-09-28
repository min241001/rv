package com.android.launcher3.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 蓝牙工具类
 * 权限:
 * android.permission.BLUETOOTH
 * android.permission.BLUETOOTH_ADMIN
 * android.permission.ACCESS_FINE_LOCATION  查找设备需要
 */
public class BluetoothUtils {

    private static BluetoothUtils instance = null;
    private final BluetoothAdapter defaultAdapter;
    private BluetoothReceiver bluetoothReceiver;
    private BluetoothReceiverListener listener;


    public static BluetoothUtils getInstance() {
        if (instance == null) {
            synchronized (BluetoothUtils.class) {
                if (instance == null) {
                    instance = new BluetoothUtils();
                }
            }
        }
        return instance;
    }

    private BluetoothUtils() {
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            throw new NullPointerException("当前设备不支持蓝牙");
        }
    }

    /**
     * 蓝牙状态
     */
    public boolean isEnable() {
        return defaultAdapter.isEnabled();
    }

    /**
     * 申请蓝牙权限,启动蓝牙
     *
     * @return 启动结果
     */
    public boolean enable() {
        return defaultAdapter.enable();
    }

    /**
     * 申请蓝牙权限,禁用蓝牙
     *
     * @return 禁用结果
     */
    public boolean disable() {
        return defaultAdapter.disable();
    }

    /**
     * 查找设备
     * 如果返回 false 尝试动态申请 {@link android.Manifest.permission#ACCESS_FINE_LOCATION} 权限
     *
     * @return 查找成功返回 true,否则返回 false
     */
    public boolean discoveryDevice() {
        if (defaultAdapter.isDiscovering()) {
            defaultAdapter.cancelDiscovery();
        }
        return defaultAdapter.startDiscovery();
    }

    /**
     * 取消查找
     *
     * @return 取消结果
     */
    public boolean cancelDiscovery() {
        if (listener != null) {
            listener.cancelDiscovery();
        }
        return defaultAdapter.cancelDiscovery();
    }

    /**
     * 查找已经绑定的设备
     *
     * @return 设备列表
     */
    public List<BluetoothDevice> getBoundDevice() {
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        return new ArrayList<>(bondedDevices);
    }

    /**
     * 配对设备
     *
     * @param device 目标设备
     * @return 配对结果
     */
    public boolean createBound(BluetoothDevice device) {
        if (defaultAdapter.isDiscovering()) {
            defaultAdapter.cancelDiscovery();
        }
        try {
            Method createBond = device.getClass().getMethod("createBond");
            createBond.invoke(device);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取消与设备的配对
     *
     * @param device 目标设备
     * @return 配对结果
     */
    public boolean cancelBound(BluetoothDevice device) {
        if (defaultAdapter.isDiscovering()) {
            defaultAdapter.cancelDiscovery();
        }
        try {
            Method removeBond = device.getClass().getMethod("removeBond");
            removeBond.invoke(device);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 判断设备是否连接
     * 设备关机后实时读取的话会依然是连接状态
     *
     * @param device 设备
     * @return 连接状态
     */
    public boolean isConnected(BluetoothDevice device) {
        try {
            Method isConnected = device.getClass().getMethod("isConnected");
            isConnected.setAccessible(true);
            return (boolean) isConnected.invoke(device);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取当前连接中的设备
     * 如果有多个设备连接,仅返回第一个
     *
     * @return 连接中的设备
     */
    public BluetoothDevice getConnectedDevice() {
        List<BluetoothDevice> boundDevice = BluetoothUtils.getInstance().getBoundDevice();
        for (int i = 0; i < boundDevice.size(); i++) {
            BluetoothDevice device = boundDevice.get(i);
            boolean connected = BluetoothUtils.getInstance().isConnected(device);
            if (connected) {
                return device;
            }
        }

        return null;
    }

    /**
     * 获取设备电量
     *
     * @param device 设备
     * @return 电量
     */
    public int getDeviceBatteryLevel(BluetoothDevice device) {
        int level = 0;
        if (device == null) {
            return level;
        }
        try {
            Method getBatteryLevel = device.getClass().getMethod("getBatteryLevel", new Class[]{});
            getBatteryLevel.setAccessible(true);
            level = (int) getBatteryLevel.invoke(device);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return level;
    }


    /**
     * 获取主要设备类型
     * 通过 {@link BluetoothDevice#getBluetoothClass()} 获取 BluetoothClass
     * 通过 {@link android.bluetooth.BluetoothClass#getMajorDeviceClass()} 获取设备的主要类型
     * 根据主要类型设置蓝牙列表的icon
     * 或判断设备类型是否为需要的设备等
     *
     * @param device {@link BluetoothDevice}
     * @return 设备类型
     */
    public String getDeviceClassIc(BluetoothDevice device) {
        // 电脑
        final int COMPUTER = 0x0100;
        // 手机
        final int PHONE = 0x0200;
        // 媒体设备
        final int AUDIO_VIDEO = 0x0400;
        // 可穿戴设备
        final int WEARABLE = 0x0700;
        // 健康设备(BLE?)
        final int HEALTH = 0x0900;
        // 外设
        final int PERIPHERAL = 0x0500;

        // 其他
        final int MISC = 0x0000;
        final int NETWORKING = 0x0300;
        final int IMAGING = 0x0600;
        final int TOY = 0x0800;
        final int UNCATEGORIZED = 0x1F00;

        String deviceClassType = null;
        switch (device.getBluetoothClass().getMajorDeviceClass()) {
            case PERIPHERAL:
                deviceClassType = "外接设备";
                break;
            case COMPUTER:
                deviceClassType = "电脑";
                break;
            case PHONE:
                deviceClassType = "手机";
                break;
            case AUDIO_VIDEO:
                deviceClassType = "媒体设备";
                break;
            case WEARABLE:
                deviceClassType = "健康设备";
                break;
            default:
                deviceClassType = "其他";
                break;
        }

        return deviceClassType;
    }

    /**
     * 注册蓝牙广播
     * 需要及时调用 {@link #unregisterReceiver(Context)} 注销广播
     *
     * @param context
     * @param listener
     */
    public void registerReceiver(Context context, BluetoothReceiverListener listener, String... actions) {
        this.listener = listener;
        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter intentFilter = new IntentFilter();

        // 自行添加需要的Action,在 otherAction 中读取回调
        for (int i = 0; i < actions.length; i++) {
            intentFilter.addAction(actions[i]);
        }

        // 开始查找设备
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        // 停止查找设备
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 蓝牙开关状态改变
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 设备连接状态改变
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        // 找到设备
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        // 设备绑定状态发生变化
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        context.registerReceiver(bluetoothReceiver, intentFilter);
    }

    /**
     * 注销广播
     *
     * @param context
     */
    public void unregisterReceiver(Context context) {
        if (bluetoothReceiver != null) {
            context.unregisterReceiver(bluetoothReceiver);
            this.listener = null;
        }
    }

    /**
     * 蓝牙广播监听
     */
    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (listener == null) {
                return;
            }
            BluetoothDevice device = null;
            switch (Objects.requireNonNull(intent.getAction())) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int connectionState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);

                    listener.connectionStateChange(device, connectionState);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    listener.startDiscovery();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    listener.stopDiscovery();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    listener.foundDevice(device);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    if (state == 10) {
                        listener.disableBluetooth();
                    } else if (state == 12) {
                        listener.enableBluetooth();
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int boundState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                    listener.boundStatusChange(device, boundState);
                    break;
                default:
                    listener.otherAction(context, intent);
                    break;
            }
        }
    }

    /**
     * 收到广播回调
     */
    public static class BluetoothReceiverListener {
        /**
         * 找到设备回调
         *
         * @param device 蓝牙设备
         */
        public void foundDevice(BluetoothDevice device) {

        }

        /**
         * 开始查找设备
         */
        public void startDiscovery() {

        }

        /**
         * 停止查找设备
         */
        public void stopDiscovery() {

        }

        /**
         * 启用蓝牙
         */
        public void enableBluetooth() {

        }

        /**
         * 禁用蓝牙
         */
        public void disableBluetooth() {

        }

        /**
         * 设备连接状态发生改变
         *
         * @param device          {@link BluetoothDevice}
         * @param connectionState {@link BluetoothAdapter#STATE_CONNECTED} 已连接
         *                        {@link BluetoothAdapter#STATE_CONNECTING} 连接中
         *                        {@link BluetoothAdapter#STATE_DISCONNECTED} 已断开
         */
        public void connectionStateChange(BluetoothDevice device, int connectionState) {

        }

        /**
         * 其他的广播
         */
        public void otherAction(Context context, Intent intent) {

        }

        /**
         * 取消发现
         * 没有取消的广播,在本工具类的 {@link BluetoothUtils#cancelDiscovery()} 中调用
         */
        public void cancelDiscovery() {

        }

        /**
         * 设备绑定状态发生变化
         *
         * @param device     {@link BluetoothDevice}
         * @param boundState {@link BluetoothDevice#BOND_NONE},
         *                   {@link BluetoothDevice#BOND_BONDING},
         *                   {@link BluetoothDevice#BOND_BONDED}.
         */
        public void boundStatusChange(BluetoothDevice device, int boundState) {

        }
    }
}
