package com.android.launcher3.moudle.notification.notity;


import com.android.launcher3.moudle.notification.bean.NotificationBean;

/**
 * @Author: shensl
 * @Description：通知监听
 * @CreateDate：2024/1/9 14:22
 * @UpdateUser: shensl
 */
public interface NotifyListener {
    /**
     * 接收到通知栏消息
     * @param bean
     */
    void onReceiveMessage(NotificationBean bean);

    /**
     * 移除掉通知栏消息
     * @param packageName
     */
    void onRemovedMessage(String packageName);
}