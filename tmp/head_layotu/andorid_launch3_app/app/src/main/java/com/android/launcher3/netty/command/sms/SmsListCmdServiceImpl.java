package com.android.launcher3.netty.command.sms;

import androidx.lifecycle.Observer;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.netty.constant.ServiceConstant;
import com.baehug.lib.ipc.service.sms.SmsServiceIpcService;
import com.baehug.lib.ipc.service.sms.model.SmsIdModel;
import com.baehug.lib.ipc.service.sms.model.SmsListModel;
import com.baehug.lib.ipc.util.IpcHelper;
import com.baehug.lib.ipc.util.TaskService;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;
import com.baehug.lib.nettyclient.constant.CmdConstant;

public class SmsListCmdServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "查询短信列表";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";
    @Override
    public void ipcListenerInit() {
        super.ipcListenerInit();
        SmsServiceIpcService.c2sListenSmsList(new Observer<SmsListModel>() {
            @Override
            public void onChanged(SmsListModel smsListModel) {
                try {
                    if (smsListModel == null) {
                        LogUtil.e(TAG + "收到短信app发送的数据有误，原因为：参数为空");
                        return;
                    }

                    String response = null;
                    response = formatSendMsgContent(listModelToMsgContent(smsListModel.list));
                    LogUtil.d(TAG + "收到短信app下发的指令，并且转发回复给netty服务器，内容为：" + response.toString(), LogUtil.TYPE_RELEASE);
                    sendMsg(response);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG + "收到短信app下发的指令，并且转发回复给netty服务器，格式化数据有误：" + smsListModel);
                }
            }
        });
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        if (StringUtils.isBlank(msg)) {
            LogUtil.e(TAG + "收到netty服务器下发的指令错误：msg为空");
            return;
        }

        IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(), ServiceConstant.SMS_PACKAGE, ServiceConstant.SMS_SERVICE, new TaskService() {
        // IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(), "com.baehug.ipc", "com.baehug.ipc.TestSmsService", new TaskService() {
            @Override
            public void myTask() {
                LogUtil.d(TAG + "发送指令给短信app，内容为：" + msg, LogUtil.TYPE_RELEASE);
                SmsServiceIpcService.s2cGetSmsList(new SmsIdModel(msg));
            }
        });

    }

    @Override
    public String getCommandType() {
        return CmdConstant.SMS_LIST;
    }

    public Boolean sendMsg(Object obj) {
        if (obj != null && obj instanceof SmsListModel) {
            SmsListModel smsListModel = (SmsListModel) obj;
            if (smsListModel.list != null && smsListModel.list.size() > 0) {
                return NettyTcpClient.getInstance().sendMsg(formatSendMsgListModel(smsListModel.list));
            }
        }
        return false;
    }
}
