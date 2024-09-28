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
import com.baehug.lib.ipc.service.addessbook.model.PhlListModel;
import com.baehug.lib.ipc.util.IpcHelper;
import com.baehug.lib.ipc.util.TaskService;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class PhlListCmdServiceImpl extends BaseCmdServiceImpl<PhlListItemModel> {
    private static final String CMD_NAME = "查询白名单";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    public void ipcListenerInit() {
        super.ipcListenerInit();

        // 收到客户端，就是通讯录app发过来的通讯录列表数据，在这里去发送列表数据给到服务端的netty
        AddressBookServiceIpcService.c2sListenPHLList(new Observer<PhlListModel>() {
            @Override
            public void onChanged(PhlListModel phlListModel) {
                try {
                    if (phlListModel == null) {
                        LogUtil.e(TAG + "收到通讯录app发送的数据有误，原因为：参数为空", LogUtil.TYPE_RELEASE);
                        return;
                    }

                    String response = null;
                    response = formatSendMsgContent(listModelToMsgContent(phlListModel.list));
                    LogUtil.d(TAG + "收到通讯录app下发的指令，并且转发回复给netty服务器，内容为：" + response.toString(), LogUtil.TYPE_RELEASE);
                    sendStrMsg(response);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG + "收到通讯录app下发的指令，并且转发回复给netty服务器，格式化数据有误：" + phlListModel, LogUtil.TYPE_RELEASE);
                }
            }
        });
    }

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.PHL_LIST;
    }

    public Boolean sendMsg(Object obj) {
        return NettyTcpClient.getInstance().sendMsg();
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        // [XM*1815164228*0013*PHL_LIST,1608506547]
        // 取id【1608506547】之后的所有数据，用list返回
        try {
            if (StringUtils.isBlank(msg)) {
                LogUtil.e(TAG + "收到netty服务器下发的指令错误：msg为空", LogUtil.TYPE_RELEASE);
                return;
            }

            // 1 收到服务端netty发过来查询通讯录的指令，
            //   转发送给处理方，即通讯录app
            // IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(),  "com.baehug.ipc", "com.baehug.ipc.TestPhoneListService", new TaskService() {
            IpcHelper.getInstance().checkServiceAndAutoStartServiceBeforeSendMsg(App.getInstance(), ServiceConstant.CONTACT_PACKAGE, ServiceConstant.CONTACT_SERVICE, new TaskService() {
                @Override
                public void myTask() {
                    LogUtil.d(TAG + "发送指令给通讯录app，内容为：" + msg, LogUtil.TYPE_RELEASE);
                    PhlIdModel phlIdModel = new PhlIdModel(msg);
                    AddressBookServiceIpcService.s2cGetPHLList(phlIdModel);
                }
            });

            // 2 接收处理方响应结果
            // ipcListenerInit 中实现
        } catch (Exception e) {
            LogUtil.e(TAG + "格式化数据有误：" + msg, LogUtil.TYPE_RELEASE);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
