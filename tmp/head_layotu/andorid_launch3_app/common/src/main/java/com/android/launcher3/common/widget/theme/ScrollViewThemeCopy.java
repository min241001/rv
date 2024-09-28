package com.android.launcher3.common.widget.theme;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.android.launcher3.common.utils.LogUtil;

/**
 * @Author: shensl
 * @Description：{滑动主题控件}
 * @CreateDate：2023/12/4 14:56
 * @UpdateUser: shensl
 */
abstract class ScrollViewThemeCopy extends BaseViewGroup implements ScrollViewThemeInterface {

    private float mTranslateX; // x轴平移量
    private float mTranslateY; // y轴平移量
    private float mLastTouchX; // x轴平移量
    private float mLastTouchY; // y轴平移量

    private boolean isTopOrBottom;// 是否到顶部或者底部

    public ScrollViewThemeCopy(Context context) {
        super(context);
    }

    public ScrollViewThemeCopy(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewThemeCopy(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float mStartX;
    private float mStartY;

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mStartX = ev.getX();
//                mStartY = ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float moveX = ev.getX();
//                float moveY = ev.getY();
//                float deltaX = Math.abs(moveX - mStartX);
//                float deltaY = Math.abs(moveY - mStartY);
//                // 判断滑动距离，如果大于一定值，则认为是滑动事件，拦截事件传递给子视图处理
//                if (deltaX > deltaY && deltaX > 10) {
//                    LogUtil.d("是滑动事件");
//                    return true;
//                }
//                LogUtil.d("是点击事件");
//                break;
//        }
//        return super.onInterceptTouchEvent(ev);
//    }

    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private long pressTimeMillis;// 按压时间

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getOnTouchType() == OnTouchType.NONE) {
            return super.onTouchEvent(event);
        }

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            // 重置实例
            velocityTracker.clear();
        }
        // 把事件添加进 VelocityTracker
        velocityTracker.addMovement(event);

        LogUtil.d(TAG+"onTouchEvent===========", LogUtil.TYPE_RELEASE);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录初始触摸点坐标
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                mStartX = event.getX();
                mStartY = event.getY();
                pressTimeMillis = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
//                LogUtil.d(TAG+"scrollY = "+getScrollY());
//                if (getScrollY() == 0) {
//                    // 滑动到了顶部
//                    LogUtil.d(TAG+"滑动到了顶部");
//                    isTopOrBottom = true;
//                } else if (getScrollY() == getHeight()) {
//                    // 滑动到了底部
//                    LogUtil.d(TAG+"滑动到了底部");
//                    isTopOrBottom = true;
//                }

                float x = event.getX();
                float y = event.getY();

                // 计算触摸点的移动距离
                float dx = (x - mLastTouchX)*getSlidingSpeed();
                float dy = (y - mLastTouchY)*getSlidingSpeed();

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

            case MotionEvent.ACTION_UP:
                // units：计算的时间长度，单位ms
                // 填入1000，那么 getXVelocity() 返回的值就是每1000ms内手指移动的像素数

                // maxVelocity：速度上限，超过这个速度，计算出的速度会回落到这个速度
                // 填了200，而实时速度是300，那么实际的返回速度将是200

                // 获取 maxVelocity 的方式：
                // ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
                // int maxVelocity = viewConfiguration.getScaledMaximumFlingVelocity()
                velocityTracker.computeCurrentVelocity(1000, 200);

                // 在获取速度之前需要调用 computeCurrentVelocity 计算
                // 速度计算公式： 速度 = （终点位置 - 起点位置） / 时间段  【速度根据滑动方向不同可以为负值】
                int xVelocity = (int) velocityTracker.getXVelocity();
                int yVelocity = (int) velocityTracker.getYVelocity();

//                LogUtil.d(TAG+",xVelocity = "+xVelocity+",yVelocity = "+yVelocity);

                // 抬起的时候判断是否滑动事件
                if(System.currentTimeMillis() - pressTimeMillis < 100L){
                    float deltaX = Math.abs(event.getX() - mStartX);
                    float deltaY = Math.abs(event.getY() - mStartY);
                    LogUtil.d(TAG+",deltaX = "+deltaX+",deltaY = "+deltaY, LogUtil.TYPE_RELEASE);
                    // 判断滑动距离，如果大于一定值，则认为是滑动事件，拦截事件传递给子视图处理
                    if(deltaX<2 && deltaY<2){// 10个像素内的误差，算点击事件
                        LogUtil.d(TAG+"是点击事件+++++++++++++", LogUtil.TYPE_RELEASE);
                        return true;
                    }

                    LogUtil.d(TAG+"是滑动事件===========", LogUtil.TYPE_RELEASE);
                }

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

}
