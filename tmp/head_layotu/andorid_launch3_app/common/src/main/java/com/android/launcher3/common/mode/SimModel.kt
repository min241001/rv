package com.android.launcher3.common.mode

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import com.android.launcher3.common.CommonApp
import com.android.launcher3.common.R
import com.android.launcher3.common.constant.SettingsConstant
import com.android.launcher3.common.constant.SettingsConstant.SIM_ACTION
import com.android.launcher3.common.constant.SettingsConstant.SIM_TASK
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.dialog.Loading
import com.android.launcher3.common.receiver.TaskBroadcastReceiver

class SimModel(val context: Context) {

    private val alarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    private val telephonyManager by lazy { context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager}

    private val loading by lazy { Loading.init(context) }

    fun init(){
        val simState = telephonyManager.simState
        when (simState) {
            TelephonyManager.SIM_STATE_ABSENT -> {
                isCheckSim()
            }

            TelephonyManager.SIM_STATE_READY -> {
                cancelTask()
            }
        }
    }

     fun isCheckSim(){
         if (!hasSimCard()){
             val shutdown = AppLocalData.getInstance().shutdown
             if (!shutdown){
                 loading.startPregress(R.layout.layout_sim)
             }
             val simMode = AppLocalData.getInstance().simMode
             if (simMode == 1){
                 timedTasks()
             }else {
                 cancelTask()
             }
         }else{
             cancelTask()
         }

    }

    private fun hasSimCard() : Boolean{
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if ( tm.simState == TelephonyManager.SIM_STATE_ABSENT || tm.simState == TelephonyManager.SIM_STATE_UNKNOWN){
            return false
        }
        return true
    }

    private fun timedTasks(){
        val intent = Intent(context, TaskBroadcastReceiver::class.java)
        intent.action = SIM_ACTION
        val pendingIntent = PendingIntent.getBroadcast(context, SIM_TASK, intent, 0)
        // 无SIM卡自动关机时间15分钟
        val triggerTime = System.currentTimeMillis() + 15 * 60 * 1000
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    private fun cancelTask(){
        val intent = Intent(context, TaskBroadcastReceiver::class.java)
        intent.action = SIM_ACTION
        val pendingIntent = PendingIntent.getBroadcast(
            context, SIM_TASK, intent, 0
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    fun register(){
        context.contentResolver.registerContentObserver(
            Settings.System.getUriFor(SettingsConstant.SIM_MODE),
            true,
            observer
        )
    }

    fun unRegister(){
        context.contentResolver.unregisterContentObserver(observer)
    }

    private val observer = object : ContentObserver(Handler()) {
        override fun onChange(change: Boolean) {
            val simMode = AppLocalData.getInstance().simMode
            if (simMode == 1){
                timedTasks()
            }else {
                cancelTask()
            }
        }
    }
}