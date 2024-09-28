package com.android.launcher3.netty.command.location;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.android.launcher3.netty.schedule.RepeatTaskHelper;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

/**
 * 设置定位监测频率
 *
 * @author mahz
 * @since 2024/1/25 19:56
 */
public class ModLocationFrqCmdServiceImpl extends BaseCmdServiceImpl {
    private static final String CMD_NAME = "设置定位监测频率";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.MOD_LOCATION_FRQ;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        if (StringUtils.isNotBlank(msg)) {
            try {
                Long period = Long.parseLong(msg) * 1000;
                RepeatTaskHelper.getInstance().addCronTask(XmxxCmdConstant.MOD_LOCATION_FRQ, period);
                tcpClient.sendMsg(formatSendMsgContent(msg));
            } catch (Exception exception) {
                LogUtil.e(TAG, "receiveMsg: ", exception, LogUtil.TYPE_RELEASE);
            }
        }
        // 回复的指令模版：[XM*334588000000156*0004*FIND] CmdContentConstant
        /*String response = formatSendMsgContent(StringUtils.randomNum(4));
        LogUtil.d(TAG + "回复netty服务器的内容：" + response);
        tcpClient.sendMsg(response);*/
    }
}
