package com.android.launcher3.netty.command.power;

import android.provider.Settings;

import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.bean.VersionNoBean;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

/**
 * 电量查询的指令
 *
 * @author mahz
 * @since 2024/1/24 10:49
 */
public class PowerCmdServiceImpl extends BaseCmdServiceImpl<VersionNoBean> {
    private static final String CMD_NAME = "电量查询";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";
    @Override
    public String getCommandType() {
        return XmxxCmdConstant.POWER;
    }
    private Long lastPowerTime = System.currentTimeMillis() - 5000L;
    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        if (System.currentTimeMillis() - lastPowerTime < 5000L){
            return;
        }

        // ToastUtils.show4Debug(App.getInstance(), "我正在处理" + CMD_NAME + "的指令,内容为:" + msg);

        // 回复的指令模版：[XM*334588000000156*0004*FIND] CmdContentConstant
        int battery = 0;
        try {
            battery = AppLocalData.getInstance().getBattery();
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String response = formatSendMsgContent(battery + "");
        LogUtil.d(TAG + "回复netty服务器的内容：" + response, LogUtil.TYPE_RELEASE);
        tcpClient.sendMsg(response);

    }
}
