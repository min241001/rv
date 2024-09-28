package com.android.launcher3.moudle.timer.custom;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * * Author : fangzheng
 * Date : 2024/9/24
 * Details : 自定义暂停界面的UI布局
 */


public class CustomView extends View {
    private Paint circlePaint;
    private Paint arcPaint;
    private Paint textPaint;
    private float strokeWidth = 20f;
    private int circleColor = Color.LTGRAY;
    private int arcColor = Color.BLUE;
    private int textColor = Color.BLACK;
    private float percentage = 0; // 百分比值

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(strokeWidth);
        circlePaint.setColor(circleColor);

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(strokeWidth);
        arcPaint.setColor(arcColor);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2 - strokeWidth;

        // 画背景圆环
        canvas.drawCircle(width / 2, height / 2, radius, circlePaint);

        // 画进度弧
        RectF oval = new RectF(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth);
        canvas.drawArc(oval, -90, (percentage / 100) * 360, false, arcPaint);

        // 画百分比文本
        String percentageText = (int) percentage + "%";
        canvas.drawText(percentageText, width / 2, height / 2 + textPaint.getTextSize() / 4, textPaint);
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
        invalidate(); // 重新绘制
    }
}
