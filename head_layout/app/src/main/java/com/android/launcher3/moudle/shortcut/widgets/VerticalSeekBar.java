package com.android.launcher3.moudle.shortcut.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.launcher3.R;

public class VerticalSeekBar extends View {

    //背景颜色
    private int bgColor;
    //进度颜色
    private int progressColor;
    //当前进度
    private float progress = 10;
    //总进度
    private float maxProgress = 100;
    //当前UI高度与view高度的比例
    private double progressRate = 0;
    //记录按压时手指相对于组件view的高度
    private float downY;
    //手指移动的距离，视为亮度调整
    private float moveDistance;

    private Paint mPaint;//画笔

    //矩形圆角
    private float radiusXY = 12;

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar);
        bgColor = typedArray.getColor(R.styleable.VerticalSeekBar_bgColor, ContextCompat.getColor(getContext(), R.color.color_1e1e1e));
        progressColor = typedArray.getColor(R.styleable.VerticalSeekBar_progressColor, ContextCompat.getColor(getContext(), R.color.white));
        progress = typedArray.getFloat(R.styleable.VerticalSeekBar_progress, 10);
        maxProgress = typedArray.getFloat(R.styleable.VerticalSeekBar_maxProgress, 100);
        radiusXY = typedArray.getDimension(R.styleable.VerticalSeekBar_radiusXY, radiusXY);
        typedArray.recycle();

        initPaint();
    }

    private void initPaint() {
        //获取当前百分比率
        progressRate = getProgressRate();
        //背景画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//设置防抖动
        mPaint.setColor(bgColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(0);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public int getProgress() {
        return (int) progress;
    }

    public void setProgress(int mProgress) {
        progress = mProgress;
        progressRate = getProgressRate();
        invalidate();
    }

    /**
     * 计算比例
     */
    private double getProgressRate() {
        return (double) progress / maxProgress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //保存
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        onDrawBackground(canvas); //画背景
        onDrawProgress(canvas); //画进度
        //恢复到特定的保存点
        canvas.restoreToCount(layerId);
    }

    /**
     * 画圆弧背景
     *
     * @param canvas
     */
    private void onDrawBackground(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bgColor);
        int with = getWidth();
        int height = getHeight();
        RectF rectF = new RectF(0, 0, with, height);
        canvas.drawRoundRect(rectF, radiusXY, radiusXY, mPaint);
    }

    /**
     * 画亮度背景-方形-随手势上下滑动而变化用来显示亮度大小
     *
     * @param canvas
     */
    private void onDrawProgress(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        int with = getWidth();
        int height = getHeight();

        mPaint.setColor(progressColor);
        float progressHeight = (canvas.getHeight() - (int) (canvas.getHeight() * progressRate));
        canvas.drawRect(0, progressHeight, with, height, mPaint);
        mPaint.setXfermode(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveDistance = downY - event.getY();
                //计算手指移动后亮度UI占比大小
                calculateLoudRate();
                downY = event.getY();

                break;
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    listener.onProgressChange((int) (progressRate * 100));
                }
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 计算手指移动后UI占比大小，视其为大小
     */
    private void calculateLoudRate() {
        progressRate = (getHeight() * progressRate + moveDistance) / getHeight();
        if (progressRate >= 1) {
            progressRate = 1;
        }
        if (progressRate <= 0) {
            progressRate = 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    //附加
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    //分离，拆卸
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    //进度发生变化的回调接口
    public interface OnProgressChangedListener {
        void onProgressChange(int progress);
    }

    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        this.listener = listener;
    }

    //进度移动监听
    private OnProgressChangedListener listener = null;
}
