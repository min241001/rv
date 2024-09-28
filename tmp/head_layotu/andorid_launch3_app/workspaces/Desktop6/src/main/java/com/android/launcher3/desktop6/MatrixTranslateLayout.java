package com.android.launcher3.desktop6;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;


/**
 * 自定义Layout实现偏移
 */
public class MatrixTranslateLayout extends LinearLayout {

    private int parentHeight = 0;
    private int iconHeight = 60;
    private int topOffset = 0;

    public MatrixTranslateLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParentHeight(int height) {
        parentHeight = height;
    }

    public void setIconHeight(int height) {
        iconHeight = height;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();

        if (topOffset == 0) {
            topOffset = getHeight() / 2;
        }
        int top = getTop()+topOffset;

//        float tran = calculateTranslate(top , parentHeight);
        float tran2 = onTranslate(top , parentHeight);


        Matrix m = canvas.getMatrix();
//        m.setTranslate(tran*2,0);
        m.setTranslate(tran2,0);
        canvas.concat(m);
        super.dispatchDraw(canvas);
        canvas.restore();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float calculateTranslate(int top, int h) {
        float result = 0f;
        int hh = h / 2;
        result = Math.abs(top - hh) * -1f;
        return (float) (result / 2);
    }

    //重点：计算Item 偏移量的函数
    private float onTranslate(int top, int h) {
        float result = 0f;
        int radius = iconHeight;

        //屏幕的半径 - Item上需要向左平移的部分
        //屏幕的半径 - Item上需要向左平移的部分
//        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics dm = new DisplayMetrics();
//        manager.getDefaultDisplay().getMetrics(dm);
//        radius = dm.widthPixels-62;

        if (top <= iconHeight / 2) { //左边半圆的上半部分
            result = (float) Math.abs(radius - Math.sqrt(Math.pow(radius, 2) - Math.pow(radius - top, 2)));
        } else if (top > iconHeight / 2) {//左边半圆的下半部分
            result = (float) Math.abs(radius - Math.sqrt(Math.pow(radius, 2) - Math.pow(top - radius, 2))) * -1;
        }
        return result;
    }
}
