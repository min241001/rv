package com.android.launcher3.moudle.notification.queue;


import com.android.launcher3.moudle.notification.bean.NotificationBean;

/**
 * @Author: shensl
 * @Description：通知队列
 * @CreateDate：2024/1/9 13:44
 * @UpdateUser: shensl
 */
public class NotificationLinkedQueue extends BaseLinkedQueue<NotificationBean> {

    private static NotificationLinkedQueue instance;

    private NotificationLinkedQueue() {
        super(10);
    }

    public static NotificationLinkedQueue getInstance() {
        if (instance == null) {
            instance = new NotificationLinkedQueue();
        }
        return instance;
    }

}
