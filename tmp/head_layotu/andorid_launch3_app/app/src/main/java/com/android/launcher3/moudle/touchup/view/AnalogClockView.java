package com.android.launcher3.moudle.touchup.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import com.android.launcher3.R;

/**
 * Create by pengmin on 2024/9/2 .
 */

public class AnalogClockView extends View {
    private Time mCalendar;
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;
    private Drawable mClock;
    private int mClockWidth;
    private int mClockHeight;
    private boolean mAttached;
    private float mHour;
    private float mMinutes;
    private float mSeconds;
    private boolean mChanged;
    // /增加了秒针显示所用到的秒表
    private static String debug = "AnalogClockView";
    private static int SECONDS_FLAG = 0;
    private Message secondsMsg;
    // /end
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    onTimeChanged();// 重新获取的系统的当前时间，得到时，分，秒
                    invalidate();// 强制绘制，调用自身的onDraw();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public AnalogClockView(Context context) {
        this(context, null);
    }

    public AnalogClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public AnalogClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Resources r = context.getResources();

        if (mClock == null) {
            mClock = r.getDrawable(R.mipmap.ic_clock);
        }
        if (mHourHand == null) {
            mHourHand = r.getDrawable(R.mipmap.ic_clock_hour);
        }
        if (mMinuteHand == null) {
            mMinuteHand = r.getDrawable(R.mipmap.ic_clock_minute);
        }
        if (mSecondHand == null) {
            mSecondHand = r.getDrawable(R.mipmap.ic_clock_second);
        }
        mCalendar = new Time();
        mClockWidth = mClock.getIntrinsicWidth();
        mClockHeight = mClock.getIntrinsicHeight();
    }

    @Override
    /*
     * * 吸附到窗体上
     */
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
        }

        // NOTE: It's safe to do these after registering the receiver since the
        // receiver always runs
        // in the main thread, therefore the receiver can't run before this
        // method returns.

        // The time zone may have changed while the receiver wasn't registered,
        // so update the Time
        mCalendar = new Time();
        // Make sure we update to the current time
        onTimeChanged();
        initSecondsThread();
    }

    private void initSecondsThread() {
        secondsMsg = mHandler.obtainMessage(SECONDS_FLAG);
        Thread newThread = new Thread() {
            @Override
            public void run() {
                while (mAttached) {
                    // 如果这个消息不重新获取的话，
                    // 会抛一个this message is already in use 的异常
                    secondsMsg = mHandler.obtainMessage(SECONDS_FLAG);
                    mHandler.sendMessage(secondsMsg);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        newThread.start();
    }

    @Override
    /*
     * * 脱离窗体 在按home按键，不触发这个事件，所以这个应用的监听还是持续监听着。
     * 如果外部修改系统事件，action=Intent.ACTION_TIME_CHANGED 按back按键，触发事件，下次从onCreate从新载入
     */
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            mAttached = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        float hScale = 1.0f;
        float vScale = 1.0f;
        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mClockWidth) {
            hScale = (float) widthSize / (float) mClockWidth;
        }
        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mClockHeight) {
            vScale = (float) heightSize / (float) mClockHeight;
        }
        float scale = Math.min(hScale, vScale);
        setMeasuredDimension(resolveSize((int) (mClockWidth * scale),
                widthMeasureSpec), resolveSize((int) (mClockHeight * scale),
                heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }
        int availableWidth = getWidth();
        int availableHeight = getHeight();
        int x = availableWidth / 2;
        int y = availableHeight / 2;
        //绘制表盘
        final Drawable dial = mClock;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();
        boolean scaled = false;
        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                    (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }
        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);
        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, x, y);
        //绘制时针
        final Drawable hourHand = mHourHand;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w / 2), y - (h - h / 5), x + (w / 2), y + (h / 5));
        }
        hourHand.draw(canvas);
        canvas.restore();
        canvas.save();
        //绘制分针
        canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);
        final Drawable minuteHand = mMinuteHand;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h - h / 5), x + (w / 2), y + (h / 5));
        }
        minuteHand.draw(canvas);
        canvas.restore();
        canvas.save();
        //绘制秒针
        canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);
        final Drawable secondHand = mSecondHand;
        if (changed) {
            w = secondHand.getIntrinsicWidth();
            h = secondHand.getIntrinsicHeight();
            /*secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
                    + (h / 2));*/
            secondHand.setBounds(x - (w / 2), y - (h - h / 5), x + (w / 2), y + (h / 5)
            );
        }
        secondHand.draw(canvas);
        canvas.restore();
        // /end
        // /增加秒针的绘制
        canvas.save();
        //canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);
        if (scaled) {
            canvas.restore();
        }
    }

    /**
     * 改变时间
     */
    private void onTimeChanged() {
        mCalendar.setToNow();// ///获取手机自身的当前时间，而非实际中的标准的北京时间
        int hour = mCalendar.hour;// 小时
        int second = mCalendar.second;// 秒
        int minute = mCalendar.minute;// 分钟
        mHour = hour;// + mMinutes / 60.0f;
        mSeconds = second;// + mMu / 60.0f;;
        mMinutes = minute;
        mChanged = true;
    }
}
