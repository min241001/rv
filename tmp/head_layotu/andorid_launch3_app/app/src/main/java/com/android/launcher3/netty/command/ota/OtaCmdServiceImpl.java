package com.android.launcher3.netty.command.ota;

import static com.android.launcher3.common.constant.SettingsConstant.UPDATE_FLAG_DETAIL;
import static com.android.launcher3.common.constant.SettingsConstant.UPDATE_FLAG_KEY;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.mode.OtaModel;
import com.android.launcher3.common.network.resp.UpgradeDetailResp;
import com.android.launcher3.common.utils.GsonUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.moudle.launcher.view.LauncherActivity;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class OtaCmdServiceImpl extends BaseCmdServiceImpl {
    private static final String CMD_NAME = "OTA升级";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";
    private final OtaModel otaModel = new OtaModel();


    @Override
    public String getCommandType() {
        return XmxxCmdConstant.OTA_LK;
    }

    @Override
    public Boolean sendMsg(Object obj) {
        return NettyTcpClient.getInstance().sendMsg(formatSendMsgContent(""));
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的OTA指令：" + msg, LogUtil.TYPE_RELEASE);
        if (msg != null) {
            //[1.0.1,709883289254494208] 指令实例
            if (msg.contains(",")) {
                checkUpgrade(msg.split(",")[0], msg.split(",")[1]);
            } else {
                checkUpgrade(msg, "");
            }
        }
    }

    private void checkUpgrade(String msg, String patch) {
        try {
            String version = BuildConfig.VERSION;
            if (isVersionUpgrade(version, msg)) {
                otaModel.upgrade();
            } else {
                if (patch != null && !patch.isEmpty()) {
                    //当前线上版本号不大于launcher版本号，进行增量更新版本号查询
                    Long patchNumber = (Long) SharedPreferencesUtils.getParam(CommonApp.getInstance(), UPDATE_FLAG_KEY, Long.parseLong("0"));
                    if (patchNumber != null) {
                        //补丁版本号，0表示无补丁
                        Long newPatch = Long.parseLong(patch);
                        if (newPatch > patchNumber) {
                            //缓存本地增量更新版本号
                            SharedPreferencesUtils.setParam(CommonApp.getInstance(), UPDATE_FLAG_KEY, newPatch);
                            //清空本地缓存增量策略
                            SharedPreferencesUtils.removeKey(CommonApp.getInstance(), UPDATE_FLAG_DETAIL);
                            //请求增量策略接口
                            otaModel.checkNewPatch(LauncherActivity.context, version, String.valueOf(newPatch));
                        } else {
                            String param = (String) SharedPreferencesUtils.getParam(CommonApp.getInstance(), UPDATE_FLAG_DETAIL, "null");
                            if (param != null && !param.isEmpty() && !param.equals("null")){
                                UpgradeDetailResp response = GsonUtil.INSTANCE.fromJson(param, UpgradeDetailResp.class);
                                otaModel.checkUpdate(LauncherActivity.context,version,response);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isVersionUpgrade(String currentVersion, String version) {
        String[] currentVersionArray = currentVersion.split("\\.");
        String[] versionArray = version.split("\\.");

        int length = Math.max(currentVersionArray.length, versionArray.length);
        for (int i = 0; i < length; i++) {
            int current = i < currentVersionArray.length && !currentVersionArray[i].isEmpty() ? Integer.parseInt(currentVersionArray[i]) : 0;
            int saved = i < versionArray.length && !versionArray[i].isEmpty() ? Integer.parseInt(versionArray[i]) : 0;
            if (current < saved) {
                return true;
            } else if (current > saved) {
                return false;
            }
        }
        return false;
    }

}
