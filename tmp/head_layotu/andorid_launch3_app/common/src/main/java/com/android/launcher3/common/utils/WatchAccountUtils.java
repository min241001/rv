package com.android.launcher3.common.utils;

import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.data.AppLocalData;

public class WatchAccountUtils {
    private static final String TAG = "WatchAccountUtil";
    /**
     * 手表账号本地缓存key
     */
    private static final String SP_WATCH_ACCT_ID_KEY = "_WATCH_ACCT_ID_";
    private static volatile WatchAccountUtils instance;

    /**
     * 手表账号
     */
    private String waAcctId = "";

    private static boolean ifOpenAcctUtil = true;

    /**
     * 是否启用本工具类
     *
     * @param ifOpen 是否启用
     */
    public static void ifOpenAcctUtil(Boolean ifOpen) {
        ifOpenAcctUtil = ifOpen;
    }

    public static WatchAccountUtils getInstance() {
        if (null == instance) {
            synchronized (WatchAccountUtils.class) {
                if (null == instance) {
                    instance = new WatchAccountUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 获取账号
     *
     * @return 一般为10位的账号
     */
    public String getWaAcctId() {
        // 获取本地缓存
        waAcctId = (String) SharedPreferencesUtils.getParam(CommonApp.getInstance(), getSpKey(), "");
        if (TextUtils.isEmpty(waAcctId)){
            waAcctId = AppLocalData.getInstance().getWatchId();
        }
        Log.d(TAG, "getWaAcctId: 从本地缓存中获取账号数据：" + waAcctId);
        return waAcctId;
    }

    /**
     * 根据imei和iccid 获取 SharedPreferences 对应的key
     *
     * @return SharedPreferences 对应的key
     */
    private String getSpKey() {
        String imei = "";
        try {
            imei = PhoneSIMCardUtil.getInstance().getImei();
        } catch (Exception exception) {
            Log.d(TAG, "getSpKey: 获取imei" + exception);
        }
        return SP_WATCH_ACCT_ID_KEY + imei;
    }

}
