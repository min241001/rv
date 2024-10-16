package com.xbx.balldemo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

/**
 * Create by pengmin on 2024/10/15 .
 */
public class AnimationView  extends View implements ValueAnimator.AnimatorUpdateListener {

    public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
    static final float BALL_SIZE = 50f;
    static final float FULL_TIME = 500;

    public AnimationView(Context context) {
        super(context);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (ShapeHolder shapeHolder : balls) {
            canvas.save();

            canvas.translate(shapeHolder.getX(), shapeHolder.getY());
            shapeHolder.getShap().draw(canvas);
            canvas.restore();

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN
                && event.getAction() != MotionEvent.ACTION_MOVE) {
            return false;

        }
        //
        Show(event.getX(), event.getY(),null);
        return true;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        this.invalidate();

    }

    public ShapeHolder addBall(float x, float y) {

        OvalShape circle = new OvalShape();
        circle.resize(BALL_SIZE, BALL_SIZE);
        ShapeDrawable shape = new ShapeDrawable(circle);
        ShapeHolder shapeHolder = new ShapeHolder(shape);

        shapeHolder.setX(x - BALL_SIZE / 2);
        shapeHolder.setY(y - BALL_SIZE / 2);

        int red = (int) (Math.random() * 255);
        int green = (int) (Math.random() * 255);
        int blue = (int) (Math.random() * 255);

        int color = 0xff000000 + ((red << 16) | (green << 8) | blue);
        int darkColor = 0xff000000 + (((red / 4) << 16) | (green / 4 << 8) | (blue / 4));

        Paint paint = shape.getPaint();
        RadialGradient radialGradient = new RadialGradient(x, y, BALL_SIZE, color, darkColor,
                Shader.TileMode.CLAMP);
        paint.setShader(radialGradient);

        shapeHolder.setPaint(paint);

        balls.add(shapeHolder);

        return shapeHolder;
    }

    public void Show(float x,float y,View v){
        ShapeHolder newBall = addBall(x, y);
        float h = getHeight();
        if(v !=null){
            h = v.getHeight();
        }

        float startY = newBall.getY();
        float endY = h - BALL_SIZE;

        int duration = (int) (FULL_TIME * ((h - y) / h));
        if (duration < 0) {
            duration = 0;
        }

        // 定义下落的属性动画/////////////////////////////////////////////////////////////////////////
        ValueAnimator fallAnim = ObjectAnimator.ofFloat(newBall, "y", startY, endY);
        fallAnim.setDuration(duration);
        fallAnim.setInterpolator(new AccelerateDecelerateInterpolator());// 加速插值
        fallAnim.addUpdateListener(this);

        // 定义小球压扁的属性动画:画布位置左移，宽度拉长/////////////////////////////////////////////////////////////////////////
        ValueAnimator squashAnim1 = ObjectAnimator.ofFloat(newBall, "x", newBall.getX(),
                newBall.getX() - BALL_SIZE / 2);
        squashAnim1.setDuration(duration / 4);
        squashAnim1.setInterpolator(new DecelerateInterpolator());
        squashAnim1.setRepeatCount(1);
        squashAnim1.setRepeatMode(ValueAnimator.REVERSE);
        squashAnim1.addUpdateListener(this);

        ValueAnimator squashAnim2 = ObjectAnimator.ofFloat(newBall, "width",
                newBall.getWidth(),
                newBall.getWidth() + BALL_SIZE / 2);
        squashAnim2.setDuration(duration / 4);
        squashAnim2.setInterpolator(new DecelerateInterpolator());
        squashAnim2.setRepeatCount(1);
        squashAnim2.setRepeatMode(ValueAnimator.REVERSE);
        squashAnim2.addUpdateListener(this);
        // 定义小球拉伸的属性动画:画布位置下移，高度压低/////////////////////////////////////////////////////////////////////////
        ValueAnimator stretchAnim1 = ObjectAnimator.ofFloat(newBall, "y",
                endY,
                endY + BALL_SIZE / 2);
        stretchAnim1.setDuration(duration / 4);
        stretchAnim1.setInterpolator(new DecelerateInterpolator());
        stretchAnim1.setRepeatCount(1);
        stretchAnim1.setRepeatMode(ValueAnimator.REVERSE);
        stretchAnim1.addUpdateListener(this);

        ValueAnimator stretchAnim2 = ObjectAnimator.ofFloat(newBall, "height",
                newBall.getHeight(),
                newBall.getHeight() - BALL_SIZE / 2);
        stretchAnim2.setDuration(duration / 4);
        stretchAnim2.setInterpolator(new DecelerateInterpolator());
        stretchAnim2.setRepeatCount(1);
        stretchAnim2.setRepeatMode(ValueAnimator.REVERSE);
        stretchAnim2.addUpdateListener(this);
        // 定义小球弹起的属性动画://///////////////////////////////////////////////////////////////////////
        ValueAnimator bounceBackAnim = ObjectAnimator.ofFloat(newBall, "y",
                endY,
                startY);
        bounceBackAnim.setDuration(duration);
        bounceBackAnim.setInterpolator(new DecelerateInterpolator());
        bounceBackAnim.addUpdateListener(this);

        // 定义渐隐的属性动画/////////////////////////////////////////////////////////////////////////
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f);
        fadeAnim.setDuration(250);
        fadeAnim.addUpdateListener(this);
        fadeAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                balls.remove(((ObjectAnimator) animation).getTarget());
            }

        });

        // 定义属性动画集合：掉落--压扁&拉伸--弹起/////////////////////////////////////////////////////////////////////////
        AnimatorSet bouncer = new AnimatorSet();
        bouncer.play(fallAnim).before(squashAnim1);
        bouncer.play(squashAnim1).with(squashAnim2).with(stretchAnim1).with(stretchAnim2);
        bouncer.play(bounceBackAnim).after(squashAnim1);
        // 属性动画集合，将上面的bouncer和fadeAnim组合起来，并安排顺序
        AnimatorSet set = new AnimatorSet();
        set.play(bouncer).before(fadeAnim);
        set.start();

    }

}