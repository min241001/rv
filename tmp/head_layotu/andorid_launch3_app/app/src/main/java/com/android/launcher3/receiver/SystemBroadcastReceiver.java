package com.android.launcher3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;

import com.android.launcher3.App;
import com.android.launcher3.common.constant.SettingsConstant;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ToastUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @description home键监测、系统锁屏状态监
 */
public class SystemBroadcastReceiver {
    private static final String TAG = SystemBroadcastReceiver.class.getSimpleName();
    public static final String SOS_ACTION = "android.intent.action.xmxx.sos";
    private Context mContext;
    private ScreenBroadcastReceiver screenReceiver;

    /*
     * Home 键
     */
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";

    private static final String SYSTEM_DIALOG_REASON_GLOBAL = "globalactions";

    //private static final String SOS_ACTION = "android.intent.action.xmxx.sos";

    public SystemBroadcastReceiver(Context context) {
        mContext = context;
        screenReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * 广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            setHomeListener(intent);
        }
    }

    private void setHomeListener(Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason == null){
                return;
            }

            if (intent.getExtras().getBoolean("myReason")){
                screenReceiver.abortBroadcast();
            }else if (SYSTEM_DIALOG_REASON_GLOBAL.equals(reason)) {
                Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                it.putExtra("myReason",true);
                mContext.sendBroadcast(it);
                sos();

            }
        }else if (SOS_ACTION.equals(action)){
            sos();
        }
    }

    public void start() {
        registerBroadcastReceiver();
    }


    /**
     * 停止监听，销毁广播
     */
    public void unregisterBroadcastReceiver() {
        mContext.unregisterReceiver(screenReceiver);
    }

    /**
     * 启动screen状态和home键的广播接收器
     */
    public void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(SOS_ACTION);
        mContext.registerReceiver(screenReceiver, filter);
    }

    private void sos(){
        String sos = Settings.System.getString(App.getInstance().getContentResolver(), SettingsConstant.WATCH_SOS);
        LogUtil.d("正在拨打sos号码：" + sos != null ? sos : "无sos号码", LogUtil.TYPE_RELEASE);
        if (sos != null && !sos.equals("")) {
            String[] split = sos.split(",");
            List<String> list = Arrays.asList(split);
            new PhoneStateManager(mContext, list).register();
        } else {
            ToastUtils.show("请到家长app端设置sos号码");
        }
    }
}
