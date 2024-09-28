package com.android.launcher3.netty.command.phl;

import androidx.lifecycle.Observer;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.constant.ServiceConstant;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.ipc.service.addessbook.AddressBookServiceIpcService;
import com.baehug.lib.ipc.service.addessbook.model.ModWlkeySwitchModel;
import com.baehug.lib.ipc.service.addessbook.model.PhlListItemModel;
import com.baehug.lib.ipc.util.IpcHelper;
import com.baehug.lib.ipc.util.TaskService;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class ModWlkeySwitchCmdServiceImpl extends BaseCmdServiceImpl<PhlListItemModel> {
    private static final String CMD_NAME = "白名单开关";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    public void ipcListenerInit() {
        super.ipcListenerInit();
        AddressBookServiceIpcService.c2sListenModWlkeySwitch(new Observer<ModWlkeySwitchModel>() {
            @Override
            public void onChanged(ModWlkeySwitchModel modWlkeySwitchModel) {
                try {
                    if (modWlkeySwitchModel == null) {
                        LogUtil.d(TAG + "收到电话app发送的数据有误，原因为：参数为空", LogUtil.TYPE_RELEASE);
                        return;
                    }

                    String response = null;
                    response = formatSendMsgContent(modelToMsgContent(modWlkeySwitchModel));
                    LogUtil.d(TAG + "收到电话app下发的指令，并且转发回复给netty服务器，内容为：" + response.toString(), LogUtil.TYPE_RELEASE);
                    sendStrMsg(response);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG + "收到电话app下发的指令，并且转发回复给netty服务器，格式化数据有误：" + modWlkeySwitchModel, LogUtil.TYPE_RELEASE);
                }
            }
        });
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        if (msg == null || msg.trim().length() == 0) {
            LogUtil.e(TAG + "收到netty服务器下发的指令错误：msg为空", LogUtil.TYPE_RELEASE);
            return;
        }
        if (!(msg.equals("1") || msg.equals("2") || msg.equals("3"))) {
            LogUtil.e(TAG + "netty服务器下发的指令错误：参数错误，参数类型错误，正确的参数为：【1 开启、2 关闭、3、30秒后自动接听】", LogUtil.TYPE_RELEASE);
            return;
        }

        // 测试专用
        // IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(),  "com.baehug.ipc", "com.baehug.ipc.TestPhoneListService", new TaskService() {
        IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(),  ServiceConstant.DIALER_PACKAGE, ServiceConstant.DIALER_SERVICE, new TaskService() {
            @Override
            public void myTask() {
                LogUtil.d(TAG + "发送指令给电话app，内容为：" + msg, LogUtil.TYPE_RELEASE);
                ModWlkeySwitchModel modWlkeySwitchModel = new ModWlkeySwitchModel(Integer.parseInt(msg));
                AddressBookServiceIpcService.s2cModWlkeySwitch(modWlkeySwitchModel);
            }
        });
    }

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.MOD_WLKEY_SWITCH;
    }

    public Boolean sendMsg(Object obj) {
        return NettyTcpClient.getInstance().sendMsg();
    }
}
