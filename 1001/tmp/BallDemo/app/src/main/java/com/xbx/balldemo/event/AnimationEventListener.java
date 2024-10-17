package com.xbx.balldemo.event;

import android.animation.Animator;

/**
 * Create by pengmin on 2024/10/17 .
 */
public interface AnimationEventListener {
    interface OnAnimationStartListener {
        void onAnimationStart(Animator animator);
    }

    interface OnAnimationEndListener {
        void onAnimationEnd(Animator animator);
    }

    interface OnAnimationCancelListener {
        void onAnimationCancel(Animator animator);
    }

    interface OnAnimationRepeatListener {
        void onAnimationRepeat(Animator animator);
    }
}
