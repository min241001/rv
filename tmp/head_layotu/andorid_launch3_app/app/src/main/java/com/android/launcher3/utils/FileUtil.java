package com.android.launcher3.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.android.launcher3.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/3 10:59
 * @UpdateUser: jamesfeng
 */
public class FileUtil {
    /**
     * 根据包名获取类(耗时操作，慎用）
     * @param packageName 包名
     * @return 类数组
     */
    public static List<Class<?>> getClassesByPacketName(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            Context applicationContext = App.getInstance().getApplicationContext();
            ApplicationInfo applicationInfo = applicationContext.getApplicationInfo();

            // Assuming classes are in the "classes.dex" directory
            String dexPath = new File(applicationInfo.sourceDir).getAbsolutePath();
            DexFile dexFile = new DexFile(dexPath);

            for (Enumeration<String> classNames = dexFile.entries(); classNames.hasMoreElements(); ) {
                String className = classNames.nextElement();
                if (className.startsWith(packageName) && !className.contains("$")) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (ClassNotFoundException e) {
                        // Handle exception if needed
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 读取res/raw 文件夹下的文本txt资源文件
     * @param context
     * @param resourceName 资源名
     * @return
     */
    public static String readTextFile(Context context,int resourceName) {
        StringBuilder text = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line).append("\n");
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}
