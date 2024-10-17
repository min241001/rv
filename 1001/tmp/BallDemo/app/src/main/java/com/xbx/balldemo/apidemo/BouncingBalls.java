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

import static android.os.SystemClock.sleep;

import android.animation.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xbx.balldemo.R;
import com.xbx.balldemo.event.AnimatorBackListener;
import com.xbx.balldemo.util.ScreenUtil;
import com.xbx.balldemo.view.BallAnimationView;

import java.util.ArrayList;
import java.util.List;


public class BouncingBalls extends Activity implements AnimatorBackListener {
    /**
     * Called when the activity is first created.
     */
    private final static String TAG = "bbs";
    private final static String TAG2 = "bbc";
    private Context context;
    BallAnimationView animator;
    private Button btn;
    private int count = 0;
    private int absolutely_x = 0;
    private int absolutely_y = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bouncing_balls);
        btn = (Button) findViewById(R.id.btn);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        context = this;
        absolutely_x = (ScreenUtil.getScreenWidth(this) - (int) getResources().getDimension(R.dimen.w_width)) / 16;
        absolutely_y = ScreenUtil.getScreenHeight(this)/8 - (int) getResources().getDimension(R.dimen.w_height) / 2;

        Log.i(TAG2,"screen_x:"+ ScreenUtil.getScreenWidth(this)+"    screen_y:"+ ScreenUtil.getScreenHeight(this));
        Log.i(TAG2,"absolutely_x:"+absolutely_x+"    absolutely_y:"+absolutely_y);
        Log.i(TAG2,"dimen_x:"+getResources().getDimension(R.dimen.w_width));
        Log.i(TAG2,"dimen_y:"+getResources().getDimension(R.dimen.w_height));
        animator = new BallAnimationView(this);
        container.addView(animator);
        InitPopupWindows();
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //InitPopupWindows();
                // PopupShow();
                count++;
                //animator.Show(container.getX() / 2, container.getY());
                animator.Show(container.getX() / 2, absolutely_y + (int) getResources().getDimension(R.dimen.w_height) / 2);
            }
        });
    }

    @Override
    public void onAnimationStart(Animator var1, int i) {
        Log.i(TAG, i + " start");
    }

    @Override
    public void onAnimationEnd(Animator var1, int i) {
        Log.i(TAG, i + " end:" + count);
        //animator.stop();
        PopupShow();
    }

    @Override
    public void onAnimationCancel(Animator var1, int i) {
        Log.i(TAG, i + " cancel");
    }

    @Override
    public void onAnimationRepeat(Animator var1, int i) {
        Log.i(TAG, i + " repeat");
    }

    WindowManager windowManager = null;
    View popup_v = null;
    WindowManager.LayoutParams layoutParams = null;

    public void InitPopupWindows() {
        popup_v = LayoutInflater.from(this).inflate(R.layout.layout_tips, null);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |    //不拦截页面点击事件
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            layoutParams.format = PixelFormat.TRANSLUCENT;
            //layoutParams.x = absolutely_x;
            layoutParams.y = absolutely_y;
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.TOP;
            layoutParams.width = (int) getResources().getDimension(R.dimen.w_width);
            layoutParams.height = (int) getResources().getDimension(R.dimen.w_height);
            layoutParams.windowAnimations = R.style.popwindowAnimStyle;
            InitPopupView(popup_v);

        }

    }

    private void InitPopupView(View v) {
        ImageView iv = v.findViewById(R.id.ball_popup_iv);
        iv.setImageResource(R.drawable.ic_launcher_background);
        TextView tv1 = v.findViewById(R.id.ball_popup_tv1);
        TextView tv2 = v.findViewById(R.id.ball_popup_tv2);
        tv1.setText("微信消息");
        tv2.setText("new message");
    }

    private void PopupShow() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (windowManager != null && layoutParams != null) {
                        windowManager.removeViewImmediate(popup_v);
                    }

                } catch (Exception e) {
                }finally{
                    windowManager.addView(popup_v, layoutParams);
                }
            }
        });


    }

    private void PopupDismiss() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (windowManager != null) {
                    windowManager.removeViewImmediate(popup_v);
                    windowManager = null;
                }
            }
        }, 1);
    }
}