package com.android.launcher3.common.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class BaseActivityManager {

    private static BaseActivityManager sInstance = new BaseActivityManager();
    private WeakReference<Activity> sCurrentActivityWeakRef;
    private List<Activity> activityList = new LinkedList<Activity>();

    private BaseActivityManager() { }

    public synchronized static BaseActivityManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<>(activity);
    }

    public void addActivity(Activity activity) {
        if (!activityList.contains(activity))
            activityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activityList.contains(activity))
            activityList.remove(activity);
    }

    public void exitToHome() {
        try {
            for (Activity activity:activityList) {
                if (activity != null) {
                    String className = activity.getClass().getSimpleName();
                    if (!className.equals("LauncherActivity"))
                        activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void finishActivityList() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    public List<Activity> getActivityList() {
        return activityList;
    }
}
