package com.android.launcher3.moudle.notification.presenter;

import android.util.Log;

import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.moudle.notification.bean.NotificationBean;
import com.android.launcher3.moudle.notification.notity.NotifyHelper;
import com.android.launcher3.moudle.notification.notity.NotifyListener;
import com.android.launcher3.moudle.notification.queue.NotificationLinkedQueue;
import com.android.launcher3.moudle.notification.view.NotificationView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author: shensl
 * @Description：上拉消息栏presenter层实现
 * @CreateDate：2023/12/11 18:22
 * @UpdateUser: shensl
 */
public class NotificationPresenter extends BasePresenter<NotificationView> implements INotificationPresenter {
    @Override
    public void attachView(NotificationView view) {
        super.attachView(view);
        NotifyHelper.getInstance().setNotifyListener(notifyListener);
    }

    @Override
    public void detachView() {
        super.detachView();
        NotifyHelper.getInstance().removeNotifyListener();
    }

    @Override
    public List<NotificationBean> getNotificationBeanList() {
        List<NotificationBean> notifications = NotificationLinkedQueue.getInstance().getQueues();
        if (notifications != null && notifications.size() > 0) {
            Collections.sort(notifications, new Comparator<NotificationBean>() {
                @Override
                public int compare(NotificationBean n1, NotificationBean n2) {
                    // 根据时间属性降序排序
                    return Long.compare(n2.getTime(), n1.getTime());
                }
            });
        }
        return notifications;
    }

    @Override
    public boolean isEmpty(List<NotificationBean> notifications) {
        if (notifications == null || notifications.size() == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void initData() {
        List<NotificationBean> notificationBeanList = getNotificationBeanList();
        if (notificationBeanList != null && notificationBeanList.size() > 0) {
            if (isViewAttached()) {
                getView().setNotificationNoneGone();
            }
        } else {
            if (isViewAttached()) {
                getView().setNotificationNoneVisible();
            }
        }
    }

    @Override
    public void cleanData() {
        NotificationLinkedQueue.getInstance().clearQueues();
        if (isViewAttached()) {
            getView().setNotificationNoneVisible();
            getView().cleanData();
        }
    }

    // 自定义通知监听
    final NotifyListener notifyListener = new NotifyListener() {
        @Override
        public void onReceiveMessage(NotificationBean bean) {
            if (isViewAttached()) {
                getView().setNotificationNoneGone();
                getView().updateData(getNotificationBeanList());
            }
        }

        @Override
        public void onRemovedMessage(String packageName) {
            List<NotificationBean> notifications = NotificationLinkedQueue.getInstance().getQueues();
            if (isViewAttached()) {
                if (isEmpty(notifications)) {
                    getView().setNotificationNoneVisible();
                }
                getView().updateData(notifications);
            }
        }
    };

    public void updateData() {
        List<NotificationBean> notifications = NotificationLinkedQueue.getInstance().getQueues();
        if (isViewAttached()) {
            if (isEmpty(notifications)) {
                getView().setNotificationNoneVisible();
            }
            getView().updateData(notifications);
        }
    }

}
