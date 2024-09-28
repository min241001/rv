package com.android.launcher3.moudle.notification.bean;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.android.launcher3.moudle.island.IslandMsgBean;

/**
 * @Author: shensl
 * @Description：消息通知实体类
 * @CreateDate：2024/1/9 10:22
 * @UpdateUser: shensl
 */
public class NotificationBean {

    private Drawable appIcon;
    private String appName;
    private String packageName;
    private String title;
    private String content;
    private long time;

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "NotificationBean{" +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
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
        NotificationBean notificationBean = (NotificationBean) obj;
        //多重逻辑处理，去除应用名、包名相同的记录
        if (this.getAppName().equals(notificationBean.getAppName())
                && this.getPackageName().equals(notificationBean.getPackageName())) {
            return true;
        }
        return false;
    }
}
