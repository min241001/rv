package com.android.launcher3.common.widget.theme;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 全滑动主题
 */
public abstract class AllScrollViewTheme extends ScrollViewTheme {

    public AllScrollViewTheme(Context context) {
        super(context);
    }

    public AllScrollViewTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AllScrollViewTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected OnTouchType getOnTouchType() {
        return OnTouchType.ALL;
    }

}
