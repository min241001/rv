package com.android.launcher3.moudle.shortcut.presenter;

import com.android.launcher3.moudle.shortcut.bean.Widget;

import java.util.List;

/**
 * @Author: yanyong
 * @Description：
 * @CreateDate：2024/7/1
 */
public interface IPullDownView {

    /**
     * 刷新自定义横向滑动控件
     *
     * @param list 控件数据集
     */
    void onUpdateWidgetData(List<Widget> list);

    /**
     * 刷新状态栏网络状态数据
     *
     * @param resourceId 图片资源id
     */
    void setSignalImageResource(int resourceId);

    /**
     * 回调电量进度
     *
     * @param battery    电量
     * @param resourceId 图片资源
     */
    void setBatteryProgress(int battery, int resourceId);

    /**
     * 清除缓存完成通知
     */
    void onClearComplete(long duration, String availMem, String totalMem);
}
