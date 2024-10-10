package com.android.launcher3.common.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

/**
 * @Author: shensl
 * @Description：启动应用工具类
 * @CreateDate：2024/1/8 9:30
 * @UpdateUser: shensl
 */
public class AppLauncherUtils {

    // 启动默认活动
    public static void launchApp(Context context, String packageName) {

        String route = "video" ;

        if (packageName.equals("com.baehug.dialer")){
            AppLauncherUtils.launchAppAction(context,"com.baehug.dialer.call");
            return;
        }

        if (packageName.equals("com.baehug.watch.wechat")){
            route = "wechat";
            packageName = "com.baehug.videochat";
        }

        if (!AppUtils.isAppInstalled(context, packageName)) {
            // 应用未安装
            LogUtil.e("应用未安装");
            return;
        }

        // 是否有默认的启动活动
        if (!AppUtils.hasDefaultActivity(context)) {
            // 没有默认的启动活动
            LogUtil.e("没有默认的启动活动");
            return;
        }

        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            LogUtil.e("packageManager == null");
            return;
        }

        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            LogUtil.e("launchIntent == null");
            return;
        }

        if (packageName.equals("com.baehug.videochat")){
            launchIntent.putExtra("route",route);
        }

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
    }

    // 显式意图
    public static void launchActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }

    public static void launchActivity(Context context, Class<?> activityClassm , Bundle bundle) {
        Intent intent = new Intent(context, activityClassm);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }


    // 隐式意图
    public static void launchIntent(Context context, String action, Uri data) {
        Intent intent = new Intent(action);
        if (data != null) {
            intent.setData(data);
        }
        context.startActivity(intent);
    }

    // 应用链接
    public static void launchAppLink(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void launchAppAction(Context context, String action) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            context.startActivity(intent);
        }catch (Exception e){
            ToastUtils.show("打开应用失败");
        }
    }

    public static void launchHome(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // 定时任务
    public static void launchAppWithAlarm(Context context, Class<?> receiverClass) {
        Intent intent = new Intent(context, receiverClass);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);
    }

    // 应用小组件
    public static void launchAppWithWidget(Context context, Class<?> activityClass, int[] widgetIds) throws PendingIntent.CanceledException {
        Intent intent = new Intent(context, activityClass);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        pendingIntent.send();
    }


}
