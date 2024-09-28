package com.android.launcher3.netty.command.inner;

import android.os.Handler;
import android.os.Looper;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.dialog.ShutdownDialog;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class PowerOffCmdServiceImpl extends BaseCmdServiceImpl {

    private ShutdownDialog dialog = ShutdownDialog.Companion.init(CommonApp.getInstance());

    private static Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.POWER_OFF;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        if (Boolean.TRUE.equals(dialog.isShow())){
            return;
        }
        // 接收指令模版：[XM*334588000000156*0004*POWER_OFF]
        LogUtil.d("收到手表发过的【远程关机】指令:" + msg, LogUtil.TYPE_RELEASE);
        handler.post(() -> dialog.startPregress());
        // 回复的指令模版：[XM*334588000000156*0004*POWER_OFF] CmdContentConstant
        String response = formatSendMsgContent("");
        LogUtil.d("回复【远程关机】的指令: " + response.toString(), LogUtil.TYPE_RELEASE);
        tcpClient.sendMsg(response);
        }
}
