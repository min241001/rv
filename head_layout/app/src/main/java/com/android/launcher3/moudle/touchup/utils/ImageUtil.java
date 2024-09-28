package com.android.launcher3.moudle.touchup.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * Create by pengmin on 2024/9/6 .
 */
public class ImageUtil {
    public static Bitmap blurBitmap(Bitmap bitmap, Context context) {

        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(context);

        // 创建高斯模糊对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //设定模糊度(注：Radius最大只能设置25.f)
        blurScript.setRadius(15.f);

        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        // Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        // recycle the original bitmap
        bitmap.recycle();

        // After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }
    public static Bitmap getBitmap(Context context, int drawableId)
    {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        return getBitmap(drawable);
    }
    public static Bitmap getBitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable)
        {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        else
        {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

}
