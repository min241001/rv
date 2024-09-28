package com.android.launcher3.dial1;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import com.android.launcher3.base.BaseFragment;

/**
 * @Description：壁纸1
 */
public class Dial1Fragment extends BaseFragment {

    private ImageView animation;
    private AnimationDrawable frameAnimation;

    @Override
    protected int getResourceId() {
        return R.layout.dial1_fragment_main;
    }

    @Override
    protected void initView() {
        animation = findViewById(R.id.frame_animation);
    }

    @Override
    protected void initData() {
        // 获取AnimationDrawable
        frameAnimation = (AnimationDrawable) animation.getBackground();
        // 在UI线程之外启动动画
        if (animation != null){
            animation.post(new Runnable() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            });
        }
    }

    @Override
    protected void handleScreenOn() {
        // 在UI线程之外启动动画
        if (animation != null){
            animation.post(new Runnable() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            });
        }
    }

    @Override
    protected void handleScreenOff() {
        // 在UI线程之外启动动画
        if (animation != null){
            animation.post(new Runnable() {
                @Override
                public void run() {
                    frameAnimation.stop();
                }
            });
        }
    }
}
