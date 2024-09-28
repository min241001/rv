package com.android.launcher3.common.widget.theme;

import android.view.View;

import androidx.annotation.ColorInt;

import com.android.launcher3.common.bean.AppBean;

import java.util.List;

/**
 * 主题接口
 */
public interface ThemeInterface {

    /**
     * 设置主题监听
     * @param listener
     */
    void setIThemeListener(IThemeListener listener);

    /**
     * 设置数据
     *
     * @param appBeans
     */
    void setDefaultData(List<AppBean> appBeans);

    /**
     * 设置选中的控件
     * @param child
     * @param position
     */
    void setOnClickListener(View child,int position);

    /**
     * 获取数据
     * @return
     */
    List<AppBean> getDefaultData();

    /**
     * 计算子视图的半径
     *
     * @return
     */
    int calcChildViewRadius();

    /**
     * 开发者切记：在此处实现你要的布局
     *
     * @param isOnTouchEvent 是否触摸返回
     * @param left
     * @param top
     */
    void onLayoutReset(boolean isOnTouchEvent, float left, float top);

    /**
     * 是否缩放
     *
     * @return
     */
    boolean isScale();

    /**
     * 边距值
     *
     * @return
     */
    int getPaddingInterval();

    /**
     * 是否添加文本
     * @return
     */
    boolean isAddTextView();

    /**
     * 文字颜色，仅在 isAddTextView() 是 true 的情况下生效
     * @return
     */
    @ColorInt
    int getTextColor();

    /**
     * 文字大小，仅在 isAddTextView() 是 true 的情况下生效
     * @return
     */
    float getTextSize();

}