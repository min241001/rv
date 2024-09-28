package com.android.launcher3.common.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueTextView extends TextView {

    public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MarqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        /*setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setOverScrollMode(TextView.OVER_SCROLL_NEVER);
        setFocusable(true);
        setHorizontallyScrolling(true);
        setFreezesText(true);
        setSelected(true);
        requestFocus();*/
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override

    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(focused);
        }
    }
}
