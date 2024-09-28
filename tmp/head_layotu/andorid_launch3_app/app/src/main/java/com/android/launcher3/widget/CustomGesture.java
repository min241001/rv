package com.android.launcher3.widget;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.android.launcher3.common.utils.LogUtil;

/**
 * @Author: shensl
 * @Description：自定义手势监听，定义一个实现GestureDetector.OnGestureListener接口的内部类来处理手势事件
 * @CreateDate：2023/12/12 9:16
 * @UpdateUser: shensl
 */
public abstract class CustomGesture extends GestureDetector.SimpleOnGestureListener {

    private static final String TAG = CustomGesture.class.getSimpleName() + "--->>>";

    private static final float SWIPE_THRESHOLD = 20f; // 默认最小滑动距离
    private static final float SWIPE_VELOCITY_THRESHOLD = 20f; // 默认最小滑动速度

    private float swipeDistance;// 最小滑动距离
    private float swipeVelocityThreshold;// 最小滑动速度
    private int width = 200;

    public CustomGesture() {
        this(SWIPE_THRESHOLD, SWIPE_VELOCITY_THRESHOLD);
    }

    public CustomGesture(int width) {
        this(SWIPE_THRESHOLD, SWIPE_VELOCITY_THRESHOLD);
        if (width > 0) {
            this.width = width / 2;
        }
    }

    public CustomGesture(float swipeDistance, float swipeVelocityThreshold) {
        if (swipeDistance > SWIPE_THRESHOLD) {
            this.swipeDistance = swipeDistance;
        } else {
            this.swipeDistance = SWIPE_THRESHOLD;
        }

        if (swipeVelocityThreshold > SWIPE_VELOCITY_THRESHOLD) {
            this.swipeVelocityThreshold = swipeVelocityThreshold;
        } else {
            this.swipeVelocityThreshold = SWIPE_VELOCITY_THRESHOLD;
        }
    }

    public float getSwipeDistance() {
        return swipeDistance;
    }

    public void setSwipeDistance(float swipeDistance) {
        this.swipeDistance = swipeDistance;
    }

    public float getSwipeVelocityThreshold() {
        return swipeVelocityThreshold;
    }

    public void setSwipeVelocityThreshold(float swipeVelocityThreshold) {
        this.swipeVelocityThreshold = swipeVelocityThreshold;
    }

    public interface GestureListener {
        // 从左到右，即右滑
        void onLeftToRight();

        // 从右到左，即左滑
        void onRightToLeft();

        // 从上到下，即下滑
        void onTopToBottom(boolean left);

        // 从下到上，即上滑
        void onBottomToTop(boolean left);

        // 长按
        void onLongPress();
    }

    private GestureListener listener;

    public void setGestureListener(GestureListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // 非空及首页判断
        if (e1 != null && e2 != null && isInHome()) {

            float absX = e2.getX() - e1.getX();
            float absY = e2.getY() - e1.getY();
            Log.i(TAG, "onScroll: y2 " + e2.getY() + " y1 " + e1.getY());
            LogUtil.d(TAG + ",滑动事件状态 absY = " + absY + ",absX = " + absX, LogUtil.TYPE_RELEASE);

            // 判断滑动速度是否达到最小阈值
            if (Math.abs(absX) > Math.abs(absY) && Math.abs(absX) > getSwipeDistance() && Math.abs(absX) > getSwipeVelocityThreshold()) {
                if (absX > 0) {
                    // 向右滑动
                    LogUtil.d(TAG + "向右滑动", LogUtil.TYPE_RELEASE);
                    if (listener != null) {
                        listener.onLeftToRight();
                    }
                } else {
                    // 向左滑动
                    LogUtil.d(TAG + "向左滑动", LogUtil.TYPE_RELEASE);
                    if (listener != null) {
                        listener.onRightToLeft();
                    }
                }
            } else if (Math.abs(absY) > getSwipeDistance() && Math.abs(absY) > getSwipeVelocityThreshold()) {
                if (e1.getY() < 30 && absY > 0) {
                    // 向下滑动
                    LogUtil.d(TAG + "向下滑动 width " + width, LogUtil.TYPE_RELEASE);
                    if (listener != null) {
                        listener.onTopToBottom(e1.getX() < width);
                    }
                } else if (absY < -100) {
                    // 向上滑动
                    LogUtil.d(TAG + "向上滑动", LogUtil.TYPE_RELEASE);
                    if (listener != null) {
                        listener.onBottomToTop(e1.getX() < width);
                    }
                }
            }

        }

        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (!isInHome()) {
            return;
        }
        LogUtil.d(TAG + "长按动作", LogUtil.TYPE_RELEASE);
        if (listener != null) {
            listener.onLongPress();
        }
    }

    protected abstract boolean isInHome();

}
