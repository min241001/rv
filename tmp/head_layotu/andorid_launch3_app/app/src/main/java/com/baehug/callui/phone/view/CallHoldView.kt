package com.baehug.callui.phone.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.android.launcher3.R
import com.baehug.callui.phone.manager.PhoneCallManager
import kotlinx.android.synthetic.main.view_caller_hold.view.*

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 通话时输入数字页
 */
@TargetApi(Build.VERSION_CODES.M)
class CallHoldView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var mCallId: String? = null
    var callSwitchListener: CallSwitchListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_caller_hold, this, true)
        setOnClickListener {
            mCallId?.let { callId ->
                callSwitchListener?.onCallSwitch(callId)
            }
        }
    }

    fun handleHold(callId: String?) {
        if (PhoneCallManager.instance.getCurrentCallSize() <= 1) {
            hide()
            return
        }
        PhoneCallManager.instance.getCallById(callId)?.let {
            show(callId)
        } ?: hide()
    }

    private inline fun show(callId: String?) = PhoneCallManager.instance.getCallById(callId)?.let {
        if (callId == mCallId) {
            return@let false
        }
        mCallId = callId
        tv_caller_hold_phone_number.text = PhoneCallManager.instance.getNumberByCallId(callId)
        visibility = View.VISIBLE
        true
    } ?: false

    fun hide() {
        mCallId = null
        visibility = View.GONE
    }

    interface CallSwitchListener {

        fun onCallSwitch(callId: String)

    }

}