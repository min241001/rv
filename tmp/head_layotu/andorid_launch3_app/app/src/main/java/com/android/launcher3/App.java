package com.android.launcher3;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.BatteryUtil;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.moudle.alarm.utils.AlarmUtils;
import com.android.launcher3.moudle.launcher.util.WatchAccountUtils;
import com.android.launcher3.moudle.shortcut.service.StateChangeService;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.moudle.shortcut.util.ShutcutMenuUtil;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.netty.schedule.RepeatTaskHelper;
import com.android.launcher3.receiver.PhoneReceiver;
import com.android.launcher3.receiver.SystemBroadcastReceiver;
import com.android.launcher3.utils.Battery;
import com.android.launcher3.utils.SPManager;
import com.baehug.callui.phone.manager.CallerShowManager;
import com.baehug.callui.phone.receiver.PhoneStateReceiver;
import com.baehug.lib.ipc.util.ServiceUtils;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends CommonApp {

    private static final String TAG = "App_121212";
    public static boolean IS_DEBUG = false;// 非调试阶段

    private Battery mBattery;

    private SystemBroadcastReceiver mReceiver;
    private PhoneReceiver phoneReceiver;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        String appVersion = "0.0.0";
        // 此方法中的log不可修改成LogUtil，否则会报热修复资源错误。
        try {
            appVersion = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            Log.e(TAG, "attachBaseContext: ", e);
        }
        Log.i(TAG, "attachBaseContext: appVersion " + appVersion);
        // 热修复初始化
        SophixManager.getInstance()
                .setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub((mode, code, info, handlePatchVersion) -> {
                    // 补丁加载回调通知
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 表明补丁加载成功
                        Log.i(TAG, "attachBaseContext: onLoad 补丁加载成功 code " + code);
//                        LogUtil.i(TAG, "onLoad: 补丁加载成功 code " + code, LogUtil.TYPE_RELEASE);
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        SharedPreferencesUtils.setParam(this, "KEY_RESTART_LAUNCHER", true);
                        Log.i(TAG, "attachBaseContext: onLoad 新补丁生效需要重启 code " + code);
                        // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                        // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely结束进程，以此加快应用补丁。
                    } else {
                        Log.i(TAG, "attachBaseContext: onLoad code " + code + ", info " + info);
                        // https://help.aliyun.com/document_detail/434886.html?spm=a2c4g.434885.0.0.11903b93RjOXon 常见状态码
                        // 其它错误信息, 查看PatchStatus类说明
                    }
                }).initialize();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SophixManager.getInstance().queryAndLoadNewPatch();
        mBattery = new Battery(this);
        mReceiver = new SystemBroadcastReceiver(this);
        phoneReceiver = new PhoneReceiver();
        AppLocalData.getInstance().setAppData(1);
        //耗电日志标识
        AppLocalData.getInstance().setBatterySwitch(1);
        //是否在上课禁用时间段
        if (LauncherAppManager.isForbiddenInClass(this,"")){
            AppLocalData.getInstance().setIsInClassTime(0);
        } else {
            AppLocalData.getInstance().setIsInClassTime(1);
        }
        PhoneSIMCardUtil.getInstance().init(this);
        ShutcutMenuUtil.getInstance().init(this);
        BatteryUtil.getInstance().init(this);
        SPManager.getInstance();
        // 启动网络监听服务
        ServiceUtils.startService(this, StateChangeService.class);
        if (IS_DEBUG) {
            LogUtil.d("调试阶段，屏蔽该功能", LogUtil.TYPE_RELEASE);
            NettyManager.ifOpenNettyManager(false);
            WatchAccountUtils.ifOpenAcctUtil(false);
        } else {
            mBattery.register();
            mReceiver.start();
            NettyManager.ifOpenNettyManager(true);
            WatchAccountUtils.ifOpenAcctUtil(true);
            // 初始化netty 参数及配置
            NettyManager.initNettyCmdServiceAndParameter();
            RepeatTaskHelper.getInstance().initRepeatTaskWhenReboot();
        }
        CrashReport.initCrashReport(this, "3b74456e22", false);
        RepeatTaskHelper.getInstance().addCheckNettyTask((4*60*1000L));
        AlarmUtils.bootStartAlarm(this);
        initCallerShow();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (IS_DEBUG) {
            LogUtil.d("调试阶段，屏蔽该功能", LogUtil.TYPE_RELEASE);
        } else {
            mBattery.unregister();
            mReceiver.unregisterBroadcastReceiver();
        }
    }

    public void initCallerShow() {
        CallerShowManager.Companion.getInstance().initCallerShow(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(new PhoneStateReceiver(), filter);
    }
}