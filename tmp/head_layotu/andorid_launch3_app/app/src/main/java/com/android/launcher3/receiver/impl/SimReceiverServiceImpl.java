package com.android.launcher3.receiver.impl;

import static com.android.launcher3.common.constant.SettingsConstant.APP_RECEIVER;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.android.launcher3.App;
import com.android.launcher3.common.bean.WatchAppBean;
import com.android.launcher3.common.constant.SettingsConstant;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.mode.SimModel;
import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.resp.ConfigResp;
import com.android.launcher3.common.network.resp.SosListResp;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.moudle.launcher.util.WaAcctIdListener;
import com.android.launcher3.moudle.launcher.util.WatchAccountUtils;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.netty.command.monitor.WatchAppManager;
import com.android.launcher3.netty.schedule.RepeatTaskHelper;
import com.android.launcher3.receiver.StateChangeBroadcastReceiver;
import com.android.launcher3.receiver.core.BaseReceiverServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimReceiverServiceImpl extends BaseReceiverServiceImpl {
    private static final String TAG = "SimReceiverServiceImpl";

    @Override
    public String action() {
        return StateChangeBroadcastReceiver.ACTION_SIM;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        new SimModel(context).init();
        if (!PhoneSIMCardUtil.getInstance().isSIMCardInserted()) {
            LogUtil.d(TAG, "onReceive: 检测到卡被拔出来了，帐号id已经被清除。请将卡重新装回去，并且重启手表！！！", LogUtil.TYPE_RELEASE);
            //定时任务本地数据删除
            Map<String, Long> localRepeatTask = RepeatTaskHelper.getInstance().getLocalRepeatTask(AppLocalData.getInstance().getWatchId());
            if (localRepeatTask != null && !localRepeatTask.isEmpty()) {
                // 1 检测切换新账号先清除本地定时任务
                for (String key : localRepeatTask.keySet()) {
                    RepeatTaskHelper.getInstance().cancelCronTask(key);
                }
            }
            //应用监督本地数据删除
            List<WatchAppBean> list = SharedPreferencesUtils.getList(context, AppLocalData.getInstance().getWatchId());
            if (list != null && !list.isEmpty()) {
                SharedPreferencesUtils.setList(context, AppLocalData.getInstance().getWatchId(), null);
                context.sendBroadcast(new Intent(APP_RECEIVER));
            }
            //上课禁用本地数据删除
            String param = (String) SharedPreferencesUtils.getParam(context, SharedPreferencesUtils.getOverSilenceTimeKey(), "");
            if (param != null & !param.isEmpty()) {
                SharedPreferencesUtils.setParam(context, SharedPreferencesUtils.getOverSilenceTimeKey(), "");
                //改为不在上课禁用时间段
                AppLocalData.getInstance().setIsInClassTime(1);
            }
            WatchAccountUtils.getInstance().clearWaAcctId();
            NettyManager.closeNetty();
        } else {
            WatchAccountUtils.getInstance().checkAndGetAcctId();
            NettyManager.checkNettyState();
            String waAcctIdStr = WatchAccountUtils.getInstance().getWaAcctId();
            getappSwitchJsonDTOSList(waAcctIdStr);
            getSosList(waAcctIdStr);
            WatchAccountUtils.getInstance().addWaAcctIdListener(new WaAcctIdListener() {
                @Override
                public void waAcctIdChange(String newAcctId, String oldAcctId) {
                    getSosList(newAcctId);
                }
            });

        }
    }

    private void getappSwitchJsonDTOSList(String waAcctIdStr) {
        if (StringUtils.isNotBlank(waAcctIdStr)) {
            Integer waAccId = 0;
            try {
                waAccId = Integer.parseInt(waAcctIdStr);
            } catch (Exception exception) {
                return;
            }
            String imei = PhoneSIMCardUtil.getInstance().getImei();
            String iccid = PhoneSIMCardUtil.getInstance().getIccid();
            String phoneNum = PhoneSIMCardUtil.getInstance().getPhoneNum();
            String macAddressByDataNetwork = AppUtils.getMacAddress(App.getInstance());
            LogUtil.d("获取到的设备mac地址为 ============= " + macAddressByDataNetwork, LogUtil.TYPE_RELEASE);
            if (StringUtils.isBlank(imei)) {
                LogUtil.d(TAG, "checkSimCardExist: 当前设备异常，无法获取imei号", LogUtil.TYPE_RELEASE);
                return;
            }
            if (StringUtils.isBlank(iccid)) {
                LogUtil.d(TAG, "checkSimCardExist: 可能不兼容该卡，无法获取该卡的iccid", LogUtil.TYPE_RELEASE);
                return;
            }
            LogUtil.d(TAG + "有sim卡,imei号为：" + imei + ", iccid:" + iccid, LogUtil.TYPE_RELEASE);
            ApiHelper.getConfig(new BaseListener<ConfigResp>() {
                @Override
                public void onError(int code, String msg) {
                    LogUtil.d(TAG + "获取账号配置信息失败" + msg, LogUtil.TYPE_RELEASE);
                }

                @Override
                public void onSuccess(ConfigResp configResp) {
                    //进行应用监督配置信息操作
                    if (configResp.getAppSwitchJsonDTOS() != null && !configResp.getAppSwitchJsonDTOS().isEmpty()) {
                        List<WatchAppBean> receiveAppBeans = new ArrayList<>();
                        for (ConfigResp.AppSwitchJsonDTOSBean dtosBean : configResp.getAppSwitchJsonDTOS()) {
                            LogUtil.d(TAG, "onGetConfigSuccess: SIM卡iccid = " + PhoneSIMCardUtil.getInstance().getIccid(), LogUtil.TYPE_DEBUG);
                            LogUtil.d(TAG, "onGetConfigSuccess: SIM卡Imei = " + PhoneSIMCardUtil.getInstance().getImei(), LogUtil.TYPE_DEBUG);
                            LogUtil.d(TAG, "onSuccess: 接口返回的应用名 = " + dtosBean.getAppName() + "应用监督开关状态 =" + dtosBean.getSwitchStatus(), LogUtil.TYPE_RELEASE);
                            receiveAppBeans.add(new WatchAppBean(dtosBean.getApplicationId(), dtosBean.getAppName(), String.valueOf(dtosBean.getSwitchStatus())));
                        }
                        WatchAppManager.saveAll(App.getInstance(), configResp.getWaAcctId(), receiveAppBeans);
                    }
                    //上课禁用数据处理
                    if (configResp.getCourseDisabledList() != null && !configResp.getCourseDisabledList().isEmpty()) {
                        //10:30-14:30-1-0111110
                        String insertChar = "";
                        String result = "";
                        if (configResp.getCourseDisabledList().size() > 1) {
                            insertChar = ",";
                        }
                        for (int i = 0; i < configResp.getCourseDisabledList().size(); i++) {
                            if (i == configResp.getCourseDisabledList().size() - 1) {
                                insertChar = "";
                            }
                            result += configResp.getCourseDisabledList().get(i).getStartTime() + "-" +
                                    configResp.getCourseDisabledList().get(i).getEndTime() + "-" +
                                    configResp.getCourseDisabledList().get(i).getStatus() + "-" +
                                    configResp.getCourseDisabledList().get(i).getWeekSwitches() + insertChar;
                        }
                        SharedPreferencesUtils.setParam(App.getInstance(), SharedPreferencesUtils.getOverSilenceTimeKey(), result.trim());
                        //是否在上课禁用时间段
                        if (LauncherAppManager.isForbiddenInClass(App.getInstance(), "")) {
                            AppLocalData.getInstance().setIsInClassTime(0);
                        } else {
                            AppLocalData.getInstance().setIsInClassTime(1);
                        }
                    }
                }
            }, imei, iccid, macAddressByDataNetwork, phoneNum);
        } else {
            LogUtil.d(TAG + "获取账号配置信息失败，原因为帐号id为空", LogUtil.TYPE_RELEASE);
        }
    }

    private void getSosList(String waAcctIdStr) {
        if (StringUtils.isNotBlank(waAcctIdStr)) {
            Integer waAccId = 0;
            try {
                waAccId = Integer.parseInt(waAcctIdStr);
            } catch (Exception exception) {
                return;
            }
            ApiHelper.getSosList(new BaseListener<SosListResp>() {
                String TAG = "获取sos列表数据";

                @Override
                public void onError(int code, String msg) {
                    if (StringUtils.isBlank(msg)) {
                        LogUtil.d(TAG + "获取sos失败的请求失败，错误内容为空，code=" + code, LogUtil.TYPE_RELEASE);
                    }

                    LogUtil.d(TAG + "获取sos失败的请求失败，code=" + code + ", 错误信息为：" + msg, LogUtil.TYPE_RELEASE);
                }

                @Override
                public void onSuccess(SosListResp response) {
                    if (response == null) {
                        LogUtil.d(TAG + "获取sos失败失败，原因是：调用请求成功，但是返回的response为空", LogUtil.TYPE_RELEASE);
                    }
                    LogUtil.d(TAG + "获取sos成功，数据为：" + response.getList(), LogUtil.TYPE_RELEASE);
                    String sos = "";
                    for (String item : response.getList()) {
                        if (StringUtils.isNotBlank(item)) {
                            sos += item + ",";
                        }
                    }
                    Settings.System.putString(App.getInstance().getContentResolver(), SettingsConstant.WATCH_SOS, StringUtils.abandonLastChar(sos));
                }
            }, waAccId);
        } else {
            LogUtil.d(TAG + "获取sos失败，原因为帐号id为空", LogUtil.TYPE_RELEASE);
        }
    }
}
