package com.android.launcher3.common.mode

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.launcher3.common.constant.SettingsConstant
import com.android.launcher3.common.receiver.ProcessBroadcastReceiver

class ProcessMode(val context: Context) {

    private val alarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    fun processTasks(){
        val intent = Intent(context, ProcessBroadcastReceiver::class.java)
        intent.action = SettingsConstant.PROCESS_ACTION
        val pendingIntent = PendingIntent.getBroadcast(context, SettingsConstant.PROCESS_TASK, intent, 0)
        val triggerTime = System.currentTimeMillis() + 15 * 60 * 1000
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    fun cancelTask(){
        val intent = Intent(context, ProcessBroadcastReceiver::class.java)
        intent.action = SettingsConstant.PROCESS_ACTION
        val pendingIntent = PendingIntent.getBroadcast(
            context, SettingsConstant.PROCESS_TASK, intent, 0
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    fun getRunningPackageNames(){
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses

        for (processInfo in runningAppProcesses) {
            val packageName = processInfo.processName
            if (packageName == "com.android.launcher3" || packageName == "com.baehug.sms" || packageName == "com.sogou.ime.wear") {
                continue
            }
            forceStopPackage(packageName)
        }
    }

    fun getRunningService(){
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
        for (serviceInfo in runningServices) {
            val componentName = serviceInfo.service
            val packageName = componentName.packageName
            val serviceName = componentName.className
        }
    }

    private fun forceStopPackage(packageName: String) {
        Log.i("forceStopPackage", "packageName = $packageName")
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        try {
            val forceStopPackage = activityManager.javaClass.getDeclaredMethod("forceStopPackage", String::class.java)
            forceStopPackage.isAccessible = true
            forceStopPackage.invoke(activityManager, packageName)
            activityManager.killBackgroundProcesses(packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}