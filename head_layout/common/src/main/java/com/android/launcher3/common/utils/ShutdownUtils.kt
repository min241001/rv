package com.android.launcher3.common.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import com.android.launcher3.common.CommonApp
import com.android.launcher3.common.constant.SettingsConstant

object ShutdownUtils {

    fun shutdown(){
        if (Build.VERSION.SDK_INT <  Build.VERSION_CODES.O){
            val intent = Intent(SettingsConstant.ACTION_REQUEST_SHUTDOWN)
            intent.putExtra(SettingsConstant.EXTRA_KEY_CONFIRM, false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            CommonApp.getInstance().startActivity(intent)
        }else{
            try {
                val pManager = CommonApp.getInstance().
                getSystemService(Context.POWER_SERVICE) as PowerManager
                val method  = pManager.javaClass.getMethod(
                    "shutdown",
                    Boolean::class.javaPrimitiveType,
                    String::class.java,
                    Boolean::class.javaPrimitiveType
                )
                method.invoke(pManager, false, null, false)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                ToastUtils.show("关机失败!")
            }
        }
    }

    fun isCharging(context: Context): Boolean {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryIntent = context.registerReceiver(null, intentFilter)
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    }
}