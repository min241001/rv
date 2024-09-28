package com.android.launcher3.netty.command.location;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ToastUtils;
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
public class LocationFrqCmdServiceImpl extends BaseCmdServiceImpl {
    private static final String CMD_NAME = "查询频率状态";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.LOCATION_FRQ;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        ToastUtils.show4Debug("我正在处理" + CMD_NAME + "的指令,内容为:" + msg);
        Long period = RepeatTaskHelper.getInstance().getActionTime(XmxxCmdConstant.UD);
        if (period != -1) {
            period = period / 1000;
        }
        tcpClient.sendMsg(formatSendMsgContent(period.toString()));
        // 回复的指令模版：[XM*334588000000156*0004*FIND] CmdContentConstant
        /*String response = formatSendMsgContent(StringUtils.randomNum(4));
        LogUtil.d(TAG + "回复netty服务器的内容：" + response);
        tcpClient.sendMsg(response);*/

    }
}
