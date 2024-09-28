package com.android.launcher3.moudle.notification.notity;

import com.android.launcher3.moudle.notification.bean.NotificationBean;
import com.android.launcher3.moudle.notification.queue.IslandNotificationLinkedQueue;

import java.util.LinkedList;

/**
 * @Author: shensl
 * @Description：通知监听辅助类
 * @CreateDate：2024/1/9 14:22
 * @UpdateUser: shensl
 */
public class IslandNotifyHelper {

    private static IslandNotifyHelper instance;

    private NotifyListener notifyListener;

    public static IslandNotifyHelper getInstance() {
        if (instance == null) {
            instance = new IslandNotifyHelper();
        }
        return instance;
    }

    /**
     * 收到消息
     *
     * @param bean 消息
     */
    public void onReceive(NotificationBean bean) {

        IslandNotificationLinkedQueue.getInstance().addQueue(bean);
        if (notifyListener != null) {
            notifyListener.onReceiveMessage(bean);
        }
    }

    /**
     * 移除消息
     *
     * @param packageName 消息
     */
    public void onRemoved(String packageName) {
        LinkedList<NotificationBean> linkedList = IslandNotificationLinkedQueue.getInstance().getQueues();
        if (linkedList == null || linkedList.size() == 0) {
            return;
        }
        for (NotificationBean bean : linkedList) {
            if (bean.getPackageName().equals(packageName)) {
                IslandNotificationLinkedQueue.getInstance().removeQueue(bean);
                break;
            }
        }
        if (notifyListener != null) {
            notifyListener.onRemovedMessage(packageName);
        }
    }

    /**
     * 设置监听回调方法
     *
     * @param notifyListener 通知监听
     */
    public void setNotifyListener(NotifyListener notifyListener) {
        this.notifyListener = notifyListener;
    }

    /**
     * 移除监听回调方法
     */
    public void removeNotifyListener() {
       // this.notifyListener = null;
    }

}