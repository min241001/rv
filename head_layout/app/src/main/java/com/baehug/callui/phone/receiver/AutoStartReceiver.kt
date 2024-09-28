package com.baehug.callui.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.baehug.callui.phone.service.CallListenerService
import com.baehug.callui.phone.service.TaskServiceManager

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 开机启动
 */
class AutoStartReceiver : BroadcastReceiver() {

    companion object {
        const val AUTO_START_RECEIVER = "xmxx.autostart_action"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (AUTO_START_RECEIVER == action) {
            context?.let {
                TaskServiceManager.bindStepService(Intent(it, CallListenerService::class.java))
            }
        }
    }
}