package com.android.launcher3.netty.command.power;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时开机
 *
 * @author mahz
 * @since 2024/1/25 12:42
 */
public class ModPowerCmdOnServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "定时开机";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";
    /**
     * 任务分隔符号
     */
    private String TASK_SPLIT = ",";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.MOD_POWER_ON;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);

        if (StringUtils.isNotBlank(msg)) {
            String[] strings = msg.split(TASK_SPLIT);
            if ("1".equals(strings[0])){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date date = null;
                try {
                    date = simpleDateFormat.parse(strings[1]);
//                    RepeatTaskHelper.getInstance().addCronTask(XmxxCmdConstant.MOD_POWER_ON, date.getTime());
                    String response = formatSendMsgContent(msg);
                    LogUtil.d(TAG + "回复netty服务器的内容：" + response, LogUtil.TYPE_RELEASE);
                    tcpClient.sendMsg(response);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if ("2".equals(strings[0])){
//                RepeatTaskHelper.getInstance().cancelCronTask(XmxxCmdConstant.MOD_POWER_ON);
                String response = formatSendMsgContent(msg);
                LogUtil.d(TAG + "回复netty服务器的内容：" + response, LogUtil.TYPE_RELEASE);
                tcpClient.sendMsg(response);
            }
        }

    }
}
