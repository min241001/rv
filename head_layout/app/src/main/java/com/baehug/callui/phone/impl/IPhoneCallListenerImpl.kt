package com.baehug.callui.phone.impl

import android.content.Intent
import com.android.launcher3.App
import com.android.launcher3.common.utils.LogUtil
import com.baehug.callui.phone.interfaces.IPhoneCallListener
import com.baehug.callui.phone.service.CallListenerService
import com.baehug.callui.phone.utils.PhoneCallUtil
import com.baehug.callui.phone.view.ForegroundActivity
import java.lang.Exception

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话实现类
 */
class IPhoneCallListenerImpl : IPhoneCallListener {

    private val TAG = "IPhoneCallListenerImpl"

    override fun onAnswer() {
        val mContext = App.getInstance()
        try {
            val intent = Intent(mContext, ForegroundActivity::class.java)
            intent.action = CallListenerService.ACTION_PHONE_CALL
            intent.putExtra(CallListenerService.PHONE_CALL_ANSWER, "0")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(intent)
        } catch (e: Exception) {
            LogUtil.e(TAG,"startForegroundActivity exception>>$e", LogUtil.TYPE_RELEASE)
            PhoneCallUtil.answer()
        }
    }

    override fun onOpenSpeaker() {
        PhoneCallUtil.openSpeaker()
    }

    override fun onDisconnect() {
        LogUtil.i(TAG," onDisconnect", LogUtil.DEBUG)
        val mContext = App.getInstance()
        try {
            val intent = Intent(mContext, ForegroundActivity::class.java)
            intent.action = CallListenerService.ACTION_PHONE_CALL
            intent.putExtra(CallListenerService.PHONE_CALL_DISCONNECT, "0")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(intent)
        } catch (e: Exception) {
            LogUtil.e(TAG,"startForegroundActivity exception>>$e", LogUtil.TYPE_RELEASE)
            PhoneCallUtil.disconnect()
        }

    }

}