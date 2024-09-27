package com.android.launcher3.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Converter {

    public static Drawable fileToDrawable(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Drawable drawable = new BitmapDrawable(bitmap);
            return drawable;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
