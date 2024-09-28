package com.android.launcher3.netty.command.wechat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.android.launcher3.App;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.NotificationUtils;
import com.android.launcher3.moudle.light.LightActivity;
import com.android.launcher3.moudle.record.base.RecordConstant;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

import java.util.List;

/**
 * 腾讯微聊消息通知现类
 */
public class msgCmdServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "腾讯微聊消息通知";

    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.TX_IM_PUSH;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d("腾讯微聊消息通知: 收到netty服务器下发的指令 =======" + msg, LogUtil.TYPE_RELEASE);

        // 有新消息时关闭手电筒应用
        ActivityManager activityManager = (ActivityManager) App.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);

        if (runningTasks != null && !runningTasks.isEmpty()) {
            ActivityManager.RunningTaskInfo topTask = runningTasks.get(0);
            String className = topTask.topActivity.getClassName();
            if (className.equals("com.android.launcher3.moudle.light.LightActivity") && LightActivity.instance != null) {
                LightActivity.instance.finish();
            }
        }

        if (msg.equals("1") || msg.equals("2")) {
            Intent intent = new Intent(RecordConstant.ACTION_WEILIAO_STATE_CHANGED);
            App.getInstance().sendBroadcast(intent);
        }

        //微聊服务是否在运行
        boolean appRunning = AppUtils.isServiceRunning(App.getInstance(), "com.baehug.videochat.service.MessageService");
        LogUtil.d("腾讯微聊消息通知: 微聊服务是否在运行 =======" + appRunning, LogUtil.TYPE_RELEASE);
        if (appRunning) {
            return;
        }

        if (msg.equals("1") || msg.equals("2")) {
            //语音通话消息 和 视频通话消息
            Intent intent1 = new Intent();
            intent1.setAction("android.intent.action.START_SERVICE");
            intent1.setComponent(new ComponentName("com.baehug.videochat", "com.baehug.videochat.service.MessageService"));
            // 启动服务
            App.getInstance().startService(intent1);

        } else if (msg.equals("3")) {
            //普通文本消息
            NotificationUtils.INSTANCE.sendNotification(App.getInstance(),"微聊",
                    com.android.launcher3.common.R.drawable.icon_wechat,"您有新的微聊消息",1);
        }
    }
}
