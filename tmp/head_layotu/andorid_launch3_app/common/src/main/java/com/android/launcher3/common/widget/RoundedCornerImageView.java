package com.android.launcher3.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * 圆角控件
 */
public class RoundedCornerImageView extends androidx.appcompat.widget.AppCompatImageView {
    private float cornerRadius = 10.0f;

    public RoundedCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundedCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 设置视图背景为透明，以展示圆角效果
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获取ImageView原本的Drawable
        Drawable drawable = getDrawable();

        if (drawable == null) {
            super.onDraw(canvas);
            return;
        }

        // 创建Path来定义圆角
        Path path = new Path();
        int width = getWidth();
        int height = getHeight();
        float radius = cornerRadius;

        // 圆角的区域
        RectF rect = new RectF(0, 0, width, height);

        // 添加圆角到Path中
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);

        // 保存Canvas当前的状态
        canvas.save();

        // 裁剪出圆角区域
        canvas.clipPath(path);

        // 绘制ImageView的原本Drawable
        super.onDraw(canvas);

        // 恢复Canvas状态
        canvas.restore();
    }

    public void setCornerRadius(float radius) {
        cornerRadius = radius;
        invalidate();
    }
}
