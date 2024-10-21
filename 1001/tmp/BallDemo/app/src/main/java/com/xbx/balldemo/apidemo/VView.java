package com.xbx.balldemo.apidemo;


import android.animation.Animator;
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
import com.xbx.balldemo.event.AnimatorBackListener;
import com.xbx.balldemo.event.AnimatorBackListener2;

/**
 * Created by viseator on 3/8/17.
 * Wu Di
 * Email: viseator@gmail.com
 */

public class VView extends LinearLayout implements ValueAnimator.AnimatorUpdateListener,
        View.OnClickListener, Animator.AnimatorListener {
    private   String TAG = "@vir VView";
    public  int radius = (int)getResources().getDimension(R.dimen.ball_size);;
    private int animationHeight;
    private Paint paint = new Paint();
    private ValueAnimator animator;
    private boolean isDown = true;

    //private int canvasHeight,canvasWidth;
    private int xPos =0;
    private int yPos =0 ;
    private MainActivity context;

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
        setOnClickListener(this);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         xPos = getWidth()/2;
         yPos = (getHeight()-radius)/8;//-radius

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (animator == null) {//ball_color
            //paint.setColor(getResources().getColor(R.color.Gray));
            paint.setColor(getResources().getColor(R.color.ball_color));
            paint.setAntiAlias(true);
        }
        drawCircle(canvas);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        yPos = (int) animation.getAnimatedValue()-radius;
        invalidate();
    }

    void drawCircle(Canvas canvas) {
        canvas.drawCircle(xPos, yPos, radius/2, paint);
    }

    void init(int start, int end,MainActivity context) {
        this.context = context;
        xPos = start;
        yPos = end;
        animationHeight = yPos;
        isDown = true;
        animator = ValueAnimator.ofInt(animationHeight/8, end);
        animator.setDuration(500);
        animator.setRepeatCount(1);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(this);
        animator.addListener(this);
    }

    @Override
    public void onClick(View v) {
        Show(null);
    }
    public void Show(MainActivity context){
        Dismiss();
        int x = getWidth()/2;
        int y = getHeight()-radius;
        init(x,y,context);
        animator.start();
    }
    public void Dismiss(){
        if (animator != null) {
            animator.end();
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        addAnimatorBackListener(context,3);
        ValueAnimator vAnimation = (ValueAnimator) animation;
        if (isDown) {
           // animationHeight = (int) (animationHeight * 0.5);
           // animationHeight = (int) (animationHeight);
        }
        isDown = !isDown;
        if (isDown) {
            //vAnimation.setIntValues(canvasHeight - animationHeight, canvasHeight);
           // vAnimation.setInterpolator(new AccelerateInterpolator());
            vAnimation.setIntValues(animationHeight/8, yPos);
             vAnimation.setInterpolator(new AccelerateInterpolator());
        } else {
            //vAnimation.setIntValues(canvasHeight, canvasHeight - animationHeight);
            //vAnimation.setInterpolator(new DecelerateInterpolator());
            vAnimation.setIntValues(yPos,animationHeight/8);
            vAnimation.setInterpolator(new DecelerateInterpolator());
        }
    }


    @Override
    public void onAnimationStart(Animator animation) {
        addAnimatorBackListener(context,0);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        addAnimatorBackListener(context,1);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        addAnimatorBackListener(context,2);
    }

    public void addAnimatorBackListener(AnimatorBackListener2 al, int i){
          switch(i){
              case 0:
                  al.onAnimationStart(i);
                  break;
              case 1:
                  al.onAnimationEnd(i);
                  break;
              case 2:
                  al.onAnimationCancel(i);
                  break;
              case 3:
                  al.onAnimationRepeat(i);
                  break;
              default:break;
          }
    }
}