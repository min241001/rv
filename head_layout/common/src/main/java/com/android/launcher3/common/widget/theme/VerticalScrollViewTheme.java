package com.android.launcher3.common.widget.theme;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 垂直滑动主题
 */
public abstract class VerticalScrollViewTheme extends ScrollViewTheme {

    public VerticalScrollViewTheme(Context context) {
        super(context);
    }

    public VerticalScrollViewTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScrollViewTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected OnTouchType getOnTouchType() {
        return OnTouchType.VER;
    }

}