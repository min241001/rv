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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.xbx.balldemo.R;
import com.xbx.balldemo.event.AnimatorBackListener3;
import com.xbx.balldemo.util.Constants;
import com.xbx.balldemo.util.ScreenUtil;
import com.xbx.balldemo.view.BallAnimationView;
import com.xbx.balldemo.view.MessageWindow;


public class BouncingBalls extends Activity implements AnimatorBackListener3 {
    /**
     * Called when the activity is first created.
     */

    private Context context;
    BallAnimationView animator;
    private Button btn;

    private int absolutely_x = 0;
    private int absolutely_y = 0;
    private MessageWindow mpw = null;
    LinearLayout container = null;
    private int count2 = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View v  = LayoutInflater.from(this).inflate(R.layout.bouncing_balls, null, false);
        setContentView(R.layout.bouncing_balls);
        context = this;
        btn = (Button) findViewById(R.id.btn);
        container = (LinearLayout) findViewById(R.id.container);
        InitAnimation();

        GetPremisstion();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count2++;
                container.setVisibility(View.VISIBLE);
                animator.Show(container.getWidth() / 2, absolutely_y);//+ (int) getResources().getDimension(R.dimen.w_height) / 2
                Log.i("bbc", "000");
                //animator.Show((float) ScreenUtil.getScreenWidth(BouncingBalls.this) / 2, absolutely_y + (float) (int) getResources().getDimension(R.dimen.w_height) / 2);
            }
        });
    }

    private void InitAnimation() {
        absolutely_x = ScreenUtil.getScreenWidth(this) / 32;
        absolutely_y = (ScreenUtil.getScreenHeight(this) / 8);//+ (int) getResources().getDimension(R.dimen.w_height)) / 2
        animator = new BallAnimationView(this);
        container.addView(animator);
        mpw = new MessageWindow(this, absolutely_x, absolutely_y);
    }

    @Override
    public void onAnimationStart(int i) {
        Log.i(Constants.bbsTAG, i + " start");
    }

    int count = 0;
    @Override
    public void onAnimationEnd(int i) {
        Log.i(Constants.bbsTAG, i + " end:" + count);
        count++;
        //animator.dismiss();
        count2--;
        if (count2 <= 0) {
            container.setVisibility(View.GONE);
        }
        mpw.ShowWindow();
    }

    @Override
    public void onAnimationCancel(int i) {
        Log.i(Constants.bbsTAG, i + " cancel");
    }

    @Override
    public void onAnimationRepeat(int i) {
        Log.i(Constants.bbsTAG, i + " repeat");

    }

    private void GetPremisstion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            startActivityForResult(intent, 2909);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN &&
                event.getAction() != MotionEvent.ACTION_MOVE) {
            //Show(event.getX(), event.getY());
            return false;
        }

        return true;
    }*/


   /*implements AnimatorBackListener

   @Override
   public void onAnimationStart(Animator var1, int i) {
       Log.i(Constants.bbsTAG, i + " start");
   }

    @Override
    public void onAnimationEnd(Animator var1, int i) {
        Log.i(Constants.bbsTAG, i + " end:" + count);
        //animator.dismiss();
        count2--;
        if(count2<=0) {
            container.setVisibility(View.GONE);
        }
        mpw.ShowWindow();
    }

    @Override
    public void onAnimationCancel(Animator var1, int i) {
        Log.i(Constants.bbsTAG, i + " cancel");
    }

    @Override
    public void onAnimationRepeat(Animator var1, int i) {
        Log.i(Constants.bbsTAG, i + " repeat");
    }*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        mpw.CloseWindow();
        animator.Close();
    }


}



