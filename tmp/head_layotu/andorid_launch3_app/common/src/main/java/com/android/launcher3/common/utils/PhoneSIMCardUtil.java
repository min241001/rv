package com.android.launcher3.common.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.launcher3.common.data.AppLocalData;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: shensl
 * @Description：手机SIM卡工具类
 * @CreateDate：2024/1/4 11:02
 * @UpdateUser: shensl
 */
public class PhoneSIMCardUtil {

    public static final String TAG = PhoneSIMCardUtil.class.getSimpleName();

    private static final String UNKNOWN = "未知";// 无法识别的情况下，显示未知

    private Context mContext;
    private TelephonyManager mTelephonyManager;

    private static PhoneSIMCardUtil instance;

    private PhoneSIMCardUtil() {

    }

    public static PhoneSIMCardUtil getInstance() {
        if (instance == null) {
            instance = new PhoneSIMCardUtil();
        }
        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
        this.mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    // 运营商名称
    public String getOperatorName() {
        String operatorName = mTelephonyManager.getSimOperatorName();
        final List<String> names = Arrays.asList("中国移动", "中国联通", "中国电信", "中国广电");
        if (!TextUtils.isEmpty(operatorName) && names.contains(operatorName)) {
            return operatorName;
        }
        // 当前运营商未知或非中国运营商
        return UNKNOWN;
    }

    // 是否插入SIM卡
    public boolean isSIMCardInserted() {
        boolean isSIMCardInserted = mTelephonyManager == null ? false : mTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT;
        return isSIMCardInserted;
    }

    /**
     * 获取sim卡的状态
     *
     * @return
     */
    public int getSimState() {
        return mTelephonyManager.getSimState();
    }

    /**
     * 获取imei
     *
     * @return
     */
    public String getImei() {
        String imei ="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = mTelephonyManager.getImei();
        }else {
            imei =  mTelephonyManager.getDeviceId();
        }
        return imei;
    }

    public String getIccid() {
        SubscriptionManager sm = SubscriptionManager.from(mContext);
        List<SubscriptionInfo> sList = sm.getActiveSubscriptionInfoList();
        if (sList != null && !sList.isEmpty()) {
            SubscriptionInfo info = sList.get(0);
            if (info != null) {
                return info.getIccId();
            }
        }
        return "";
    }

    public String getPhoneNum(){
        String phoneNum = "";
        try {
            phoneNum = mTelephonyManager.getLine1Number();
            if(phoneNum == null){
                phoneNum = "";
            }
        }catch (Exception exception){
            phoneNum = "";
        }
        return phoneNum;
    }

    public boolean SIMHint(Context context,String packageName) {
        if ("com.baehug.dialer".equals(packageName)){
            return false;
        }
        //未插入SIM卡
        if (!PhoneSIMCardUtil.getInstance().isSIMCardInserted()) {
            LogUtil.d("请插入SIM卡", LogUtil.TYPE_RELEASE);
            ToastUtils.show("请插入SIM卡");
            return true;
        }

        //未获取到iccid
        if (StringUtils.isBlank(PhoneSIMCardUtil.getInstance().getIccid())) {
            LogUtil.d("没获取到iccid", LogUtil.TYPE_RELEASE);
            ToastUtils.show("无法识别SIM卡");
            return true;
        }

        //未获取到账号
        if (StringUtils.isBlank(AppLocalData.getInstance().getWatchId())) {
            LogUtil.d("没获取到账号", LogUtil.TYPE_RELEASE);
            ToastUtils.show("无账号");
            //发送广播获取账号
            Intent intent = new Intent("com.android.launcher3.common.updateid");
            context.sendBroadcast(intent);
            return true;
        }

        return false;
    }


}