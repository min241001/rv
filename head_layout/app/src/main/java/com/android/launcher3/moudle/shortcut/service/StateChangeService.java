package com.android.launcher3.moudle.shortcut.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.android.launcher3.moudle.launcher.util.VersionUtils;
import com.android.launcher3.moudle.launcher.util.WatchAccountUtils;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.receiver.StateChangeBroadcastReceiver;

import java.util.List;

/**
 * @Author: shensl
 * @Description：实现对网络、WIFI等的监听
 * @CreateDate：2024/1/11 11:02
 * @UpdateUser: shensl
 */
public class StateChangeService extends Service {

    private static final String TAG = StateChangeService.class.getSimpleName() + "--->>>";

    private static final int WHAT_INIT = 0x00;
    private static final long WHAT_DELAY = 5000L;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case WHAT_INIT:
                    // 初始化帐号id, 初始化成功之后，也会同步去开启netty 客户端
                    WatchAccountUtils.getInstance().checkAndGetAcctId();
                    NettyManager.checkNettyState();
                    // 设置版本号
                    VersionUtils.getXimengOSVersion();
                    break;
            }
        }
    };

    private StateChangeBroadcastReceiver networkChangeReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        // 创建并注册 CONNECTIVITY_ACTION 广播接收器
        networkChangeReceiver = new StateChangeBroadcastReceiver();
        // 注册 CONNECTIVITY_ACTION 广播接收器
        List<String> actions = networkChangeReceiver.getActions();
        if (actions != null && actions.size() > 0) {
            IntentFilter intentFilter = new IntentFilter();
            for (String action : actions) {
                intentFilter.addAction(action);
            }
            registerReceiver(networkChangeReceiver, intentFilter);
        }
        // 延迟5秒发送
        mHandler.sendEmptyMessageDelayed(WHAT_INIT, WHAT_DELAY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消注册 CONNECTIVITY_ACTION 广播接收器
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
