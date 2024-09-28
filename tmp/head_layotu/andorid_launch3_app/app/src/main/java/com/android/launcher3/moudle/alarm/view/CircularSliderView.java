package com.android.launcher3.moudle.alarm.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.launcher3.R;

public class CircularSliderView extends View {

    private static final String TAG = "CircularSliderView";
    private Paint circlePaint;
    private Paint thumbPaint;
    private Path circlePath;
    private float centerX, centerY;
    private float radius;
    private float thumbX, thumbY;
    private final int distance = 25;
    private OnMoveAction onMoveAction;
    private int hourValue;
    private boolean isMinute = true;
    private float startAngle = -1;

    public interface OnMoveAction {
        void moveAction();
    }

    public CircularSliderView(Context context) {
        super(context);
        init();
    }

    public CircularSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(getContext().getColor(com.android.launcher3.dial2.R.color.transparent));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(20);

        thumbPaint = new Paint();
        thumbPaint.setColor(getContext().getColor(R.color.color_ffb310));
        thumbPaint.setStyle(Paint.Style.FILL);

        circlePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = w / 2f;
        centerY = h / 2f;
        radius = Math.min(centerX, centerY) - distance;

        thumbX = centerX;
        thumbY = distance;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the circle path
        circlePath.reset();
        circlePath.addCircle(centerX, centerY, radius, Path.Direction.CW);
        canvas.drawPath(circlePath, circlePaint);

        // Draw the thumb
        canvas.drawCircle(thumbX, thumbY, 5, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                updateThumbPosition(currentX, currentY, event);
                return true;
            case MotionEvent.ACTION_UP:
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void updateThumbPosition(float x, float y, MotionEvent event) {

        // 获取当前触摸点的角度
        float currentAngle = (float) Math.atan2(y - centerY, x - centerX);
        currentAngle = (float) Math.toDegrees(currentAngle);
        // 确保角度在 [0, 360) 范围内
        currentAngle = (currentAngle + 360) % 360;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startAngle = currentAngle;
            return;
        }

        // 计算角度差值
        float angle = currentAngle - startAngle;
        // 确保角度差值在 [-180, 180) 范围内
        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }

        float atan2 = (float) Math.atan2(thumbY - centerY, thumbX - centerX);
        thumbX = centerX + radius * (float) Math.cos(atan2 + Math.toRadians(angle) / (isMinute ? 1 : 10));
        thumbY = centerY + radius * (float) Math.sin(atan2 + Math.toRadians(angle) / (isMinute ? 1 : 10));

        int hour = getHour();
        if (!isMinute && hour == hourValue) {
            return;
        }

        if (!isMinute) {
            setStartPosition(hour, isMinute);
//            startRingAnimation(hour);
            hourValue = hour;
        } else {
            invalidate();
        }

        // 更新起始角度
        startAngle = currentAngle;

        if (onMoveAction != null) {
            onMoveAction.moveAction();
        }
    }

    // 执行动画设置
    private void startRingAnimation(int hour) {

        // 动画时长
        long animationDuration = 1000; // 例如动画时长为 2000 毫秒
        boolean hourFormat = DateFormat.is24HourFormat(getContext());
        float angleHour;
        float startHour;
        if (hourFormat) {
            angleHour = (hour % 24) * 15.0f;
            startHour = (hourValue % 24) * 15.0f;
        } else {
            angleHour = (hour % 12) * 30.0f;
            startHour = (hourValue % 12) * 30.0f;
        }
        // 创建 ValueAnimator
        ValueAnimator animator = ValueAnimator.ofFloat(startHour, angleHour); // 从 0 度到 360 度，一圈
        // 设置动画时长
        animator.setDuration(animationDuration);
        // 更新动画过程中的位置
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            // 将角度转换为弧度
            float angleInRadians = (float) Math.toRadians(animatedValue);
            // 计算点的坐标
            thumbX = centerX + radius * (float) Math.cos(angleInRadians);
            thumbY = centerY + radius * (float) Math.sin(angleInRadians);
            invalidate();
        });
        // 启动动画
        animator.start();
    }

    public void setOnMoveAction(OnMoveAction onMoveAction) {
        this.onMoveAction = onMoveAction;
    }

    // 设置起始位置的方法
    public void setStartPosition(int num, boolean isMinute) {

        this.isMinute = isMinute;
        // 计算时针的角度（单位为度）
        float angleHour;
        if (isMinute) {
            angleHour = (num % 60) * 6.0f;
        } else {
            if (DateFormat.is24HourFormat(getContext())) {
                angleHour = (num % 24) * 15.0f;
            } else {
                angleHour = (num % 12) * 30.0f;
            }
        }

        // 将角度转换为弧度
        double angleHourRad = Math.toRadians(angleHour);
        // 计算时针末端的坐标
        thumbX = (float) (centerX + radius * Math.sin(angleHourRad));
        thumbY = (float) (centerY - radius * Math.cos(angleHourRad));

//        if (isMinute) {
        invalidate();
//        }
    }

    // 获取小时数
    public int getHour() {

        double atan2Value = (float) Math.atan2(thumbY - centerY, thumbX - centerX);
        double minAngle = -Math.PI;
        double maxAngle = Math.PI;
        double angleRange = maxAngle - minAngle;
        boolean hourFormat = DateFormat.is24HourFormat(getContext());
        double time = hourFormat ? 24.0 : 12.0;
        double hourValue = (atan2Value - minAngle) * (time / angleRange);
        if (hourValue < 0) {
            hourValue += time;
        } else if (hourValue >= time) {
            hourValue -= time;
        }
        int value = Math.round(Math.round(hourValue)) - (hourFormat ? 6 : 3);
//        int value = (int) hourValue - (hourFormat ? 6 : 3);
        if (value < 0) {
            value = (int) time + value;
        } else if (value == 0) {
            value = hourFormat ? 0 : 12;
        }
        return value;
    }

    // 获取小时数
    public int getMinute() {
        double atan2Value = (float) Math.atan2(thumbY - centerY, thumbX - centerX);
        double minAngle = -Math.PI;
        double maxAngle = Math.PI;
        double angleRange = maxAngle - minAngle;
        double minuteValue = (atan2Value - minAngle) * (60.0 / angleRange);
        if (minuteValue < 0) {
            minuteValue += 60.0;
        } else if (minuteValue >= 60.0) {
            minuteValue -= 60.0;
        }
//        int value = Math.round(Math.round(minuteValue)) - 15;
        int value = (int) minuteValue - 15;
        if (value < 0) {
            value = 60 + value;
        }
        return value;
    }
}
