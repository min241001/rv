package com.android.launcher3.netty.command.course;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class QuerySilenceTimServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "查询上课禁用时间段设置";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.SILENCE_TIME;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        Object param = SharedPreferencesUtils.getParam(App.getInstance(), SharedPreferencesUtils.getOverSilenceTimeKey(), "");
        sendMsg(param);
    }

    @Override
    public Boolean sendMsg(Object obj) {
        String response = null;
        try {
            response = formatSendMsgContent(String.valueOf(obj));
            LogUtil.d(TAG + "收到获取手表应用列表的指令，并且转发回复给netty服务器，内容为：" + response, LogUtil.TYPE_RELEASE);
            sendStrMsg(response);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
