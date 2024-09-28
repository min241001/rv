package com.android.launcher3.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;

public class PullLinearLayout extends LinearLayout {

    private int currentPos = 0;
    private Scroller scroller;

    public PullLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量子控件的大小
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = getVerticalHeight();
        //设置子控件的位置
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(view.getLeft(), view.getTop() - height, view.getRight(), view.getBottom() - height);
        }
    }

    private void init() {
        scroller = new Scroller(getContext());
    }

    private GestureDetector detector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            //相对滑动：Y方向滑动多少距离，view就跟着滑动多少距离
            //手指向上滑动
            if (e2.getY() < e1.getY() && getScrollY() >= 0) {
                //滑到的y值超过0时，反弹回去
                if (getScrollY() > 0) scrollBy(0, -getScrollY());
                //否则什么也不做
            } else if (getScrollY() <= 0) {
                //如果手指向下滑动并且没有超过抽屉页的滑动范围,就滑动页面
                if (!(e2.getY() > e1.getY() && getScrollY() == -getVerticalHeight())) {
                    scrollBy(0, (int) distanceY);
                    if (e2.getY() > e1.getY()) { //手指向下滑动
                        currentPos = getScrollY() / (getVerticalHeight() / 4);
                    } else { //手指向上滑动
                        currentPos = getScrollY() / (4 * getVerticalHeight() / 5);
                    }
                    if (currentPos > 0) currentPos = 0;
                    if (currentPos < -1) currentPos = -1;
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将event信息传给detector;
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                scroller.startScroll(0, getScrollY(), 0, getVerticalHeight() * currentPos - getScrollY());
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(0, scroller.getCurrY());
            postInvalidateDelayed(10);
        }
    }

    private int getVerticalHeight() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }
}
