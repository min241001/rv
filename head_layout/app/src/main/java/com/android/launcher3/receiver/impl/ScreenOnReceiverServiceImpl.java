package com.android.launcher3.receiver.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.launcher3.App;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.mode.ProcessMode;
import com.android.launcher3.moudle.launcher.util.WatchAccountUtils;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.android.launcher3.receiver.core.BaseReceiverServiceImpl;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.CmdService;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/5 10:53
 * @UpdateUser: jamesfeng
 */
public class ScreenOnReceiverServiceImpl extends BaseReceiverServiceImpl {
    private static final String TAG = "ScreenOnReceiverService";

    /**
     * 暗屏10秒后
     */
    private final Long SCREEN_ON_ACTION_TIME = 1000 * 2L;

    private ProcessMode processMode = new ProcessMode(CommonApp.getInstance());

    private Long lastSendDelayTime = System.currentTimeMillis() - SCREEN_ON_ACTION_TIME;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 亮屏处理，亮屏5秒后，可以认为当前处于活跃状态
            Log.d(TAG, "handleMessage: 接收到亮屏消息");
            PowerManager powerManager = (PowerManager) App.getInstance().getSystemService(Context.POWER_SERVICE);
            if (powerManager != null && powerManager.isScreenOn()) {
                Log.d(TAG, "handleMessage: 执行亮屏操作");
                if (NettyManager.nettyParameter.getHeatBeatTime() < NettyManager.SCREEN_ON_HB_TIME) {
                    NettyManager.nettyParameter.setHeatBeatTime(NettyManager.SCREEN_ON_HB_TIME);
                    if (!AppLocalData.getInstance().getPower()){
                        NettyManager.forceResetAndConnect();
                    }
                }
            }
        }
    };

    @Override
    public String action() {
        return Intent.ACTION_SCREEN_ON;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        processMode.cancelTask();
        Log.d(TAG, "onReceive: 这个是来自实现类的内容" + action());
        WatchAccountUtils.getInstance().checkAndGetAcctId();
        NettyManager.checkNettyState();
        // 2 秒内的重复数据不处理
        if ((System.currentTimeMillis() - lastReceiveTime) > 20000) {
            // 亮屏状态下心跳时间为300秒
            handler.sendEmptyMessageDelayed(1, SCREEN_ON_ACTION_TIME);
            lastSendDelayTime = System.currentTimeMillis();
        }
        // 同步时间
        CmdService cmdService = NettyTcpClient.getInstance().getCmdService(XmxxCmdConstant.TIME);
        if (cmdService != null) {
            cmdService.sendMsg("");
        }
    }
}
