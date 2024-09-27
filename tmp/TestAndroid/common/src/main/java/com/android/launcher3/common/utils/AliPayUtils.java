package com.android.launcher3.common.utils;

import static com.android.launcher3.common.constant.SettingsConstant.ALIPAY_CHECK;

import android.content.Context;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.dialog.AliPayCheckDialog;
import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.resp.AliPayCheckResp;

public class AliPayUtils {

    public static AliPayCheckDialog aliPayCheckDialog = AliPayCheckDialog.Companion.init(CommonApp.getInstance());

    /**
     * 检测支付宝激活状态
     *
     * @param imei
     */
    public static void aliPayCheck(Context context, String imei, boolean isApplication) {
        ApiHelper.alipayCheck(new BaseListener<AliPayCheckResp>() {
            @Override
            public void onError(int code, String msg) {
                LogUtil.d("error === " + code + msg, LogUtil.TYPE_RELEASE);
                if (isApplication){
                    return;
                }
                ToastUtils.show(msg);
            }

            @Override
            public void onSuccess(AliPayCheckResp aliPayCheckResp) {
                LogUtil.d("success === " + aliPayCheckResp.getActivationStatus(), LogUtil.TYPE_RELEASE);
                //激活状态 3 已激活
                if (aliPayCheckResp.getActivationStatus() == 3) {
                    //支付宝是否已激活 0 是 未激活  1 已激活
                    AppLocalData.getInstance().setAlipayData(1);
                    SharedPreferencesUtils.setParam(context, ALIPAY_CHECK, true);
                    if (!isApplication){
                        CallerClass caller = new CallerClass();
                        LauncherAppManager launcherAppManager = new LauncherAppManager();
                        caller.setCallback(launcherAppManager, context);
                        caller.doSomething();
                    }
                } else {
                    //支付宝是否已激活 0 是 未激活  1 已激活
                    AppLocalData.getInstance().setAlipayData(0);
                    if (!isApplication){
                        aliPayCheckDialog.startProgress(imei);
                        aliPayCheckDialog.resetUI(aliPayCheckResp.getActivationStatus());
                    }
                }
            }
        }, imei);
    }

    public static class CallerClass {
        private Context context;
        private AliPayCheckResult callback;

        public void setCallback(AliPayCheckResult callback, Context context) {
            this.callback = callback;
            this.context = context;
        }

        public void doSomething() {
            // 在适当的时候调用回调方法
            if (callback != null) {
                callback.success(context);
            }
        }
    }

    /**
     * 添加激活支付宝申请
     *
     * @param imei
     */
    public static void addAliPayRegister(String imei) {
        ApiHelper.addAlipayRegister(new BaseListener<Void>() {
            @Override
            public void onError(int code, String msg) {
                LogUtil.d("error === " + code + msg, LogUtil.TYPE_RELEASE);
                ToastUtils.show(msg);
            }

            @Override
            public void onSuccess(Void aliPayCheckResp) {
                LogUtil.d("success", LogUtil.TYPE_RELEASE);
                aliPayCheckDialog.result();
            }
        }, imei);
    }


}
