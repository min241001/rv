package com.android.launcher3.netty.command.sos;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class SosListCmdServiceImpl extends BaseCmdServiceImpl {
    public void ipcListenerInit() {
        super.ipcListenerInit();
    }

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.SOS_LIST;
    }

    public Boolean sendMsg(Object obj) {
        return NettyTcpClient.getInstance().sendMsg();
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d("收到手表发过的【SOS号码查询】指令: " + msg, LogUtil.TYPE_RELEASE);
        /*Settings.System.putString(App.getInstance().getContentResolver(), SettingsConstant.WATCH_SOS,msg);
        String response = formatSendMsgContent(msg);
        LogUtil.d("回复【SOS 号码查询】的指令: " + response.toString());
        tcpClient.sendMsg(response);*/
    }
}
