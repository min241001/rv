package com.android.launcher3.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiUtils {
    private final WifiManager wifiManager;

    public WifiUtils(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    // 检查WiFi是否可用
    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    // 打开WiFi
    public void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WiFi
    public void closeWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    // 扫描WiFi网络
    public void scanWifi() {
        wifiManager.startScan();
    }

    // 获取扫描到的WiFi列表
    public List<WifiConfiguration> getScanResults() {
        return wifiManager.getConfiguredNetworks();
    }

    // 获取当前连接的WiFi
    public WifiInfo getConnectedWifiInfo() {
        return wifiManager.getConnectionInfo();
    }

    // 判断是否有当前可用的WiFi连接
    public boolean isWifiConnected() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo != null && wifiInfo.getNetworkId() != -1;
    }

    // 关闭所有连接
    public void disconnectWifi() {
        wifiManager.disconnect();
    }

    // 连接WiFi
    public void connectToWifi(WifiConfiguration wifiConfig) {
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.enableNetwork(netId, true);
    }

    // 移除WiFi
    public void removeWifi(int netId) {
        wifiManager.removeNetwork(netId);
        wifiManager.saveConfiguration();
    }

    // 添加WiFi到系统
    public int addNetwork(WifiConfiguration wifiConfig) {
        return wifiManager.addNetwork(wifiConfig);
    }

    // 创建配置
    public WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        switch (type) {
            case 1:
                config.hiddenSSID = true;
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case 2:
                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case 3:
                config.preSharedKey = "\"" + password + "\"";
                break;
        }
        return config;
    }

    // 获取是否已经存在的配置
    public WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    // 去除同名WIFI
    public void removeSameWifi(String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.removeNetwork(existingConfig.networkId);
            }
        }
    }

    // 判断一个扫描结果中，是否包含了某个名称的WIFI
    public boolean isScanResultExist(String ssid, List<WifiConfiguration> existingConfigs) {
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                return true;
            }
        }
        return false;
    }

    // WiFi安全类型枚举
    public enum WifiSecurityType {
        OPEN, WEP, WPA
    }
}