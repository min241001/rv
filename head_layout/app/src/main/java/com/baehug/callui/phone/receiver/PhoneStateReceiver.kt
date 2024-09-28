package com.baehug.callui.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.baehug.callui.phone.impl.PhoneStateActionImpl

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话状态监听
 */
class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val action = intent?.action
            if (Intent.ACTION_NEW_OUTGOING_CALL == action || TelephonyManager.ACTION_PHONE_STATE_CHANGED == action) {
                try {
                    val manager = it.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    var state = manager.callState
                    val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                    if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action, true)) {
                        state = 1000
                    }
                    dealWithCallAction(state, phoneNumber)
                } catch (e: Exception) {
                }
            }
        }
    }

    //来去电的几个状态
    private fun dealWithCallAction(state: Int?, phoneNumber: String?) {
        when (state) {
            // 来电状态
            TelephonyManager.CALL_STATE_RINGING -> {
                PhoneStateActionImpl.instance.onRinging(phoneNumber)
            }
            // 空闲状态(挂断)
            TelephonyManager.CALL_STATE_IDLE -> {
                PhoneStateActionImpl.instance.onHandUp()
            }
            // 摘机状态(接听)
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                PhoneStateActionImpl.instance.onPickUp(phoneNumber)
            }
            1000 -> {   //拨打电话广播状态
                PhoneStateActionImpl.instance.onCallOut(phoneNumber)
            }
        }
    }


}