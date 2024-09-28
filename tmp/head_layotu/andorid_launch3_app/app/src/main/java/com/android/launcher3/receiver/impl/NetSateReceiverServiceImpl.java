package com.android.launcher3.receiver.impl;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.NetworkUtils;
import com.android.launcher3.moudle.launcher.util.WatchAccountUtils;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.receiver.core.BaseReceiverServiceImpl;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/5 14:13
 * @UpdateUser: jamesfeng
 */
public class NetSateReceiverServiceImpl extends BaseReceiverServiceImpl {
    private static final String TAG = "NetSateReceiverServiceI";

    @Override
    public String action() {
        return ConnectivityManager.CONNECTIVITY_ACTION;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 2 秒内的重复数据不处理
        if ((System.currentTimeMillis() - lastReceiveTime) > 2000) {
            // 有网情况下检查账号及netty 状态
            if (NetworkUtils.isNetworkAvailable(App.getInstance())) {
                WatchAccountUtils.getInstance().checkAndGetAcctId();
                NettyManager.checkNettyState();
            }
        }
    }
}
