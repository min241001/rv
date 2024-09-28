package com.android.launcher3.netty.command.sms;

import androidx.lifecycle.Observer;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.LogUtil;
import com.baehug.lib.ipc.service.sms.SmsServiceIpcService;
import com.baehug.lib.ipc.util.IpcHelper;
import com.baehug.lib.ipc.util.TaskService;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;
import com.baehug.lib.nettyclient.constant.CmdConstant;

public class DelAllSmsCmdServiceImpl extends BaseCmdServiceImpl {

    public void ipcListenerInit() {
        super.ipcListenerInit();
        SmsServiceIpcService.c2sListenDelAllSmsList(new Observer() {
            @Override
            public void onChanged(Object o) {
                sendMsg("");
            }
        });

    }

    @Override
    public String getCommandType() {
        return CmdConstant.DEL_ALL_SMS;
    }

    public Boolean sendMsg(Object obj) {
        return NettyTcpClient.getInstance().sendMsg();
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        System.out.println("DelAllSmsServiceImpl:" + msg);
        LogUtil.i("DelAllSmsServiceImpl:" + msg, LogUtil.TYPE_RELEASE);
        // 1 发送给处理方
        IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(), "com.baehug.sms", "android.intent.action.smsService", new TaskService() {
            @Override
            public void myTask() {
                SmsServiceIpcService.s2cDelAllSmsList();
            }
        });
    }
}
