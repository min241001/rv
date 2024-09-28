package com.baehug.callui.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.baehug.callui.phone.view.PhoneActivity
import com.baehug.callui.phone.manager.CustomNotifyManager
import com.baehug.callui.phone.utils.GlobalActivityLifecycleMonitor
import java.lang.reflect.Method

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 通知广播点击
 */
class NotificationBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 判断app是否在前台
        val action = intent.action
        collapseStatusBar(context)
        if (GlobalActivityLifecycleMonitor.isAppOnForeground) {
            return
        }
        when (action) {
            CustomNotifyManager.ACTION_NOTIFICATION_CLICK -> {
                val i = Intent(context, PhoneActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }
        }
    }

    private fun collapseStatusBar(context: Context) {
        try {
            val statusBarManager = context.getSystemService("statusbar")
            val collapse: Method
            collapse = statusBarManager.javaClass.getMethod("collapsePanels")
            collapse.invoke(statusBarManager)
        } catch (localException: Exception) {
            localException.printStackTrace()
        }
    }
}