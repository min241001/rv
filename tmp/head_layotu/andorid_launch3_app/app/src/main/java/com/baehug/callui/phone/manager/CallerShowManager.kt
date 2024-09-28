package com.baehug.callui.phone.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.baehug.callui.phone.service.CallListenerService
import com.baehug.callui.phone.service.TaskServiceManager

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话显示管理
 */
class CallerShowManager private constructor() {

    companion object {
        val instance: CallerShowManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CallerShowManager()
        }
    }

    fun initCallerShow(context: Context?) {
        try {
            context?.let {
                TaskServiceManager.bindStepService(Intent(it, CallListenerService::class.java))
                FloatingWindowManager.instance.initManager(it)
            }
        } catch (e: Exception) {
        }
    }

    fun setRingShow(activity: Activity, listener: OnPerManagerListener?) {
        CallerShowPermissionManager.instance.checkAndRequestPhonePermission(activity, object : CallerShowPermissionManager.CallBack {
            override fun onFailed() {
                listener?.onDenied()
            }

            override fun onSuccess() {
                listener?.onGranted()
            }
        })
    }

    interface OnPerManagerListener {

        fun onGranted()

        fun onDenied()

    }

}