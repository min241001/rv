package com.android.launcher3.receiver.core;

import android.content.Context;
import android.content.Intent;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/5 09:55
 * @UpdateUser: jamesfeng
 */
public class BaseReceiverServiceImpl implements ReceiverService {
    private static final String TAG = "BaseReceiverServiceImp";
    public Long lastReceiveTime = System.currentTimeMillis();
    private CopyOnWriteArraySet<ReceiverListener> listenerSet = new CopyOnWriteArraySet();

    @Override
    public String action() {
        return null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        for (ReceiverListener receiverListener : listenerSet) {
            receiverListener.onReceive(context, intent);
        }
        lastReceiveTime = System.currentTimeMillis();
    }

    @Override
    public void addListener(ReceiverListener receiverListener) {
        listenerSet.add(receiverListener);
    }

    @Override
    public void removeListener(ReceiverListener receiverListener) {
        listenerSet.remove(receiverListener);
    }

    @Override
    public void clearAllListener() {
        listenerSet.clear();
    }
}
