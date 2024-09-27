package com.android.launcher3.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.android.launcher3.common.CommonApp;

public class ScreenUtil {

    public static int dpToPx(Resources resources, int dp) {
        float density = resources.getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    public static int getScreenHeight() {
        Resources resources = CommonApp.getInstance().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }
    /*** 获取屏幕的宽度* @param context:* @return :*/
    public static int getScreenWidth() {
        WindowManager manager = (WindowManager) CommonApp.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

}
