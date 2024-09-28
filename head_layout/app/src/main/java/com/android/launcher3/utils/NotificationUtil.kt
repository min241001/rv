package com.android.launcher3.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService

object NotificationUtil {

     @SuppressLint("PrivateApi")
     fun registerNotify(context: Context){
        try {
            val notification = NotificationListenerService::class.java
            val registerMethod = notification.getDeclaredMethod("registerAsSystemService",
                Context::class.java, ComponentName::class.java, Int::class.java)
            registerMethod.invoke(context, context,
                context::class.java.canonicalName?.let {
                    ComponentName(context.packageName,
                        it
                    )
                }, -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

     @SuppressLint("PrivateApi")
     fun unregisterNotify(){
        try{
            val notification = Class.forName("android.service.notification.NotificationListenerService");
            val methodGetString = notification.getDeclaredMethod("jxUnregisterAsSystemService");
            methodGetString.isAccessible = true
            methodGetString.invoke(null)
        }catch( e:Exception){
            e.printStackTrace();
        }
    }
}