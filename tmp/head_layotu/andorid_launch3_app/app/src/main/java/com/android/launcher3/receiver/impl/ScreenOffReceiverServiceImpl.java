package com.android.launcher3.receiver.impl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;

import androidx.annotation.NonNull;

import com.android.launcher3.App;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.mode.ProcessMode;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.receiver.core.BaseReceiverServiceImpl;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/5 14:24
 * @UpdateUser: jamesfeng
 */
public class ScreenOffReceiverServiceImpl extends BaseReceiverServiceImpl {
    private static final String TAG = "ScreenOffReceiverService";
    /**
     * 暗屏5分钟后
     */
    private final Long SCREEN_OFF_ACTION_TIME = 1000 * 300L;

    private ProcessMode processMode = new ProcessMode(CommonApp.getInstance());

    private Long lastSendDelayTime = System.currentTimeMillis() - SCREEN_OFF_ACTION_TIME;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 暗屏处理，暗屏超过5分钟，可以认为用户
            LogUtil.d(TAG, "handleMessage: 接收到暗屏消息", LogUtil.TYPE_RELEASE);
            CommonApp context = App.getInstance();
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null && !powerManager.isScreenOn()) {
                LogUtil.d(TAG, "handleMessage: 执行暗屏操作", LogUtil.TYPE_RELEASE);
                if (NettyManager.nettyParameter.getHeatBeatTime() < NettyManager.SCREEN_OFF_HB_TIME) {
                    NettyManager.nettyParameter.setHeatBeatTime(NettyManager.SCREEN_OFF_HB_TIME);
                    NettyManager.forceResetAndConnect();
                }
            }
            boolean restart = (boolean) SharedPreferencesUtils.getParam(
                    context, "KEY_RESTART_LAUNCHER", false);
            // 有补丁包重启launcher应用
            if (restart) {
                LogUtil.d(TAG, "handleMessage: restart launcher", LogUtil.TYPE_RELEASE);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                SharedPreferencesUtils.setParam(context, "KEY_RESTART_LAUNCHER", false);
            }
        }
    };

    @Override
    public String action() {
        return Intent.ACTION_SCREEN_OFF;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        processMode.processTasks();
        // 60 秒内只发送一次暗屏操作，避免反复亮屏和暗屏
        if ((System.currentTimeMillis() - lastSendDelayTime) > SCREEN_OFF_ACTION_TIME) {
            LogUtil.d(TAG, "onReceive: 发送暗屏", LogUtil.TYPE_RELEASE);
            handler.sendEmptyMessageDelayed(1, SCREEN_OFF_ACTION_TIME);
            lastSendDelayTime = System.currentTimeMillis();
        }
    }
}
