package com.android.launcher3.moudle.alarm.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.db.alarm.AlarmDBEngine;
import com.android.launcher3.common.db.alarm.AlarmModel;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.moudle.alarm.view.ListenerDialog;

/**
 * 开启闹钟
 */
public class AlarmClockReceiver extends BroadcastReceiver {

    @SuppressLint("StaticFieldLeak")
    public static ListenerDialog listenerDialog = new ListenerDialog(CommonApp.getInstance());

    private Handler mHandler = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int id = intent.getIntExtra("alarm", 0);
            ThreadPoolUtils.getExecutorService().execute(() -> {
                AlarmModel alarmModel = AlarmDBEngine.getInstance(context).quary_alarm_id(id);
                intent.putExtra("alarmModel", alarmModel);
                mHandler.post(() -> listenerDialog.startProgress(intent));
            });
        }
    }
}
