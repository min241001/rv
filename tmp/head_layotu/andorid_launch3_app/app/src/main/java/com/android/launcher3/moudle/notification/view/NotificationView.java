package com.android.launcher3.moudle.notification.view;

import com.android.launcher3.moudle.notification.bean.NotificationBean;

import java.util.List;

/**
 * @Author: shensl
 * @Description： 上拉消息栏view层接口
 * @CreateDate：2023/12/11 18:21
 * @UpdateUser: shensl
 */
public interface NotificationView {
    void setNotificationNoneVisible();

    void setNotificationNoneGone();

    void cleanData();

    void updateData(List<NotificationBean> notificationBeanList);
}
