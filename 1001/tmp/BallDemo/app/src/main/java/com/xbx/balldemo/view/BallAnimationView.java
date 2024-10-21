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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.xbx.balldemo.R;
import com.xbx.balldemo.apidemo.BouncingBalls;
import com.xbx.balldemo.apidemo.ShapeHolder;
import com.xbx.balldemo.event.*;
import com.xbx.balldemo.util.Constants;

import java.util.ArrayList;

/**
 * Create by pengmin on 2024/10/17 .
 */
public class BallAnimationView extends View implements ValueAnimator.AnimatorUpdateListener{

    private static final int RED = 0xffFF8080;
    private static final int BLUE = 0xff8080FF;
    private static final int CYAN = 0xff80ffff;
    //private static final int GREEN = 0xff80ff80;
    private static final int translucent = 0x00000000;
    private static final int translucent2 = 0x00000001;

    public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
    AnimatorSet animation = null;
    ShapeHolder newBall;
    final float y_range = 500;
    float ball_size = 50f;
    float ball_increment = 25.0f;
    float startX = 0;
    float startY = 0;
    float endY = 0;

    ValueAnimator fadeAnim=null;
    AnimatorSet animatorSet = null;
    private BouncingBalls context;
    private  ValueAnimator colorAnim= null;
    public BallAnimationView(BouncingBalls context) {
        super(context);
        this.context = context;
        //setBackgroundColor(Color.TRANSPARENT);
        ball_size = getResources().getDimension(R.dimen.ball_size);
        ball_increment = ball_size/4;
        //float h = getHeight();// 屏幕高度
        // Animate background color
        float y  = (float)getHeight();
        startY=0;
        if(newBall != null){
            startY = newBall.getY();
        }
        endY = getHeight() - ball_size;
        float h = (float)getHeight();
        //float eventY = event.getY();
        //int duration = (int)(y_range * ((h - y)/h));
        int duration = 0;
        // Note that setting the background color will automatically invalidate the
        // view, so that the animated color, and the bouncing balls, get redisplayed on
        // every frame of the animation.
         colorAnim = ObjectAnimator.ofInt(this, "backgroundColor",translucent,translucent2);
        //ValueAnimator colorAnim = ObjectAnimator.ofInt( this, "backgroundColor",translucent,translucent);
        //ValueAnimator colorAnim = ObjectAnimator.ofInt(this, "backgroundColor",(int)startY, (int)endY);
        //getResources().getColor(R.color.translucent)

        colorAnim.setDuration(duration);
        //colorAnim.setEvaluator(new ArgbEvaluator());
        //colorAnim.setInterpolator(new AccelerateInterpolator());
        colorAnim.setInterpolator(new LinearInterpolator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN &&
                event.getAction() != MotionEvent.ACTION_MOVE) {
            Show(event.getX(), event.getY());
            return false;
        }

        return true;
    }

    int count=0;
    public void Show(float x,float y){
        count++;
        Log.i(Constants.bavTAG,"count:"+count+"x:"+x+"y:"+y);
        //ShapeHolder newBall = addBall(x,y);
        newBall = addBall(x,y);
        // Bouncing animation with squash and stretch
        startX = x;
        startY = newBall.getY();
        endY = getHeight() - ball_size;
        float h = (float)getHeight();
        //float eventY = event.getY();
        int duration = (int)(y_range * ((h - y)/h));
        Log.i(Constants.bavTAG,"duration:"+duration);
        if(duration <0){
            duration = 0;
        }
        ValueAnimator bounceAnim = ObjectAnimator.ofFloat(newBall, "y", startY, endY);
        bounceAnim.setDuration(duration);
        bounceAnim.setInterpolator(new AccelerateInterpolator());
        bounceAnim.addUpdateListener(this);
        ValueAnimator squashAnim1 = ObjectAnimator.ofFloat(newBall, "x", newBall.getX(),
                newBall.getX() - ball_increment);
        squashAnim1.setDuration(duration/4);
        squashAnim1.setRepeatCount(1);
        squashAnim1.setRepeatMode(ValueAnimator.REVERSE);
        squashAnim1.setInterpolator(new DecelerateInterpolator());
        squashAnim1.addUpdateListener(this);
        ValueAnimator squashAnim2 = ObjectAnimator.ofFloat(newBall, "width", newBall.getWidth(),
                newBall.getWidth() + ball_size/2);//
        squashAnim2.setDuration(duration/4);
        squashAnim2.setRepeatCount(1);
        squashAnim2.setRepeatMode(ValueAnimator.REVERSE);
        squashAnim2.setInterpolator(new DecelerateInterpolator());
        squashAnim2.addUpdateListener(this);
        ValueAnimator stretchAnim1 = ObjectAnimator.ofFloat(newBall, "y", endY,
                endY + ball_increment);
        stretchAnim1.setDuration(duration/4);
        stretchAnim1.setRepeatCount(1);
        stretchAnim1.setInterpolator(new DecelerateInterpolator());
        stretchAnim1.setRepeatMode(ValueAnimator.REVERSE);
        stretchAnim1.addUpdateListener(this);
        ValueAnimator stretchAnim2 = ObjectAnimator.ofFloat(newBall, "height",
                newBall.getHeight(), newBall.getHeight() - ball_increment);
        stretchAnim2.setDuration(duration/4);
        stretchAnim2.setRepeatCount(1);
        stretchAnim2.setInterpolator(new DecelerateInterpolator());
        stretchAnim2.setRepeatMode(ValueAnimator.REVERSE);
        stretchAnim2.addUpdateListener(this);
        ValueAnimator bounceBackAnim = ObjectAnimator.ofFloat(newBall, "y", endY,
                startY);
        bounceBackAnim.setDuration(duration);
        bounceBackAnim.setInterpolator(new DecelerateInterpolator());
        bounceBackAnim.addUpdateListener(this);
        // Sequence the down/squash&stretch/up animations
        AnimatorSet bouncer = new AnimatorSet();
        bouncer.play(bounceAnim).before(squashAnim1);
        bouncer.play(squashAnim1).with(squashAnim2);
        bouncer.play(squashAnim1).with(stretchAnim1);
        bouncer.play(squashAnim1).with(stretchAnim2);
        bouncer.play(bounceBackAnim).after(stretchAnim2);
        //bouncer.addUpdateListener(this);

        // Fading animation - remove the ball when the animation is done
         fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f);
        fadeAnim.setDuration(250);
        fadeAnim.addUpdateListener(this);
        //addAnimatorBackListener(context,1);
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                addAnimatorBackListener(context,0);

                balls.remove(((ObjectAnimator)animation).getTarget());
            }
            @Override
            public void onAnimationStart(Animator animation) {
                addAnimatorBackListener(context,1);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                addAnimatorBackListener(context,2);
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                addAnimatorBackListener(context,3);
            }



        });

        // Sequence the two animations to play one after the other
        animatorSet = new AnimatorSet();
        animatorSet.play(bouncer).before(fadeAnim);
        // Start the animation
        animatorSet.start();
        //addAnimatorBackListener(context,0);

    }
    public void addAnimatorBackListener(AnimatorBackListener3 al, int i){
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

    public void dismiss(){
        if(animatorSet!=null&&animatorSet.isRunning()){
            animatorSet.cancel();
        }
    }
    public void Close(){
        if(animatorSet!=null&&animatorSet.isRunning()){
            animatorSet.cancel();
        }
        if(colorAnim!=null&&colorAnim.isRunning()) {
            colorAnim.cancel();
        }
    }

    private ShapeHolder addBall(float x, float y) {
        OvalShape circle = new OvalShape();
        circle.resize(ball_size, ball_size);
        ShapeDrawable drawable = new ShapeDrawable(circle);
        ShapeHolder shapeHolder = new ShapeHolder(drawable);
        shapeHolder.setX(x - ball_size/2);
        shapeHolder.setY(y - ball_size/2);
        /*int red = (int)(Math.random() * 255);
        int green = (int)(Math.random() * 255);
        int blue = (int)(Math.random() * 255);
        int color = 0xff000000 | red << 16 | green << 8 | blue;*/
        int color = getResources().getColor(R.color.ball_color);
        Paint paint = drawable.getPaint(); //new Paint(Paint.ANTI_ALIAS_FLAG);
        //int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
        int darkColor = getResources().getColor(R.color.ball_color);
        RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
                ball_size, color, darkColor, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        shapeHolder.setPaint(paint);
        balls.add(shapeHolder);
        return shapeHolder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
        for (int i = 0; i < balls.size(); ++i) {
            ShapeHolder shapeHolder = balls.get(i);
            canvas.save();
            canvas.translate(shapeHolder.getX(), shapeHolder.getY());
            shapeHolder.getShape().draw(canvas);
            canvas.restore();
        }
    }






    //end
    Animator.AnimatorListener listener;
    public Animator.AnimatorListener GetListener( AnimatorBackListener al,int i){
        if(listener== null) {
            listener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    al.onAnimationStart(animation,i);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    al.onAnimationRepeat(animation,i);

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    al.onAnimationEnd(animation,i);

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    al.onAnimationCancel(animation,i);

                }
            };
        }
        return listener;
    }
    Animator.AnimatorListener listener2;
    public Animator.AnimatorListener GetListener2( AnimatorBackListener al,int i){
        if(listener2== null) {
            listener2 = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    al.onAnimationStart(animation,i);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    al.onAnimationRepeat(animation,i);

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    al.onAnimationEnd(animation,i);

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    al.onAnimationCancel(animation,i);

                }
            };
        }
        return listener2;
    }
    public void addAnimatorBackListener2(AnimatorBackListener al,int i){
        if(i==0) {
            animatorSet.removeListener(GetListener(al,i));
            animatorSet.addListener(GetListener(al,i));
        }else{
            fadeAnim.removeListener(GetListener2(al,i));
            fadeAnim.addListener(GetListener2(al,i));
        }
    }



    private AnimationEventListener.OnAnimationStartListener onAnimationStartListener;
    private AnimationEventListener.OnAnimationEndListener onAnimationEndListener;
    private AnimationEventListener.OnAnimationCancelListener onAnimationCancelListener;
    private AnimationEventListener.OnAnimationRepeatListener onAnimationRepeatListener;
    public void setAnimationStart(AnimationEventListener.OnAnimationStartListener mOnAnimationStartListener) {
        this.onAnimationStartListener = mOnAnimationStartListener;
    }
    public void setAnimationEnd(AnimationEventListener.OnAnimationEndListener mOnAnimationEndListener) {
        this.onAnimationEndListener = mOnAnimationEndListener;
    }

    public void setAnimationCancel(AnimationEventListener.OnAnimationCancelListener mOnAnimationCancelListener) {
        this.onAnimationCancelListener = mOnAnimationCancelListener;
    }

    public void setAnimationRepeat(AnimationEventListener.OnAnimationRepeatListener mOnAnimationRepeatListener) {
        this.onAnimationRepeatListener = mOnAnimationRepeatListener;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.invalidate();
    }
}