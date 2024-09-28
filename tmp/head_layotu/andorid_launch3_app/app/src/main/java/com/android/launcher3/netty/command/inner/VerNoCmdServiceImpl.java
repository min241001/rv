package com.android.launcher3.netty.command.inner;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.bean.VersionNoBean;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

/**
 * 版本查询的指令
 */
public class VerNoCmdServiceImpl extends BaseCmdServiceImpl<VersionNoBean> {

    private static final String CMD_NAME = "版本查询";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.VER_NO;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "对外版本号：" + AppUtils.getAppVersion(App.getInstance())
                        + " launch版本号：" + AppUtils.getAppVersionCode(App.getInstance())
                        + " setting版本号：" + AppUtils.getSettingVersionCode(App.getInstance())
                        + " 屏幕分辨率：" + AppUtils.getScreen(App.getInstance())
                        + " 系统版本：" + AppUtils.getAndroidVersion()
                        + " RAM 整数 单位MB：运存" + AppUtils.getTotalRAM(App.getInstance()) + "MB"
                        + " ROM 整数 单位MB：内存" + AppUtils.getTotalRoM() + "MB"
                        + " mac地址：" + AppUtils.getMacAddress(App.getInstance())
                        + " 固件信息：" + AppUtils.getAppVersion(App.getInstance())
                , LogUtil.TYPE_RELEASE);
        VersionNoBean versionNoBean = new VersionNoBean();
        versionNoBean.setS1OutVersion(AppUtils.getAppVersion(App.getInstance()));
        versionNoBean.setS2LauncherVersion(AppUtils.getAppVersionCode(App.getInstance()));
        versionNoBean.setS3SettingVersion(AppUtils.getSettingVersionCode(App.getInstance()));
        versionNoBean.setS4ScreenResolution(AppUtils.getScreen(App.getInstance()));
        versionNoBean.setS5SystemVersion("android " + AppUtils.getAndroidVersion());
        versionNoBean.setS6Ram(AppUtils.getTotalRAM(App.getInstance()));
        versionNoBean.setS7Rom(AppUtils.getTotalRoM());
        versionNoBean.setS8Mac(AppUtils.getMacAddress(App.getInstance()));
        versionNoBean.setS9firmware(AppUtils.getAppVersion(App.getInstance()));
        String response = formatSendMsgModel(versionNoBean);
        LogUtil.d(TAG + "回复netty服务器的内容：" + response, LogUtil.TYPE_RELEASE);
        tcpClient.sendMsg(response);
    }
}
