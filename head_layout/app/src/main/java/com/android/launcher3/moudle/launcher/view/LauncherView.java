package com.android.launcher3.moudle.launcher.view;

/**
 * @Author: shensl
 * @Description： 启动页view层接口
 * @CreateDate：2023/12/10 17:39
 * @UpdateUser: shensl
 */
public interface LauncherView {

    /**
     * 加载表盘失败
     */
    void showLoadFaceError();

    /**
     * 加载桌面风格失败
     */
    void showLoadWorkspaceError();

    void setCurrentItem(int position);

}