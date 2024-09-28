package com.android.launcher3.common.widget.theme;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.utils.LogUtil;

import java.util.List;

/**
 * 基础控件，所有的控件，继承自该类进行扩展
 */
abstract class BaseViewGroup extends ViewGroup implements ThemeInterface {

    protected String TAG = getClass().getSimpleName() + "--->>>";

    protected int radius;// 圆形半径
    protected int imageRadius;// 子视图半径
    protected int imageWidth;// 子视图直径
    private List<AppBean> appBeans;// 数据
    private IThemeListener listener;// 监听者

    @Override
    public void setIThemeListener(IThemeListener listener) {
        this.listener = listener;
    }

    public BaseViewGroup(Context context) {
        this(context, null);
    }

    public BaseViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算半径
        radius = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec)) / 2;

        // 计算子视图的半径
        imageRadius = calcChildViewRadius();

        // 计算子视图的直径
        imageWidth = imageRadius * 2;

        // 测量每个子视图的尺寸
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, MeasureSpec.makeMeasureSpec(imageRadius * 2, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(imageRadius * 2, MeasureSpec.EXACTLY));
        }

        // 设置布局的尺寸
        super.setMeasuredDimension(radius * 2, radius * 2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            onLayoutReset(false, l, t);
        }
    }

    @Override
    public void setDefaultData(List<AppBean> appBeans) {
        if (appBeans == null || appBeans.size() == 0) {
            return;
        }
        this.appBeans = appBeans;

        removeAllViews();

        // 创建ImageView并添加到布局中
        int index = -1;
        for (AppBean bean : appBeans) {
            ImageView view = new ClipPathCircleImage(getContext());
            ViewGroup.LayoutParams params = getImageViewParams();
            if (params != null) {
                view.setLayoutParams(params);
            }
            view.setImageDrawable(bean.getIcon());
            if (getPaddingInterval() > 0) {
                view.setPadding(getPaddingInterval(), getPaddingInterval(), getPaddingInterval(), getPaddingInterval());
            }
            addView(view);
            // 设置子视图点击事件
            index++;
            setOnClickListener(view, index);
        }

        // 创建TextView并添加到布局中
        if (isAddTextView()) {
            for (AppBean bean : appBeans) {
                TextView view = new TextView(getContext());
                view.setText(bean.getName());
                view.setTextSize(getTextSize());
                view.setTextColor(getTextColor());
                if (getPaddingInterval() > 0) {
                    view.setPadding(getPaddingInterval(), getPaddingInterval(), getPaddingInterval(), getPaddingInterval());
                }
                addView(view);
            }
        }

        requestLayout();
    }

    protected ViewGroup.LayoutParams getImageViewParams(){
        return null;
    }

    @Override
    public List<AppBean> getDefaultData() {
        return appBeans;
    }

    private int mPostion;

    @Override
    public void setOnClickListener(View child, int position) {
        child.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPostion = position;
                LogDebug("是点击事件,position = " + position + ",event.getAction() = " + event.getAction());
                return false;
            }
        });
    }

    // 处理点击事件的逻辑
    protected void handleClickEvent() {
        if (listener != null) {
            List<AppBean> appBeans = getDefaultData();
            if (appBeans != null && appBeans.size() > 0) {
                listener.onItemClick(appBeans.get(mPostion));
            }
        }
    }

    @Override
    public boolean isScale() {
        return false;
    }

    @Override
    public int getPaddingInterval() {
        return 0;
    }

    @Override
    public boolean isAddTextView() {
        return false;
    }

    @Override
    public int getTextColor() {
        return Color.WHITE;
    }

    @Override
    public float getTextSize() {
        return 15f;
    }

    protected void LogDebug(String msg) {
        LogUtil.d(TAG + msg, LogUtil.TYPE_RELEASE);
    }
}