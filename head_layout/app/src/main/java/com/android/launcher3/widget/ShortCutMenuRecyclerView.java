package com.android.launcher3.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2024/1/26 16:11
 * @UpdateUser: shensl
 */
public class ShortCutMenuRecyclerView extends RecyclerView {
    public ShortCutMenuRecyclerView(@NonNull Context context) {
        super(context);
    }

    public ShortCutMenuRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShortCutMenuRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //通知父层ViewGroup不要拦截点击事件
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

}
