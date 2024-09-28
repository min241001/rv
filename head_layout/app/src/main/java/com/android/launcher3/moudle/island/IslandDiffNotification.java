package com.android.launcher3.moudle.island;

import com.android.launcher3.moudle.notification.bean.NotificationBean;
import com.android.launcher3.moudle.notification.queue.IslandNotificationLinkedQueue;

import java.util.LinkedList;

public class IslandDiffNotification {

    private static IslandDiffNotification instance;

    public static IslandDiffNotification getInstance() {
        if (instance == null) {
            instance = new IslandDiffNotification();
        }
        return instance;
    }


    public boolean hasAudio() {
        LinkedList<NotificationBean> linkedList = IslandNotificationLinkedQueue.getInstance().getQueues();
        if (linkedList == null || linkedList.size() == 0) {
            return false;
        }
        for (NotificationBean bean : linkedList) {
            if (bean.getPackageName().equals("com.ximalayaos.wearkid") || bean.getPackageName().equals("bubei.tingshu.wear")) {
                return true;
            }
        }
        return false;
    }


    public boolean hasTelMsg() {
        LinkedList<NotificationBean> linkedList = IslandNotificationLinkedQueue.getInstance().getQueues();
        if (linkedList == null || linkedList.size() == 0) {
            return false;
        }
        for (NotificationBean bean : linkedList) {
            if (bean.getPackageName().equals("com.baehug.dialer")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSmsMsg() {
        LinkedList<NotificationBean> linkedList = IslandNotificationLinkedQueue.getInstance().getQueues();
        if (linkedList == null || linkedList.size() == 0) {
            return false;
        }
        for (NotificationBean bean : linkedList) {
            if (bean.getPackageName().equals("com.baehug.sms")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasWechatMsg() {
        LinkedList<NotificationBean> linkedList = IslandNotificationLinkedQueue.getInstance().getQueues();
        if (linkedList == null || linkedList.size() == 0) {
            return false;
        }
        for (NotificationBean bean : linkedList) {
            if (bean.getPackageName().equals("com.baehug.watch.wechat") || bean.getPackageName().equals("com.baehug.videochat")) {
                return true;
            }
        }
        return false;
    }

}
