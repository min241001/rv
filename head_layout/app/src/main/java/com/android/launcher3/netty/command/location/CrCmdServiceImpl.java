package com.android.launcher3.netty.command.location;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

/**
 * 功能描述
 *
 * @author mahz
 * @since 2024/1/25 19:52
 */
public class CrCmdServiceImpl extends BaseCmdServiceImpl {
    private static final String CMD_NAME = "定位请求";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.CR;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        ToastUtils.show4Debug("我正在处理" + CMD_NAME + "的指令,内容为:" + msg);
        if (msg != null && msg.equals("1")) {
            LogUtil.d(TAG, "receiveMsg: 通过自定义定位方法定位", LogUtil.TYPE_RELEASE);
            NettyTcpClient.getInstance().getCmdService(XmxxCmdConstant.UD).receiveMsg(tcpClient, msg);
        } else {
            LogUtil.d(TAG, "receiveMsg: 通过sdk定位", LogUtil.TYPE_RELEASE);
            NettyTcpClient.getInstance().getCmdService(XmxxCmdConstant.LOCATION).receiveMsg(tcpClient, msg);

        }
    }
}
