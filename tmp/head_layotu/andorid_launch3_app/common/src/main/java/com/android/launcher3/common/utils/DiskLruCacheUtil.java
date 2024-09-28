package com.android.launcher3.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskLruCacheUtil {

    private DiskLruCache mDiskLruCache;

    private final File dir = Environment.getExternalStorageDirectory();

    public static final String file = ".launcher";

    public void initDiskLruCache(){
        try {
            mDiskLruCache = DiskLruCache.open(
                    new File(dir + File.separator + file),
                    1,
                    1,
                    10 * 1024 * 1024
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putBitmapToDisk(String key, Bitmap bitmap){
        OutputStream outputStream = null;
        try  {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                outputStream = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap getBitmapFromDisk(String key){
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try (DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key)) {
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
}
