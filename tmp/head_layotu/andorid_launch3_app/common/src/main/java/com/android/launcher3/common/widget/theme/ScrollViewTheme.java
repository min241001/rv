package com.android.launcher3.common.widget.theme;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.view.ViewCompat;

/**
 * @Author: shensl
 * @Description：滑动主题控件
 * @CreateDate：2023/12/4 14:56
 * @UpdateUser: shensl
 */
abstract class ScrollViewTheme extends BaseViewGroup implements ScrollViewThemeInterface {

    private float mTranslateX; // x轴平移量
    private float mTranslateY; // y轴平移量
    private float mLastTouchX; // x轴平移量
    private float mLastTouchY; // y轴平移量

    public ScrollViewTheme(Context context) {
        this(context, null);
    }

    public ScrollViewTheme(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollViewTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true; // 拦截所有MOVE事件
        }
        return false;
    }

    private long mLastTime;// 上次时间
    private static final long MAX_TIME = 200L;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getOnTouchType() == OnTouchType.NONE) {
            return super.onTouchEvent(event);
        }

        LogDebug("onTouchEvent,event.getAction() = " + event.getAction());

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                LogDebug("MotionEvent.ACTION_UP");
                if (System.currentTimeMillis() - mLastTime < MAX_TIME) {
                    handleClickEvent();
                }
                break;

            case MotionEvent.ACTION_DOWN:
                LogDebug("MotionEvent.ACTION_DOWN");
                // 记录初始触摸点坐标
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                mLastTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                LogDebug("MotionEvent.ACTION_MOVE");
                float x = event.getX();
                float y = event.getY();

                // 计算触摸点的移动距离
                float dx = (x - mLastTouchX) * getSlidingSpeed();
                float dy = (y - mLastTouchY) * getSlidingSpeed();
                LogDebug("x = " + x + ",y = " + y + ",mLastTouchX = " + mLastTouchX + ",mLastTouchY = " + mLastTouchY + ",dx = " + dx + ",dy = " + dy);

                // 更新平移变换的值
                mTranslateX += dx;
                mTranslateY += dy;

                switch (getOnTouchType()) {
                    case ALL:// 全滑动
                        // 移动到指定位置
                        if (isScrollToLocation()) {
                            scrollTo((int) -mTranslateX, (int) -mTranslateY);
                        }

                        // 布局重置
                        if (getChildCount() > 0) {
                            onLayoutReset(true, mTranslateX, mTranslateY);
                        }
                        break;
                    case HOR:// 左右滑动
                        // 移动到指定位置
                        if (isScrollToLocation()) {
                            scrollTo((int) -mTranslateX, 0);
                        }

                        // 布局重置
                        if (getChildCount() > 0) {
                            onLayoutReset(true, mTranslateX, 0);
                        }
                        break;
                    case VER:// 上下滑动
                        LogDebug("isScrollTop() = " + isScrollTop() + ",isScrollBottom() = " + isScrollBottom());

                        // 移动到指定位置
                        if (isScrollToLocation()) {
                            scrollTo(0, (int) -mTranslateY);
                        }

                        // 布局重置
                        if (getChildCount() > 0) {
                            onLayoutReset(true, 0, mTranslateY);
                        }
                        break;
                }

                // 更新最后一次触摸点坐标
                mLastTouchX = x;
                mLastTouchY = y;
                break;
        }

        return true;
    }

    public enum OnTouchType {
        NONE,// 无滑动
        VER,// 上下滑动
        HOR,// 左右滑动
        ALL,// 全滑动
    }

    protected OnTouchType getOnTouchType() {
        return OnTouchType.NONE;
    }

    @Override
    public boolean isScrollToLocation() {
        return true;
    }

    @Override
    public float getSlidingSpeed() {
        return 1.5f;
    }

    /**
     * 判断是否可以在垂直方向上滚动
     *
     * @param direction 滚动方向，大于0表示检查是否可以向下滚动，小于0表示检查是否可以向上滚动
     * @return 是否可以滚动
     */
    private boolean isCanScrollVertically(int direction) {
        return ViewCompat.canScrollVertically(this, direction);
    }

    /**
     * 判断是否滑动到顶部
     *
     * @return 是否滑动到顶部
     */
    public boolean isScrollTop() {
        return getScrollY() == 0;
    }

    /**
     * 判断是否滑动到底部
     *
     * @return 是否滑动到底部
     */
    public boolean isScrollBottom() {
        return getScrollY() == getHeight();
    }
}
