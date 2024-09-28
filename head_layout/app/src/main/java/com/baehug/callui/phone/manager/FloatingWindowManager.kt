package com.baehug.callui.phone.manager

import android.content.Context
import com.baehug.callui.phone.impl.IPhoneCallListenerImpl
import com.baehug.callui.phone.view.FloatingWindow

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 悬浮窗管理类
 */
class FloatingWindowManager private constructor() {
    var context: Context? = null
    companion object {
        val instance: FloatingWindowManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FloatingWindowManager()
        }
    }

    private var fw: FloatingWindow? = null

    fun initManager(context: Context) {
        this.context = context
    }

    fun show(number: String?, isCallIn: Boolean) {
        if(context != null){
            fw = FloatingWindow(
                    context,
                    IPhoneCallListenerImpl()
            )
            fw?.show(number, isCallIn)
        }
    }

    fun dismiss() {
        fw?.dismiss()
    }
}