package com.baehug.callui.phone.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.android.launcher3.App

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 通知栏管理
 */
object TaskServiceManager {
    private var stepServiceBinder: TaskServiceBinder? = null
    @JvmStatic
    fun bindStepService(intent: Intent?) {
        val applicationContext = App.getInstance()
        if (intent == null) {
            return
        }
        try {
            if (stepServiceBinder != null && stepServiceBinder!!.isBinderAlive) {
                val service = stepServiceBinder!!.service
                service?.forceForeground(intent)
                return
            }
            applicationContext.bindService(intent, object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                    if (binder is TaskServiceBinder) {
                        stepServiceBinder = binder
                        val service = stepServiceBinder!!.service
                        service?.forceForeground(intent)
                    }
                    try {
                        applicationContext.unbindService(this)
                    } catch (e: Exception) {
                    }
                }

                override fun onServiceDisconnected(name: ComponentName) {}
            }, Context.BIND_AUTO_CREATE)
        } catch (e: SecurityException) {
        }
    }
}