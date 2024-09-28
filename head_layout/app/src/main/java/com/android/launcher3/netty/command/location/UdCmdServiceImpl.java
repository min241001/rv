package com.android.launcher3.netty.command.location;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.BatteryUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.android.launcher3.utils.DeviceStateSwitchUtil;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;
import com.baehug.lib.orginallocation.LocationHelper;
import com.baehug.lib.orginallocation.LocationInfoModel;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/1/26 09:15
 * @UpdateUser: jamesfeng
 */
public class UdCmdServiceImpl extends BaseCmdServiceImpl {
    private static final String CMD_NAME = "回复定位，wifi、基站或GPS";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    private Long lastLocationTime = System.currentTimeMillis() - 10000;

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.UD;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        sendMsg("");
    }

    @Override
    public Boolean sendMsg(Object obj) {
        // 限流
        if (System.currentTimeMillis() - lastLocationTime < 10000){
            return false;
        }
        lastLocationTime = System.currentTimeMillis();
        ThreadPoolUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (obj instanceof String) {
                        LocationInfoModel locationInfoModel = LocationHelper.getInstance().getLocationInfo(App.getInstance(), true, false,true);
                        String deviceState = DeviceStateSwitchUtil.getHexString(DeviceStateSwitchUtil.DeviceStateConstant.UNKNOWN, DeviceStateSwitchUtil.DeviceStateConstant.UNKNOWN);
                        String response = locationInfoModel.formatToTcpAnswerInfo(BatteryUtil.getInstance().getBattery(), 0, PhoneSIMCardUtil.getInstance().getCellularSignalStrength(), deviceState);
                        response = formatSendMsgContent(response);
                        LogUtil.d(TAG + "收到获取定位下发的指令，并且转发回复给netty服务器，内容为：" + response.toString(), LogUtil.TYPE_RELEASE);
                        sendStrMsg(response);
                    }
                }catch (Exception e){
                    LogUtil.e(TAG, "sendMsg: " + e.getMessage(), LogUtil.TYPE_RELEASE);
                }
            }
        });
        return true;
    }
}


