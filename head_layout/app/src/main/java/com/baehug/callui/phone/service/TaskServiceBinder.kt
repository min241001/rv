package com.baehug.callui.phone.service

import android.os.Binder
import java.lang.ref.WeakReference

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : Service 管理
 */
class TaskServiceBinder : Binder() {
    private var weakService: WeakReference<CallListenerService>? = null

    /**
     * Inject service instance to weak reference.
     */
    fun onBind(service: CallListenerService) {
        weakService = WeakReference(service)
    }

    val service: CallListenerService?
        get() = if (weakService == null) null else weakService!!.get()
}