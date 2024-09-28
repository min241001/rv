package com.android.launcher3.moudle.notification.notity;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.dialog.MsgDialog;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.island.MsgIslandDialog;
import com.android.launcher3.moudle.light.LightActivity;
import com.android.launcher3.moudle.notification.bean.NotificationBean;
import com.android.launcher3.moudle.notification.util.WhitelistUtils;
import com.android.launcher3.utils.FileLogUtil;
import com.android.launcher3.utils.NotificationUtil;

import java.util.List;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = NotificationListener.class.getSimpleName() + "--->>>";

    private static final String DIALER = "com.baehug.dialer";

    private static final String WECHAT = "com.baehug.watch.wechat";
    private MsgDialog dialog = MsgDialog.Companion.init(CommonApp.getInstance());
    private MsgIslandDialog msgIslandDialog;


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        LogUtil.d(TAG + "onNotificationPosted", LogUtil.TYPE_RELEASE);

        if (sbn.getNotification() == null || TextUtils.isEmpty(getPackageName())) return;

        // 是否在白名单
        if (WhitelistUtils.isPackageAllowed(sbn.getPackageName())) {
            return;
        }

        //喜马拉雅
        //com.ximalayaos.wearkid/.ui.home.HomeActivity
        if (sbn.getPackageName().equals("com.ximalayaos.wearkid")) {

            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            String text = extras.getString(Notification.EXTRA_TEXT);
            String message = extras.getString(Notification.EXTRA_MESSAGES);
            //测试个人远程分支提交
            //测试个人远程分支提交2

            NotificationBean bean = new NotificationBean();
            bean.setAppIcon(AppUtils.getAppIcon(getApplicationContext(), "com.ximalayaos.wearkid"));
            bean.setAppName(AppUtils.getAppName(getApplicationContext(), "com.ximalayaos.wearkid"));
            bean.setPackageName("com.ximalayaos.wearkid");
            bean.setContent("喜马拉雅有一条消息");
            bean.setTime(sbn.getPostTime());

            NotifyHelper.getInstance().onReceive(bean);
            IslandNotifyHelper.getInstance().onReceive(bean);
            return;
        }

        //懒人听书
        //bubei.tingshu.wear/.ui.home.HomeActivity
        if (sbn.getPackageName().equals("bubei.tingshu.wear")) {

            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            String text = extras.getString(Notification.EXTRA_TEXT);
            String message = extras.getString(Notification.EXTRA_MESSAGES);

            NotificationBean bean = new NotificationBean();
            bean.setAppIcon(AppUtils.getAppIcon(getApplicationContext(), "bubei.tingshu.wear"));
            bean.setAppName(AppUtils.getAppName(getApplicationContext(), "bubei.tingshu.wear"));
            bean.setPackageName("bubei.tingshu.wear");
            bean.setContent("懒人听书有一条消息");
            bean.setTime(sbn.getPostTime());
            IslandNotifyHelper.getInstance().onReceive(bean);
        }

        if (sbn.getPackageName().equals("com.android.dialer")) {
            if (sbn.getTag() == null) {
                return;
            }

            if (sbn.getTag().equals("MissedCallNotifier")) {
                NotificationBean bean = new NotificationBean();
                bean.setAppIcon(AppUtils.getAppIcon(getApplicationContext(), DIALER));
                bean.setAppName(AppUtils.getAppName(getApplicationContext(), DIALER));
                bean.setPackageName(DIALER);
                bean.setContent("您有新的未接来电");
                bean.setTime(sbn.getPostTime());
                showNotify(sbn, bean);
                NotifyHelper.getInstance().onReceive(bean);
                IslandNotifyHelper.getInstance().onReceive(bean);
            }
            return;
        }


        if (sbn.getPackageName().equals("com.baehug.videochat") || sbn.getPackageName().equals("com.android.launcher3")) {
            Notification notification = sbn.getNotification();
            if (notification != null) {
                String titleText = notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                if (titleText.equals("微聊")) {
                    NotificationBean bean = new NotificationBean();
                    bean.setAppIcon(AppUtils.getAppIcon(getApplicationContext(), WECHAT));
                    bean.setAppName(AppUtils.getAppName(getApplicationContext(), WECHAT));
                    bean.setPackageName(WECHAT);
                    bean.setContent("您有新的微聊消息");
                    bean.setTime(sbn.getPostTime());
                    showNotify(sbn, bean);
                    NotifyHelper.getInstance().onReceive(bean);
                    IslandNotifyHelper.getInstance().onReceive(bean);
                    return;
                }
            }
        }

        // 构造数据
        NotificationBean bean = new NotificationBean();
        bean.setAppIcon(AppUtils.getAppIcon(getApplicationContext(), sbn.getPackageName()));
        bean.setAppName(AppUtils.getAppName(getApplicationContext(), sbn.getPackageName()));
        bean.setPackageName(sbn.getPackageName());
        Notification notification = sbn.getNotification();
        if (notification != null) {
            CharSequence titleText = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
            String title = "";
            if (titleText != null) {
                title = titleText.toString();
            }
            bean.setTitle(title);
            CharSequence contentText = notification.extras.getCharSequence(Notification.EXTRA_TEXT);
            String content = "";
            if (contentText != null) {
                content = contentText.toString();
            }
            bean.setContent(content);
        }
        bean.setTime(sbn.getPostTime());

        String msg = bean.toString();
        if (!TextUtils.isEmpty(msg)) {
            LogUtil.d(TAG + "bean.toString() = " + msg, LogUtil.TYPE_RELEASE);
            FileLogUtil.saveInfoToFile(TAG + msg);
        }

        showNotify(sbn, bean);


        // 非空判断
        if (TextUtils.isEmpty(bean.getAppName()) || TextUtils.isEmpty(bean.getPackageName()) || TextUtils.isEmpty(bean.getContent())) {
            String outMsg = "应用名/包名/内容等不能为空";
            LogUtil.e(outMsg);
            FileLogUtil.saveInfoToFile(TAG + outMsg);
            return;
        }

        showNotify(sbn, bean);

        // 发送通知
        NotifyHelper.getInstance().onReceive(bean);
        IslandNotifyHelper.getInstance().onReceive(bean);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        LogUtil.d(TAG + "onNotificationRemoved", LogUtil.TYPE_RELEASE);
        if (sbn.getNotification() == null) return;

        // 是否在白名单
        if (WhitelistUtils.isPackageAllowed(getPackageName())) {
            return;
        }

        // 发送通知
        NotifyHelper.getInstance().onRemoved(sbn.getPackageName());
        IslandNotifyHelper.getInstance().onRemoved(sbn.getPackageName());
    }


    @Override
    public void onCreate() {
        super.onCreate();
        NotificationUtil.INSTANCE.registerNotify(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationUtil.INSTANCE.unregisterNotify();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    private void showNotify(StatusBarNotification sbn, NotificationBean bean) {
        if (bean.getPackageName().equals("com.baehug.watch.wechat")
                || sbn.getPackageName().equals("com.baehug.videochat")
                || sbn.getPackageName().equals("com.baehug.sms")
                || bean.getPackageName().equals("com.baehug.dialer")) {

            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);

            if (runningTasks != null && !runningTasks.isEmpty()) {
                ActivityManager.RunningTaskInfo topTask = runningTasks.get(0);
                String packageName = topTask.topActivity.getPackageName();
                if (!packageName.equals(sbn.getPackageName()) || sbn.getPackageName().equals("com.android.launcher3")) {
                    try {

                        dialog.startPregress(bean.getPackageName(), bean.getAppIcon(), bean.getAppName(), bean.getContent(), bean.getTime());

//                        msgIslandDialog = MsgIslandDialog.getInstance();
//                        msgIslandDialog.init(bean.getAppIcon(), bean.getContent());
                        // 有新消息时关闭手电筒应用
                        String className = topTask.topActivity.getClassName();
                        if (className.equals("com.android.launcher3.moudle.light.LightActivity") && LightActivity.instance != null) {
                            LightActivity.instance.finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}