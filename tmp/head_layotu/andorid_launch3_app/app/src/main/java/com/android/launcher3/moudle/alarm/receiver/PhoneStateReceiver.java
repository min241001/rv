package com.android.launcher3.moudle.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {

    public static final String CALL_STATE = "com.android.launcher3.callstate";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取广播中的电话状态
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state != null) {
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // 电话铃声响起，表示有来电
                context.sendBroadcast(new Intent(CALL_STATE));
            }
        }
    }
}
