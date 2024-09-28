package com.android.launcher3.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.launcher3.common.CommonApp
import com.android.launcher3.common.constant.SettingsConstant.PROCESS_ACTION
import com.android.launcher3.common.mode.ProcessMode

class ProcessBroadcastReceiver : BroadcastReceiver()  {

    private val processMode by lazy { ProcessMode(CommonApp.getInstance()) }

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){

            PROCESS_ACTION ->{
                processMode.getRunningPackageNames()
                //processMode.getRunningService()
            }
        }
    }
}