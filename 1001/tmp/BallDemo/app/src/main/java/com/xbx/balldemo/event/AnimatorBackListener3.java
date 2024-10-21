package com.xbx.balldemo.event;

import android.animation.Animator;

/**
 * Create by pengmin on 2024/10/17 .
 */
public interface AnimatorBackListener3 {
    void onAnimationStart(int i);

    void onAnimationEnd(int i);

    void onAnimationCancel(int i);

    void onAnimationRepeat(int i);
}
