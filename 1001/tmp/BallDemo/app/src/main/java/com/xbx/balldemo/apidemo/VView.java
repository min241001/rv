package com.xbx.balldemo.apidemo;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.xbx.balldemo.MainActivity;
import com.xbx.balldemo.R;

/**
 * Created by viseator on 3/8/17.
 * Wu Di
 * Email: viseator@gmail.com
 */

public class VView extends LinearLayout implements  ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener{
    private static final String TAG = "@vir VView";
    public static final int radius = 30;
    private int xPos = radius;
    public int yPos = radius;
    private Paint paint = new Paint();
    private ValueAnimator animator;
    private boolean isDown = true;
    private int animationHeight;
    private int canvasHeight;
    public VView(Context context) {
        super(context);
    }

    public VView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setWillNotDraw(false);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init(radius, canvasHeight);
        animationHeight = canvasHeight + radius;
        isDown = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (animator == null) {
            canvasHeight = canvas.getHeight() - radius;
            paint.setColor(getResources().getColor(R.color.Gray));
            paint.setAntiAlias(true);
        }
        drawCircle(canvas);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        yPos = (int) animation.getAnimatedValue();
        invalidate();
    }

    void drawCircle(Canvas canvas) {
        canvas.drawCircle(xPos, yPos, radius, paint);
    }


    void init(int start, int end) {
        final int RED = 0xffFF8080;
        final int BLUE = 0xff8080FF;
        animator = ValueAnimator.ofInt(start, end,RED, BLUE);
        animator.setDuration(500);
        animator.setRepeatCount(1);
        //animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        //animator.setRepeatMode(ValueAnimator.REVERSE);
        //animator.setInterpolator(new AccelerateInterpolator());
        animator.setEvaluator(new ArgbEvaluator());
        //animator.setInterpolator(new ArgbEvaluator());

    }
    public void end(){
        if (animator != null) {
            animator.end();
        }
    }
    public void start(){
        if(!animator.isRunning()){
            animator.start();
        }
    }


    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {
        ValueAnimator vAnimation = (ValueAnimator) animator;
        if (isDown) {
            animationHeight = (int) (animationHeight * 0.5);
        }
        isDown = !isDown;
        if (isDown) {
            vAnimation.setIntValues(canvasHeight - animationHeight, canvasHeight);
            vAnimation.setInterpolator(new AccelerateInterpolator());
        } else {
            vAnimation.setIntValues(canvasHeight, canvasHeight - animationHeight);
            vAnimation.setInterpolator(new DecelerateInterpolator());
        }
    }
}