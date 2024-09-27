package com.android.launcher3.common.utils;

import static com.android.launcher3.common.CompileConfig.SYSTEM_RECORD;
import static com.android.launcher3.common.constant.AppPackageConstant.BLOOD_ACTION;
import static com.android.launcher3.common.constant.AppPackageConstant.BREATHE_ACTION;
import static com.android.launcher3.common.constant.AppPackageConstant.CAMERA_ACTION;
import static com.android.launcher3.common.constant.AppPackageConstant.GALLERY_ACTION;
import static com.android.launcher3.common.constant.AppPackageConstant.HEART_ACTION;
import static com.android.launcher3.common.constant.AppPackageConstant.MB_ACTION;
import static com.android.launcher3.common.constant.AppPackageConstant.PRESSURE_ACTION;
import static com.android.launcher3.common.constant.AppPackageConstant.TEMPERATURE_ACTION;
import static com.android.launcher3.common.constant.AppPackageConstant.VIDEO_ACTION;
import static com.android.launcher3.common.constant.SettingsConstant.ALIPAY_CHECK;
import static com.android.launcher3.common.constant.SettingsConstant.WATCH_Y09B;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.bean.WatchAppBean;
import com.android.launcher3.common.constant.AppPackageConstant;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.dialog.ComDialog;
import com.android.launcher3.common.mode.PowerSavingModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description：启动APP管理
 */
public class LauncherAppManager implements AliPayCheckResult {

    /**
     * 启动系统应用
     *
     * @param act
     * @param appBean
     */
    public static void launcherApp(Activity act, AppBean appBean) {
        if (appBean == null) {
            ToastUtils.show("应用未安装");
            return;
        }
        LogUtil.d("应用包名：" + appBean.getPackageName(), LogUtil.TYPE_RELEASE);

        if (isForbiddenInClass(act.getApplicationContext(), appBean.getPackageName())) {
            ToastUtils.show(R.string.app_forbidden_in_class);
            return;
        }

        if (isAppForbidden(act.getApplicationContext(), appBean.getPackageName())) {
            ToastUtils.show(R.string.app_forbidden);
            return;
        }


        Intent intent;
        // 是否是系统应用
        if (isSystemApp(appBean.getPackageName())) {
            switch (appBean.getPackageName()) {
                case AppPackageConstant.PACKAGE_CAMERA:// 相机
                    if (AppUtils.isAppInstalled(CommonApp.getInstance(), AppPackageConstant.PACKAGE_CAMERA)) {
                        intent = new Intent();
                        intent.setAction(CAMERA_ACTION);
                        act.startActivity(intent);
                    } else {
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("org.codeaurora.snapcam", "com.android.camera.CameraLauncher"));
                        startActivity(act, intent);
                    }
                    break;

                case AppPackageConstant.PACKAGE_GALLERY3D:// 相册
                    if (AppUtils.isAppInstalled(CommonApp.getInstance(), AppPackageConstant.PACKAGE_GALLERY3D)) {
                        intent = new Intent();
                        intent.setAction(GALLERY_ACTION);
                        act.startActivity(intent);
                    } else {
                        startVideo(act);
                    }
                    break;

                case AppPackageConstant.PACKAGE_RECORD:
                    if (SYSTEM_RECORD) {
                        intent = new Intent(Intent.ACTION_MAIN);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || Build.DISPLAY.contains(WATCH_Y09B)) {
                            intent.setComponent(new ComponentName("com.android.soundrecorder", "com.android.soundrecorder.SoundRecorder"));
                        } else {
                            intent.setComponent(new ComponentName("com.android.soundrecorder", "com.sprd.soundrecorder.RecorderActivity"));
                        }
                        startActivity(act, intent);
                        return;
                    }
                    intent = new Intent(AppPackageConstant.PACKAGE_RECORD_CUSTOM);
                    startActivity(act, intent);
                    break;
                case AppPackageConstant.PACKAGE_MUSIC:// 系统音乐
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setComponent(new ComponentName("simon.snow.music", "snow.music.activity.welcome.WelcomeActivity"));
                    startActivity(act, intent);
                    break;

                case AppPackageConstant.PACKAGE_FACERECOGNITION:// 人脸识别
                    ShortCutUtils.INSTANCE.startChooseLock(act);
                    break;

                case AppPackageConstant.PACKAGE_UTIL:// 工具
                    intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.launcher3", "com.android.launcher3.moudle.toolapp.activity.ToolAppActivity"));
                    startActivity(act, intent);
                    break;

                case AppPackageConstant.LAUNCHER_STYLE:// 风格
                    intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.launcher3", "com.android.launcher3.moudle.selector.view.WorkspaceSelectorActivity"));
                    startActivity(act, intent);
                    break;

                case AppPackageConstant.PACKAGE_VIDEO:// 视频
                    if (AppUtils.isAppInstalled(CommonApp.getInstance(), AppPackageConstant.PACKAGE_GALLERY3D)) {
                        try {
                            intent = new Intent();
                            intent.setAction(VIDEO_ACTION);
                            act.startActivity(intent);
                        } catch (Exception e) {
                            startVideo(act);
                        }
                    } else {
                        startVideo(act);
                    }
                    break;

                case AppPackageConstant.PACKAGE_WALLET:
                    if (PhoneSIMCardUtil.getInstance().SIMHint(act.getApplicationContext(), AppPackageConstant.PACKAGE_WALLET)) {
                        return;
                    }
                    intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.launcher3", "com.android.launcher3.moudle.wallet.activity.WalletActivity"));
                    startActivity(act, intent);
                    break;

                case AppPackageConstant.PACKAGE_MEET:
                    if (PhoneSIMCardUtil.getInstance().SIMHint(act.getApplicationContext(), AppPackageConstant.PACKAGE_WALLET)) {
                        return;
                    }

                    intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.launcher3", "com.android.launcher3.moudle.meet.view.AddFriendActivity"));
                    startActivity(act, intent);
                    break;

                case AppPackageConstant.PACKAGE_POWER:
                    ComDialog.Builder builder = new ComDialog.Builder(act, () -> {
                        AppLocalData.getInstance().setPower(true);
                        PowerSavingModel.INSTANCE.openPowerSaving();
                        AppLauncherUtils.jumpActivity(act, 0);
                    });
                    builder.create();
                    builder.setTitle(act.getString(com.android.launcher3.common.R.string.super_power_mode));
                    builder.setContent(act.getString(R.string.open_power));
                    builder.show();
                    break;

                case AppPackageConstant.PACKAGE_COURSE:
                    if (PhoneSIMCardUtil.getInstance().SIMHint(act.getApplicationContext(), AppPackageConstant.PACKAGE_COURSE)) {
                        return;
                    }
                    intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.launcher3", "com.android.launcher3.moudle.toolapp.activity.CourseActivity"));
                    startActivity(act, intent);
                    break;

                case AppPackageConstant.PACKAGE_BREATHE:
                    intent = new Intent();
                    intent.setAction(BREATHE_ACTION);
                    act.startActivity(intent);
                    break;

                case AppPackageConstant.PACKAGE_LIGHT:

                    intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.launcher3", "com.android.launcher3.moudle.light.LightActivity"));
                    startActivity(act, intent);

                    break;

                case AppPackageConstant.PACKAGE_BLOOD://血氧
                    intent = new Intent();
                    intent.setAction(BLOOD_ACTION);
                    act.startActivity(intent);
                    break;

                case AppPackageConstant.PACKAGE_PRESSURE://血压
                    intent = new Intent();
                    intent.setAction(PRESSURE_ACTION);
                    act.startActivity(intent);
                    break;
                case AppPackageConstant.PACKAGE_HEART://心率
                    intent = new Intent();
                    intent.setAction(HEART_ACTION);
                    act.startActivity(intent);
                    break;

                case AppPackageConstant.PACKAGE_TEMPERATURE://体温
                    intent = new Intent();
                    intent.setAction(TEMPERATURE_ACTION);
                    act.startActivity(intent);
                    break;

                case AppPackageConstant.PACKAGE_MB://体温
                    intent = new Intent();
                    intent.setAction(MB_ACTION);
                    act.startActivity(intent);
                    break;
            }
        } else {
            if (isNeedSIMApp(appBean.getPackageName()) && PhoneSIMCardUtil.getInstance().SIMHint(act.getApplicationContext(), appBean.getPackageName())) {
                return;
            }
            // 通过页面来启动
            if (appBean.getPackageName().toUpperCase().contains("ACTIVITY") && appBean.getPreinstall() == 1) {
                try {
                    intent = new Intent(act.getApplicationContext(), Class.forName(appBean.getPackageName()));
                    startActivity(act, intent);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            // 通过包名启动应用
            launchAppByPackageName(act, appBean.getPackageName());
        }
    }

    /**
     * 是否是系统应用
     *
     * @param packageName 包名
     * @return
     */
    private static boolean isSystemApp(String packageName) {
        List<String> packageNames = Arrays.asList(
                AppPackageConstant.PACKAGE_FACERECOGNITION, // 人脸识别
                AppPackageConstant.PACKAGE_CAMERA, // 系统相机
                AppPackageConstant.PACKAGE_GALLERY3D, // 系统相册
                AppPackageConstant.PACKAGE_RECORD, // 系统录音
                AppPackageConstant.PACKAGE_VIDEO, // 视频
                AppPackageConstant.PACKAGE_UTIL, // 工具
                AppPackageConstant.PACKAGE_MUSIC, // 系统音乐
                AppPackageConstant.PACKAGE_WALLET,//西萌钱包
                AppPackageConstant.PACKAGE_MEET,//加好友
                AppPackageConstant.PACKAGE_POWER,
                AppPackageConstant.PACKAGE_COURSE,//课程表
                AppPackageConstant.LAUNCHER_STYLE,//风格
                AppPackageConstant.PACKAGE_BREATHE,//呼吸灯
                AppPackageConstant.PACKAGE_LIGHT,//手电筒
                AppPackageConstant.PACKAGE_BLOOD,//血氧
                AppPackageConstant.PACKAGE_PRESSURE,//血压
                AppPackageConstant.PACKAGE_HEART,//心率
                AppPackageConstant.PACKAGE_MB,//秒表
                AppPackageConstant.PACKAGE_TEMPERATURE

        );
        return packageNames.contains(packageName);
    }

    /**
     * 是否不需要未插卡拦截操作
     *
     * @param packageName 包名
     * @return
     */
    public static boolean isNeedSIMApp(String packageName) {
        List<String> packageNames = Arrays.asList(
                "com.baehug.videochat", // 视频通话
                "com.baehug.theme", // 西萌主题
                "com.baehug.appstore", // 应用市场
                "com.baehug.watch.wechat", // 微聊
                "com.baehug.contacts", // 联系人
                "com.baehug.sms" // 短信
        );
        return packageNames.contains(packageName);
    }

    /**
     * 是否已被家长端禁用
     */
    public static boolean isAppForbidden(Context context, String packageName) {
        List<WatchAppBean> watchAppBeans = SharedPreferencesUtils.getList(context, AppLocalData.getInstance().getWatchId());
        if (watchAppBeans != null && !watchAppBeans.isEmpty()) {
            for (WatchAppBean watchBean : watchAppBeans) {
                if (packageName.equals(watchBean.getS01PackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否已被家长端禁用,应用监督
     */
    public static boolean isAppForbidden(Context context) {
        List<WatchAppBean> watchAppBeans = SharedPreferencesUtils.getList(context, AppLocalData.getInstance().getWatchId());
        if (watchAppBeans != null && !watchAppBeans.isEmpty()) {
            for (WatchAppBean watchBean : watchAppBeans) {
                if (watchBean.getS03MType().equals("2")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否是上课禁用的时间段
     */
    public static boolean isForbiddenInClass(Context context, String packageName) {
        //先判断是否是短信或者拨号的包名(上课禁用期间可以使用短信或者拨号)
//        if (isAnyApp(packageName)) {
//            return false;
//        }
        //获取本地上课禁用数据
        String param = (String) SharedPreferencesUtils.getParam(context, SharedPreferencesUtils.getOverSilenceTimeKey(), "");
        if (param != null && !param.isEmpty()) {
            //数据结构[10:30-14:30-1-0111110,10:30-14:30-1-0111110,10:30-14:30-1-0111110]
            //判断是否是多条数据
            if (param.contains(",")) {
                //是多条数据进行切割
                String[] splitA = param.split(",");
                for (String resultA : splitA) {
                    String[] splitB = resultA.split("-");
                    //先判断今天是否在上课禁用的日期
                    if (isOpenInClass(splitB[2]) && isOpenToday(splitStr(splitB[3]))) {
                        //再判断当前时间是否在上课禁用时间内
                        boolean compareStartTimeResult = compareStartTime(getTime(), splitB[0]);
                        boolean compareEndTimeResult = compareEndTime(getTime(), splitB[1]);
                        if (compareStartTimeResult && compareEndTimeResult) {
                            AppLocalData.getInstance().setIsInClassTime(0);
                            return true;
                        }
                    }
                }
                AppLocalData.getInstance().setIsInClassTime(1);
                return false;
            } else {
                //单条数据结构 (10:30-14:30-1-0111110) 直接进行切割字符串
                String[] splitC = param.split("-");
                //先判断今天是否在上课禁用的日期
                if (isOpenInClass(splitC[2]) && isOpenToday(splitStr(splitC[3]))) {
                    //再判断当前时间是否在上课禁用时间内
                    boolean compareStartTimeResult = compareStartTime(getTime(), splitC[0]);
                    boolean compareEndTimeResult = compareEndTime(getTime(), splitC[1]);
                    if (compareStartTimeResult && compareEndTimeResult) {
                        AppLocalData.getInstance().setIsInClassTime(0);
                        return true;
                    }
                }
                AppLocalData.getInstance().setIsInClassTime(1);
                return false;
            }
        }
        AppLocalData.getInstance().setIsInClassTime(1);
        return false;
    }

    //判断今天是否开启上课禁用
    public static boolean isOpenToday(List<Integer> indexList) {
        //获取当前是星期几
        String today = getWhatDay();
        String dayOfWeekString;
        for (Integer index : indexList) {
            switch (index) {
                case 0:
                    dayOfWeekString = "星期日";
                    break;
                case 1:
                    dayOfWeekString = "星期一";
                    break;
                case 2:
                    dayOfWeekString = "星期二";
                    break;
                case 3:
                    dayOfWeekString = "星期三";
                    break;
                case 4:
                    dayOfWeekString = "星期四";
                    break;
                case 5:
                    dayOfWeekString = "星期五";
                    break;
                case 6:
                    dayOfWeekString = "星期六";
                    break;
                default:
                    dayOfWeekString = "未知";
                    break;
            }
            if (today.equals(dayOfWeekString)) {
                return true;
            }
        }
        return false;
    }

    //判断启动的应用包名是否是拨号和短信
    public static boolean isAnyApp(String packageName) {
        return packageName.equals("com.baehug.sms") || packageName.equals("com.baehug.dialer");
    }

    //判断当前的禁用时段是否开启
    public static boolean isOpenInClass(String str) {
        return "1".equals(str);
    }

    //拆分字符串
    public static List<Integer> splitStr(String str) {
        char[] chars = str.toCharArray();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '1') {
                list.add(i);
            }
        }
        return list;
    }

    //获取当前是星期几
    public static String getWhatDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekString;
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                dayOfWeekString = "星期日";
                break;
            case Calendar.MONDAY:
                dayOfWeekString = "星期一";
                break;
            case Calendar.TUESDAY:
                dayOfWeekString = "星期二";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeekString = "星期三";
                break;
            case Calendar.THURSDAY:
                dayOfWeekString = "星期四";
                break;
            case Calendar.FRIDAY:
                dayOfWeekString = "星期五";
                break;
            case Calendar.SATURDAY:
                dayOfWeekString = "星期六";
                break;
            default:
                dayOfWeekString = "未知";
                break;
        }
        return dayOfWeekString;
    }

    //比较开始时间大小
    public static boolean compareStartTime(String nowTime, String startTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date time1 = null;
        Date time2 = null;
        try {
            time1 = simpleDateFormat.parse(nowTime);
            time2 = simpleDateFormat.parse(startTime);
            if (time1.before(time2)) {
                // time1 在 time2 之前
                return false;
            } else if (time1.after(time2)) {
                // time1 在 time2 之后
                return true;
            } else {
                // time1 和 time2 相等
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    //比较结束时间大小
    public static boolean compareEndTime(String nowTime, String endTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date time1 = null;
        Date time2 = null;
        try {
            time1 = simpleDateFormat.parse(nowTime);
            time2 = simpleDateFormat.parse(endTime);
            if (time1.before(time2)) {
                // time1 在 time2 之前
                return true;
            } else if (time1.after(time2)) {
                // time1 在 time2 之后
                return false;
            } else {
                // time1 和 time2 相等
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    //获取当前时间(hour + ":" + minute)
    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        return hour + ":" + minute;
    }

    // 启动默认活动
    private static void launchAppByPackageName(Activity context, String packageName) {
        String route = "video";
        if (packageName.equals("com.baehug.watch.wechat")) {
            route = "wechat";
            packageName = "com.baehug.videochat";
        }
        // 判断应用是否已安装
        if (!AppUtils.isAppInstalled(context, packageName)) {
            // 应用未安装
            LogUtil.e("应用未安装");
            ToastUtils.show("应用未安装");
            return;
        }
        // 是否有默认的启动活动
        if (!AppUtils.hasDefaultActivity(context)) {
            // 没有默认的启动活动
            LogUtil.e("没有默认的启动活动");
            ToastUtils.show("应用未安装");
            return;
        }

        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            ToastUtils.show("应用未安装");
            return;
        }

        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            ToastUtils.show("应用未安装");
            return;
        }

        if (packageName.equals("com.baehug.videochat")) {
            launchIntent.putExtra("route", route);
        }

        //支付宝逻辑处理
        if ("com.eg.android.AlipayGphone".equals(packageName)) {
            boolean result = (boolean) SharedPreferencesUtils.getParam(context, ALIPAY_CHECK, false);
            if (!result) {
                String imei = PhoneSIMCardUtil.getInstance().getImei();
                LogUtil.d(imei, LogUtil.TYPE_RELEASE);
                AliPayUtils.aliPayCheck(context, imei, false);
                return;
            }
        }
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
    }

    //启动应用
    public static void startActivity(Activity act, Intent intent) {
        startActivityForResult(act, intent, -1);
    }

    /**
     * 启动应用
     *
     * @param act
     * @param intent      意图
     * @param requestCode 请求码，如果是-1，则请求码无效
     */
    private static void startActivityForResult(Activity act, Intent intent, int requestCode) {
        try {
            if (intent != null && intent.resolveActivity(act.getPackageManager()) != null) {
                if (requestCode == -1) {
                    act.startActivity(intent);
                } else {
                    act.startActivityForResult(intent, requestCode);
                }
            } else {
                LogUtil.e("启动应用失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //跳转到系统的视频
    public static void startVideo(Activity act) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("org.codeaurora.gallery", "com.android.gallery3d.app.GalleryActivity"));
        intent.setType("image/*");
        startActivity(act, intent);
    }

    public static void startGallery(Activity act) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("org.codeaurora.gallery", "com.android.gallery3d.app.GalleryActivity"));
        intent.setType("image/*");
        startActivity(act, intent);
    }

    @Override
    public void success(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage("com.eg.android.AlipayGphone");
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
    }
}
