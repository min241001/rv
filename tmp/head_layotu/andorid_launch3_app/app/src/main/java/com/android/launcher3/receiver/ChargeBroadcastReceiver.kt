package com.android.launcher3.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.os.BatteryManager
import com.android.launcher3.common.CommonApp
import com.android.launcher3.common.constant.SettingsConstant.CHARGE_ACTION
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.dialog.ChargeDialog
import com.android.launcher3.common.dialog.LowBatteryDialog
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.common.utils.ShutdownUtils.isCharging

class ChargeBroadcastReceiver : BroadcastReceiver() {

    private val dialog by lazy { ChargeDialog.init(CommonApp.getInstance()) }

    private val mLoading by lazy { LowBatteryDialog.init(CommonApp.getInstance()) }

    private val batteryManager by lazy { CommonApp.getInstance().getSystemService(BATTERY_SERVICE) as BatteryManager }

    override fun onReceive(context : Context, intent : Intent) {

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val battery = (level / scale.toFloat() * 100).toInt()

        when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED ->{
                if (batteryManager.isCharging){
                    val switch = AppLocalData.getInstance().powerSwitch
                    if (switch == 0){
                        dialog.setBattery(battery)
                    }
                }
                AppLocalData.getInstance().battery = battery
            }

            Intent.ACTION_BATTERY_LOW -> {
                when(battery){
                    5,10,20 ->{
                        mLoading.startPregress(battery)
                    }
                }
            }

            Intent.ACTION_BATTERY_OKAY -> {

            }

            Intent.ACTION_POWER_CONNECTED -> {
                setBattery(context)
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                dialog.stopProgress()
            }

            CHARGE_ACTION -> {
                setBattery(context)
            }
        }
    }

    private fun setBattery(context: Context) {
        val switch = AppLocalData.getInstance().powerSwitch
        LogUtil.i("setBattery: " + isTestOrEngineer(context), LogUtil.TYPE_RELEASE)
        if (switch == 0 && !isTestOrEngineer(context)) {
            if (isCharging(CommonApp.getInstance())) {
                val property =
                    batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                AppLocalData.getInstance().battery = property
                dialog.startProgress(property)
            }
        } else {
            dialog.stopProgress()
        }
    }

    // 是否测试模式或工程模式
    private fun isTestOrEngineer(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = manager.getRunningTasks(1)
        if (tasks != null && tasks.size == 1) {
            val componentName = tasks.get(0).topActivity
            val packageName = componentName!!.packageName
            LogUtil.i( "isTestOrEngineer: packageName $packageName", LogUtil.TYPE_DEBUG)
            if (packageName == null) {
                return false
            }
            if (packageName.contains("com.sprd.engineermode")
                || packageName.contains("com.sprd.validationtools")
                || packageName.contains("com.mediatek.engineermode")
                || packageName.contains("com.android.factorytest")) {
                return true
            }
        }
        return false
    }
}