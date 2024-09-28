package com.android.launcher3.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.android.launcher3.common.constant.SettingsConstant.CHARGE_ACTION
import com.android.launcher3.receiver.ChargeBroadcastReceiver

 class Battery(context: Context) {
    private val mContext: Context
    private val receiver: ChargeBroadcastReceiver

    init {
        mContext = context
        receiver = ChargeBroadcastReceiver()
    }

    fun register() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_BATTERY_LOW)
        filter.addAction(Intent.ACTION_BATTERY_OKAY)
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(CHARGE_ACTION)
        mContext.registerReceiver(receiver, filter)
    }

    fun unregister() {
        mContext.unregisterReceiver(receiver)
    }
}