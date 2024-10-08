package com.renny.contractgridview.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.renny.contractgridview.R;
import com.renny.contractgridview.base.Constants;
import com.renny.contractgridview.bean.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengmin on 2024/8/30.
 */
public class AppUtils {
    /**
     * 获取手机已安装应用列表
     *
     * @param context
     * @param isFilterSystem 是否过滤系统应用
     * @return
     */
    private static final String TAG = "apputil";
    public static List<AppInfoBean> defaultApp = new ArrayList<AppInfoBean>();
    public static List<AppInfoBean> favApp = new ArrayList<AppInfoBean>();
    public static List<AppInfoBean> apps = new ArrayList<>();
    //public static String[] package_keys = new String[]{"android.calendar", "weather", "sport", "migumusic", "deskclock", "compassapp", "jx.timer", "alarm_clock"};//,"","","",
    //public static int[] app_colors = new int[]{R.color.color_calendar, R.color.color_weather, R.color.color_sport, R.color.color_migumusic, R.color.color_deskclock, R.color.color_compassapp, R.color.color_timer, R.color.color_alarm_clock};

    public static List<AppInfoBean> getApps(Context context, Handler handler, boolean isFilterSystem) {
        try {
            AppInfoBean bean = null;
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> list = packageManager.getInstalledPackages(0);
            defaultApp.clear();
            favApp.clear();
            apps.clear();
            for (PackageInfo p : list) {
                bean = new AppInfoBean();
                //bean.setLabel(p.applicationInfo.packageName);
                bean.setApp_icon(p.applicationInfo.loadIcon(packageManager));
                bean.setApp_name(packageManager.getApplicationLabel(p.applicationInfo).toString());
                bean.setPackage_name(p.applicationInfo.packageName);
                int flags = p.applicationInfo.flags;
                LogUtil.i(TAG, "package_name;" + p.applicationInfo.packageName, 0);
                //PackageInfo packageInfo = packageManager.getPackageInfo(p.applicationInfo.packageName,packageManager.GET_ACTIVITES);

                //}
                //}
                // 判断是否是属于系统的apk
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    // bean.setSystem(true);
                    defaultApp.add(bean);
                    favApp.add(bean);
                } else {
                    Intent launcherIntent = packageManager.getLaunchIntentForPackage(p.applicationInfo.packageName);
                    if (launcherIntent != null) {
                        boolean flag = true;
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(launcherIntent, 0);
                        for (ResolveInfo a : activities) {
                            if (a.activityInfo.packageName.equals(p.applicationInfo.packageName)) {
                                // for (int i = 0; i < package_keys.length; i++) {
                                //if (p.applicationInfo.packageName.contains(package_keys[i])) {
                                //bean.setId(i);
                                defaultApp.add(bean);
                                favApp.add(bean);
                                LogUtil.i(TAG, " not system package_name;" + p.applicationInfo.packageName, 0);
                            }
                        }
                    }
                }
                apps.add(bean);
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "e:" + e, 0);
        } finally {
            handler.sendEmptyMessage(Constants.msgWhat.UPDATE_APPS_DATA);
        }
        return apps;
    }

    public static void LauncherActivity(Context context, String package_path) {

        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        intent = packageManager.getLaunchIntentForPackage(package_path);
        if (intent == null) {
            //Toast.makeText(context, "未安装", Toast.LENGTH_LONG).show();
        } else {
            context.startActivity(intent);
        }
    }


    public static void InitData(Context context, Handler handler) {
        try {
            List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
            defaultApp.clear();
            apps.clear();
            favApp.clear();
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                int versionCode = packageInfo.versionCode;
                String versionName = packageInfo.versionName;
                String packageName = packageInfo.packageName;
                //packageManager.getApplicationLabel(p.applicationInfo).toString()
                AppInfoBean bean = new AppInfoBean();
                //bean.setLabel(p.applicationInfo.packageName);
                bean.setApp_icon(appIcon);
                bean.setApp_name(appName);
                bean.setPackage_name(packageName);
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    bean.setId(i);
                    defaultApp.add(bean);
                    favApp.add(bean);
                } else {
                    Intent launcherIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    if (launcherIntent != null) {
                        List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(launcherIntent, 0);
                        for (ResolveInfo a : activities) {
                            if (a.activityInfo.packageName.equals(packageName)) {
                                defaultApp.add(bean);
                                favApp.add(bean);
                            }
                        }
                    }
                }

                apps.add(bean);
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "e:" + e, 0);
        } finally {
            handler.sendEmptyMessage(Constants.msgWhat.UPDATE_APPS_DATA);
            //handler.sendEmptyMessage(Constants.msgWhat.UPDATE_APPS_DATA);
        }
    }
}
