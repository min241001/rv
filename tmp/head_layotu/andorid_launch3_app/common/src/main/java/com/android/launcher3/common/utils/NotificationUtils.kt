package com.android.launcher3.common.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.DrawableRes

object NotificationUtils {

    fun sendNotification(context: Context,title : CharSequence,@DrawableRes icon:Int, text:CharSequence, id:Int = 1){
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var builder = Notification.Builder(context)
        if (Build.VERSION.SDK_INT <  Build.VERSION_CODES.O){
            builder.setContentTitle(title)
                .setSmallIcon(icon)
                .setContentText(text)
        }else{
            val channelId = "channel_id"
            val channel = NotificationChannel(channelId, "Launcher Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            builder = Notification.Builder(context, channelId)
                .setContentTitle(title)
                .setSmallIcon(icon)
                .setContentText(text)
        }
        notificationManager.notify(id, builder.build())
    }
}