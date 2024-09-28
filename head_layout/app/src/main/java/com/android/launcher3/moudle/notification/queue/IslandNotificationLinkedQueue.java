package com.android.launcher3.moudle.notification.queue;


import com.android.launcher3.moudle.island.IslandMsgBean;
import com.android.launcher3.moudle.notification.bean.NotificationBean;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: shensl
 * @Description：通知队列
 * @CreateDate：2024/1/9 13:44
 * @UpdateUser: shensl
 */
public class IslandNotificationLinkedQueue extends BaseLinkedQueue<NotificationBean> {

    private static IslandNotificationLinkedQueue instance;

    private IslandNotificationLinkedQueue() {
        super(10);
    }

    public static IslandNotificationLinkedQueue getInstance() {
        if (instance == null) {
            instance = new IslandNotificationLinkedQueue();
        }
        return instance;
    }



}
