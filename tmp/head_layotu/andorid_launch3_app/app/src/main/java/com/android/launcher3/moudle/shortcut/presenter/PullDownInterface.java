package com.android.launcher3.moudle.shortcut.presenter;

public interface PullDownInterface {

    /**
     * 控件底部按钮点击时间监听
     *
     * @param type 按钮类型
     */
    void onViewClickListener(int type);

    /**
     * 状态栏的蓝牙图标是否显示
     *
     * @param visibility 是否显示
     */
    void onStatusBarBleListener(int visibility);

    /**
     * 显示/关闭静音图标
     *
     * @param visibility 是否显示
     */
    void onStatusBarMuteListener(int visibility);

    /**
     * 回调wifi的按钮的监听
     *
     * @param open 是否打开
     */
    void onStatusBarWifiListener(boolean open);
}
