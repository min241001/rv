package com.android.launcher3.moudle.notification.queue;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.island.IslandMsgBean;
import com.android.launcher3.moudle.notification.bean.NotificationBean;
import com.android.launcher3.utils.FileLogUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: shensl
 * @Description：基础队列
 * @CreateDate：2024/1/9 13:42
 * @UpdateUser: shensl
 */
class BaseLinkedQueue<E> {
    private static final int MAX_SIZE = 10; // 最大容量
    private LinkedList<E> linkedList;
    private int maxSize;

    public BaseLinkedQueue(int maxSize) {
        this.linkedList = new LinkedList<>();
        if (maxSize <= 0) {
            this.maxSize = MAX_SIZE;
        } else {
            this.maxSize = maxSize;
        }
    }

    public void addQueue(E e) {
        try {
            if (linkedList.size() >= maxSize) {
                E e1 = linkedList.removeLast(); // 移除第一条消息
                LogUtil.d("移除第一条消息，当前消息是【" + e1.toString() + "】", LogUtil.TYPE_DEBUG);
                FileLogUtil.debugMode("移除第一条消息，当前消息是【" + e1.toString() + "】");
            }
            linkedList.addFirst(e); // 添加新的消息至队首
            FileLogUtil.debugMode("添加1条消息成功，当前共【" + linkedList.size() + "】条消息");
            for (int i = 0; i < linkedList.size(); i++) {
                LogUtil.d("打印当前【" + i + "】条消息，当前消息是【" + linkedList.get(i).toString() + "】", LogUtil.TYPE_DEBUG);
                FileLogUtil.debugMode("打印当前【" + i + "】条消息，当前消息是【" + linkedList.get(i).toString() + "】");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void removeQueue(E e) {
        if (linkedList.isEmpty()) {
            return;
        }

        linkedList.remove(e);
    }


    public void clearQueues() {
        linkedList.clear();
    }

    public LinkedList<E> getQueues() {
        return linkedList;

    }

    /**
     *
     * @return  去重后的消息列
     */
    public LinkedList<E> getNoRepeatQueues() {
        LinkedList<E> list = new LinkedList<>();

        for (E e : linkedList) {
            //list去重复，内部重写equals
            if (!list.contains(e)) {
                list.add(e);
            }
        }
        linkedList.clear();
        linkedList.addAll(list);
        return linkedList;
    }

}