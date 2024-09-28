package com.android.launcher3.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.launcher3.R;

/**
 * Author : yanyong
 * Date : 2024/9/9
 * Details : 自定义滑动控件
 */
public class SlideSwitch extends View {

    private static final String TAG = "SlideSwitch1";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 画圆 画滑动开关背景
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 画文本
    boolean mIsOn = false;
    private float mCurX = 0; // 圆心位置
    private float mCenterY; // y固定
    private float mViewWidth; // 控件宽度
    private float mRadius; // 圆半径
    private float mLineStart; // 直线段开始的位置（横坐标，即
    private float mLineEnd; // 直线段结束的位置（纵坐标
    private float mLineWidth;
    private final float SCALE = 7.5f; // 控件长度为滑动的圆的半径的倍数
    private OnStateChangedListener onStateChangedListener;
    private String mSwitchText; // 文本
    private float mTextSize; // 字体大小
    private Bitmap mBitmap;
    private Drawable mIcon;
    private int mHeight;
    private int mWidth;
    private int mBgColor;

    public SlideSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlideSwitch(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.slide_switch);
        mSwitchText = typedArray.getString(R.styleable.slide_switch_text);
        mIcon = typedArray.getDrawable(R.styleable.slide_switch_icon);
        mTextSize = context.getResources().getDimension(R.dimen._24sp);

        int width = mIcon.getIntrinsicWidth();
        int height = mIcon.getIntrinsicHeight();
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        mIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        mIcon.draw(canvas);

        mHeight = mBitmap.getHeight();
        mWidth = mBitmap.getWidth();
        mBgColor = Color.rgb(30, 29, 32);

        Log.i(TAG, "init: mHeight " + mHeight + " mWidth " + mWidth + " width " + width + " height " + height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCurX = event.getX();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mCurX > mViewWidth * 2 / 3) {
                mCurX = mLineEnd;
                if (null != onStateChangedListener) {
                    onStateChangedListener.onStateChanged(true);
                }
            } else {
                mCurX = mLineStart;
                if (null != onStateChangedListener) {
                    onStateChangedListener.onStateChanged(false);
                }
            }
        }
        /*通过刷新调用onDraw*/
        this.postInvalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*保持宽是高的SCALE / 2倍， 即圆的直径*/
        this.setMeasuredDimension(this.getMeasuredWidth(), (int)(this.getMeasuredWidth() * 2 / SCALE));
        mViewWidth = this.getMeasuredWidth();
        mRadius = mViewWidth / SCALE;
        mLineWidth = mRadius * 2f; //直线宽度等于滑块直径
        mCurX = mRadius;
        mCenterY = this.getMeasuredWidth() / SCALE; //centerY为高度的一半
        mLineStart = mRadius;
        mLineEnd = (SCALE - 1) * mRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*限制滑动范围*/
        mCurX = mCurX > mLineEnd ? mLineEnd : mCurX;
        mCurX = mCurX < mLineStart ? mLineStart : mCurX;

        /*划线*/
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineWidth);
        /*左边部分的线，绿色*/
        mPaint.setColor(mBgColor);
        canvas.drawLine(mLineStart, mCenterY, mCurX, mCenterY, mPaint);
        /*右边部分的线，灰色*/
        mPaint.setColor(mBgColor);
        canvas.drawLine(mCurX, mCenterY, mLineEnd, mCenterY, mPaint);

        // 画文本
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(Color.WHITE);
        float width = mTextPaint.measureText(mSwitchText);
        canvas.drawText(mSwitchText, (mViewWidth - width) / 2, mCenterY + 5, mTextPaint);

        /*画圆*/
        /*画最左和最右的圆，直径为直线段宽度， 即在直线段两边分别再加上一个半圆*/
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBgColor);
        canvas.drawCircle(mLineEnd, mCenterY, mLineWidth / 2, mPaint);
        mPaint.setColor(mBgColor);
        canvas.drawCircle(mLineStart, mCenterY, mLineWidth / 2, mPaint);
        /*图片滑块*/
        canvas.drawBitmap(mBitmap, mCurX - mWidth / 2, 0, mPaint);
    }

    public void resetCurX() {
        mCurX = mLineStart;
        mIsOn = false;
        invalidate();
    }

    /*设置开关状态改变监听器*/
    public void setOnStateChangedListener(OnStateChangedListener o) {
        this.onStateChangedListener = o;
    }

    /*内部接口，开关状态改变监听器*/
    public interface OnStateChangedListener {
        void onStateChanged(boolean state);
    }
}
