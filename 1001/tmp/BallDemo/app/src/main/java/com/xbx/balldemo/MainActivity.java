package com.xbx.balldemo;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.xbx.balldemo.apidemo.BouncingBalls;

import com.xbx.balldemo.apidemo.VView;
import com.xbx.balldemo.event.AnimatorBackListener2;
import com.xbx.balldemo.view.AnimationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , AnimatorBackListener2 {
    private Button b, b2,b3;
    private VView animator;
    private final static String TAG = "main";

    //ValueAnimator.AnimatorUpdateListener,
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        b = (Button) findViewById(R.id.btn);
        b2 = (Button) findViewById(R.id.btn2);
        b3 = (Button) findViewById(R.id.btn3);
        animator = (VView) findViewById(R.id.anima_view);
        b.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        //animator.addUpdateListener(this);
        //animator.addListener(this);
       // animator.setOnClickListener(this);

    }

    private void ball2() {
        LinearLayout container = new LinearLayout(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(new AnimationView(this), lp);
        setContentView(container);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                Intent intent = new Intent(this, BouncingBalls.class);
                startActivity(intent);
                break;
            case R.id.btn2:
                ball2();
                break;
            case R.id.btn3:
                animator.Show(this);
                break;
            default:
                break;
        }
    }

int count;
    @Override
    public void onAnimationStart(int i) {
        Log.i(TAG, i + " start");
    }

    @Override
    public void onAnimationEnd(int i) {
        count+=i;
        Log.i(TAG, count + " end");
    }

    @Override
    public void onAnimationCancel(int i) {
        Log.i(TAG, i + " cancel");
    }

    @Override
    public void onAnimationRepeat(int i) {
        Log.i(TAG, i + " repeat");
    }

    //others
   /* private void showShakeAnimation(final View view, final int y) {
        if (y >= 0) {
            return;
        }
        TranslateAnimation anim = new TranslateAnimation(0,
                0, 0, y);
        anim.setInterpolator(new CycleInterpolator(1f));
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showShakeAnimation(view, y + 3);//循环跳动
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }*/
}