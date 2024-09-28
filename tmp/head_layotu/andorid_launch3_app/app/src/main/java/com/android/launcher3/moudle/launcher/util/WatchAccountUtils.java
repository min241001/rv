package com.android.launcher3.moudle.launcher.util;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.App;
import com.android.launcher3.common.bean.WatchAppBean;
import com.android.launcher3.common.constant.SettingsConstant;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.resp.ConfigResp;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.netty.command.monitor.WatchAppManager;
import com.android.launcher3.netty.schedule.RepeatTaskHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WatchAccountUtils {
    private static final String TAG = "WatchAccountUtil";
    /**
     * 手表账号本地缓存key
     */
    private static final String SP_WATCH_ACCT_ID_KEY = "_WATCH_ACCT_ID_";
    private static volatile WatchAccountUtils instance;
    /**
     * 手表账号handler
     */
    private static WaAcctIdHandler mWaAcctIdHandler;
    /**
     * 是否正在尝试从网络请求账号
     */
    private final AtomicBoolean ifGettingWaAcctIdFromNet = new AtomicBoolean(false);
    /**
     * 是否正在尝试多次从网络获取账号数据
     */
    private final AtomicBoolean ifMultipleTimesGetWaAcctIdFromNet = new AtomicBoolean(false);
    private final List<WaAcctIdListener> waAcctIdListenerList = new ArrayList<>(4);
    /**
     * 手表账号
     */
    private String waAcctId = "";
    private long lastMultipleTimes = System.currentTimeMillis();

    private static boolean ifOpenAcctUtil = true;

    /**
     * 是否启用本工具类
     *
     * @param ifOpen 是否启用
     */
    public static void ifOpenAcctUtil(Boolean ifOpen) {
        ifOpenAcctUtil = ifOpen;
    }

    public static WatchAccountUtils getInstance() {
        if (null == instance) {
            synchronized (WatchAccountUtils.class) {
                if (null == instance) {
                    instance = new WatchAccountUtils();
                    mWaAcctIdHandler = new WaAcctIdHandler();
                }
            }
        }
        return instance;
    }

    /**
     * 获取账号
     *
     * @return 一般为10位的账号
     */
    public String getWaAcctId() {
        if (!ifOpenAcctUtil) {
            return "";
        }
        // 没有插入sim 卡，直接返回空""
        if (!PhoneSIMCardUtil.getInstance().isSIMCardInserted()) {
            return "";
        }
        if (StringUtils.isBlank(waAcctId)) {
            // 获取本地缓存
//            waAcctId = (String) SharedPreferencesUtils.getParam(App.getInstance(), getSpKey(), "");
//            AppLocalData.getInstance().setWatchId(waAcctId);
            waAcctId = AppLocalData.getInstance().getWatchId();
            Log.d(TAG, "getWaAcctId: 从本地缓存中获取账号数据：" + waAcctId);
        }
        return waAcctId;
    }

    /**
     * 判断账号是否存在，若不存在，尝试从网络获取
     */
    public void checkAndGetAcctId() {
        if (!ifOpenAcctUtil) {
            return;
        }
        // 当有插卡且无账号的情况下，获取账号数据
        if (StringUtils.isBlank(getWaAcctId()) && PhoneSIMCardUtil.getInstance().isSIMCardInserted()) {
            Log.d(TAG, "checkAndGetAcctId: 有插卡但是没有账号进行网络请求账号");
            // 从网络数据数据
            getWaAcctIdByNet();
        } else {
            Log.d(TAG, "checkAndGetAcctId: 有插卡有账号");
        }
    }

    /**
     * 清空账号
     */
    public void clearWaAcctId() {
        if (!ifOpenAcctUtil) {
            return;
        }
        updateWaAcctId("");
        Settings.System.putString(App.getInstance().getContentResolver(), SettingsConstant.WATCH_SOS, "");
    }

    /**
     * 更新账号信息
     *
     * @param waAcctId 账号id
     */
    private void updateWaAcctId(String waAcctId) {
        String oldWaAcctId = this.waAcctId;
        this.waAcctId = waAcctId;
        // 本地存储一份
        SharedPreferencesUtils.setParam(App.getInstance(), getSpKey(), waAcctId);
        // 将手表id放到setting中，供第三方app去调用
        AppLocalData.getInstance().setWatchId(waAcctId);
        //账号信息成功后进行配置信息请求
        if (!TextUtils.isEmpty(waAcctId)) {
            RepeatTaskHelper.getInstance().initRepeatTaskWhenReboot();
        }
        // 若账号数据有变更，进行通知
        if (oldWaAcctId != null && !oldWaAcctId.equals(waAcctId)) {
            notifyToListener(waAcctId, oldWaAcctId);
        }
    }

    /**
     * 添加账号变化监听器
     *
     * @param waAcctIdListener waAcctIdListener
     */
    public void addWaAcctIdListener(WaAcctIdListener waAcctIdListener) {
        if (!ifOpenAcctUtil) {
            return;
        }
        if (waAcctIdListener == null) {
            return;
        }
        waAcctIdListenerList.add(waAcctIdListener);
    }

    /**
     * 移除账号变化监听器
     *
     * @param waAcctIdListener waAcctIdListener
     */
    public void removeWaAcctIdListener(WaAcctIdListener waAcctIdListener) {
        if (!ifOpenAcctUtil) {
            return;
        }
        if (waAcctIdListener == null) {
            return;
        }
        waAcctIdListenerList.remove(waAcctIdListener);
    }

    /**
     * 清除所有的账号监听器数据
     */
    public void clearWaAcctIdListener() {
        if (!ifOpenAcctUtil) {
            return;
        }
        waAcctIdListenerList.clear();
    }


    /**
     * 通知其它监听器，数据变化了
     *
     * @param newWaAcctId 新账号
     * @param oldWaAcctId 旧账号
     */
    private void notifyToListener(String newWaAcctId, String oldWaAcctId) {
        if (!ifOpenAcctUtil) {
            return;
        }
        for (WaAcctIdListener waAcctIdListener : waAcctIdListenerList) {
            waAcctIdListener.waAcctIdChange(newWaAcctId, oldWaAcctId);
        }
    }

    /**
     * 根据imei和iccid 获取 SharedPreferences 对应的key
     *
     * @return SharedPreferences 对应的key
     */
    private String getSpKey() {
        String imei = "";
        try {
            imei = PhoneSIMCardUtil.getInstance().getImei();
        } catch (Exception exception) {
            Log.d(TAG, "getSpKey: 获取imei" + exception);
        }
        return SP_WATCH_ACCT_ID_KEY + imei;
    }

    /**
     * 从网络获取数据
     */
    private void getWaAcctIdByNet() {
        // 异步从网络请求账号数据，当前请求获取的账号旧为空
        // 1 为避免handler 不执行，设置超时时间为5分钟即（300秒）
        if (System.currentTimeMillis() - lastMultipleTimes > 300000L) {
            ifMultipleTimesGetWaAcctIdFromNet.set(false);
            ifGettingWaAcctIdFromNet.set(false);
        }
        // 2 防多次请求，若当前没有尝试从网络数据数据
        if (!ifMultipleTimesGetWaAcctIdFromNet.get()) {
            // 2.1 重置标记 及时间
            ifGettingWaAcctIdFromNet.set(false);
            lastMultipleTimes = System.currentTimeMillis();
            // 2.2 尝试请求
            mWaAcctIdHandler.sendEmptyMessage(1);
            // 2.3 标记从网络尝试多次网络请求
            ifMultipleTimesGetWaAcctIdFromNet.set(true);
        }
    }

    /**
     * 网络请求获取账号id
     */
    public void getWaAcctIdByNet(int times) {
        String imei = PhoneSIMCardUtil.getInstance().getImei();
        String iccid = PhoneSIMCardUtil.getInstance().getIccid();
        String phoneNum = PhoneSIMCardUtil.getInstance().getPhoneNum();
        String macAddressByDataNetwork = AppUtils.getMacAddress(App.getInstance());
        LogUtil.d("获取到的设备mac地址为 ============= " + macAddressByDataNetwork, LogUtil.TYPE_RELEASE);
        if (StringUtils.isBlank(imei)) {
            Log.d(TAG, "checkSimCardExist: 当前设备异常，无法获取imei号");
            return;
        }
        if (StringUtils.isBlank(iccid)) {
            Log.d(TAG, "checkSimCardExist: 可能不兼容该卡，无法获取该卡的iccid");
            return;
        }
        LogUtil.d(TAG + "有sim卡,imei号为：" + imei + ", iccid:" + iccid, LogUtil.TYPE_RELEASE);
        if (ifGettingWaAcctIdFromNet.get()) {
            return;
        }
        // 标记正在进行网络请求
        ifGettingWaAcctIdFromNet.set(true);
        ApiHelper.getWaAcctId(new BaseListener<ConfigResp>() {
            @Override
            public void onError(int code, String msg) {
                if (StringUtils.isBlank(msg)) {
                    LogUtil.d(TAG + "获取设备id的请求失败，错误内容为空，code=" + code, LogUtil.TYPE_RELEASE);
                    return;
                }
                if (msg.contains("Unable to resolve")) {
                    LogUtil.d(TAG + "获取设备id的请求失败，失败原因为: 失败无法上网, code=" + code + "，失败原因为:" + msg, LogUtil.TYPE_RELEASE);
                    ToastUtils.show4Debug("获取设备id的请求失败，失败原因为: 失败无法上网, code=" + code + "，失败原因为:" + msg);
                } else {
                    LogUtil.d(TAG + "获取设备id的请求失败，code = " + code + "，失败原因为:" + msg, LogUtil.TYPE_RELEASE);
                    ToastUtils.show4Debug("获取设备id的请求失败，code = " + code + "，失败原因为:" + msg);
                }
                // 2N * 3 秒后再次尝试,即第二次12秒后、第三次18秒后尝试
                if (times < 4) {
                    ifMultipleTimesGetWaAcctIdFromNet.set(false);
                    ifGettingWaAcctIdFromNet.set(false);
                    LogUtil.d(TAG + "超过3次测试，放弃从网络获取账号信息", LogUtil.TYPE_RELEASE);
                } else {
                    ifGettingWaAcctIdFromNet.set(false);
                    mWaAcctIdHandler.sendEmptyMessageAtTime(times + 1, 2L * times * 3000L);
                }

            }

            @Override
            public void onSuccess(ConfigResp response) {
                if (response == null) {
                    LogUtil.d(TAG + "获取手表id失败，原因是：调用请求成功，但是返回的response为空", LogUtil.TYPE_RELEASE);
                    return;
                }
                if ("".equals(response.getWaAcctId())) {
                    LogUtil.d(TAG + "获取手表id失败，原因是：调用请求成功，但是返回的waAcctId为空字符串", LogUtil.TYPE_RELEASE);
                    return;
                }
                ToastUtils.show4Debug("获取手表id成功，手表id为：" + response.getWaAcctId() + ", 验证码为：" + response.getCode());
                LogUtil.d(TAG + "获取手表id成功，手表id为：" + response.getWaAcctId() + "验证码：" + response.getCode(), LogUtil.TYPE_RELEASE);
                updateWaAcctId(response.getWaAcctId());
                //进行应用监督配置信息操作
                if (response.getAppSwitchJsonDTOS() != null && !response.getAppSwitchJsonDTOS().isEmpty()) {
                    List<WatchAppBean> receiveAppBeans = new ArrayList<>();
                    for (ConfigResp.AppSwitchJsonDTOSBean dtosBean : response.getAppSwitchJsonDTOS()) {
                        Log.d(TAG, "onGetConfigSuccess: SIM卡iccid = " + PhoneSIMCardUtil.getInstance().getIccid());
                        Log.d(TAG, "onGetConfigSuccess: SIM卡Imei = " + PhoneSIMCardUtil.getInstance().getImei());
                        Log.d(TAG, "onSuccess: 接口返回的应用名 = " + dtosBean.getAppName() + "应用监督开关状态 =" + dtosBean.getSwitchStatus());
                        receiveAppBeans.add(new WatchAppBean(dtosBean.getApplicationId(), dtosBean.getAppName(), String.valueOf(dtosBean.getSwitchStatus())));
                    }
                    WatchAppManager.saveAll(App.getInstance(), response.getWaAcctId(), receiveAppBeans);
                }
                //上课禁用数据处理
                if (response.getCourseDisabledList() != null && !response.getCourseDisabledList().isEmpty()) {
                    //10:30-14:30-1-0111110
                    String insertChar = "";
                    String result = "";
                    if (response.getCourseDisabledList().size() > 1) {
                        insertChar = ",";
                    }
                    for (int i = 0; i < response.getCourseDisabledList().size(); i++) {
                        if (i == response.getCourseDisabledList().size() - 1) {
                            insertChar = "";
                        }
                        result += response.getCourseDisabledList().get(i).getStartTime() + "-" +
                                response.getCourseDisabledList().get(i).getEndTime() + "-" +
                                response.getCourseDisabledList().get(i).getStatus() + "-" +
                                response.getCourseDisabledList().get(i).getWeekSwitches() + insertChar;
                    }
                    SharedPreferencesUtils.setParam(App.getInstance(), SharedPreferencesUtils.getOverSilenceTimeKey(), result.trim());
                    //是否在上课禁用时间段
                    if (LauncherAppManager.isForbiddenInClass(App.getInstance(), "")) {
                        AppLocalData.getInstance().setIsInClassTime(0);
                    } else {
                        AppLocalData.getInstance().setIsInClassTime(1);
                    }
                }
                // 初始化netty客户端
                NettyManager.checkNettyState();
                // 重置标记
                ifGettingWaAcctIdFromNet.set(false);
                ifMultipleTimesGetWaAcctIdFromNet.set(false);
            }
        }, imei, iccid, macAddressByDataNetwork, phoneNum);
    }

    private static class WaAcctIdHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (getInstance().ifGettingWaAcctIdFromNet.get()) {
                return;
            }
            getInstance().getWaAcctIdByNet(msg.what);
        }
    }
}
