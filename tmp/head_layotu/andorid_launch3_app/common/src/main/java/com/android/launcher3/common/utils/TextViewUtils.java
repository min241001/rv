package com.android.launcher3.common.utils;

import android.text.TextUtils;
import android.widget.TextView;

public class TextViewUtils {

    public static void setMarquee(TextView textView) {
        // 设置文字滚动效果
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(-1);
        textView.setOverScrollMode(TextView.OVER_SCROLL_NEVER);
        textView.setFocusable(true);
        textView.setHorizontallyScrolling(true);
        textView.setSelected(true);
        textView.requestFocus();
    }
}
