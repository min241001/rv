package com.android.launcher3.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.launcher3.common.CommonApp
import com.android.launcher3.common.constant.SettingsConstant.SIM_ACTION
import com.android.launcher3.common.dialog.SimTaskDialog

class TaskBroadcastReceiver : BroadcastReceiver()  {

    private val mLoading by lazy { SimTaskDialog.init(CommonApp.getInstance()) }

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){

            SIM_ACTION ->{
                mLoading.startPregress()
            }
        }
    }
}