package com.baehug.callui.phone.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.android.launcher3.common.utils.LogUtil

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 通知使用权
 */
class NotificationService : NotificationListenerService() {
    private val TAG = "NotificationService"

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        LogUtil.e(TAG, "Notification removed", LogUtil.TYPE_RELEASE)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        LogUtil.e(TAG, "Notification posted", LogUtil.TYPE_RELEASE)
    }
}