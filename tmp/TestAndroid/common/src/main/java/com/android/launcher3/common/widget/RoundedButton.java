package com.android.launcher3.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.android.launcher3.common.R;
import com.android.launcher3.common.utils.ScreenUtil;

/**
 * @Description：{带进度条的button自定义控件}
 */
public class RoundedButton extends AppCompatTextView {

    // 定义进度条的画笔和进度值
    private Paint rPaint,progressPaint,ringPaint;
    private int progress;
    private int offset = ScreenUtil.dpToPx(getResources(),4);// 偏移
    private int radius;
    private int rbColor;
    private int rbRingColor;
    private int rbProgressColor;
    private int rbAlpha;
    private float mStrokeWidth = 2f;// 画笔

    public RoundedButton(Context context) {
        this(context,null);
    }

    public RoundedButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedButton);
        rbColor = ta.getColor(R.styleable.RoundedButton_rb_color,Color.WHITE);
        rbRingColor = ta.getColor(R.styleable.RoundedButton_rb_ring_color,Color.WHITE);
        rbProgressColor = ta.getColor(R.styleable.RoundedButton_rb_progress_color,Color.WHITE);
        rbAlpha = ta.getInteger(R.styleable.RoundedButton_rb_alpha,255);
        ta.recycle();
        init();
    }

    private void init() {
        // 进度条画笔
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(rbProgressColor);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // 圆环画笔
        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setColor(rbRingColor);
        ringPaint.setStyle(Paint.Style.FILL);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setAlpha(rbAlpha);

        // 外环画笔
        rPaint = new Paint();
        rPaint.setAntiAlias(true);
        rPaint.setColor(rbColor);
        rPaint.setStyle(Paint.Style.STROKE);
        rPaint.setStrokeCap(Paint.Cap.ROUND);
        rPaint.setStrokeWidth(mStrokeWidth);
        rPaint.setAlpha(rbAlpha);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = getHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rRect = new RectF(mStrokeWidth/2f, mStrokeWidth/2f, getWidth()-mStrokeWidth/2f, getHeight()-mStrokeWidth/2f);

        // 绘制外环背景
        canvas.drawRoundRect(rRect, radius, radius, rPaint);

        // 绘制內环背景
        canvas.drawRoundRect(rRect, radius, radius, ringPaint);

        // 绘制进度条
        int progressWidth = (int) (getWidth() * progress / 100.0f);
        if(progressWidth >= offset) {
            RectF progressRect = new RectF(offset, offset, progressWidth - offset, getHeight() - offset);
            if(progressWidth >= (offset+radius)){
                canvas.drawRoundRect(progressRect, radius, radius, progressPaint);
            }else {
                canvas.drawRoundRect(progressRect, progressWidth-offset, progressWidth-offset, progressPaint);
            }
            rPaint.setAlpha(255);
        }

        // 绘制文本
        super.onDraw(canvas);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

}
