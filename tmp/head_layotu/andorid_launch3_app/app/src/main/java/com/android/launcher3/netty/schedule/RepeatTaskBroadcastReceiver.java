package com.android.launcher3.netty.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.launcher3.App;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.dialog.ShutdownDialog;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.android.launcher3.utils.AppSuperviseManager;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.CmdService;

import java.util.Map;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/7 16:38
 * @UpdateUser: jamesfeng
 */
public class RepeatTaskBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "RepeatTaskBroadcastReceiver";

    private ShutdownDialog dialog = ShutdownDialog.Companion.init(App.getInstance());
    private static Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: " + intent.getAction());
        String action = intent.getAction();
        switch (action) {
            case XmxxCmdConstant.MOD_LOCATION_FRQ:
                CmdService cmdService = NettyTcpClient.getInstance().getCmdService(XmxxCmdConstant.UD);
                asyncExecutorService(cmdService);
                Map<String, Long> localRepeatTask = RepeatTaskHelper.getInstance().getLocalRepeatTask(AppLocalData.getInstance().getWatchId());
                for (String key : localRepeatTask.keySet()) {
                    if (XmxxCmdConstant.MOD_LOCATION_FRQ.equals(key)){
                        RepeatTaskHelper.getInstance().addLocationTask(localRepeatTask.get(key));
                        break;
                    }
                }
                break;
            case XmxxCmdConstant.CHECK_NETTY_STATE:
                NettyManager.checkNettyState();
                RepeatTaskHelper.getInstance().addCheckNettyTask((4*60*1000L));
                break;
            case XmxxCmdConstant.MOD_POWER_OFF:
                handler.post(() -> dialog.startPregress());
                break;
            default:
                if (action.contains(XmxxCmdConstant.CLASS_BAN_START)) {
                    LogUtil.i(TAG, "onReceive: action " + action + " isForbiddenInClass "
                            + LauncherAppManager.isForbiddenInClass(App.getInstance(), ""), LogUtil.TYPE_RELEASE);
                    RepeatTaskHelper.getInstance().removeTaskMapByKey(action);
                    // 上课禁用监听定时回调
                    if (LauncherAppManager.isForbiddenInClass(App.getInstance(), "")) {
                        AppSuperviseManager.getInstance().launchClassHome();
                    }
                }
                break;
        }
    }

    protected void asyncExecutorService(CmdService cmdService) {
        if (cmdService == null) {
            return;
        }
        ThreadPoolUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                cmdService.receiveMsg(NettyTcpClient.getInstance(), "");
            }
        });
    }
}
