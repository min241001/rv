package com.android.launcher3.moudle.launcher.util;

import android.provider.Settings;

import com.android.launcher3.App;
import com.android.launcher3.common.constant.SettingsConstant;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.common.utils.ToastUtils;

public class VersionUtils {

    private static final String TAG = "版本管理--->>>";
    private VersionUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 设置ximinegOs的版本号
     * @return
     */
    public static String getXimengOSVersion() {
        String ximengOsVersion = Settings.System.getString(App.getInstance().getContentResolver(), SettingsConstant.WATCH_XIMENG_OS_VERSION);
        String launcherVersion = AppUtils.getAppVersion(App.getInstance());
        if (StringUtils.isBlank(launcherVersion)) {
            LogUtil.d(TAG + "launcher版本号为空", LogUtil.TYPE_RELEASE);
            return "ximengOS V12024 8.0.0";
        }
        LogUtil.d(TAG + "ximengOsVersion=" + ximengOsVersion + ",launcherVersion=" + launcherVersion, LogUtil.TYPE_RELEASE);
                // 如果版本不一致，则更新最新的版本号到setting中
        if (!launcherVersion.equals(ximengOsVersion)) {
            Settings.System.putString(App.getInstance().getContentResolver(), SettingsConstant.WATCH_XIMENG_OS_VERSION, launcherVersion);
        }

        LogUtil.d(TAG + "从setting里面获取的系统版本号为:【" +
                Settings.System.getString(App.getInstance().getContentResolver(), SettingsConstant.WATCH_XIMENG_OS_VERSION) +
                "】,setting key为：" +  SettingsConstant.WATCH_XIMENG_OS_VERSION, LogUtil.TYPE_RELEASE);
        ToastUtils.show4Debug("ximinegOs的版本号为：" + launcherVersion);
        return launcherVersion;
    }

    public static void clearXimengOSVersion() {
        Settings.System.putString(App.getInstance().getContentResolver(), SettingsConstant.WATCH_XIMENG_OS_VERSION, "");
    }

}
