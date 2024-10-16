package com.xbx.balldemo.view;

import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

public class ShapeHolder {
    private float x = 0, y = 0;// 注意此处的x和y是画布开始的位置
    private ShapeDrawable shap;
    private int color;
    private RadialGradient gradient; // 环形渐变
    private float alpha = 1f;
    private Paint paint;

    ShapeHolder(ShapeDrawable s) {
        this.shap = s;
    }

    public float getWidth() {
        return shap.getShape().getWidth();
    }

    public void setWidth(float width) {
        Shape s = shap.getShape();
        s.resize(width, s.getHeight());
    }

    public float getHeight() {
        return shap.getShape().getHeight();
    }

    public void setHeight(float height) {
        Shape s = shap.getShape();
        s.resize(s.getWidth(), height);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public ShapeDrawable getShap() {
        return shap;
    }

    public void setShap(ShapeDrawable shap) {
        this.shap = shap;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public RadialGradient getGradient() {
        return gradient;
    }

    public void setGradient(RadialGradient gradient) {
        this.gradient = gradient;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

}