package com.renny.contractgridview.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtil {

    public static int dpToPx(Resources resources, int dp) {
        float density = resources.getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }
    /*** 获取屏幕的宽度* @param context:* @return :*/
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

}
