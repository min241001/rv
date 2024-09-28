package com.android.launcher3.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;

/**
 * @Author: shensl
 * @Description：动画工具类
 * @CreateDate：2024/1/24 16:56
 * @UpdateUser: shensl
 */
public class AnimatorUtil {

    /**
     * 开始动画
     * @param view
     * @return
     */
    public static ObjectAnimator startAnimator(View view) {
        if (view == null) {
            throw new NullPointerException("view==null");
        }
        // 创建动画对象，并设置旋转属性
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationY", 0f, 360f);
        // 设置动画的持续时间为2秒
        rotation.setDuration(2000L);
        // 启动动画
        rotation.start();
        return rotation;
    }

    /**
     * 结束动画
     * @param rotation
     */
    public static void stopAnimator(ObjectAnimator rotation) {
        if (rotation != null) {
            rotation.cancel();
        }
    }

}
