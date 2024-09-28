package com.android.launcher3.netty.command.sos;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class ModSosSmsSwitchCmdServiceImpl extends BaseCmdServiceImpl  {
    private static final String CMD_NAME = "SOS短信报警开关";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    public void ipcListenerInit() {
        super.ipcListenerInit();
        /*SmsServiceIpcService.c2sListenDelAllSmsList(new Observer() {
            @Override
            public void onChanged(Object o) {
                sendMsg("");
            }
        });
*/
    }

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.MOD_SOS_SMS_SWITCH;
    }

    public Boolean sendMsg(Object obj) {
        return NettyTcpClient.getInstance().sendMsg();
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d("收到手表发过的【SOS短信报警开关】指令: " + msg, LogUtil.TYPE_RELEASE);
        // 1 发送给处理方
        /*IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(), "com.baehug.ipc", "com.baehug.ipc.TestSmsService", new TaskService() {
            @Override
            public void myTask() {
                SmsServiceIpcService.s2cDelAllSmsList();
            }
        });*/
        // 2 接收处理方响应结果
        // ipcListenerInit 中实现

        // 如果成功的响应结果 1 开启、2 关闭
        String response = formatSendMsgContent(msg);
        LogUtil.d("回复【SOS 短信报警开关】的指令: " + response.toString(), LogUtil.TYPE_RELEASE);
        tcpClient.sendMsg(response);
    }
}
