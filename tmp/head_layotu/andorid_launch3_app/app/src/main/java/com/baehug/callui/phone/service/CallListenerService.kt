package com.baehug.callui.phone.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.android.launcher3.App
import com.baehug.callui.phone.impl.PhoneStateListenerImpl
import com.baehug.callui.phone.manager.CustomNotifyManager
import com.baehug.callui.phone.receiver.AutoStartReceiver
import com.baehug.callui.phone.utils.PhoneCallUtil

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话监听
 */
class CallListenerService : Service() {

    companion object {
        const val PHONE_CALL_ANSWER = "phone_call_answer"
        const val PHONE_CALL_DISCONNECT = "phone_call_disconnect"

        const val ACTION_PHONE_CALL = "action_phone_call"    //电话操作处理（接听、挂断等）

    }

    private lateinit var phoneStateListener: PhoneStateListener
    private lateinit var telephonyManager: TelephonyManager

    private val callServiceBinder: TaskServiceBinder = TaskServiceBinder()
    private var notification: Notification? = null

    override fun onCreate() {
        super.onCreate()
        initPhoneStateListener()
    }

    fun forceForeground(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channel_megatron_1", "SuperCall", importance)
            channel.description = "description"
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        try {
            ContextCompat.startForegroundService(App.getInstance(), intent)
            notification = CustomNotifyManager.instance?.getNotifyNotification(App.getInstance())
            if (notification != null) {
                startForeground(CustomNotifyManager.STEP_COUNT_NOTIFY_ID, notification)
            } else {
                startForeground(CustomNotifyManager.STEP_COUNT_NOTIFY_ID,
                        CustomNotifyManager.instance?.getDefaultNotification(NotificationCompat.Builder(App.getInstance())))
            }
        } catch (e: Exception) {
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_STICKY
        }
        val action = intent.action ?: return START_STICKY
        when (action) {
            ACTION_PHONE_CALL -> {
                dispatchAction(intent)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        callServiceBinder.onBind(this)
        return callServiceBinder
    }

    override fun onDestroy() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        // 重新拉起
        sendBroadcast(Intent(AutoStartReceiver.AUTO_START_RECEIVER))
        super.onDestroy()
        stopForeground(true)
    }


    private fun dispatchAction(intent: Intent) {
        if (intent.hasExtra(PHONE_CALL_DISCONNECT)) {
            PhoneCallUtil.disconnect()
            return
        }
        if (intent.hasExtra(PHONE_CALL_ANSWER)) {
            PhoneCallUtil.answer()
        }

    }

    private fun initPhoneStateListener() {
        phoneStateListener = PhoneStateListenerImpl(applicationContext)
        // 设置来电监听器
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }
}