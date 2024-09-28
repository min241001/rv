package com.baehug.callui.phone.manager

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.android.launcher3.common.utils.ThreadPoolUtils
import com.baehug.callui.phone.utils.ContactUtil
import com.baehug.callui.phone.utils.ContactUtil.getContentCallLog

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 联系人管理类
 */
object ContactManager {

    private val mHandler = Handler(Looper.getMainLooper())

    fun getContentCallLog(mContext: Context?, number: String?, callBack: Callback?) {
        ThreadPoolUtils.getExecutorService().execute(Runnable {
            val contentCallLog = getContentCallLog(mContext, number)
            callFinish(contentCallLog, callBack)
        })
    }

    private fun callFinish(log: ContactUtil.ContactInfo?, callBack: Callback?) {
        if (callBack == null) {
            return
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            callBack.onFinish(log)
            return
        }
        mHandler.post(Runnable { callBack.onFinish(log) })
    }

    interface Callback {
        fun onFinish(contentCallLog: ContactUtil.ContactInfo?)
    }
}