package com.android.launcher3.moudle.record.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.launcher3.R;

public class VertSeekBar extends View {

    private static final int MAX_VALUE = 100;
    private static final int MIN_VALUE = 0;

    private final int mFgColor = Color.WHITE;
    private final int mBgColor = Color.parseColor("#6c6c6c");
    private float mX;
    private float mY;
    private float mRadius;
    private float mProgress;
    private float mLeft, mTop, mRight, mBottom;
    private float mWidth, mHeight;
    private final Paint mRectPaint = new Paint();
    private final RectF mRectBg = new RectF();
    private final Paint mRoundPaint = new Paint();
    protected OnStateChangeListener onStateChangeListener;

    public VertSeekBar(Context context) {
        this(context, null);
        init();
    }

    public VertSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        float shadowWidth = getResources().getDimension(R.dimen._2dp);
        mRadius = getResources().getDimension(R.dimen._20dp);
        mLeft = width * 0.45f; // 背景左边缘坐标
        mRight = width * 0.55f; // 背景右边缘坐标
        mTop = 0;
        mBottom = height;
        mWidth = mRight - mLeft; // 背景宽度
        mHeight = mBottom - mTop; // 背景高度
        mX = (float) width / 2; // 圆心的x坐标
        mY = (float) (1 - 0.01 * mProgress) * mHeight;//圆心y坐标

        // init paint
        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setStyle(Paint.Style.FILL);
        mRoundPaint.setColor(mFgColor);
        // 添加阴影效果
        mRoundPaint.setShadowLayer(shadowWidth, 0, 0, Color.GRAY);

        mRectBg.set(mLeft, mTop, mRight, mBottom);
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawForeground(canvas);
        drawCircle(canvas);
        mRectPaint.reset();
    }

    private void drawBackground(Canvas canvas) {
        mRectBg.set(mLeft, mTop, mRight, mBottom);
        mRectPaint.setColor(mBgColor);
        canvas.drawRoundRect(mRectBg, mWidth / 2, mWidth / 3, mRectPaint);
    }

    private void drawForeground(Canvas canvas) {
        float progressHeight = (mBottom - (int) (mBottom * mProgress / 100));
        mRectBg.set(mLeft, progressHeight, mRight, mBottom);
        mRectPaint.setColor(mFgColor);
        canvas.drawRoundRect(mRectBg, mWidth / 2, mWidth / 3, mRectPaint);
    }

    private void drawCircle(Canvas canvas) {
        mY = Math.max(mY, mRadius);// 判断thumb边界
        mY = Math.min(mY, mHeight - mRadius);
        canvas.drawCircle(mX, mY, mRadius, mRoundPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mY = event.getY();
        mProgress = (mHeight - mY) / mHeight * MAX_VALUE;
        if (mProgress < MIN_VALUE) {
            this.mProgress = MIN_VALUE;
        } else if (mProgress > MAX_VALUE) {
            this.mProgress = MAX_VALUE;
        }

        float indicatorOffset = mHeight / MAX_VALUE * mProgress - mRadius * 1.5F;
        indicatorOffset = indicatorOffset < 0 ? 0 : (Math.min(indicatorOffset, mHeight - mRadius * 2));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onStateChangeListener != null) {
                    onStateChangeListener.onStartTouch(this);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (onStateChangeListener != null) {
                    onStateChangeListener.onStopTrackingTouch(this, mProgress);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (onStateChangeListener != null) {
                    onStateChangeListener.onStateChangeListener(this, mProgress, indicatorOffset);
                }
                setmProgress(mProgress);
                this.invalidate();
                break;
        }

        return true;
    }

    public interface OnStateChangeListener {
        void onStartTouch(View view);

        void onStateChangeListener(View view, float progress, float indicatorOffset);

        void onStopTrackingTouch(View view, float progress);
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public void setmProgress(float mProgress) {
        this.mProgress = mProgress;
        invalidate();
    }
}
