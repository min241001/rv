package com.android.launcher3.common.base;

import android.graphics.Bitmap;
import com.android.launcher3.common.utils.DiskLruCacheUtil;

public class WallpaperUtil {

    private static String KEY = "wallpaper";

    // 获取壁纸
    public static Bitmap getWallpaper() {
        try {
            DiskLruCacheUtil diskLruCacheUtil = new DiskLruCacheUtil();
            diskLruCacheUtil.initDiskLruCache();
            return diskLruCacheUtil.getBitmapFromDisk(KEY);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // 设置壁纸
    public static void setWallpaper(Bitmap wallpaperBitmap) {
        try {
            DiskLruCacheUtil diskLruCacheUtil = new DiskLruCacheUtil();
            diskLruCacheUtil.initDiskLruCache();
            diskLruCacheUtil.putBitmapToDisk(KEY,wallpaperBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}