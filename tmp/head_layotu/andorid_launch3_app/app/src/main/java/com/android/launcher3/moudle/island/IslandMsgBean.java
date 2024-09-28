package com.android.launcher3.moudle.island;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class IslandMsgBean {

    private String msg;
    private String appName;
    private String packageName;
    private Drawable appIcon;

    public IslandMsgBean(String msg, String appName, String packageName, Drawable appIcon) {
        this.msg = msg;
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
    }

    public IslandMsgBean() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public String toString() {
        return "IslandMsgBean{" +
                "msg='" + msg + '\'' +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", appIcon=" + appIcon +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        IslandMsgBean user = (IslandMsgBean) obj;
        //多重逻辑处理，去除年龄、姓名相同的记录
        if (this.getMsg().equals(user.getMsg())
                && this.getAppName().equals(user.getAppName())
                && this.getPackageName().equals(user.getPackageName())) {
            return true;
        }
        return false;
    }
}
