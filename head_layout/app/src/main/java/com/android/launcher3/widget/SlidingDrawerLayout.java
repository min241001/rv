package com.android.launcher3.widget;

import static com.android.launcher3.common.utils.ScreenUtil.getScreenHeight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;


import com.android.launcher3.R;
import com.android.launcher3.common.utils.LogUtil;

public class SlidingDrawerLayout extends FrameLayout {

    private static final String TAG = SlidingDrawerLayout.class.getSimpleName() + "--->>>";

    public static final int DIRECTION_BOTTOM = 0;
    public static final int DIRECTION_TOP = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_RIGHT = 3;
    private int mDirection = DIRECTION_BOTTOM;
    private boolean mIsAllShow = true;
    private ValueAnimator mAnim;
    private int mVisibleLength = 0;
    private int lastY;
    private int lastX;
    private View mBodyView;
    private GestureDetector mGestureDetector;

    private static final float MINI_WIDTH = 20f;
    private static final float MINI_SPEED = 20f;

    public SlidingDrawerLayout(Context context) {
        super(context);
    }

    public SlidingDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SlidingDrawerLayout);
        mDirection = a.getInt(R.styleable.SlidingDrawerLayout_direction, DIRECTION_BOTTOM);
        mVisibleLength = (int) a.getDimension(R.styleable.SlidingDrawerLayout_visiableLength, 0);
    }

    public SlidingDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void init() {
        mBodyView = getChildAt(0);
        mGestureDetector = new GestureDetector(getContext(), new SlideGestureListener());
        post(() -> {
            hide();//默认隐藏
        });
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG + ev.getAction(), LogUtil.TYPE_RELEASE);
        int distance = getDistance(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //判断落在子view上
                if (!isInView(mBodyView, ev)) {
                    return super.onTouchEvent(ev);
                }
                cancleAnimator();
                mGestureDetector.onTouchEvent(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                caulMargin(distance);
                if (isAnimationDirectionChange()) { //移动超过中间动画方向改变
                    mIsAllShow = false;
                } else {
                    mIsAllShow = true;
                }
                mGestureDetector.onTouchEvent(ev);
                return true;
            case MotionEvent.ACTION_UP:
                calAnimation();
                mGestureDetector.onTouchEvent(ev);
                return true;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int getDistance(MotionEvent ev) {
        int y = (int) ev.getRawY();
        int x = (int) ev.getRawX();
        int distance = 0;
        switch (mDirection) {
            case DIRECTION_BOTTOM:
                distance = lastY - y;
                break;
            case DIRECTION_TOP:
                distance = y - lastY;
                break;
            case DIRECTION_LEFT:
                distance = x - lastX;
                break;
            case DIRECTION_RIGHT:
                distance = lastX - x;
                break;
        }
        lastY = y;
        lastX = x;
        return distance;
    }

    /**
     * 动画的方法是否改变,高度/宽度 减去可以长度的一半为分界线
     *
     * @return
     */
    private boolean isAnimationDirectionChange() {
        switch (mDirection) {
            case DIRECTION_BOTTOM:
            case DIRECTION_TOP:
                return getBodyMargin() > (getHeight() - mVisibleLength) / 2;
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                return getBodyMargin() > (getWidth() - mVisibleLength) / 2;
        }
        return false;
    }

    private void calAnimation() {
        if (mIsAllShow) {
            startAnimator(0);
        } else {
            startAnimator(getMaxLength());
        }


        if (listener != null) {
            listener.onDrawerOpenedOrClosed(mIsAllShow);
        }
    }


    public void close(){

        if (getBodyMargin() != 0){
            return;
        }

        if (mAnim != null && mAnim.isRunning()) {
            mAnim.cancel();
        }

        mAnim = ValueAnimator.ofInt(getBodyMargin(), getMaxLength());
        mAnim.setDuration(200);
        mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnim.setTarget(this);
        mAnim.addUpdateListener(animation -> {
            int currentValue = (Integer) animation.getAnimatedValue();
            setBodyMargin(currentValue);
            if (currentValue == getMaxLength()) {
                mIsAllShow = true;
            }
        });
        mAnim.start();

        if (listener != null) {
            listener.onDrawerOpenedOrClosed(mIsAllShow);
        }
    }


    private void caulMargin(int distance) {
        LogUtil.d(TAG +  "getBodyMrgin = " + getBodyMargin(), LogUtil.TYPE_RELEASE);
        LogUtil.d(TAG + "distance = " + distance, LogUtil.TYPE_RELEASE);
        int margin = getBodyMargin() - distance;
        if (isAllowMargin(margin)) {//拖动区域限制
            setBodyMargin(margin);
        }
    }

    /**
     * 收起或打开动画
     *
     * @param end 结束高度
     */
    public void startAnimator(final int end) {

        if (mAnim != null && mAnim.isRunning()) {
            mAnim.cancel();
        }
        mAnim = ValueAnimator.ofInt(getBodyMargin(), end);
        mAnim.setDuration(200);
        mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnim.setTarget(this);
        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (Integer) animation.getAnimatedValue();
                setBodyMargin(currentValue);
                if (currentValue == end) {//切换状态
                    mIsAllShow = !mIsAllShow;
                }
            }
        });
        mAnim.start();

    }

    public void cancleAnimator() {
        if (mAnim != null && mAnim.isRunning()) {
            mAnim.cancel();
        }
    }


    /**
     * 返回是否在允许范围内
     *
     * @param margin
     * @return
     */
    private boolean isAllowMargin(int margin) {

        return margin >= 0 && margin <= (getHeight() - mVisibleLength);
    }

    public int getBodyMargin() {

        LayoutParams paramsBody = (LayoutParams) mBodyView.getLayoutParams();
        int topMargin = paramsBody.topMargin;

        switch (mDirection) {
            case DIRECTION_BOTTOM:
                topMargin = paramsBody.topMargin;
                break;
            case DIRECTION_TOP:
                topMargin = paramsBody.bottomMargin;
                break;
            case DIRECTION_LEFT:
                topMargin = paramsBody.rightMargin;
                break;
            case DIRECTION_RIGHT:
                topMargin = paramsBody.leftMargin;
                break;
        }
        return topMargin;
    }

    public void setBodyMargin(int margin) {
        LogUtil.d(TAG + "margin = " + margin, LogUtil.TYPE_RELEASE);
        LayoutParams paramsBody = (LayoutParams) mBodyView.getLayoutParams();
        switch (mDirection) {
            case DIRECTION_BOTTOM:
                paramsBody.topMargin = margin;
                break;
            case DIRECTION_TOP:
                paramsBody.bottomMargin = margin;
                break;
            case DIRECTION_LEFT:
                paramsBody.rightMargin = margin;
                break;
            case DIRECTION_RIGHT:
                paramsBody.leftMargin = margin;
                break;
        }
        mBodyView.setLayoutParams(paramsBody);
    }

    public int getMaxLength() {
        switch (mDirection) {
            case DIRECTION_BOTTOM:
            case DIRECTION_TOP:
                return getScreenHeight() - mVisibleLength;
            default:
                return getWidth() - mVisibleLength;

        }
    }

    //滑动手势
    class SlideGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent ev) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            calAnimation();
            return false;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogUtil.d(TAG + ",手指在触摸屏上迅速移动，并松开的动作", LogUtil.TYPE_RELEASE);
            if (e1 != null && e2 != null) {
                float absX = e2.getX() - e1.getX();
                float absY = e2.getY() - e1.getY();
                LogUtil.d(TAG + ",滑动事件状态 absY = " + absY + ",absX = " + absX + ",mDirection = " + mDirection, LogUtil.TYPE_RELEASE);

                // 判断滑动速度是否达到最小阈值
                if (Math.abs(absX) > Math.abs(absY) && Math.abs(absX) > MINI_WIDTH && Math.abs(absX) > MINI_SPEED) {
                    if (absX > 0) {
                        // 向右滑动
                        LogUtil.d(TAG + "向右滑动", LogUtil.TYPE_RELEASE);
                        if (mDirection == DIRECTION_RIGHT) {
                            if (mIsAllShow) {
                                mIsAllShow = false;
                                calAnimation();
                                return true;
                            }
                        }
                    } else {
                        // 向左滑动
                        LogUtil.d(TAG + "向左滑动", LogUtil.TYPE_RELEASE);
                        if (mDirection == DIRECTION_LEFT) {
                            if (mIsAllShow) {
                                mIsAllShow = false;
                                calAnimation();
                                return true;
                            }
                        }
                    }
                } else if (Math.abs(absY) > MINI_WIDTH && Math.abs(absY) > MINI_SPEED) {
                    if (absY > 0) {
                        // 向下滑动
                        LogUtil.d(TAG + "向下滑动", LogUtil.TYPE_RELEASE);
                        if (mDirection == DIRECTION_BOTTOM) {
                            if (mIsAllShow) {
                                mIsAllShow = false;
                                calAnimation();
                                return true;
                            }
                        }
                    } else {
                        // 向上滑动
                        LogUtil.d(TAG + "向上滑动", LogUtil.TYPE_RELEASE);
                        if (mDirection == DIRECTION_TOP) {
                            if (mIsAllShow) {
                                mIsAllShow = false;
                                calAnimation();
                                return true;
                            }
                        }
                    }
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    /**
     * 设置可视长度/高度
     *
     * @param visibleLength
     */
    public void setVisibleLength(int visibleLength) {
        mVisibleLength = visibleLength;
    }

    /**
     * 关闭
     */
    public void hide() {
        mIsAllShow = false;
        calAnimation();
    }

    /**
     * 显示
     */
    public void show() {
        mIsAllShow = true;
        calAnimation();
    }


    public boolean isShowing() {
        LayoutParams paramsBody = (LayoutParams) mBodyView.getLayoutParams();
        return paramsBody.topMargin <= 0;
    }

    /**
     * 判断触摸的点是否在view范围内
     */
    private boolean isInView(View v, MotionEvent event) {
        Rect frame = new Rect();
        v.getHitRect(frame);
        float eventX = event.getX();
        float eventY = event.getY();
        return frame.contains((int) eventX, (int) eventY);
    }

    public interface SlideTouchListener {
        void onDrawerOpenedOrClosed(boolean isShow);
    }

    private SlideTouchListener listener;

    public void setSlideTouchListener(SlideTouchListener listener) {
        this.listener = listener;
    }

}