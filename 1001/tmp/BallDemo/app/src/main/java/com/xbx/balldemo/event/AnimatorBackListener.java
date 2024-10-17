package com.xbx.balldemo.event;

import android.animation.Animator;

/**
 * Create by pengmin on 2024/10/17 .
 */
public interface AnimatorBackListener {
    void onAnimationStart(Animator var1,int i);

    void onAnimationEnd(Animator var1,int i);

    void onAnimationCancel(Animator var1,int i);

    void onAnimationRepeat(Animator var1,int i);
}
