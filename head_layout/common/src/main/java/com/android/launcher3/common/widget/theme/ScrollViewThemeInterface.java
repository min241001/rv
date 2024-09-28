package com.android.launcher3.common.widget.theme;

/**
 * @Author: shensl
 * @Description：{文件用途}
 * @CreateDate：2023/12/4 15:00
 * @UpdateUser: shensl
 */
public interface ScrollViewThemeInterface {

    /**
     * 是否滑动到指定位置
     * @return
     */
    boolean isScrollToLocation();

    /**
     * 获取滑动速度，默认是1.0f
     * @return
     */
    float getSlidingSpeed();

}
