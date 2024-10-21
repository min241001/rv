package com.xbx.balldemo.util;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class ItemViewTouchListener2 implements View.OnTouchListener {
    private final String TAG = "mpw";
    private final WindowManager.LayoutParams layoutParams;
    private final WindowManager windowManager;
    private float lastX;
    private float lastY;

    public ItemViewTouchListener2(WindowManager.LayoutParams layoutParams, WindowManager windowManager) {
        this.layoutParams = layoutParams;
        this.windowManager = windowManager;
    }
    public boolean onTouch( View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float mPosX = event.getX();
                float mPosY = event.getY();
                Log.i(TAG, "down mPosX:" + mPosX + "mPosY:" + mPosY);
            break;
            case MotionEvent.ACTION_UP:
                float mPosX2 = event.getX();
                float mPosY2 = event.getY();
                Log.i(TAG, "up mPosX:" + mPosX2 + "mPosY:" + mPosY2);
                break;
            case MotionEvent.ACTION_MOVE:
                if (this.lastX == 0.0F || this.lastY == 0.0F) {
                    this.lastX = event.getRawX();
                    this.lastY = event.getRawY();
                }

                float nowX = event.getRawX();
                float nowY = event.getRawY();

                float movedX = nowX - this.lastX;
                float movedY = nowY - this.lastY;
                Log.i(TAG, "move  movedX:" +movedX + " movedY:" +  movedY);
                WindowManager.LayoutParams lp = this.layoutParams;
                lp.x += (int)movedX;
                lp.y += (int)movedY;
                WindowManager wm = this.windowManager;
                if (wm != null) {
                    wm.updateViewLayout(view, this.layoutParams);
                }
                this.lastX = nowX;
                this.lastY = nowY;
                break;
            default:
                break;
        }

        return true;
    }
}