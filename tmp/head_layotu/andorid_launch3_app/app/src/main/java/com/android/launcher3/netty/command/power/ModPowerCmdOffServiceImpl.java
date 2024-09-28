package com.android.launcher3.netty.command.power;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.android.launcher3.netty.schedule.RepeatTaskHelper;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

import java.util.Calendar;

/**
 * 定时关机
 *
 * @author mahz
 * @since 2024/1/25 19:24
 */
public class ModPowerCmdOffServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "定时关机";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";
    /**
     * 任务分隔符号
     */
    private String TASK_SPLIT = ",";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.MOD_POWER_OFF;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        try {
            if (StringUtils.isNotBlank(msg)) {
                String[] strings = msg.split(TASK_SPLIT);
                if ("1".equals(strings[0])) {
                    String[] split = strings[1].split(":");

                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));   // 小时（24小时制）
                    calendar1.set(Calendar.MINUTE, Integer.parseInt(split[1]));           // 分钟
                    calendar1.set(Calendar.SECOND, 0);           // 秒

                    RepeatTaskHelper.getInstance().addCronTask(XmxxCmdConstant.MOD_POWER_OFF, calendar1.getTimeInMillis());
                    tcpClient.sendMsg(formatSendMsgContent(msg));

                } else if ("2".equals(strings[0])) {
                    RepeatTaskHelper.getInstance().cancelCronTask(XmxxCmdConstant.MOD_POWER_OFF);
                    tcpClient.sendMsg(formatSendMsgContent(msg));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
