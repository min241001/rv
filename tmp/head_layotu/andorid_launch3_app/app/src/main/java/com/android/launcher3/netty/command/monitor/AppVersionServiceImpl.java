package com.android.launcher3.netty.command.monitor;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/7/23 16:40
 * @UpdateUser: jamesfeng
 */
public class AppVersionServiceImpl extends BaseCmdServiceImpl {
    private static final String CMD_NAME = "获取指定应用的版本信息";

    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.APP_VERSION;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        Log.d(TAG, "receiveMsg: 收到netty服务器下发的指令 =======获取指定应用的版本信息====" + msg);
        try {
            if (StringUtils.isBlank(msg)) {
                LogUtil.e(TAG + "收到netty服务器下发的指令错误：msg为空");
                return;
            }
            sendMsg(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean sendMsg(Object obj) {
        try {
            String packageName = String.valueOf(obj);
            PackageInfo pInfo = App.getInstance().getPackageManager().getPackageInfo(packageName, 0);
            String versionName = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            String response = Build.MODEL + "," + packageName + "," + versionName + "," + versionCode;
            response = formatSendMsgContent(response);
            LogUtil.d(TAG + "收到获取指定app的指令，并且转发回复给netty服务器，内容为：" + response, LogUtil.TYPE_RELEASE);
            sendStrMsg(response);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(e.toString(), LogUtil.DEBUG);
        }
        return false;
    }
}
