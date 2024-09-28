package com.baehug.callui.phone.service

import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.launcher3.App
import com.baehug.callui.phone.manager.PhoneCallManager

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 拨打电话服务
 */
@RequiresApi(Build.VERSION_CODES.M)
class PhoneCallService : InCallService() {

    private val TAG = "PhoneCallService"

    companion object {
        const val ACTION_SPEAKER_ON = "action_speaker_on"
        const val ACTION_SPEAKER_OFF = "action_speaker_off"
        const val ACTION_MUTE_ON = "action_mute_on"
        const val ACTION_MUTE_OFF = "action_mute_off"

        fun startService(action: String?) {
            val intent = Intent(App.getInstance(), PhoneCallService::class.java).apply {
                this.action = action
            }
            App.getInstance().startService(intent)
        }
    }

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        Log.i(TAG, "onCallAdded: ${call.toString()}")
        call?.let {
            it.registerCallback(callback)
            PhoneCallManager.instance.addCall(it)
        }
    }

    override fun onCallRemoved(call: Call?) {
        super.onCallRemoved(call)
        Log.i(TAG, "onCallRemoved: ${call.toString()}")
        call?.let {
            it.unregisterCallback(callback)
            PhoneCallManager.instance.removeCall(it)
        }
    }

    override fun onCanAddCallChanged(canAddCall: Boolean) {
        super.onCanAddCallChanged(canAddCall)
        Log.i(TAG, "onCanAddCallChanged: canAddCall $canAddCall")
        PhoneCallManager.instance.onCanAddCallChanged(canAddCall)
    }

    private val callback: Call.Callback = object : Call.Callback() {
        override fun onStateChanged(call: Call?, state: Int) {
            Log.i(TAG, "onStateChanged: call ${call.toString()} state $state")
            super.onStateChanged(call, state)
            PhoneCallManager.instance.onCallStateChanged(call, state)
        }

        override fun onCallDestroyed(call: Call) {
            Log.i(TAG, "onCallDestroyed: call ${call.toString()}")
            call.hold()
            super.onCallDestroyed(call)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SPEAKER_ON -> setAudioRoute(CallAudioState.ROUTE_SPEAKER)
            ACTION_SPEAKER_OFF -> setAudioRoute(CallAudioState.ROUTE_EARPIECE)
            ACTION_MUTE_ON -> setMuted(true)
            ACTION_MUTE_OFF -> setMuted(false)
            else -> {
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}