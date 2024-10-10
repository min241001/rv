package com.android.launcher3.base.utils;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.renny.contractgridview.R;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


/**
 * Create by pengmin on 2024/9/6 .
 */
public class CommonUtil {


    public static void SetBackResult(Activity a, int type,int source_position,int targetPosition ){
        boolean flag = false;
        if (type == 1) {
            flag = true;
        }
        Intent intent = new Intent();//源，目标Source, target
        intent.putExtra("flag", flag);
        intent.putExtra("target_position", 0);
        intent.putExtra("source_position", source_position);
        a.setResult(RESULT_OK, intent);
        a.finish();
        //AppUtils.LauncherActivity(getActivity(), beans.get(0).getPackage_name());

    }
    public static void SetBlur(Activity a, BlurView v) {
        float radius = 12f;
        View decorView = a.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        ColorDrawable drawable = new ColorDrawable(a.getResources().getColor(R.color.colorOverlay2));
        ColorDrawable drawable2 = new ColorDrawable(a.getResources().getColor(R.color.colorOverlay3));
        //Drawable d = a.getResources().getDrawable(android:R.drawable.tR.drawable.blur_background);
        v.setupWith(rootView, new RenderScriptBlur(a)) // or RenderEffectBlur
                .setFrameClearDrawable(drawable) // Optional
                .setBlurRadius(radius)
                .setFrameClearDrawable(drawable2)
        //.setOverlayColor(a.getResources().getColor(R.color.colorOverlay2))
        ;
    }
}
