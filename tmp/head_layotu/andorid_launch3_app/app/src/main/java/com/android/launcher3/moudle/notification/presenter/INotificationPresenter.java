package com.android.launcher3.moudle.notification.presenter;


import com.android.launcher3.moudle.notification.bean.NotificationBean;

import java.util.List;

/**
 * @Author: shensl
 * @Description： 上拉消息栏presenter层接口
 * @CreateDate：2023/12/11 18:22
 * @UpdateUser: shensl
 */
public interface INotificationPresenter {

    List<NotificationBean> getNotificationBeanList();

    // 是否空数据
    boolean isEmpty(List<NotificationBean> notifications);

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 清除数据
     */
    void cleanData();
}
