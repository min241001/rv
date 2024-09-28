package com.android.launcher3.netty.command.linkmaintenance;


import static com.android.launcher3.common.constant.SettingsConstant.UPDATE_FLAG_DETAIL;
import static com.android.launcher3.common.constant.SettingsConstant.UPDATE_FLAG_KEY;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.launcher3.App;
import com.android.launcher3.BuildConfig;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.mode.OtaModel;
import com.android.launcher3.common.network.resp.UpgradeDetailResp;
import com.android.launcher3.common.utils.GsonUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.moudle.launcher.view.LauncherActivity;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

public class LinkMaintenanceCmdServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "链路保持";

    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";
    private final OtaModel otaModel = new OtaModel();

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.KA;
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
                LogUtil.d(TAG + "launcher3获取下载URL", LogUtil.TYPE_RELEASE);
                otaModel.upgrade();
            } else {
                if (patch != null && !patch.isEmpty()) {
                    //当前线上版本号不大于launcher版本号，进行增量更新版本号查询
                    Long patchNumber = (Long) SharedPreferencesUtils.getParam(CommonApp.getInstance(), UPDATE_FLAG_KEY, Long.parseLong("0"));
                    if (patchNumber != null) {
                        //补丁版本号，0表示为第一次默认为 0
                        Long newPatch = Long.parseLong(patch);
                        if (newPatch > patchNumber) {
                            LogUtil.d(TAG + "增量升级策略需要请求新的", LogUtil.TYPE_RELEASE);
                            //缓存本地增量更新版本号
                            SharedPreferencesUtils.setParam(CommonApp.getInstance(), UPDATE_FLAG_KEY, newPatch);
                            //清空本地缓存增量策略
                            SharedPreferencesUtils.removeKey(CommonApp.getInstance(), UPDATE_FLAG_DETAIL);
                            //请求增量策略接口
                            otaModel.checkNewPatch(LauncherActivity.context, version, String.valueOf(newPatch));
                        } else {
                            String param = (String) SharedPreferencesUtils.getParam(CommonApp.getInstance(), UPDATE_FLAG_DETAIL, "null");
                            if (param != null && !param.isEmpty() && !param.equals("null")){
                                LogUtil.d(TAG + "走到本地的增量升级策略", LogUtil.TYPE_RELEASE);
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

    @Override
    public Boolean sendMsg(Object obj) {
        String response;
        try {
            long currentTimeMillis = System.currentTimeMillis();
            int battery;
            battery = AppLocalData.getInstance().getBattery();
            //networkState -1、未知、1 WIFI、2 4G、3 3G、4 2G
            int networkState = getNetworkState();
            String randomStepNum = StringUtils.randomNum(4);
            String content = currentTimeMillis +","+ battery +","+ networkState +","+ randomStepNum;
            response = formatSendMsgContent(content);
            LogUtil.d(TAG + "终端发送内容为：" + response, LogUtil.TYPE_RELEASE);
            sendStrMsg(response);
        } catch (Exception e) {
            Log.d(TAG, "sendMsg: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    //获取当前设备网络状态
    private int getNetworkState() {
        //type -1、未知、1 WIFI、2 4G、3 3G、4 2G
        int type = -1;
        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // 当前连接的是WiFi网络
                type = 1;
                return type;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // 当前连接的是移动数据网络
                TelephonyManager manager = (TelephonyManager) App.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
                int networkType = manager.getNetworkType();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        type = -1;
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        type = 4;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        type = 3;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        type = 2;
                        break;
                    case TelephonyManager.NETWORK_TYPE_NR:
                        break;
                }
                return type;
            }
        }
        return type;
    }

    //是否需要进行版本升级
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
