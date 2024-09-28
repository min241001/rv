package com.android.launcher3.utils;

import static com.android.launcher3.common.constant.AppPackageConstant.LAUNCHER_STYLE;
import static com.android.launcher3.common.constant.AppPackageConstant.PACKAGE_COURSE;
import static com.android.launcher3.common.constant.AppPackageConstant.PACKAGE_FACERECOGNITION;
import static com.android.launcher3.common.constant.AppPackageConstant.PACKAGE_MEET;
import static com.android.launcher3.common.constant.AppPackageConstant.PACKAGE_MICRO_CHAT;
import static com.android.launcher3.common.constant.AppPackageConstant.PACKAGE_POWER;
import static com.android.launcher3.common.constant.AppPackageConstant.PACKAGE_RECORD;
import static com.android.launcher3.common.constant.AppPackageConstant.PACKAGE_UTIL;
import static com.android.launcher3.common.constant.AppPackageConstant.PACKAGE_VIDEO;
import static com.android.launcher3.common.constant.AppPackageConstant.PACKAGE_WALLET;

import android.app.ActivityManager;
import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;

import com.android.launcher3.App;
import com.android.launcher3.common.bean.WatchAppBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.AppLauncherUtils;
import com.android.launcher3.common.utils.AudioFocusManager;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.moudle.notification.util.TimeUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.android.launcher3.netty.schedule.RepeatTaskHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Author : yanyong
 * Date : 2024/8/2
 * Details : 应用监督管理器
 */
public class AppSuperviseManager {

    private static final String TAG = "AppSuperviseManager_123123";
    private static AppSuperviseManager Instance;

    private ActivityManager mActivityManager;

    // 系统运行应用，上课禁用不需要退出的应用包名
    public static final String[] APP_WHITE = {
            "com.android.launcher3",
            "android.process.acore",
            "com.sprd.commlog",
            "com.adups.fota",
            "com.android.systemui",
            "system",
            "com.sprd.wirelesstools",
            "com.android.modemnotifier",
            "com.android.keychain",
            "com.sprd.commlog",
            "com.android.phone",
            "com.watch.sales",
            "com.android.defcontainer",
            "com.sogou.ime.wear"};

    // 系统应用包名
    public static final String[] SYSTEM_APP_PKG = {
            "com.android.launcher3",
            "com.baehug.dialer",
            "com.baehug.weather",
            "com.baehug.contacts",
            "com.baehug.watch.wechat",
            "com.baehug.videochat",
            "com.baehug.album",
            "com.baehug.course",
            "com.baehug.sms",
            "com.baehug.appstore",
            "com.baehug.wallet",
            "com.baehug.camera",
            "com.baehug.meet",
            "com.ximeng.qrcode",
            "com.baehug.settings",
            "com.sogou.ime.wear"
    };

    public static final String[] LAUNCHER_APP = {
        "com.android.launcher3", // 桌面
        "com.baehug.dialer", // 电话
        "com.baehug.weather", // 天气
        PACKAGE_MICRO_CHAT, // 微聊
        PACKAGE_COURSE, // 课程表
        "com.baehug.sms", // 短信
        PACKAGE_RECORD, // 录音
        "com.baehug.theme", // 西萌主题
        "com.baehug.videochat", // 视频通话
        "com.baehug.appstore", // 应用市场
        PACKAGE_WALLET, // 西萌钱包
        LAUNCHER_STYLE, // 风格
        "com.baehug.camera", // 相机
        "com.baehug.album", // 相册
        PACKAGE_MEET, // 加好友
        "com.baehug.qrcode", // 二维码
        PACKAGE_UTIL, // 工具
        PACKAGE_POWER, // 超级省电
        "com.sogou.ime.wear" // 搜狗输入法
    };

    // 上次添加任务的日期
    private int mLastDay;

    // 上次添加任务的参数
    private String mLastParam;

    private String mClassBanType = "\"s03MType\":\"2\"";

    private AppSuperviseManager() {
        mActivityManager = (ActivityManager) App.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
    }
    
    public static AppSuperviseManager getInstance() {
        if (Instance == null) {
            synchronized (AppSuperviseManager.class) {
                if (Instance == null) {
                    Instance = new AppSuperviseManager();
                }
            }
        }
        return Instance;
    }

    /**
     * 启动桌面，其他应用都返回后台
     */
    public void launchHome() {
        String list = AppLocalData.getInstance().getAppDisableList();
        if (TextUtils.isEmpty(list)){
            LogUtil.i(TAG, "launchHome: list is null", LogUtil.TYPE_RELEASE);
            return;
        }
        LogUtil.i(TAG, "launchHome: list " + list, LogUtil.TYPE_RELEASE);
        if (list.contains(mClassBanType)) {
            exitRunningTasks(list);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<WatchAppBean>>(){}.getType();
        List<WatchAppBean> arrayList = gson.fromJson(list, type);
        List<String> appSystems = Arrays.asList(LAUNCHER_APP);
        for (WatchAppBean watchBean : arrayList) {
            if (watchBean.getS03MType().equals("2")) {
                String pkgName = watchBean.getS01PackageName();
                exitApp(pkgName);
                if (watchBean.getS03MType().equals("2") && !appSystems.contains(pkgName)) {
                    killAssignPkg(pkgName);
                }
            }
        }
    }

    /**
     * 上课禁用退出应用
     */
    public void launchClassHome(){
        AudioFocusManager.getInstance().getFlag();
        AudioFocusManager.getInstance().requestAudioFocus(AudioManager.AUDIOFOCUS_GAIN);
        AppLauncherUtils.launchHome();

        killApp();
    }

    /**
     * 退出应用
     */
    private void exitApp(String pkgName){
        List<ActivityManager.RunningTaskInfo> runningTasks = mActivityManager.getRunningTasks(1);
        if (runningTasks != null && !runningTasks.isEmpty()) {
            ActivityManager.RunningTaskInfo topTask = runningTasks.get(0);
            String packageName = topTask.topActivity.getPackageName();
            String className = topTask.topActivity.getClassName();
            LogUtil.i(TAG, "exitApp: pkgName " + pkgName + " packageName " + packageName
                    + " className " + className, LogUtil.TYPE_RELEASE);
            if (packageName.equals(pkgName)) {
                AppLauncherUtils.launchHome();
            } else {
                String task = topTask.topActivity.toString();
                LogUtil.i(TAG, "exitApp: task " + task + " pkgName " + pkgName + " isBuilt "
                        + isBuilt(task, pkgName), LogUtil.DEBUG);
                if (!task.contains("LauncherActivity")){
                    if (isBuilt(task,pkgName)){
                        AppLauncherUtils.launchHome();
                    }
                }
            }
        }
    }

    private boolean isBuilt(String task, String pkgName) {
        LogUtil.i(TAG, "isBuilt: pkgName " + pkgName + " task " + task, LogUtil.DEBUG);
        switch (pkgName) {
            case PACKAGE_COURSE:
                if (task.contains("CourseActivity")) {
                    return true;
                }
                break;

            case PACKAGE_UTIL:
                if (task.contains("toolapp.activity") || task.contains("moudle.alarm")
                        || task.contains("moudle.calendar")) {
                    return true;
                }
                break;

            case PACKAGE_MEET:
                if (task.contains("meet")) {
                    return true;
                }
                break;

            case PACKAGE_WALLET:
                if (task.contains("WalletActivity")) {
                    return true;
                }
                break;

            case LAUNCHER_STYLE:
                if (task.contains("WorkspaceSelectorActivity")) {
                    return true;
                }
                break;
            case PACKAGE_RECORD:
                if (task.contains("soundrecorder") || task.contains("moudle.record")) {
                    return true;
                }
                break;

            case PACKAGE_FACERECOGNITION:
                if (task.contains("com.android.settings") || task.contains("cn.heils.faceunlock")) {
                    return true;
                }
                break;
            case PACKAGE_VIDEO:
                if (task.contains("album.activity")) {
                    return true;
                }
                break;
            case PACKAGE_MICRO_CHAT:
                if (task.contains("com.baehug.videochat.activity")) {
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * 杀死所有运行的应用
     */
    public void killApp() {
        List<ActivityManager.RunningAppProcessInfo> processes = mActivityManager.getRunningAppProcesses();
        if (processes == null || processes.isEmpty()) {
            LogUtil.i(TAG, "killAllApp: watchAppBeans is null", LogUtil.TYPE_RELEASE);
            return;
        }
        List<String> appSystems = Arrays.asList(LAUNCHER_APP);
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (appSystems.contains(process.processName)) {
                LogUtil.i(TAG, "killAllApp: 白名单应用 " + process.processName, LogUtil.TYPE_RELEASE);
                continue;
            }
            LogUtil.i(TAG, "killAllApp: processName " + process.processName, LogUtil.TYPE_RELEASE);
            killAssignPkg(process.processName);
        }
    }

    /**
     * 杀掉指定应用
     *
     * @param pkgName 包名
     */
    private void killAssignPkg(String pkgName){
        Method method = null;
        try {
            method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(mActivityManager, pkgName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加上课禁用任务
     */
    public void addClassBanTask() {
        //获取本地上课禁用数据
        String param = (String) SharedPreferencesUtils.getParam(App.getInstance(),
                SharedPreferencesUtils.getOverSilenceTimeKey(), "");
        LogUtil.i(TAG, "addClassBanTask: param " + param, LogUtil.TYPE_RELEASE);
        if (TextUtils.isEmpty(param)) {
            LogUtil.i(TAG, "addClassBanTask: 今日没有需要添加的禁用任务", LogUtil.TYPE_RELEASE);
            return;
        }
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == mLastDay && param.equals(mLastParam)) {
            LogUtil.i(TAG, "addClassBanTask: 禁用任务已添加。 " + mLastParam + " " + param, LogUtil.TYPE_RELEASE);
            return;
        }
        mLastDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        mLastParam = param;
        String[] split = param.split(",");
        for (String str : split) {
            String[] arr = str.split("-");
            if (arr.length < 4 || !LauncherAppManager.isOpenInClass(arr[2]) ||
                    !LauncherAppManager.isOpenToday(LauncherAppManager.splitStr(arr[3]))) {
                LogUtil.i(TAG, "addClassBanTask: " + LauncherAppManager.getWhatDay() + "当前时间段没有上课禁用。 " + str, LogUtil.TYPE_RELEASE);
                continue;
            }
            String time = arr[0];
            long l = TimeUtils.getClassBanTime(time);
            long period = l - System.currentTimeMillis();
            if (l == -1 || period <= 0) {
                LogUtil.i(TAG, "addClassBanTask: 已经是上课禁用时间，不需要添加定时任务。 " + str, LogUtil.TYPE_RELEASE);
                continue;
            }
            LogUtil.i(TAG, "addClassBanTask: 添加一条任务 time " + time + " period " + period
                    + " " + LauncherAppManager.getWhatDay(), LogUtil.TYPE_RELEASE);
            RepeatTaskHelper.getInstance().addClassBanTask(period, XmxxCmdConstant.CLASS_BAN_START + time);
        }
    }

    /**
     * 退出最近任务
     */
    private void exitRunningTasks(String list) {
        if (!list.contains(mClassBanType)) {
            return;
        }
        try {
            List<ActivityManager.RunningTaskInfo> runningTasks = mActivityManager.getRunningTasks(1);
            if (runningTasks != null && !runningTasks.isEmpty()) {
                ActivityManager.RunningTaskInfo topTask = runningTasks.get(0);
                String packageName = topTask.topActivity.getPackageName();
                String className = topTask.topActivity.getClassName();
                if (packageName.equals("com.android.systemui")
                        && className.contains("com.android.systemui.recents.RecentsActivity")) {
                    LogUtil.i(TAG, "exitRunningTasks: ", LogUtil.TYPE_RELEASE);
                    AppLauncherUtils.launchHome();
                }
            }
        } catch (SecurityException ignored) {

        }
    }
}
