package com.android.launcher3.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.android.launcher3.common.widget.RecyclerViewExt;

public class RecyclerViewExtC extends RecyclerViewExt {
    private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
    private boolean canSliding = false;

    public RecyclerViewExtC(Context context) {
        super(context);
    }

    public RecyclerViewExtC(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewExtC(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == RecyclerViewExtC.SCROLL_STATE_IDLE) {
            canSliding = !canScrollVertically(-1);
        }
        super.onScrollStateChanged(state);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (canSliding) {
                    canSliding = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                canSliding = !canScrollVertically(-1);
                break;
        }
        return super.onTouchEvent(ev);
    }
}
