package com.baehug.callui.phone.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author : yanyong
 * Date : 2024/7/30
 * Details : 点击放大控件
 */
public class ZoomableLayout extends LinearLayout {

    public ZoomableLayout(@NonNull Context context) {
        super(context);
    }

    public ZoomableLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomableLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                zoomView(1.5f, 100);
                break;
            case MotionEvent.ACTION_UP:
                zoomView(1.0f, 30);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void zoomView(float scale, long duration) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", scale);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(duration);
        animatorSet.start();
    }
}
