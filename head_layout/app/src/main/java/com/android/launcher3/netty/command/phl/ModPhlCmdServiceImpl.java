package com.android.launcher3.netty.command.phl;

import androidx.lifecycle.Observer;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.netty.constant.ServiceConstant;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.ipc.service.addessbook.AddressBookServiceIpcService;
import com.baehug.lib.ipc.service.addessbook.model.PhlIdModel;
import com.baehug.lib.ipc.service.addessbook.model.PhlListItemModel;
import com.baehug.lib.ipc.util.IpcHelper;
import com.baehug.lib.ipc.util.TaskService;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class ModPhlCmdServiceImpl extends BaseCmdServiceImpl<PhlListItemModel> {

    private static final String CMD_NAME = "修改白名单";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    public void ipcListenerInit() {
        super.ipcListenerInit();

        // 监听通讯录app发送过来的数据
        AddressBookServiceIpcService.c2sListenModPHL(new Observer<PhlIdModel>() {
            @Override
            public void onChanged(PhlIdModel phlIdModel) {
                try {
                    if (phlIdModel == null) {
                        LogUtil.e(TAG + "收到通讯录app发送的数据有误，原因为：参数为空", LogUtil.TYPE_RELEASE);
                        return;
                    }

                    String response = null;
                    response = formatSendMsgContent(modelToMsgContent(phlIdModel));
                    LogUtil.d(TAG + "收到通讯录app下发的指令，并且转发回复给netty服务器，内容为：" + response.toString(), LogUtil.TYPE_RELEASE);
                    sendStrMsg(response);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG + "收到通讯录app下发的指令，并且转发回复给netty服务器，格式化数据有误：" + phlIdModel, LogUtil.TYPE_RELEASE);
                }
            }
        });
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);

        try {
            if (StringUtils.isBlank(msg)) {
                LogUtil.e(TAG + "收到netty服务器下发的指令错误：msg为空", LogUtil.TYPE_RELEASE);
                return;
            }

            PhlListItemModel phlListItemModel = msgContentToModel(msg, PhlListItemModel.class, false);
            LogUtil.d(TAG + "格式化之后的数据为：" + phlListItemModel, LogUtil.TYPE_RELEASE);

            // 1 发送给处理方
            // IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(),  "com.baehug.ipc", "com.baehug.ipc.TestPhoneListService", new TaskService() {
            IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(), ServiceConstant.CONTACT_PACKAGE, ServiceConstant.CONTACT_SERVICE, new TaskService() {
                @Override
                public void myTask() {
                    LogUtil.d(TAG + "发送指令给通讯录app，内容为：" + msg, LogUtil.TYPE_RELEASE);
                    AddressBookServiceIpcService.s2cModPHL(phlListItemModel);
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG + "格式化数据有误：" + msg, LogUtil.TYPE_RELEASE);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.MOD_PHL;
    }

    public Boolean sendMsg(Object obj) {
        return NettyTcpClient.getInstance().sendMsg();
    }
}