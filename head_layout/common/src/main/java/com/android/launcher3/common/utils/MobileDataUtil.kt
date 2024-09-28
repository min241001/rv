package com.android.launcher3.common.utils

import android.content.Context
import android.telephony.TelephonyManager
import java.lang.reflect.InvocationTargetException

object MobileDataUtil {

    fun getDataEnabled(context : Context):Boolean {
        val  telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return try {
            val method = telephonyManager.javaClass.getDeclaredMethod("getDataEnabled")
            method.invoke(telephonyManager) as Boolean
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            false
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            false
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            false
        }
    }

    fun setDataEnabled(context : Context , enabled: Boolean) {
        val  telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val method = telephonyManager.javaClass.getDeclaredMethod("setDataEnabled", Boolean::class.javaPrimitiveType)
            method.invoke(telephonyManager, enabled)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}