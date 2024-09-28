package com.android.launcher3.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;

/**
 * 四角圆弧形柱状图
 */
public class CustomBarChart extends BarChart {

    private float mRoundedBarRadius = 0f;
    private boolean mDrawRoundedBars;

    public CustomBarChart(Context context) {
        super(context);
    }

    public CustomBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new CustomBarChartRenderer(this, mAnimator, mViewPortHandler, mDrawRoundedBars, mRoundedBarRadius);
    }

    public void setRoundedBarRadius(float mRoundedBarRadius) {
        this.mRoundedBarRadius = mRoundedBarRadius;
        this.mDrawRoundedBars = true;
        init();
    }
}
