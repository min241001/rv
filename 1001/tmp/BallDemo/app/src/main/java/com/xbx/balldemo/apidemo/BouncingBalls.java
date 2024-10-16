/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xbx.balldemo.apidemo;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import android.animation.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xbx.balldemo.R;
import com.xbx.balldemo.view.AnimationView;

import java.util.ArrayList;


public class BouncingBalls extends Activity {
    /** Called when the activity is first created. */
    private Context context;
    MyAnimationView  animator;
    private Button btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bouncing_balls);
        btn = (Button)findViewById(R.id.btn);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        context =  this;
        animator = new MyAnimationView(this);
        container.addView(animator);

        //
        /*LinearLayout container = new LinearLayout(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(animator, lp);
        setContentView(container);*/
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                animator.Show(container.getX()/2,container.getY());
            }
        });

    }


    public class MyAnimationView extends View {

        private static final int RED = 0xffFF8080;
        private static final int BLUE = 0xff8080FF;
        private static final int CYAN = 0xff80ffff;
        //private static final int GREEN = 0xff80ff80;
        private static final int translucent = 0x00000000;
        private static final int translucent2 = 0x00000001;

        public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
        AnimatorSet animation = null;
        ShapeHolder newBall;
        static final float y_range = 500;
        static final float ball_size = 50f;
        float startX = 0;
        float h = 0;
        float startY = 0;
        float endY = 0;

        public MyAnimationView(Context context) {
            super(context);
            setBackgroundColor(Color.TRANSPARENT);
            //float h = getHeight();// 屏幕高度

            /*float startY=0;
            if(newBall != null){
            startY = newBall.getY();
            }
            float endY = h - ball_size;

            int duration = (int) (FULL_TIME * ((h - startX / h)));
            if (duration < 0) {
                duration = 0;
            }*/
            // Animate background color
            float y = h;
            startY=0;
            if(newBall != null){
                startY = newBall.getY();
            }
            endY = getHeight() - 50f;
            float h = (float)getHeight();
            //float eventY = event.getY();
            int duration = (int)(y_range * ((h - y)/h));
            // Note that setting the background color will automatically invalidate the
            // view, so that the animated color, and the bouncing balls, get redisplayed on
            // every frame of the animation.
            ValueAnimator colorAnim = ObjectAnimator.ofInt(this, "backgroundColor",translucent,translucent2);
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
        public void Show(float x,float y){
            Log.i("bb","x:"+x+"y:"+y);
            //ShapeHolder newBall = addBall(x,y);
            h = y;
            startX = x;
            newBall = addBall(x,y);

            // Bouncing animation with squash and stretch
            //float startY = newBall.getY();
            //float endY = getHeight() - 50f;
            startY = newBall.getY();
            endY = getHeight() - 50f;
            float h = (float)getHeight();
            //float eventY = event.getY();
            int duration = (int)(y_range * ((h - y)/h));
            ValueAnimator bounceAnim = ObjectAnimator.ofFloat(newBall, "y", startY, endY);
            bounceAnim.setDuration(duration);
            bounceAnim.setInterpolator(new AccelerateInterpolator());
            ValueAnimator squashAnim1 = ObjectAnimator.ofFloat(newBall, "x", newBall.getX(),
                    newBall.getX() - 25f);
            squashAnim1.setDuration(duration/4);
            squashAnim1.setRepeatCount(1);
            squashAnim1.setRepeatMode(ValueAnimator.REVERSE);
            squashAnim1.setInterpolator(new DecelerateInterpolator());
            ValueAnimator squashAnim2 = ObjectAnimator.ofFloat(newBall, "width", newBall.getWidth(),
                    newBall.getWidth() + 50);
            squashAnim2.setDuration(duration/4);
            squashAnim2.setRepeatCount(1);
            squashAnim2.setRepeatMode(ValueAnimator.REVERSE);
            squashAnim2.setInterpolator(new DecelerateInterpolator());
            ValueAnimator stretchAnim1 = ObjectAnimator.ofFloat(newBall, "y", endY,
                    endY + 25f);
            stretchAnim1.setDuration(duration/4);
            stretchAnim1.setRepeatCount(1);
            stretchAnim1.setInterpolator(new DecelerateInterpolator());
            stretchAnim1.setRepeatMode(ValueAnimator.REVERSE);
            ValueAnimator stretchAnim2 = ObjectAnimator.ofFloat(newBall, "height",
                    newBall.getHeight(), newBall.getHeight() - 25);
            stretchAnim2.setDuration(duration/4);
            stretchAnim2.setRepeatCount(1);
            stretchAnim2.setInterpolator(new DecelerateInterpolator());
            stretchAnim2.setRepeatMode(ValueAnimator.REVERSE);
            ValueAnimator bounceBackAnim = ObjectAnimator.ofFloat(newBall, "y", endY,
                    startY);
            bounceBackAnim.setDuration(duration);
            bounceBackAnim.setInterpolator(new DecelerateInterpolator());
            // Sequence the down/squash&stretch/up animations
            AnimatorSet bouncer = new AnimatorSet();
            bouncer.play(bounceAnim).before(squashAnim1);
            bouncer.play(squashAnim1).with(squashAnim2);
            bouncer.play(squashAnim1).with(stretchAnim1);
            bouncer.play(squashAnim1).with(stretchAnim2);
            bouncer.play(bounceBackAnim).after(stretchAnim2);

            // Fading animation - remove the ball when the animation is done
            ValueAnimator fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f);
            fadeAnim.setDuration(250);
            fadeAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    balls.remove(((ObjectAnimator)animation).getTarget());

                }
            });

            // Sequence the two animations to play one after the other
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(bouncer).before(fadeAnim);

            // Start the animation
            animatorSet.start();

        }

        private ShapeHolder addBall(float x, float y) {
            OvalShape circle = new OvalShape();
            circle.resize(50f, 50f);
            ShapeDrawable drawable = new ShapeDrawable(circle);
            ShapeHolder shapeHolder = new ShapeHolder(drawable);
            shapeHolder.setX(x - 25f);
            shapeHolder.setY(y - 25f);
            int red = (int)(Math.random() * 255);
            int green = (int)(Math.random() * 255);
            int blue = (int)(Math.random() * 255);
            int color = 0xff000000 | red << 16 | green << 8 | blue;
            Paint paint = drawable.getPaint(); //new Paint(Paint.ANTI_ALIAS_FLAG);
            int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
            RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
                    50f, color, darkColor, Shader.TileMode.CLAMP);
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
    }
    private void initAnim() {
        ValueAnimator
                valueAnimator = ValueAnimator.ofInt(0, 100, 50, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // pg.setProgress((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Toast.makeText(context, "动画开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(context, "动画结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Toast.makeText(context, "动画取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Toast.makeText(context, "动画重复", Toast.LENGTH_SHORT).show();
            }
        });
        valueAnimator.setDuration(5000);
        valueAnimator.setRepeatCount(1);
    }



}