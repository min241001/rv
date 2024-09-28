package com.android.launcher3.receiver;

import android.content.Context;
import android.content.Intent;

import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.receiver.core.BaseReceiverServiceImpl;
import com.android.launcher3.receiver.core.ReceiverListener;
import com.android.launcher3.receiver.impl.NetSateReceiverServiceImpl;
import com.android.launcher3.receiver.impl.ScreenOffReceiverServiceImpl;
import com.android.launcher3.receiver.impl.ScreenOnReceiverServiceImpl;
import com.android.launcher3.receiver.impl.SimReceiverServiceImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/5 09:29
 * @UpdateUser: jamesfeng
 */
public class ReceiverManager {
    private static volatile ReceiverManager instance;
    private static Map<String, BaseReceiverServiceImpl> concurrentHashMap;

    public static ReceiverManager getInstance() {
        if (null == instance) {
            synchronized (ReceiverManager.class) {
                if (null == instance) {
                    instance = new ReceiverManager();
                    concurrentHashMap = new ConcurrentHashMap<>(30);
                    initReceiverImpl();
                }
            }
        }
        return instance;
    }

    private static void initReceiverImpl() {
        ScreenOnReceiverServiceImpl screenOnReceiverService = new ScreenOnReceiverServiceImpl();
        ScreenOffReceiverServiceImpl screenOffReceiverService = new ScreenOffReceiverServiceImpl();
        NetSateReceiverServiceImpl netSateReceiverService = new NetSateReceiverServiceImpl();
        SimReceiverServiceImpl simReceiverService = new SimReceiverServiceImpl();
        concurrentHashMap.put(screenOnReceiverService.action(), screenOnReceiverService);
        concurrentHashMap.put(screenOffReceiverService.action(), screenOffReceiverService);
        concurrentHashMap.put(netSateReceiverService.action(), netSateReceiverService);
        concurrentHashMap.put(simReceiverService.action(),simReceiverService);
    }

    /**
     * 添加监听
     *
     * @param action
     * @param receiverListener
     */
    public void addListener(String action, ReceiverListener receiverListener) {
        if (StringUtils.isBlank(action) || receiverListener == null) {
            return;
        }
        BaseReceiverServiceImpl imp = concurrentHashMap.get(action);
        if (imp != null) {
            imp.addListener(receiverListener);
        }
    }

    /**
     * 移除监听
     *
     * @param action
     * @param receiverListener
     */
    public void removeListener(String action, ReceiverListener receiverListener) {
        if (StringUtils.isBlank(action) || receiverListener == null) {
            return;
        }
        BaseReceiverServiceImpl imp = concurrentHashMap.get(action);
        if (imp != null) {
            imp.removeListener(receiverListener);
        }
    }

    /**
     * 清空监听
     *
     * @param action
     * @param receiverListener
     */
    public void clearAllListener(String action, ReceiverListener receiverListener) {
        BaseReceiverServiceImpl imp = concurrentHashMap.get(action);
        if (imp != null) {
            imp.clearAllListener();
        }

    }

    public void clearAllListener() {
        for (String key : concurrentHashMap.keySet()) {
            concurrentHashMap.get(key).clearAllListener();
        }
    }

    /**
     * 添加实现类
     *
     * @param baseReceiverServiceImpl 继承baseReceiveServiceImp的实现类
     */
    public void addReceiverImpl(BaseReceiverServiceImpl baseReceiverServiceImpl) {
        if (baseReceiverServiceImpl == null) {
            return;
        }
        concurrentHashMap.put(baseReceiverServiceImpl.action(), baseReceiverServiceImpl);
    }

    /**
     * 获取实现类
     *
     * @param action action
     * @return 继承BaseReceiverServiceImpl的实现类
     */
    public BaseReceiverServiceImpl getReceiverImpl(String action) {
        if (StringUtils.isBlank(action)) {
            return null;
        }
        return concurrentHashMap.get(action);
    }

    /**
     * 处理消息
     *
     * @param context context
     * @param action  action
     * @param intent  intent
     */

    public void onReceive(Context context, String action, Intent intent) {
        BaseReceiverServiceImpl receiverService = getReceiverImpl(action);
        if (receiverService != null) {
            receiverService.onReceive(context, intent);
        }
    }

    /**
     * 移除实现类
     *
     * @param baseReceiveServiceImp 继承baseReceiveServiceImp的实现类
     */
    public void removeReceiverImpl(BaseReceiverServiceImpl baseReceiveServiceImp) {
        if (baseReceiveServiceImp == null) {
            return;
        }
        concurrentHashMap.remove(baseReceiveServiceImp.action(), baseReceiveServiceImp);
    }

    /**
     * 清空所有的实现类
     */
    public void clearReceiverImpl() {
        concurrentHashMap.clear();
    }


}
