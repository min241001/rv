package com.android.launcher3.receiver;

import static com.android.launcher3.common.constant.SettingsConstant.CHARGE_ACTION;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.AudioFocusManager;

/**
 * 电话监听广播
 */
public class PhoneReceiver extends BroadcastReceiver {
    private static final String TAG = PhoneReceiver.class.getSimpleName();
    private AudioManager mAudioManager;
    private int mFlag;
    private AudioFocusRequest mFocusRequest;


    @Override
    public void onReceive(Context context, Intent intent) {

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


        if (mAudioManager.isMusicActive()) {
            mFlag = 1;
        } else {
            mFlag = 2;
        }
        Log.e(TAG, "initData: mFlag $mFlag isMusicActive " + mAudioManager.isMusicActive());


        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // 如果是拨打电话
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            String outNumber = this.getResultData();// 去电号码
            Log.i(TAG, "call OUT 1:" + phoneNumber);
            Log.i(TAG, "call OUT 2:" + outNumber);

        } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
            // 如果是来电
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            // 来电号码
            String mIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(TAG, "call IN 1:" + state);
            Log.i(TAG, "call IN 2:" + mIncomingNumber);

            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG, "**********************监测到电话呼入!!!!*****");
                    AudioFocusManager.getInstance().requestAudioFocus();//关闭音频
                    //保存数据到系统数据库
                    Settings.System.putInt(context.getContentResolver(), "POWER_SWITCH", 1);
                    intent = new Intent(CHARGE_ACTION);
                    context.sendBroadcast(intent);
                    if (LauncherAppManager.isForbiddenInClass(context, "com.baehug.util")) {
                        // 挂断来电
                        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        try {
                            Class c = Class.forName(telephony.getClass().getName());
                            java.lang.reflect.Method m = c.getDeclaredMethod("getITelephony");
                            m.setAccessible(true);
                            Object telephonyService = m.invoke(telephony);
                            c = Class.forName(telephonyService.getClass().getName());
                            m = c.getDeclaredMethod("endCall");
                            m.setAccessible(true);
                            m.invoke(telephonyService);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "**********************监测到接听电话!!!!************");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "**********************监测到挂断电话!!!!*******************");
                    AudioFocusManager.getInstance().freedAudioFocus();//释放音频
                    Settings.System.putInt(CommonApp.getInstance().getContentResolver(), "POWER_SWITCH", 0);
                    intent = new Intent(CHARGE_ACTION);
                    context.sendBroadcast(intent);
                    break;
            }
        }
    }
}
