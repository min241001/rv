package com.android.launcher3.netty.command.sms;

import androidx.lifecycle.Observer;

import com.android.launcher3.App;
import com.baehug.lib.ipc.service.sms.SmsServiceIpcService;
import com.baehug.lib.ipc.service.sms.model.SmsIdListModel;
import com.baehug.lib.ipc.util.IpcHelper;
import com.baehug.lib.ipc.util.TaskService;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;
import com.baehug.lib.nettyclient.constant.CmdConstant;

import io.netty.util.internal.StringUtil;

public class DelSmsCmdServiceImpl extends BaseCmdServiceImpl {
    @Override
    public void ipcListenerInit() {
        super.ipcListenerInit();
        SmsServiceIpcService.c2sListenDelSmsList(new Observer<SmsIdListModel>() {
            @Override
            public void onChanged(SmsIdListModel smsIdListModel) {
                sendMsg(smsIdListModel);
            }
        });

    }

    @Override
    public String getCommandType() {
        return CmdConstant.DEL_SMS;
    }

    public Boolean sendMsg(Object obj) {
        if (obj != null && obj instanceof SmsIdListModel) {
            SmsIdListModel listModel = (SmsIdListModel) obj;
            if (!StringUtil.isNullOrEmpty(listModel.smsIds)) {
                return NettyTcpClient.getInstance().sendMsg(formatSendMsgContent(listModel.smsIds));
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        // 1 发送给处理方
        IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(), "com.baehug.sms", "android.intent.action.smsService", new TaskService() {
            @Override
            public void myTask() {
                SmsServiceIpcService.s2cDelSmsList(new SmsIdListModel(msg));
            }
        });
        // 2 接收处理方响应结果
        // ipcListenerInit 中实现
    }


}
