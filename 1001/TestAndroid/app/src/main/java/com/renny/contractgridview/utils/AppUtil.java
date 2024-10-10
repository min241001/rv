package com.renny.contractgridview.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.renny.contractgridview.R;
import com.renny.contractgridview.base.Constants;
import com.renny.contractgridview.base.LogUtil;
import com.renny.contractgridview.bean.AppInfoBean;
import com.renny.contractgridview.utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pengmin on 2024/8/30.
 */
public class AppUtil {
    /**
     * 获取手机已安装应用列表
     *
     * @param context
     * @param isFilterSystem 是否过滤系统应用
     * @return
     */
    private static final String TAG = "apputil";
    public static Thread thread = null;
    public static List<AppInfoBean> defaultApp = new ArrayList<AppInfoBean>();
    public static List<AppInfoBean> apps = new ArrayList<>();
    public static List<AppInfoBean> system_apps = new ArrayList<AppInfoBean>();
    public static String[] package_keys = new String[]{"android.calendar", "weather", "sport", "migumusic", "deskclock", "compassapp", "jx.timer", "alarm_clock"};//,"","","",
    public static int[] app_colors = new int[]{R.color.color_calendar, R.color.color_weather, R.color.color_sport, R.color.color_migumusic, R.color.color_deskclock, R.color.color_compassapp, R.color.color_timer, R.color.color_alarm_clock};

    private static void SetDefault(String packageName, AppInfoBean bean) {
        for (int j = 0; j < package_keys.length; j++) {
            if (packageName.contains(package_keys[j])) {
                bean.setId(j);
                defaultApp.add(bean);
            }
        }
    }

    private static void SetBean(Drawable appIcon, String appName, String packageName) {
        AppInfoBean bean = new AppInfoBean();
        //bean.setLabel(p.applicationInfo.packageName);
        //bean.setApp_icon(appIcon);
        bean.setApp_name(appName);
        bean.setPackage_name(packageName);
        SetDefault(packageName, bean);
        apps.add(bean);
    }

    private static void SetBean2(Drawable appIcon, String appName, String packageName) {
        AppInfoBean b2 = new AppInfoBean();
        b2.setApp_name(appName);
        b2.setPackage_name(packageName);
        b2.setLevel(2);
        b2.setId(-1);
        system_apps.add(b2);
    }

    public static void GetApps(Context context, Gson gson) {
        try {
            boolean flag = true;
            List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
            List<AppInfoBean> system_apps_temp = null;
            Set<String> t = new HashSet<String>();//p
            //system_apps.clear();//这里清一次可以重新获取一次系统App
            if (system_apps.size() > 0) {
                flag = false;
                system_apps_temp = new ArrayList<AppInfoBean>();
                system_apps_temp.addAll(system_apps);
                system_apps.clear();
            }
            defaultApp.clear();
            apps.clear();
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString().trim();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                int versionCode = packageInfo.versionCode;
                String versionName = packageInfo.versionName;
                String packageName = packageInfo.packageName;
                //packageManager.getApplicationLabel(p.applicationInfo).toString()
                //if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                    SetBean(appIcon, appName, packageName);
                } else {
                    if (system_apps_temp != null && system_apps_temp.size() > 0) {//上次记录不为空，直接加进去
                        LogUtil.i(Constants.temp_tag1, "---->else old  ");
                        for (int k = 0; k < system_apps_temp.size(); k++) {
                            if (appName.trim().equals(system_apps_temp.get(k).getApp_name().trim())) {
                                if (!t.contains(appName)) {
                                    t.add(appName);
                                    LogUtil.i(Constants.temp_tag1, "---->appname old: " + appName);
                                    SetBean(appIcon, appName, packageName);
                                    SetBean2(appIcon, appName, packageName);
                                }
                            }
                        }
                    } else {//为空就再取
                        if (appIcon != null && (!TextUtils.isEmpty(appName))) {
                            LogUtil.i(Constants.temp_tag1, "---->if new ");
                            Intent launcherIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                            if (launcherIntent != null) {
                                List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(launcherIntent, 0);
                                for (ResolveInfo a : activities) {
                                    if (a.activityInfo.packageName.equals(packageName)) {
                                        SetBean(appIcon, appName, packageName);
                                        SetBean2(appIcon, appName, packageName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            LogUtil.i(Constants.temp_tag1, "---->else end sys apps size:  " + system_apps.size());
            LogUtil.i(Constants.temp_tag1, "---->else end apps size:  " + apps.size());
            LogUtil.i(Constants.temp_tag1, "---->else end default apps size:  " + defaultApp.size());

            if (system_apps_temp != null && system_apps_temp.size() > 0) {
                CommonUtil.SaveAppsData(context,gson);
            }
            if (system_apps_temp != null) {
                system_apps_temp.clear();
                system_apps_temp = null;
            }
            t.clear();
            t = null;
        } catch (Exception e) {
            LogUtil.i(TAG, "e:" + e, 0);
        }
    }

    public static List<AppInfoBean> GetAppsA(Context context, Gson gson) {
        try {
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> list = packageManager.getInstalledPackages(0);
            List<AppInfoBean> system_apps_temp = null;
            Set<String> t = new HashSet<String>();//p
            //system_apps.clear();//这里清一次可以重新获取一次系统App
            if (system_apps.size() > 0) {
                system_apps_temp = new ArrayList<AppInfoBean>();
                system_apps_temp.addAll(system_apps);
                system_apps.clear();
            }
            defaultApp.clear();
            apps.clear();
            for (int i = 0; i < list.size(); i++) {//PackageInfo p : list
                PackageInfo p = list.get(i);
                //bean.setLabel(p.applicationInfo.packageName);
                String appName = packageManager.getApplicationLabel(p.applicationInfo).toString();
                Drawable appIcon = p.applicationInfo.loadIcon(packageManager);
                String packageName = p.applicationInfo.packageName;
                int flags = p.applicationInfo.flags;
                //LogUtil.i(TAG, "package_name;" + p.applicationInfo.packageName, 0);
                //PackageInfo packageInfo = packageManager.getPackageInfo(p.applicationInfo.packageName,packageManager.GET_ACTIVITES);
                if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    SetBean(appIcon, appName, packageName);
                } else {
                    if (system_apps_temp != null && system_apps_temp.size() > 0) {//上次记录不为空，直接加进去
                        LogUtil.i(Constants.temp_tag1, "---->else old  ");
                        for (int k = 0; k < system_apps_temp.size(); k++) {
                            if (appName.trim().equals(system_apps_temp.get(k).getApp_name().trim())) {
                                if (!t.contains(appName)) {
                                    t.add(appName);
                                    LogUtil.i(Constants.temp_tag1, "---->appname old: " + appName);
                                    SetBean(appIcon, appName, packageName);
                                    SetBean2(appIcon, appName, packageName);
                                }
                            }
                        }
                    } else {//为空就再取
                        if (appIcon != null && (!TextUtils.isEmpty(appName))) {
                            LogUtil.i(Constants.temp_tag1, "---->if new ");
                            Intent launcherIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                            if (launcherIntent != null) {
                                List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(launcherIntent, 0);
                                for (ResolveInfo a : activities) {
                                    if (a.activityInfo.packageName.equals(packageName)) {
                                        SetBean(appIcon, appName, packageName);
                                        SetBean2(appIcon, appName, packageName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            LogUtil.i(Constants.temp_tag1, "---->else end sys apps size:  " + system_apps.size());
            LogUtil.i(Constants.temp_tag1, "---->else end apps size:  " + apps.size());
            LogUtil.i(Constants.temp_tag1, "---->else end default apps size:  " + defaultApp.size());
            if (system_apps_temp != null && system_apps_temp.size() > 0) {
                CommonUtil.SaveAppsData(context,gson);
            }
            if (system_apps_temp != null) {
                system_apps_temp.clear();
                system_apps_temp = null;
            }
            t.clear();
            t = null;
        } catch (Exception e) {
            LogUtil.i(TAG, "e:" + e, 0);
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
    // 获取应用图标
    public static Drawable getAppIcon(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            return appInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
