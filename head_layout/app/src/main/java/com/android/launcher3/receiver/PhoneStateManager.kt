package com.android.launcher3.receiver

import android.annotation.SuppressLint
import android.app.Service
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import android.telephony.TelephonyManager


class PhoneStateManager(private val context: Context, private val numbers: List<String>) {
    private val receiver: PhoneStatReceiver
    private var currentIndex = 0

    private var number = ""

    private var handler = Handler(Looper.getMainLooper())

    init {
        receiver = PhoneStatReceiver()
    }

    fun register() {
        val filter = IntentFilter()
        filter.addAction("android.intent.action.PHONE_STATE")
        context.registerReceiver(receiver, filter)
        dialNextNumber()
    }

    fun unregister() {
        context.unregisterReceiver(receiver)
    }

    inner  class PhoneStatReceiver: BroadcastReceiver() {

        private var previousState = -1

        @SuppressLint("SuspiciousIndentation")
        override fun onReceive(context: Context, intent: Intent) {
            val telMgr =  context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            if (telMgr.callState != previousState)
            when (telMgr.callState) {
                TelephonyManager.CALL_STATE_IDLE ->{
                    handler.postDelayed({
                        if (getCallLogState()){
                        unregister()
                    }else{
                        dialNextNumber()
                    }
                    }, 100)
                }
            }
            previousState = telMgr.callState
        }
    }

    private fun dialNextNumber() {
        if (currentIndex >= numbers.size) {
            unregister()
            return
        }
        number = numbers[currentIndex]
        val uri = Uri.parse("tel:$number")
        val intent = Intent(Intent.ACTION_CALL, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        currentIndex++
    }

     fun getCallLogState(): Boolean {
        val cr: ContentResolver = context.contentResolver
        val cursor: Cursor? = cr.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(CallLog.Calls.DURATION),
            (CallLog.Calls.NUMBER + "= ? "),
            arrayOf(number),
            CallLog.Calls.DEFAULT_SORT_ORDER
        )

         if (cursor != null) {
            while (cursor.moveToNext()) {
                val duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION))
                return if (duration > 0){
                    cursor.close()
                    true
                }else{
                    cursor.close()
                    false
                }
            }
        }
        cursor?.close()
        return false
    }
}

