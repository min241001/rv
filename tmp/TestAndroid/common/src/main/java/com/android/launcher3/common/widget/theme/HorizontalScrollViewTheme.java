package com.android.launcher3.common.widget.theme;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 水平滑动主题
 */
public abstract class HorizontalScrollViewTheme extends ScrollViewTheme {

    public HorizontalScrollViewTheme(Context context) {
        super(context);
    }

    public HorizontalScrollViewTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollViewTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected OnTouchType getOnTouchType() {
        return OnTouchType.HOR;
    }

}
