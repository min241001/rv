package com.android.launcher3.moudle.timer.custom;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;


/**
 * * Author : fangzheng
 * Date : 2024/9/24
 * Details : 自定义数字选择器的选中与未选中字体颜色
 */

public class CustomNumberPicker extends NumberPicker {

    public CustomNumberPicker(Context context) {
        super(context);
        init();
    }

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @SuppressLint("NewApi")
    private void init() {
        // 设置默认值
        setMinValue(0);
        setMaxValue(100);
        setTextColor(Color.WHITE); // 默认选中字体颜色
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateTextColor();
    }

    private void updateTextColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                if (editText.isFocused()) {
                    editText.setTextColor(Color.WHITE); // 选中项颜色
                } else {
                    editText.setTextColor(Color.WHITE); // 褐色
                }
            }
        }
    }

    @Override
    public void setValue(int value) {
        super.setValue(value);
        updateTextColor(); // 更新颜色
    }
}





