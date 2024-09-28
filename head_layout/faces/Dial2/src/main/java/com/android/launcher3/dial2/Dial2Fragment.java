package com.android.launcher3.dial2;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.android.launcher3.base.BaseFragment;

public class Dial2Fragment extends BaseFragment {

    private AnimationDrawable animationDrawable;

    @Override
    protected int getResourceId() {
        return R.layout.dial2_fragment_main;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {

        ImageView imageView = findViewById(R.id.imageView);
        animationDrawable = (AnimationDrawable) imageView.getBackground();

        imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (animationDrawable != null) {
                    animationDrawable.setVisible(true, true);
                    animationDrawable.start();
                }
                return false;
            }
            return false;
        });
    }

    @Override
    protected void handleScreenOff() {
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }

    @Override
    protected void handleScreenOn() {
        
    }
}
