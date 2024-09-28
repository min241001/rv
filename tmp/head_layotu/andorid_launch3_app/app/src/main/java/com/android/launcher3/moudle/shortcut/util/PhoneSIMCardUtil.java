package com.android.launcher3.moudle.shortcut.util;

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import com.android.launcher3.App;
import com.android.launcher3.R;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.shortcut.callback.CallBackManager;
import com.android.launcher3.moudle.shortcut.callback.SignalStrengthCallBack;

import java.lang.reflect.Method;
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
        this.listenSignalStrengths();
    }

    // 运营商名称
    public String getOperatorName() {
        switch (mTelephonyManager.getSimOperator()) {
            case "46000":
            case "46002":
            case "46004":
            case "46007":
            case "46008":
                return App.getInstance().getString(R.string.cmcc);
            case "46001":
            case "46006":
            case "46009":
                return App.getInstance().getString(R.string.cucc);
            case "46003":
            case "46005":
            case "46011":
                return App.getInstance().getString(R.string.ctcc);
            case "46015":
                return App.getInstance().getString(R.string.cgcc);
            default: // 当前运营商未知或非中国运营商
                return UNKNOWN;
        }
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



    // 网络类型
    public String getNetworkType() {
        // 获取网络类型
        int networkType = mTelephonyManager.getNetworkType();
        // 根据网络类型返回相应的结果
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "5G";

            case TelephonyManager.NETWORK_TYPE_LTE:
                return "5G";

            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";

            default:
                return UNKNOWN;
        }
    }

    private int mLteLevel = 1;

    // 蜂窝网络信号强度
    public int getCellularSignalStrength() {
        return mLteLevel;
    }

    /**
     * 监听手机信号变化
     */
    public void listenSignalStrengths() {
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(new PhoneStateListener() {
                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    super.onSignalStrengthsChanged(signalStrength);
                    try {
                        Method method = signalStrength.getClass().getMethod("getLteLevel");
                        if (method != null) {
                            method.setAccessible(true);
                            Object obj = method.invoke(signalStrength);
                            if (obj instanceof Integer) {
                                mLteLevel = (int) obj;
                                if (mLteLevel == 0){
                                    Method method2 = signalStrength.getClass().getMethod("getGsmLevel");
                                    mLteLevel = (int) method2.invoke(signalStrength);
                                }
                            }
                            // 回调
                            SignalStrengthCallBack callBack = CallBackManager.getInstance().getCallBack(SignalStrengthCallBack.class);
                            if (callBack != null) {
                                callBack.onSignalStrength(mLteLevel);
                            }
                        }
                        LogUtil.d(TAG + "onSignalStrengthsChanged:" + mLteLevel, LogUtil.TYPE_RELEASE);
                    } catch (Exception e) {
                        LogUtil.d(TAG + "onSignalStrengthsChanged:" + e.getMessage(), LogUtil.TYPE_RELEASE);
                        e.printStackTrace();
                    }
                }
            }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }



    public void stopListeningSignalStrengths() {
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(new PhoneStateListener(), PhoneStateListener.LISTEN_NONE);
        }
    }

}