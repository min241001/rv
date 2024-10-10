package com.android.launcher3.common.utils;

import static android.content.Context.ACTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

import com.android.launcher3.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * @Description：应用工具类
 */
public class AppUtils {

    // 安装应用
    public static void installApp(Context context, String apkFilePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {
                // 跳转到权限设置页面
                Uri packageUri = Uri.parse("package:" + context.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageUri);
                context.startActivity(intent);
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFilePath), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    // 卸载应用
    public static void uninstallApp(Context context, String packageName) {
        Uri packageUri = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
        context.startActivity(uninstallIntent);
    }

    // 获取应用图标
    public static Drawable getAppIcon(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            return appInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取应用名称
    public static String getAppName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            return appInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo != null && wifiNetworkInfo.isConnected();
    }

    // 获取应用大小
    public static long getAppSize(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            return packageInfo.applicationInfo.sourceDir.length();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 获取launcher对外版本号
    public static String getAppVersion(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    // 获取launcher版本号
    public static String getAppVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    //com.baehug.settings
    // 获取setting版本号
    public static String getSettingVersionCode(Context context) {
        if (!isAppInstalled(context, context.getString(R.string.setting_packageName))){
            LogUtil.d("设置应用未安装", LogUtil.TYPE_RELEASE);
            return "null";
        } else {
            String versionCode = "";
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = null;
            try {
                packageInfo = packageManager.getPackageInfo(context.getString(R.string.setting_packageName), 0);
                versionCode = String.valueOf(packageInfo.versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return versionCode;
        }
    }

    //获取当前屏幕分辨率
    public static String getScreen(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        //屏幕可用宽度(像素个数)
        int width = point.x;
        //屏幕可用高度(像素个数)
        int height = point.y;
        return width +"*" + height;
    }

    //获取当前android系统版本
    public static String getAndroidVersion(){
        String androidVersion = Build.VERSION.RELEASE;
        return androidVersion;
    }

    //获取当前设备总RAM
    public static Integer getTotalRAM(Context context){
        String path = "/proc/meminfo";
        String ramMemorySize = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 4096);
            ramMemorySize = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(ramMemorySize != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(ramMemorySize) / (1024 * 1024)).doubleValue()));
        }
        LogUtil.d("设备运行内存===" + totalRam + "GB", LogUtil.TYPE_RELEASE);

        return totalRam * 1024;
    }

    //获取当前设备总ROM
    public static Integer getTotalRoM() {
        File dataDir = Environment.getDataDirectory();
        StatFs stat = new StatFs(dataDir.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long size = totalBlocks * blockSize;
        long GB = 1024 * 1024 * 1024;
        final long[] deviceRomMemoryMap = {2 * GB, 4 * GB, 8 * GB, 16 * GB, 32 * GB, 64 * GB, 128 * GB, 256 * GB, 512 * GB, 1024 * GB, 2048 * GB};
        String[] displayRomSize = {"2","4","8","16","32","64","128","256","512","1024","2048"};
        int i;
        for (i = 0; i < deviceRomMemoryMap.length; i++) {
            if (size <= deviceRomMemoryMap[i]) {
                break;
            }
            if (i == deviceRomMemoryMap.length) {
                i--;
            }
        }
        int RomSize = Integer.parseInt(displayRomSize[i]);
        return RomSize * 1024;
    }




    // 获取应用包名
    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    // 应用是否安装
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // 是否有默认的启动活动
    public static boolean hasDefaultActivity(Context context) {
        String packageName = context.getPackageName();
        PackageManager packageManager = context.getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
        return launchIntent != null;
    }

    // 应用是否正在运行
    public static boolean isAppRunning(Context context) {
        String packageName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        // 获取正在运行的进程列表
        List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
        if (processList != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : processList) {
                // 检查进程名称是否与应用包名相同
                if (processInfo.processName.equals(packageName)) {
                    // 检查进程状态是否为正在运行
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 用来判断服务是否运行.
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        //此处只在前30个中查找，大家根据需要调整
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }




    /**
     * 获取棋盘有多少圈， 圈数  0   1   2   3   4   5    6
     * 总数  1   9   25  49  81  121  169
     *
     * @param sum app总数
     * @return 层数
     */
    public static int getCheckerboardAppLayer(int sum) {
        if (sum > 121) {
            return 6;
        } else if (sum > 81) {
            return 5;
        } else if (sum > 49) {
            return 4;
        } else if (sum > 25) {
            return 3;
        } else if (sum > 9) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * 获取最外圈余数
     *
     * @param sum app总数
     * @return 余数
     */
    public static int getRemainder(int sum) {
        if (sum > 121) {
            return sum - 121;
        } else if (sum > 81) {
            return sum - 81;
        } else if (sum > 49) {
            return sum - 49;
        } else if (sum > 25) {
            return sum - 25;
        } else if (sum > 9) {
            return sum - 9;
        } else {
            return sum - 1;
        }
    }

    /**
     * 获取最外层一条边上最多容纳几个item
     * @return 数量
     */
    public static int getSideItemSum(int sum) {
        if (sum > 121) {
            return 11;
        } else if (sum > 81) {
            return 9;
        } else if (sum > 49) {
            return 7;
        } else if (sum > 25) {
            return 5;
        } else if (sum > 9) {
            return 3;
        } else {
            return 1;
        }
    }
}