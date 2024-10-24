package com.renny.contractgridview.utils;
import android.util.Log;

import com.renny.contractgridview.BuildConfig;

//import com.renny.contractgridview.BuildConfig;

/**
 * @Author: shensl
 * @Description：{日志工具类}
 * @CreateDate：2023/11/28 11:36
 * @UpdateUser: shensl
 */
public class LogUtil {
    public static String TAG = LogUtil.class.getSimpleName();

    // 使用这个类型，日志只会在debug模式打印
    public static final int TYPE_DEBUG = 0;

    // 使用这个类型，都可以打印日志
    public static final int TYPE_RELEASE = 1;

    public static int VERBOSE = BuildConfig.DEBUG ? 1 : 4;
    public static int DEBUG = 2;
    public static int INFO = 3;
    public static int WARN = 4;
    public static int ERROR = 5;
    public static int NOTHING = 6;

    public static int LEVEL = VERBOSE;//当前Log的打印等级
//    private static boolean needDetail = BuildConfig.DEBUG;//是否需要Log详细信息
    private static boolean needDetail = true;//是否需要Log详细信息

    public static void v(String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(TAG, getDetailInfo() + msg);
        }
    }

    public static void i(String msg) {
        if (LEVEL <= INFO) {
            Log.i(TAG, getDetailInfo() + msg);
        }
    }

    public static void w(String msg) {
        if (LEVEL <= WARN) {
            Log.w(TAG, getDetailInfo() + msg);
        }
    }

    public static void e(String msg) {
        if (LEVEL <= ERROR) {
            Log.e(TAG, getDetailInfo() + msg);
        }
    }

    public static void v(String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.v(TAG, getDetailInfo() + msg);
        }
    }

    public static void d(String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.d(TAG, getDetailInfo() + msg);
        }
    }

    public static void i(String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.i(TAG, getDetailInfo() + msg);
        }
    }

    public static void w(String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.w(TAG, getDetailInfo() + msg);
        }
    }

    public static void e(String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.e(TAG, getDetailInfo() + msg);
        }
    }

    public static void v(String TAG, String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.v(TAG, getDetailInfo() + msg);
        }
    }

    public static void d(String TAG, String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.d(TAG, getDetailInfo() + msg);
        }
    }

    public static void i(String TAG, String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.i(TAG, getDetailInfo() + msg);
        }
    }

    public static void w(String TAG, String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.w(TAG, getDetailInfo() + msg);
        }
    }

    public static void e(String TAG, String msg, int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.e(TAG, getDetailInfo() + msg);
        }
    }

    public static void e(String TAG, String msg, Exception e,  int type) {
        if (BuildConfig.DEBUG || type == TYPE_RELEASE) {
            Log.e(TAG, getDetailInfo() + msg, e);
        }
    }

    private static String getDetailInfo() {
        if (needDetail) {
            return getClassName() + "$" + getMethodName() + "(" + getLineNumber() + ")" + ": ";
        }
        return "";
    }

    /**
     * 获取Log所在的类名
     *
     * @return
     */
    private static String getClassName() {
        try {
            String classPath = Thread.currentThread().getStackTrace()[5].getClassName();
            return classPath.substring(classPath.lastIndexOf(".") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Log所在的方法名
     *
     * @return
     */
    private static String getMethodName() {
        try {
            return Thread.currentThread().getStackTrace()[5].getMethodName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Log所在的行
     *
     * @return
     */
    private static int getLineNumber() {
        try {
            return Thread.currentThread().getStackTrace()[5].getLineNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 日志的通用抬头
    public static String getLogClassName(String className) {
        return className + "--->>>";
    }

}

