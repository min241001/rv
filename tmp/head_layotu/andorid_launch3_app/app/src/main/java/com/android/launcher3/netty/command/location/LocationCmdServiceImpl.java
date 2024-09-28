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
import com.baehug.lib.sdklocation.BdLocationHelper;
import com.baehug.lib.sdklocation.XmBdLocationListener;
import com.baidu.location.BDLocation;


/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/1/30 17:18
 * @UpdateUser: jamesfeng
 */
public class LocationCmdServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "sdk定位";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";
    private Long lastLocationTime = System.currentTimeMillis() - 10000;

    public LocationCmdServiceImpl() {
        BdLocationHelper.getInstance().init(App.getInstance());
        BdLocationHelper.getInstance().addLocationListener(new XmBdLocationListener() {
            @Override
            public void locationResult(BDLocation bdLocation) {
                BdLocationModel locationModel = new BdLocationModel(bdLocation);
                locationModel.setS14Battery(BatteryUtil.getInstance().getBattery());
                locationModel.setS15Step(0);
                locationModel.setS16Rssi(PhoneSIMCardUtil.getInstance().getCellularSignalStrength());
                String deviceState = DeviceStateSwitchUtil.getHexString(DeviceStateSwitchUtil.DeviceStateConstant.UNKNOWN, DeviceStateSwitchUtil.DeviceStateConstant.UNKNOWN);
                locationModel.setS17DeviceState(deviceState);
                sendMsg(locationModel);
            }
        });
    }

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.LOCATION;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG, "receiveMsg: 收到定时定位传过来的定位命令进行定位操作========", LogUtil.TYPE_RELEASE);
        // 限流
        if (System.currentTimeMillis() - lastLocationTime < 10000){
            return;
        }
        lastLocationTime = System.currentTimeMillis();
        ThreadPoolUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BdLocationHelper.getInstance().startSingleLocation(true);
                } catch (Exception exception) {
                    System.out.println(exception.toString());
                }
            }
        });


        LogUtil.d(TAG, "receiveMsg: 当前线程" + Thread.currentThread(), LogUtil.TYPE_DEBUG);

    }

    @Override
    public Boolean sendMsg(Object obj) {
        LogUtil.d(TAG, "receiveMsg: 当前线程" + Thread.currentThread(), LogUtil.TYPE_DEBUG);

        if (obj instanceof BdLocationModel) {
            String response = null;
            try {
                String msg = modelToMsgContent(obj);
                response = formatSendMsgContent(msg);
                sendStrMsg(response);
                LogUtil.d(TAG + "收到获取定位下发的指令，并且转发回复给netty服务器，内容为：" + response.toString(), LogUtil.TYPE_RELEASE);
            } catch (IllegalAccessException e) {
                LogUtil.d(TAG, "sendMsg: " + e, LogUtil.TYPE_RELEASE);
            }
            return false;
        }
        return false;
    }
}
