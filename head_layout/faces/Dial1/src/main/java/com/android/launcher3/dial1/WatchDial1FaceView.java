package com.android.launcher3.dial1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class WatchDial1FaceView extends View {

    private Bitmap mHourHandBitmap;
    private Bitmap mMinuteHandBitmap;
    private Bitmap mSecondHandBitmap;

    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;

    public WatchDial1FaceView(Context context) {
        super(context);
        init();
    }

    public WatchDial1FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        //开启抗锯齿
        mPaint.setAntiAlias(true);
        //图片抗锯齿滤镜
        mPaint.setFilterBitmap(true);
        mHourHandBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hour);
        mMinuteHandBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.minute);
        mSecondHandBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.second);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        // 时针旋转角度
        float hourRotation = (hour + minute / 60f) * 30;
        // 分针旋转角度
        float minuteRotation = minute * 6;
        // 秒针旋转角度
        float secondRotation = second * 6;
        // 保存画布状态
        canvas.save();
        // 将画布移动到表盘中心
        canvas.translate(mCenterX, mCenterY);
        // 旋转时针
        canvas.rotate(hourRotation, 0, 0);
        canvas.drawBitmap(mHourHandBitmap, -mHourHandBitmap.getWidth() / 2, -mHourHandBitmap.getHeight(), mPaint);
        // 旋转分针
        canvas.rotate(minuteRotation - hourRotation, 0, 0);
        canvas.drawBitmap(mMinuteHandBitmap, -mMinuteHandBitmap.getWidth() / 2, -mMinuteHandBitmap.getHeight(), mPaint);
        // 旋转秒针
        canvas.rotate(secondRotation - minuteRotation, 0, 0);
        canvas.drawBitmap(mSecondHandBitmap, -mSecondHandBitmap.getWidth() / 2, -mSecondHandBitmap.getHeight()+20, mPaint);
        // 恢复画布状态
        canvas.restore();
        // 每秒重绘一次
        postInvalidateDelayed(1000);
    }
}