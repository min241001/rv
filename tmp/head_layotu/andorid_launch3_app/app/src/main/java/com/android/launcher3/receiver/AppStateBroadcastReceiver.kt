package com.android.launcher3.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.launcher3.common.constant.SettingsConstant.APP_RECEIVER

class AppStateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val intent = Intent(APP_RECEIVER)
        context.sendBroadcast(intent)
    }
}