package com.android.launcher3.netty.command.inner;

import android.os.Handler;
import android.os.Looper;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.dialog.LookWatchDialog;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;


public class FindCmdServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "找手表";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    private LookWatchDialog dialog = LookWatchDialog.Companion.init(CommonApp.getInstance());

    private static Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.FIND;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        if (Boolean.TRUE.equals(dialog.isShow())){
            return;
        }
        // 接收指令模版：[XM*334588000000156*0004*FIND]
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        handler.post(() -> dialog.startPregress());

        // 回复的指令模版：[XM*334588000000156*0004*FIND] CmdContentConstant
        String response = formatSendMsgContent("");
        LogUtil.d(TAG + "回复netty服务器的内容：" + msg, LogUtil.TYPE_RELEASE);
        tcpClient.sendMsg(response);
    }
}
