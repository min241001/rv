package com.android.launcher3.netty.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.launcher3.App;
import com.android.launcher3.common.bean.WatchAppBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.resp.ConfigResp;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.moudle.launcher.util.WaAcctIdListener;
import com.android.launcher3.moudle.launcher.util.WatchAccountUtils;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.netty.command.monitor.WatchAppManager;
import com.android.launcher3.netty.constant.XmxxCmdConstant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RepeatTaskHelper {
    private static final String TAG = "RepeatTaskHelper";

    private static volatile RepeatTaskHelper instance;

    /**
     * 是否有本地缓存
     */
    private Boolean ifHasLocalCache = false;
    /**
     * 是否正确从网络获取配置信息
     */
    private Boolean ifGettingConfigFormNet = false;
    /**
     * alarmManager
     */
    private static AlarmManager alarmManager;
    /**
     * 任务列表
     */
    private ConcurrentHashMap<String, Long> repeatTaskMap = new ConcurrentHashMap<>(10);
    /**
     * 当前账号
     */
    private String currentWaAcctId = "";
    /**
     * 任务分隔符号
     */
    private String TASK_SPLIT = ",";
    /**
     * 任务内容分隔符号
     */
    private String TASK_INFO_SPLIT = "-";


    private static final String SP_CRON_WATCH_ACCT_ID_KEY = "_CRON_WATCH_ACCT_ID_";

    public static RepeatTaskHelper getInstance() {
        if (null == instance) {
            synchronized (RepeatTaskHelper.class) {
                if (null == instance) {
                    instance = new RepeatTaskHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 获取 alarmManager
     *
     * @return alarmManager 实例
     */
    public AlarmManager getAlarmManager() {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) App.getInstance().getSystemService(Context.ALARM_SERVICE);
        }
        return alarmManager;
    }

    /**
     * 判断是否有本地缓存
     *
     * @return 结果
     */
    public boolean checkIfHasLocalCache(String waAcctId) {
        if (StringUtils.isBlank(waAcctId)) {
            return false;
        }
        if (currentWaAcctId.equals(waAcctId)) {
            return ifHasLocalCache;
        }
        String taskStr = (String) SharedPreferencesUtils.getParam(App.getInstance(), getSpKey(waAcctId), "");

        return StringUtils.isNotBlank(taskStr);
    }

    public Long getActionTime(String action) {
        Long period = repeatTaskMap.get(action);
        if (period == null) {
            return -1L;
        }
        return period;
    }

    /**
     * 重新读取配置好的定时任务并设置
     */
    public void initRepeatTaskWhenReboot() {
        // 通过账号id获取旧的历史任务
        if (StringUtils.isNotBlank(AppLocalData.getInstance().getWatchId())) {
            currentWaAcctId = WatchAccountUtils.getInstance().getWaAcctId();
            Log.d(TAG, "initRepeatTaskWhenReboot: " + currentWaAcctId);
            Map<String, Long> taskMap = getLocalRepeatTask(currentWaAcctId);
            if (taskMap.isEmpty() || taskMap.size() < 1) {
                //网络获取账号定时任务
                getConfigFromNet();
                return;
            } else {
                Log.d(TAG, "initRepeatTaskWhenReboot: " + taskMap.size());
                repeatTaskMap.clear();
                for (String key : taskMap.keySet()) {
                    Log.d(TAG, "initRepeatTaskWhenReboot: " + key);
                    Log.d(TAG, "initRepeatTaskWhenReboot: " + taskMap.get(key));
                    addCronTask(key, Long.valueOf(taskMap.get(key)));
                }
            }
        }
        //添加账号变化监听器
        WatchAccountUtils.getInstance().addWaAcctIdListener(new WaAcctIdListener() {
            @Override
            public void waAcctIdChange(String newAcctId, String oldAcctId) {
                if (newAcctId == null) {
                    newAcctId = "";
                    return;
                }
                if (!currentWaAcctId.equals(newAcctId)) {
                    synchronized (RepeatTaskHelper.class) {
                        if (!currentWaAcctId.equals(newAcctId)) {
                            currentWaAcctId = newAcctId;
                            // 1 检测切换新账号先清除本地定时任务
                            for (String key : repeatTaskMap.keySet()) {
                                cancelCronTask(key);
                            }
                            // 2 网络获取新账号定时任务
                            getConfigFromNet();
                        }
                    }
                }
            }
        });
    }

    private void getConfigFromNet() {
        getConfigFromNet(new ConfigService() {
            @Override
            public void onGetConfigSuccess(ConfigResp configResp) {
                //进行应用监督配置信息操作
                if (configResp.getAppSwitchJsonDTOS() != null && !configResp.getAppSwitchJsonDTOS().isEmpty()) {
                    List<WatchAppBean> receiveAppBeans = new ArrayList<>();
                    for (ConfigResp.AppSwitchJsonDTOSBean dtosBean : configResp.getAppSwitchJsonDTOS()) {
                        Log.d(TAG, "onGetConfigSuccess: SIM卡iccid = " + PhoneSIMCardUtil.getInstance().getIccid());
                        Log.d(TAG, "onGetConfigSuccess: SIM卡Imei = " + PhoneSIMCardUtil.getInstance().getImei());
                        Log.d(TAG, "onSuccess: 接口返回的应用名 = " + dtosBean.getAppName() + "应用监督开关状态 =" + dtosBean.getSwitchStatus());
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

                if (configResp.getRepeatTask().getLocationSec() != -1) {
                    Log.d(TAG, "onGetConfigSuccess: 添加定时定位的定时任务 =====");
                    addCronTask(XmxxCmdConstant.MOD_LOCATION_FRQ,
                            Long.valueOf(configResp.getRepeatTask().getLocationSec() * 1000));
                }
                if (StringUtils.isNotBlank(configResp.getRepeatTask().getBootOffTime()) && !configResp.getRepeatTask().getBootOffTime().equals("-1")) {
                    Log.d(TAG, "onGetConfigSuccess: 添加定时关机的定时任务 =====");
                    try {
                        String[] split = configResp.getRepeatTask().getBootOffTime().split(":");

                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));   // 小时（24小时制）
                        calendar1.set(Calendar.MINUTE, Integer.parseInt(split[1]));           // 分钟
                        calendar1.set(Calendar.SECOND, 0);           // 秒

                        addCronTask(XmxxCmdConstant.MOD_POWER_OFF, calendar1.getTimeInMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 添加定时任务
     *
     * @param action 任务名称
     * @param period 任务周期，单位毫秒
     */
    public void addCronTask(@NonNull String action, Long period) {
        //使用handler实现定时任务
        switch (action) {
            case XmxxCmdConstant.MOD_POWER_ON:
                // todo 实现定时开机需求

                break;
            case XmxxCmdConstant.MOD_POWER_OFF:
                addPowerOffTask(period);
                repeatTaskMap.put(action, period);
                saveTaskToLocal(repeatTaskMap, currentWaAcctId);
                break;
            case XmxxCmdConstant.MOD_LOCATION_FRQ:
                //设置定时定位频率
                addLocationTask(period);
                //缓存本地定时任务
                repeatTaskMap.put(action, period);
                saveTaskToLocal(repeatTaskMap, currentWaAcctId);
                break;
            case XmxxCmdConstant.CHECK_NETTY_STATE:
                addCheckNettyTask(period);
                break;
        }
    }

    /**
     * 添加上课禁用定时任务
     *
     * @param period 任务周期，单位毫秒
     * @param tag    标签，相同标签不在添加
     */
    public void addClassBanTask(Long period, @NonNull String tag) {
        // 已有相同任务不添加
        if (repeatTaskMap.containsKey(tag)) {
            Log.i(TAG, "addClassBanTask:1 任务已存在不需要添加。 tag " + tag);
            return;
        }
        // 使用handler实现定时任务
        if (tag.contains(XmxxCmdConstant.CLASS_BAN_START)) {
            Log.i(TAG, "addClassBanTask:1 tag " + tag);
            repeatTaskMap.put(tag, period);
            addTask(period, tag);
            saveTaskToLocal(repeatTaskMap, currentWaAcctId);
        }
    }

    //关机的PendingIntent
    PendingIntent powerOffPendingIntent;
    //定位的PendingIntent
    PendingIntent locationPendingIntent;
    //检查netty客户端的PendingIntent
    PendingIntent nettyPendingIntent;

    //打开定时器执行定时关机操作
    public void addPowerOffTask(long period) {
        //实现定时关机需求
        LogUtil.d(TAG, "addPowerOffTask: 当前时间已经过了今天的关机时间，走到这一步===================================", LogUtil.TYPE_RELEASE);
        Intent intent = new Intent(App.getInstance(), RepeatTaskBroadcastReceiver.class);
        intent.setAction(XmxxCmdConstant.MOD_POWER_OFF);
        powerOffPendingIntent = PendingIntent.getBroadcast(App.getInstance(), XmxxCmdConstant.PowerOffTaskCode, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(period);
        int hours = calendar.get(Calendar.HOUR_OF_DAY); // 获取小时数，24小时制
        int minutes = calendar.get(Calendar.MINUTE); // 获取分钟数

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, hours);   // 小时（24小时制）
        calendar1.set(Calendar.MINUTE, minutes);       // 分钟
        calendar1.set(Calendar.SECOND, 0);

        long timeInMillis = calendar1.getTimeInMillis();

        if (System.currentTimeMillis() > timeInMillis) {
            getAlarmManager().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis + AlarmManager.INTERVAL_DAY, powerOffPendingIntent);
        } else {
            getAlarmManager().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, powerOffPendingIntent);
        }
    }

    //打开定时器执行定时定位操作
    public void addLocationTask(long period) {
        Intent intent = new Intent(App.getInstance(), RepeatTaskBroadcastReceiver.class);
        intent.setPackage(App.getInstance().getPackageName());
        intent.setAction(XmxxCmdConstant.MOD_LOCATION_FRQ);
        locationPendingIntent = PendingIntent.getBroadcast(App.getInstance(), XmxxCmdConstant.LocationTaskCode, intent, 0);
        long triggerTime = System.currentTimeMillis() + period;
        getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, locationPendingIntent);
    }

    //打开定时器执行定时检查netty客户端操作
    public void addCheckNettyTask(long period) {
        addTask(period, XmxxCmdConstant.CHECK_NETTY_STATE);
    }

    /**
     * 添加定时任务
     *
     * @param period 定时时间
     * @param action 任务标签
     */
    private void addTask(long period, String action) {
        Intent intent = new Intent(App.getInstance(), RepeatTaskBroadcastReceiver.class);
        intent.setPackage(App.getInstance().getPackageName());
        intent.setAction(action);
        nettyPendingIntent = PendingIntent.getBroadcast(App.getInstance(), 0, intent, 0);
        long triggerTime = System.currentTimeMillis() + period;
        getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, nettyPendingIntent);
    }

    private void getConfigFromNet(ConfigService configService) {
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
        if (ifGettingConfigFormNet) {
            LogUtil.d(TAG, "getConfigFromNet: 正在从网络获取配置信息，放弃本次获取", LogUtil.TYPE_RELEASE);
            return;
        }
        ifGettingConfigFormNet = true;
        ApiHelper.getConfig(new BaseListener<ConfigResp>() {
            @Override
            public void onError(int code, String msg) {
                LogUtil.d(TAG + "获取账号配置信息失败" + msg, LogUtil.TYPE_RELEASE);
            }

            @Override
            public void onSuccess(ConfigResp configResp) {
                configService.onGetConfigSuccess(configResp);
            }
        }, imei, iccid, macAddressByDataNetwork, phoneNum);
    }

    /**
     * 取消定时任务
     *
     * @param action 任务名称
     */
    public void cancelCronTask(String action) {
        if (XmxxCmdConstant.MOD_POWER_OFF.equals(action)) {
            Log.d(TAG, "cancelCronTask: 取消定时关机任务");
            getAlarmManager().cancel(powerOffPendingIntent);
            powerOffPendingIntent.cancel();
            removeTaskMapByKey(action);
        }
        if (XmxxCmdConstant.MOD_LOCATION_FRQ.equals(action)) {
            Log.d(TAG, "cancelCronTask: 取消定时定位任务");
            getAlarmManager().cancel(locationPendingIntent);
            locationPendingIntent.cancel();
            removeTaskMapByKey(action);
        }
    }

    /**
     * 根据任务标间删除指定任务
     *
     * @param key 任务标签
     */
    public void removeTaskMapByKey(String key) {
        if (repeatTaskMap == null || repeatTaskMap.size() == 0) {
            return;
        }
        repeatTaskMap.remove(key);
        saveTaskToLocal(repeatTaskMap, currentWaAcctId);
    }

    /**
     * 获取本地周期性任务
     *
     * @param waAcctId 账号
     * @return Map<String, Long>
     */
    public Map<String, Long> getLocalRepeatTask(String waAcctId) {
        if (StringUtils.isBlank(waAcctId)) {
            return Collections.EMPTY_MAP;
        }
        Map<String, Long> map = SharedPreferencesUtils.getMap(App.getInstance(), getSpKey(waAcctId));
        return map;
    }

    /**
     * 记录任务列表到本地
     *
     * @param taskMap  任务
     * @param waAcctId 账号
     */
    public void saveTaskToLocal(Map<String, Long> taskMap, String waAcctId) {
        if (StringUtils.isBlank(waAcctId)) {
            Log.d(TAG, "saveTaskToLocal: waAcctId为空，导致定时任务存储本地失败");
            return;
        }
//        if (taskMap == null || taskMap.size() == 0) {
//            return;
//        }
        SharedPreferencesUtils.setMap(App.getInstance(), getSpKey(waAcctId), taskMap);
    }


    /**
     * 缓存缓存键值
     *
     * @return 文本
     */
    public String getSpKey(String waAcctId) {
        if (waAcctId == null) {
            return SP_CRON_WATCH_ACCT_ID_KEY + WatchAccountUtils.getInstance().getWaAcctId();
        }
        return SP_CRON_WATCH_ACCT_ID_KEY + waAcctId;
    }

    public interface ConfigService {
        void onGetConfigSuccess(ConfigResp configResp);
    }
}
