package com.android.launcher3.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;

public class FrameLayoutExt extends FrameLayout {

    public interface INavCallBack {
        void forward();

        void backward();

        void openByLongClick();
    }

    private static final String TAG = FrameLayoutExt.class.getSimpleName();
    private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
    private int direction = 0;          //-1:left, 0:unknow; 1: right
    private static boolean isLongClick = false;
    private Timer timer = null;
    private INavCallBack navCallBack;

    public FrameLayoutExt(@NonNull Context context) {
        super(context);
    }

    public FrameLayoutExt(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayoutExt(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FrameLayoutExt(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setNavCallBack(INavCallBack callBack) {
        this.navCallBack = callBack;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "---------------------------- 1 onInterceptTouchEvent：ACTION_DOWN");
                direction = 0;
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "---------------------------- 2 onInterceptTouchEvent：ACTION_MOVE");
                x2 = event.getX();
                y2 = event.getY();
                if (y1 - y2 > 60) {          //向上
                } else if (y2 - y1 > 60) {   //向下
                } else if (x1 - x2 > 60) {   //向左
                    direction = -1;
                    intercept = true;
                } else if (x2 - x1 > 60) {   //向右
                    direction = 1;
                    intercept = true;
                }
                break;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "---------------------------- 3 onTouchEvent：ACTION_DOWN");
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d(TAG, "---------------------------- 4 onTouchEvent：ACTION_MOVE");
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "---------------------------- 5 onTouchEvent：ACTION_UP");
            if (navCallBack != null) {
                if (direction == -1) {
                    navCallBack.forward();
                } else if (direction == 1) {
                    navCallBack.backward();
                }
            }
        }
        return false;
    }
}
