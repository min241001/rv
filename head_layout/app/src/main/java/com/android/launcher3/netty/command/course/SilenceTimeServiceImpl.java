package com.android.launcher3.netty.command.course;

import com.android.launcher3.App;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.android.launcher3.utils.AppSuperviseManager;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;


public class SilenceTimeServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "上课禁用时间段设置";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.OVR_SILENCE_TIME;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        SharedPreferencesUtils.setParam(App.getInstance(), SharedPreferencesUtils.getOverSilenceTimeKey(), msg);
        AppSuperviseManager.getInstance().addClassBanTask();
        //是否在上课禁用时间段
        if (LauncherAppManager.isForbiddenInClass(App.getInstance(), "")) {
            AppLocalData.getInstance().setIsInClassTime(0);
            AppSuperviseManager.getInstance().launchClassHome();
        } else {
            AppLocalData.getInstance().setIsInClassTime(1);
        }
        sendMsg("");
    }

    @Override
    public Boolean sendMsg(Object obj) {
        String response = null;
        try {
            response = formatSendMsgContent(String.valueOf(obj));
            LogUtil.d(TAG + "收到上课禁用时间段设置的指令，并且转发回复给netty服务器，内容为：" + response, LogUtil.TYPE_RELEASE);
            sendStrMsg(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
