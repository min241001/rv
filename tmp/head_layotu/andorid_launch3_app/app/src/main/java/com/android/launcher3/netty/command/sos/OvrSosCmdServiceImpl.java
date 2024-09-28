package com.android.launcher3.netty.command.sos;

import android.provider.Settings;

import com.android.launcher3.App;
import com.android.launcher3.common.constant.SettingsConstant;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class OvrSosCmdServiceImpl extends BaseCmdServiceImpl {
    public void ipcListenerInit() {
        super.ipcListenerInit();
    }

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.OVR_SOS;
    }

    public Boolean sendMsg(Object obj) {
        return NettyTcpClient.getInstance().sendMsg();
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d("收到手表发过的【修改sos】指令: " + msg, LogUtil.TYPE_RELEASE);
        Settings.System.putString(App.getInstance().getContentResolver(), SettingsConstant.WATCH_SOS,msg);
        String response = formatSendMsgContent(msg);
        LogUtil.d("回复【修改sos】的指令: " + response.toString(), LogUtil.TYPE_RELEASE);
        tcpClient.sendMsg(response);
    }
}
