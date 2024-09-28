package com.android.launcher3.moudle.shortcut.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IntentUtil {

    /**
     * 启动一个Activity
     *
     * @param context context
     * @param cls     cls
     */
    public static void startActivity(Context context, Class<?> cls) {
        try {
            Intent intent = new Intent(context, cls);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动一个Activity
     *
     * @param context context
     * @param action  启动意图
     */
    public static void startActivity(Context context, String action) {
        try {
            Intent intent = new Intent(action);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动一个带有Bundle参数的Activity
     *
     * @param context context
     * @param cls     cls
     * @param bundle  携带的数据
     */
    public static void startActivity(Context context, Class<?> cls, Bundle bundle) {
        try {
            Intent intent = new Intent(context, cls);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动一个带有Bundle参数并指定RequestCode的Activity
     *
     * @param activity    activity
     * @param cls         cls
     * @param requestCode 请求码
     * @param bundle      携带的数据
     */
    public static void startActivityForResult(Activity activity, Class<?> cls, int requestCode, Bundle bundle) {
        try {
            Intent intent = new Intent(activity, cls);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动一个带有Bundle参数的Activity
     *
     * @param context   context
     * @param pkgName   需要启动应用的包名
     * @param className 需要启动应用的全类名
     */
    public static void startActivity(Context context, String pkgName, String className) {
        try {
            Intent intent = new Intent();
            intent.setClassName(pkgName, className);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
