package com.baehug.callui.permission

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * 权限工具类
 */
object OpPermissionUtils {

    private const val TAG = "OpPermissionUtils"

    /**
     * 检查悬浮窗权限
     */
    fun checkPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun checkOp(context: Context, op: Int): Boolean {
        val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val clazz: Class<*> = AppOpsManager::class.java
            val method = clazz.getDeclaredMethod("checkOp",
                Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java)
            return AppOpsManager.MODE_ALLOWED == method.invoke(manager, op, Binder.getCallingUid(), context.packageName) as Int
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        return false
    }
}