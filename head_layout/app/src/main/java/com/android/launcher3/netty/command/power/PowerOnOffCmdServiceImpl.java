package com.android.launcher3.netty.command.power;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

/**
 * 获取设备定时开关机的状态
 *
 * @author mahz
 * @since 2024/1/25 19:26
 */
public class PowerOnOffCmdServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "获取设备定时开关机的状态";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";
    @Override
    public String getCommandType() {
        return XmxxCmdConstant.POWER_ON_OFF;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);



        /*LogUtil.d(TAG + "回复netty服务器的内容：" + response);
        tcpClient.sendMsg(response);*/

    }
}
