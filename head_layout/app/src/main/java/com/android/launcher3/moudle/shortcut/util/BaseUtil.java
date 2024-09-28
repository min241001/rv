package com.android.launcher3.moudle.shortcut.util;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.text.TextUtils;
import android.view.View;

import com.android.launcher3.App;
import com.android.launcher3.R;
import com.android.launcher3.common.utils.BatteryUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.WifiUtil;

import java.text.DecimalFormat;

public class BaseUtil {

    private static final int MIX_BATTERY = 20;// 最小电量
    private static int mLastViewId;
    private static long mLastTime;
    private static long mLastTime2;
    private static long mLastTime3;
    private static long mLastTime4;
    public static final long DURATION_200 = 200;
    public static final long DURATION_300 = 300;
    public static final long DURATION_400 = 400;
    public static final long DURATION_500 = 500;
    public static final long DURATION_600 = 600;
    public static final long DURATION_800 = 800;
    public static final String[] UNIT = {"B", "KB", "MB", "GB", "TB"};
    private static int mCount = 0;
    private static long mLastClickTime = 0L;

    /**
     * 判断是否有闹钟
     *
     * @param context context
     * @return true / false
     */
    public static boolean isAlarmClock(Context context) {
        if (context == null) {
            return false;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        AlarmManager.AlarmClockInfo info = alarmManager.getNextAlarmClock();
        return info != null;
    }

    /**
     * 查询app是否存在
     *
     * @param context context
     * @param pkgName 包名
     * @return 是否存在
     */
    public static boolean isAppExit(Context context, String pkgName) {
        if (context == null || TextUtils.isEmpty(pkgName)) {
            LogUtil.i("context or pkgName is null", LogUtil.TYPE_RELEASE);
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(pkgName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    /**
     * 根据电量获取图片
     *
     * @param battery 电量
     * @return 图片资源id
     */
    public static int getBatteryResId(int battery) {
        return battery > MIX_BATTERY ? R.drawable.pb_battery_normal : R.drawable.pb_battery_low;
    }

    /**
     * 获取wifi图片资源
     *
     * @return 图片资源id
     */
    public static int getWifiResId() {
        int signalStrength = WifiUtil.getCurrentNetworkRssi(App.getInstance());
        LogUtil.d(String.format("wifi信号强度：%d", signalStrength), LogUtil.TYPE_RELEASE);
        return SignalStrengthUtils.getWifiResourceId(signalStrength);
    }

    /**
     * 获取电量
     *
     * @return 电量数值0-100
     */
    public static int getBattery() {
        int battery = BatteryUtil.getInstance().getBattery();
        boolean isCharging = BatteryUtil.getInstance().isCharging();
        LogUtil.d(String.format("电池电量：%d, 是否正常充电：%s", battery, isCharging ? "是" : "否"), LogUtil.TYPE_RELEASE);
        return battery;
    }

    /**
     * 获取网络类型
     *
     * @return 网络类型
     */
    public static String getNetworkType() {
        return String.format("%s %s", VolteUtil.getVolteText(), PhoneSIMCardUtil.getInstance().getNetworkType());
    }

    public static boolean repeatedClicks() {
        if (System.currentTimeMillis() - mLastTime >= DURATION_800) {
            mLastTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public static boolean repeatedClicks(long duration) {
        if (System.currentTimeMillis() - mLastTime2 >= duration) {
            mLastTime2 = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public static boolean repeatedClicks(View view) {
        if (view == null) {
            return false;
        }
        if (view.getId() != mLastViewId) {
            mLastViewId = view.getId();
            if (System.currentTimeMillis() - mLastTime3 >= DURATION_300) {
                mLastTime3 = System.currentTimeMillis();
                return true;
            }
            return false;
        } else {
            mLastViewId = view.getId();
            if (System.currentTimeMillis() - mLastTime3 >= DURATION_800) {
                mLastTime3 = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    public static boolean repeatedClicks(View view, long duration) {
        if (view == null) {
            return false;
        }
        if (view.getId() != mLastViewId) {
            mLastViewId = view.getId();
            if (System.currentTimeMillis() - mLastTime4 >= DURATION_200) {
                mLastTime4 = System.currentTimeMillis();
                return true;
            }
            return false;
        } else {
            mLastViewId = view.getId();
            if (System.currentTimeMillis() - mLastTime4 >= duration) {
                mLastTime4 = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    /**
     * 是否震动模式
     *
     * @return 是否震动模式
     */
    public static boolean isVibrate() {
        AudioManager manager = (AudioManager) App.getInstance().getSystemService(Context.AUDIO_SERVICE);
        if (manager == null) {
            return false;
        }
        return manager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
    }

    /**
     * 设置响铃模式
     * AudioManager.RINGER_MODE_SILENT  静音不震动
     * AudioManager.RINGER_MODE_VIBRATE 静音震动
     * AudioManager.RINGER_MODE_NORMAL  响铃
     *
     * @param context   context
     * @param isVibrate 是否震动
     */
    public static void setRingerMode(Context context, boolean isVibrate) {
        if (context == null) {
            return;
        }
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager == null) {
            return;
        }
        manager.setRingerMode(isVibrate ? AudioManager.RINGER_MODE_SILENT : AudioManager.RINGER_MODE_NORMAL);
    }

    /**
     * 内存转换
     *
     * @param size 字节数
     * @return
     */
    public static String memoryConvert(long size) {
        if (size <= 0) {
            return "0";
        }
        int i = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, i)) + " " + UNIT[i];
    }

    /**
     * 连续点击工具判断
     */
    public static boolean continuousClicks() {
        if (mCount >= 6) {
            mCount = 0;
            return true;
        } else {
            if (mLastClickTime == 0L || System.currentTimeMillis() < mLastClickTime + 200) {
                mLastClickTime = System.currentTimeMillis();
                mCount++;
            } else {
                mCount = 0;
            }
            return false;
        }
    }
}
