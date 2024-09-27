package com.android.launcher3.common.data;

import static com.android.launcher3.common.constant.SettingsConstant.ANIM_MODE;
import static com.android.launcher3.common.constant.SettingsConstant.CHAT_CALL_STATE;
import static com.android.launcher3.common.constant.SettingsConstant.CITY_CODE;
import static com.android.launcher3.common.constant.SettingsConstant.SIM_MODE;
import static com.android.launcher3.common.constant.SettingsConstant.WATCH_POWER;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import com.android.launcher3.common.constant.SettingsConstant;
import com.android.launcher3.common.utils.SharedPreferencesUtils;

import java.io.File;

/**
 * 本地数据存储
 */
public class AppLocalData {

    private AppLocalData() {
    }

    private static AppLocalData instance;

    public synchronized static AppLocalData getInstance() {
        if (instance == null) {
            instance = new AppLocalData();
        }
        return instance;
    }

    private Context context;
    private ContentResolver contentResolver;

    private static final String SP_DEFAULT_WALLPAPER_KEY = "_DEFAULT_WALLPAPER_ID_";
    private static final String SP_DEFAULT_WORKSPACE_KEY = "_DEFAULT_WORKSPACE_ID_";
    private static final String SP_DEFAULT_FACE_KEY = "_DEFAULT_FACE_ID_";
    private static final String SP_DEFAULT_CLASSNAME_KEY = "_DEFAULT_FACE_CLASSNAME_";
    private static final String SP_DEFAULT_FILEPATH_KEY = "_DEFAULT_FACE_FILEPATH_";
    private static final String SP_SHUTDOWN = "_DEFAULT_SHUTDOWN_";
    private static final String SP_POWER = "_DEFAULT_POWER_";
    private static final String SP_WIFI = "_DEFAULT_WIFI_";
    private static final String SP_MOBILE_DATA = "_DEFAULT_MOBILE_DAT_";
    private static final String SP_BLUETOOTH = "_DEFAULT_BLUETOOTH_";

    private static final String SP_BREATHE_RHYTHM = "_DEFAULT_RHYTHM_";
    private static final String SP_BREATHE_DURATION = "_DEFAULT_DURATION_";

    private static final String SP_SPLASH = "_SP_SPLASH_";

    public void init(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    // 表盘ID
    public int getFaceDefaultId() {

        // 对于y29w 默认显示夜色刻度表盘
        // y29w设备版本号：DW_W377E_EW16B_V30_HM_Y09+L_00_A_CN_ZX_XMSH_42_64X8_20240629_1551
        //DW_W377E_EW16B_V30_HM_Y29W_00_XM_CN_ZX_XMSH_42_64X8_CN_240X296_V01_20240629_1551.pac

        if (Build.DISPLAY != null && !Build.DISPLAY.equals("")) {
            String[] split = Build.DISPLAY.split("\\+");
            if (split[0].equals("DW_W377E_EW16B_V30_HM_Y29W")) {
                return (int) SharedPreferencesUtils.getParam(context, SP_DEFAULT_FACE_KEY, 6);
            }
        }

        return (int) SharedPreferencesUtils.getParam(context, SP_DEFAULT_FACE_KEY, 1);
    }

    public void setFaceDefaultId(int id) {
        SharedPreferencesUtils.setParam(context, SP_DEFAULT_FACE_KEY, id);
    }

    //应用监督数据
    public void setAppData(int flag) {
        Settings.System.putInt(contentResolver, SettingsConstant.APP_UPDATE_FLAG, flag);
    }

    //应用监督禁用掉相册和视频
    public void setPictureVideoStatus(int flag) {
        Settings.System.putInt(contentResolver, SettingsConstant.VIDEO_PICTURE_UPDATE_FLAG, flag);
    }

    public void setAppDisableList(String list) {
        Settings.System.putString(contentResolver, SettingsConstant.APP_DISABLE_LIST, list);
    }

    public String getAppDisableList()  {
        return Settings.System.getString(contentResolver, SettingsConstant.APP_DISABLE_LIST);
    }

    //上课禁用
    public void setIsInClassTime(int flag) {
        Settings.System.putInt(contentResolver, SettingsConstant.IS_IN_CLASS_TIME, flag);
    }

    //支付宝是否已激活 0 是 未激活  1 已激活
    public void setAlipayData(int flag) {
        Settings.System.putInt(contentResolver, SettingsConstant.ALIPAY_REGISTER_FLAG, flag);
    }

    //获取设置中灵动岛开关状态
    //广播actioon com.baehug.setting.lslang 参数 1打开 关闭 0
    public int getIslandOpenState() {
        return Settings.System.getInt(contentResolver, SettingsConstant.LS_LAND_MODE, 0);
    }


    public String getFaceClassName() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return (String) SharedPreferencesUtils.getParam(context, SP_DEFAULT_CLASSNAME_KEY, "");
        } else {
            String themeName = Settings.System.getString(contentResolver, "THEME_NAME");
            return themeName != null ? themeName : "";
        }
    }

    public void setFaceClassName(String className) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            SharedPreferencesUtils.setParam(context, SP_DEFAULT_CLASSNAME_KEY, className);
        } else {
            Settings.System.putString(contentResolver, "THEME_NAME", className);
        }
    }

    public String getFaceFilePath() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return (String) SharedPreferencesUtils.getParam(context, SP_DEFAULT_FILEPATH_KEY, "");
        } else {
            String themePath = Settings.System.getString(contentResolver, "THEME_PATH");
            return themePath != null ? themePath : "";
        }
    }

    public void setFaceFilePath(String filePath) {
        String path = Environment.getExternalStorageDirectory() + File.separator + filePath;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            SharedPreferencesUtils.setParam(context, SP_DEFAULT_FILEPATH_KEY, path);
        } else {
            Settings.System.putString(contentResolver, "THEME_PATH", path);
        }
    }

    // 壁纸ID
    public int getWallpaperDefaultId() {
        return (int) SharedPreferencesUtils.getParam(context, SP_DEFAULT_WALLPAPER_KEY, 1);
    }

    public void setWallpaperDefaultId(int id) {
        SharedPreferencesUtils.setParam(context, SP_DEFAULT_WALLPAPER_KEY, id);
    }

    // 桌面分隔ID
    public int getWorkspaceDefaultId() {
        return (int) SharedPreferencesUtils.getParam(context, SP_DEFAULT_WORKSPACE_KEY, 1);
    }

    public void setWorkspaceDefaultId(int id) {
        SharedPreferencesUtils.setParam(context, SP_DEFAULT_WORKSPACE_KEY, id);
    }

    // 保存手表ID
    public void setWatchId(String waAcctId) {
        Settings.System.putString(contentResolver, SettingsConstant.WATCH_ACCOUNT_ID, waAcctId);
    }

    public String getWatchId() {
        return Settings.System.getString(contentResolver, SettingsConstant.WATCH_ACCOUNT_ID);
    }

    //耗电日志 0 - 开   1 - 关
    public void setBatterySwitch(int flag) {
        Settings.System.putInt(contentResolver, SettingsConstant.BATTERY_CHANGE, flag);
    }

    //是否进行耗电日志 0 - 开   1 - 关
    public int getBatterySwitch() {
        try {
            return Settings.System.getInt(contentResolver, SettingsConstant.BATTERY_CHANGE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //电池电量
    public void setBattery(int batteryPct) {
        Settings.System.putString(contentResolver, SettingsConstant.WATCH_BATTERY_CHANGE, String.valueOf(batteryPct));
    }

    public int getBattery() throws Settings.SettingNotFoundException {
        return Settings.System.getInt(contentResolver, SettingsConstant.WATCH_BATTERY_CHANGE, 0);
    }

    public int getPowerSwitch() {
        return Settings.System.getInt(contentResolver, SettingsConstant.POWER_SWITCH, 0);
    }

    //滑动
    public int getSlide() {
        return Settings.System.getInt(contentResolver, SettingsConstant.SLIDE, 1);
    }

    public void setShutdown(boolean status) {
        SharedPreferencesUtils.setParam(context, SP_SHUTDOWN, status);
    }

    public boolean getShutdown() {
        return (boolean) SharedPreferencesUtils.getParam(context, SP_SHUTDOWN, true);
    }

    public void setPower(boolean status) {
        Settings.System.putInt(contentResolver, WATCH_POWER, status ? 1 : 0);
        SharedPreferencesUtils.setParam(context, SP_POWER, status);
    }

    public boolean getPower() {
        return (boolean) SharedPreferencesUtils.getParam(context, SP_POWER, false);
    }

    //设置wifi状态
    public void setWifi(boolean status) {
        SharedPreferencesUtils.setParam(context, SP_WIFI, status);
    }

    //获取wifi状态
    public boolean getWifi() {
        return (boolean) SharedPreferencesUtils.getParam(context, SP_WIFI, false);
    }

    //设置数据流量状态
    public void setMobileData(boolean status) {
        SharedPreferencesUtils.setParam(context, SP_MOBILE_DATA, status);
    }

    //获取数据流量状态
    public boolean getMobileData() {
        return (boolean) SharedPreferencesUtils.getParam(context, SP_MOBILE_DATA, false);
    }

    //设置蓝牙状态
    public void setBluetooth(boolean status) {
        SharedPreferencesUtils.setParam(context, SP_BLUETOOTH, status);
    }

    //获取蓝牙状态
    public boolean getBluetooth() {
        return (boolean) SharedPreferencesUtils.getParam(context, SP_BLUETOOTH, false);
    }

    //获取城市code 0为不设置
    public int getCityCode() {
        return Settings.System.getInt(contentResolver, CITY_CODE, 0);
    }

    //获取视频通话和语音通话状态
    public boolean getChatCallState() {
        return Settings.System.getInt(contentResolver, CHAT_CALL_STATE, 0) == 1;
    }

    public int getSimMode() {
        return Settings.System.getInt(contentResolver, SIM_MODE, 1);
    }


    public void setRhythm(int id) {
        SharedPreferencesUtils.setParam(context, SP_BREATHE_RHYTHM, id);
    }

    public int getRhythm() {
        return (int) SharedPreferencesUtils.getParam(context, SP_BREATHE_RHYTHM, 0);
    }

    public void setDuration(int id) {
        SharedPreferencesUtils.setParam(context, SP_BREATHE_DURATION, id);
    }

    public int getDuration() {
        return (int) SharedPreferencesUtils.getParam(context, SP_BREATHE_DURATION, 0);
    }

    public void setSpSplash(boolean status) {
        SharedPreferencesUtils.setParam(context, SP_SPLASH, status);
    }

    public Boolean getSpSplash() {
        return (boolean) SharedPreferencesUtils.getParam(context, SP_SPLASH, true);
    }

    public int getAnimMode() {
        return (int) Settings.System.getInt(contentResolver, ANIM_MODE, 0);
    }
}
