package com.android.launcher3.receiver.core;

import android.content.Context;
import android.content.Intent;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/5 09:32
 * @UpdateUser: jamesfeng
 */
public interface ReceiverService {
    /**
     * 当前事件
     *
     * @return
     */
    String action();

    /**
     * 接收到广播消息
     *
     * @param context context
     * @param intent  intent
     */
    void onReceive(Context context, Intent intent);

    /**
     * 添加监听事件
     *
     * @param receiverListener 监听事件
     */
    void addListener(ReceiverListener receiverListener);

    /**
     * 移除监听事件
     *
     * @param receiverListener 监听事件
     */
    void removeListener(ReceiverListener receiverListener);

    /**
     * 清除所有的监听事件
     */
    void clearAllListener();
}
