package com.android.launcher3.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.ViewPager;

/**
 * @Description：手势动作
 */
public class ViewPagerExt extends ViewPager {

    private static final String TAG = ViewPagerExt.class.getSimpleName() + "---手势滑动--->>>";

    private GestureDetectorCompat mGestureDetector;
    private CustomGesture mCustomGesture;

    public ViewPagerExt(@NonNull Context context) {
        this(context, null);
    }

    public ViewPagerExt(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int widthPixels = dm.widthPixels;
        mCustomGesture = new CustomGesture(widthPixels) {
            @Override
            protected boolean isInHome() {
                return getCurrentItem() == 0;
            }
        };
        mGestureDetector = new GestureDetectorCompat(getContext(), mCustomGesture);
    }

    public void setGestureListener(CustomGesture.GestureListener listener) {
        this.mCustomGesture.setGestureListener(listener);
    }

    // 在onTouchEvent方法中调用GestureDetectorCompat对象的onTouchEvent方法：
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 当前处理哪个位置
     * 如果是0，则表示在主页
     * 如果是1，则表示在侧滑页面
     * 如果是2，则表示长按页面
     */
    private int position = 0;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}