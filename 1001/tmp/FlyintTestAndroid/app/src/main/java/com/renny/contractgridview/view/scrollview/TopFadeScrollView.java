package com.renny.contractgridview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class TopFadeScrollView extends ScrollView {
    private static final int MAX_ALPHA = 255; // 最大透明度
    private static final int SCROLL_THRESHOLD = 200; // 滑动阈值
    private View mTopView; // 顶部视图
    public TopFadeScrollView(Context context) {
        super(context);
        init();
    }
    public TopFadeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public TopFadeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        // 设置滚动监听器
        setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                updateTopViewAlpha(scrollY);
            }
        });
    }
    public void setTopView(View topView) {
        mTopView = topView;
    }
    private void updateTopViewAlpha(int scrollY) {
        // 计算透明度的值
        int alpha = (int) (MAX_ALPHA * (scrollY * 1.0f / SCROLL_THRESHOLD));
        if (alpha > MAX_ALPHA) {
            alpha = MAX_ALPHA;
        } else if (alpha < 0) {
            alpha = 0;
        }
        // 设置顶部视图的透明度
        mTopView.setAlpha(alpha);
    }
}
