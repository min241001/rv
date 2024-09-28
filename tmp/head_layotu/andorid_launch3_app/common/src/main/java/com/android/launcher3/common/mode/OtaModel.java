package com.android.launcher3.common.mode;

import static com.android.launcher3.common.constant.SettingsConstant.UPDATE_FLAG_DETAIL;
import static com.android.launcher3.common.constant.SettingsConstant.UPDATE_FLAG_KEY;
import static com.android.launcher3.common.constant.SettingsConstant.UPDATE_USER_KEY;
import static com.android.launcher3.common.network.api.Api.BASE_URL_CDN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.launcher3.common.BuildConfig;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.listener.DownloadListener;
import com.android.launcher3.common.network.resp.DataResp;
import com.android.launcher3.common.network.resp.OtaResp;
import com.android.launcher3.common.network.resp.UpgradeDetailResp;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.DateUtils;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.common.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class OtaModel {

    private static final String TAG = "OtaModel";

    /**
     * 下载状态标识
     */
    private boolean isDownload = false;

    /**
     * 增量下载状态标识
     */
    boolean downLoadFlag = false;

    /**
     * 增量安装状态标识
     */
    boolean installFlag = false;

    /**
     * 增量安装状态标识
     */
    private long delayTime;

    /**
     * com.android.launcher3最新版本查询
     */
    public void upgrade() {
        String watchId = AppLocalData.getInstance().getWatchId();
        if (StringUtils.isNotBlank(watchId)) {
            ApiHelper.upgrade(new BaseListener<OtaResp>() {

                @Override
                public void onError(int code, String msg) {
                    if (StringUtils.isBlank(msg)) {
                        LogUtil.d(TAG + "获取launcher3失败的请求失败，错误内容为空，code=" + code, LogUtil.TYPE_RELEASE);
                    }

                    LogUtil.d(TAG + "获取launcher3失败的请求失败，code=" + code + ", 错误信息为：" + msg, LogUtil.TYPE_RELEASE);
                }

                @Override
                public void onSuccess(OtaResp response) {
                    if (response == null) {
                        LogUtil.d(TAG + "获取launcher3失败失败，原因是：调用请求成功，但是返回的response为空", LogUtil.TYPE_RELEASE);
                    }

                    if (isVersionUpgrade(BuildConfig.VERSION, response.getOuterVersion())) {
                        String fileUrl = response.getFileUrl();
                        if (fileUrl != null && !fileUrl.equals("")) {
                            if (!isDownload) {
                                downLoadFile(String.format("%s%s", BASE_URL_CDN, fileUrl));
                                isDownload = true;
                            }
                        }
                    } else {
                        Log.e(TAG, "onSuccess: 不需要OTA更新");
                    }
                }
            }, watchId, BuildConfig.VERSION);
        }
    }

    private boolean isVersionUpgrade(String currentVersion, String version) {
        String[] currentVersionArray = currentVersion.split("\\.");
        String[] versionArray = version.split("\\.");

        int length = Math.max(currentVersionArray.length, versionArray.length);
        for (int i = 0; i < length; i++) {
            int current = i < currentVersionArray.length && !currentVersionArray[i].isEmpty() ? Integer.parseInt(currentVersionArray[i]) : 0;
            int saved = i < versionArray.length && !versionArray[i].isEmpty() ? Integer.parseInt(versionArray[i]) : 0;
            if (current < saved) {
                return true;
            } else if (current > saved) {
                return false;
            }
        }
        return false;
    }


    /**
     * 最新补丁详情
     */
    public void checkNewPatch(Context context, String localVersion, String patchSid) {
        String watchId = AppLocalData.getInstance().getWatchId();
        if (StringUtils.isNotBlank(watchId)) {
            ApiHelper.checkNewPatch(new BaseListener<UpgradeDetailResp>() {
                @Override
                public void onError(int code, String msg) {
                    if (StringUtils.isBlank(msg)) {
                        LogUtil.d(TAG + "获取最新补丁详情失败的请求失败，错误内容为空，code=" + code, LogUtil.TYPE_RELEASE);
                    }
                    LogUtil.d(TAG + "获取最新补丁详情失败的请求失败，code=" + code + ", 错误信息为：" + msg, LogUtil.TYPE_RELEASE);
                }

                @SuppressLint("MissingInflatedId")
                @Override
                public void onSuccess(UpgradeDetailResp response) {
                    if (response == null) {
                        LogUtil.d(TAG + "获取最新补丁详情失败，原因是：调用请求成功，但是返回的response为空", LogUtil.TYPE_RELEASE);
                    }
                    SharedPreferencesUtils.setParam(CommonApp.getInstance(), UPDATE_USER_KEY, true);
                    checkUpdate(context, localVersion, response);
                }
            }, patchSid, watchId);
        } else {
            LogUtil.d(TAG + "获取最新补丁失败，原因为帐号id为空", LogUtil.TYPE_RELEASE);
        }
    }

    //已下载文件的集合
    private List<File> localApkFiles = new ArrayList<>();
    //需要下载文件的集合
    private List<UpgradeDetailResp.WatchAppPatchVersionVOSBean> list = new ArrayList<>();

    public void checkUpdate(Context context, String localVersion, UpgradeDetailResp response) {
        //缓存本地更新策略
        SharedPreferencesUtils.setParam(CommonApp.getInstance(), UPDATE_FLAG_DETAIL, response);
        list.clear();
        list = needDownLoad(context, response);
        //先判断是否有需要更新的apk
        if (list != null && !list.isEmpty()) {
            //判断条件 - 1.版本判断
            if (isVersionUpgrade(localVersion, response.getOuterVersion(), response.getVersionUpgradeType()) || localVersion.equals(response.getOuterVersion())) {
                LogUtil.d("符合升级版本条件，进行升级", LogUtil.TYPE_RELEASE);
                //判断条件 - 2.升级时间判断
                if (response.getUpgradeTime() != null && !response.getUpgradeTime().isEmpty()) {
                    LogUtil.d("升级时间 === " + response.getUpgradeTime(), LogUtil.TYPE_RELEASE);
                    String upgradeTime = response.getUpgradeTime();
                    String[] time = upgradeTime.split("-"); //分割升级时间字符串 示例: 23:00-24:00
                    Date currentDate = DateUtils.getDate(DateUtils.getCurrentTime()); //当前时间
                    Date startDate = DateUtils.getDate(time[0]); //升级开始时间
                    Date endDate = DateUtils.getDate(time[1]);   //升级结束时间
                    if (currentDate.getTime() >= startDate.getTime() && currentDate.getTime() <= endDate.getTime()) {
                        LogUtil.d("符合升级版本条件，进行升级", LogUtil.TYPE_RELEASE);
                        uploadNext(response, context);
                    }
                } else {
                    LogUtil.d("升级时间为空，直接进行升级", LogUtil.TYPE_RELEASE);
                    uploadNext(response, context);
                }
            }
        } else {
            LogUtil.d("没有需要升级的应用，结束end", LogUtil.TYPE_RELEASE);
        }
    }

    private void uploadNext(UpgradeDetailResp response, Context context) {
        ////判断条件 - 3.升级方式，1 弹框 2 静默
        if (response.getUpgradeType() == 1) {
            //是否正在下载
            if (!downLoadFlag) {
                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = (boolean) SharedPreferencesUtils.getParam(context, UPDATE_USER_KEY, false);
                        //查询用户是否进行本次补丁升级的拒绝
                        if (flag) {
                            doNext(context, response);
                        } else {
                            LogUtil.d("用户已拒绝本次补丁升级", LogUtil.TYPE_RELEASE);
                        }
                    }
                });
            }
        } else {
            //静默升级方式 - 下载更新apk
            //是否正在下载
            if (!downLoadFlag) {
                if (AppUtils.isNetworkAvailable(context) || AppUtils.isWifiConnected(context)) {
                    successNum = 0;
                    failNum = 0;
                    for (UpgradeDetailResp.WatchAppPatchVersionVOSBean bean : list) {
                        startOTADownload(context, String.format("%s%s", BASE_URL_CDN, bean.getFileUrl()), bean.getApplicationId(), null, response.getWatchAppPatchVersionVOS().size(), null);
                    }
                    downLoadFlag = true;
                } else {
                    LogUtil.d("无网无WIFI", LogUtil.TYPE_RELEASE);
                }
            }
        }
    }

    //升级弹窗
    private AlertDialog.Builder builder = null;
    private AlertDialog dialog = null;
    private ImageView cancelBtn;
    private View customView;
    private ProgressBar progressBar;
    private TextView progressBarTitleTxt;
    private TextView updateVersion;
    private TextView progressBarTxt;

    private void doNext(Context context, UpgradeDetailResp response) {
        try {
            if (builder == null) {
                builder = new AlertDialog.Builder(context, R.style.TransparentDialogStyle);
                LayoutInflater inflater = LayoutInflater.from(context);
                customView = inflater.inflate(R.layout.custom_upgrade_dialog_layout, null);
            }
            progressBar = customView.findViewById(R.id.progressBar);
            progressBarTitleTxt = customView.findViewById(R.id.update_title);
            cancelBtn = customView.findViewById(R.id.cancel_update_btn);
            updateVersion = customView.findViewById(R.id.update_version);
            progressBarTxt = customView.findViewById(R.id.progress_txt);
            cancelBtn.setVisibility(View.VISIBLE);
            //是否强制升级，1 是 2 否
            if (response.getIfCompelUpgrade() == 1) {
                cancelBtn.setVisibility(View.GONE);
            }
            progressBarTitleTxt.setText(response.getUpgradeTitle());
            updateVersion.setText(response.getUpgradeInfo());
            progressBar.setProgress(100);
            progressBarTxt.setText("立即升级");

            builder.setCancelable(false);
            builder.setView(customView);
            if (dialog == null) {
                dialog = builder.create();
            }
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("升级成功".equals(progressBarTxt.getText().toString()) || !downLoadFlag) {
                        SharedPreferencesUtils.setParam(context, UPDATE_USER_KEY, false);
                        dialog.dismiss();
                    } else if (downLoadFlag) {
                        ToastUtils.show("下载中不可操作");
                    } else if ("安装中...".equals(progressBarTxt.getText().toString())) {
                        ToastUtils.show("安装中不可操作");
                    }
                }
            });
            progressBarTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (progressBarTxt.getText().toString()) {
                        case "立即升级":
                            if (AppUtils.isNetworkAvailable(context) || AppUtils.isWifiConnected(context)) {
                                progressBarTxt.setText("加载中...");
                                successNum = 0;
                                failNum = 0;
                                for (UpgradeDetailResp.WatchAppPatchVersionVOSBean bean : list) {
                                    //是否正在下载
                                    if (!downLoadFlag) {
                                        startOTADownload(context, String.format("%s%s", BASE_URL_CDN, bean.getFileUrl()), bean.getApplicationId(), progressBarTxt, list.size(), progressBar);
                                    }
                                }
                                downLoadFlag = true;
                            } else {
                                ToastUtils.show("网络错误,请检查！");
                                dialog.dismiss();
                            }
                            break;
                        case "升级成功":
                        case "安装失败":
                            dialog.dismiss();
                            break;
                        case "安装中...":
                            ToastUtils.show("安装中不可操作");
                            break;
                    }
                }
            });
            progressBarTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().equals("安装中...")) {
                        if (list != null && !list.isEmpty()) {
                            if (list.size() > 4) {
                                delayTime = 1000 * 90;
                            } else if (list.size() >= 1) {
                                delayTime = 1000 * 15 * list.size();
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (UpgradeDetailResp.WatchAppPatchVersionVOSBean bean : list) {
                                    String versionCode = getVersionCode(bean.getApplicationId(), context);
                                    if (versionCode.equals(bean.getAppVersion())) {
                                        installFlag = true;
                                    } else {
                                        installFlag = false;
                                    }
                                }
                                if (installFlag) {
                                    progressBarTxt.setText("升级成功");
                                } else {
                                    progressBarTxt.setText("安装失败");
                                }
                                downLoadFlag = false;
                                successNum = 0;
                                failNum = 0;
                            }
                        }, delayTime); // 3 seconds delay
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            Activity activity = (Activity) context;
            if (!dialog.isShowing() && !activity.isFinishing()) {
                dialog.show();
            }
        } catch (Exception e) {
            LogUtil.d(e.getMessage(), LogUtil.TYPE_RELEASE);
        }
    }

    private List<UpgradeDetailResp.WatchAppPatchVersionVOSBean> needDownLoad(Context context, UpgradeDetailResp response) {
        AppMode appMode = new AppMode(CommonApp.getInstance());
        List<AppBean> appBeans = appMode.loadDefaultData();
        for (int i = 0; i < appBeans.size(); i++) {
            for (int j = 0; j < response.getWatchAppPatchVersionVOS().size(); j++) {
                if (appBeans.get(i).getPackageName().equals(response.getWatchAppPatchVersionVOS().get(j).getApplicationId())) {
                    if (isVersionUpgrade(getVersionCode(appBeans.get(i).getPackageName(), context), response.getWatchAppPatchVersionVOS().get(j).getAppVersion(), 3)) {
                        //需要更新的APK
                        list.add(response.getWatchAppPatchVersionVOS().get(j));
                    }
                }
            }
        }
        if (list != null && !list.isEmpty()) {
            localApkFiles.clear();
            //判断文件是否全部下载完毕
            File folder = new File("/sdcard/OTAApks");
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (file.getName().toLowerCase().endsWith(".apk")) {
                            localApkFiles.add(file);
                        }
                    }
                }
                if (localApkFiles != null && !localApkFiles.isEmpty()) {
                    for (File file : localApkFiles) {
                        if (file.getName().toLowerCase().endsWith(".apk")) {
                            String packageName = file.getName().substring(0, file.getName().length() - 4);
                            for (int i = 0; i < list.size(); i++) {
                                if (packageName.equals(list.get(i).getApplicationId())) {
                                    if (file.length() == list.get(i).getFileSize()) {
                                        list.remove(i);
                                        installApkSilently(context, String.format("%s/%s", "OTAApks", file.getName()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    //包名获取版本号
    private String getVersionCode(String packageName, Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private int pro = 0;
    private int successNum = 0;
    private int failNum = 0;

    private void startOTADownload(Context context, String url, String fileName, TextView progressBarTxt, int size, ProgressBar progressBar) {
        ApiHelper.downloadOTAFile(new DownloadListener() {
            @Override
            public void onProgress(int progress) {
                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressBarTxt != null && progressBar != null) {
                            if (size == 1) {
                                progressBar.setProgress(progress);
                                progressBarTxt.setText(progress + "%");
                                if (progress == 100) {
                                    progressBarTxt.setText("安装中...");
                                }
                            } else {
                                if (progress == 100) {
                                    pro += 100 / size;
                                    progressBar.setProgress(pro);
                                    progressBarTxt.setText(pro + "%");
                                    if (pro == 100) {
                                        progressBarTxt.setText("安装中...");
                                    }
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(int code, String msg) {
                LogUtil.e(TAG + "文件写入失败 code = " + code + " , msg = " + msg);
                failNum += 1;
                if (failNum + successNum == size) {
                    downLoadFlag = false;
                }
                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                            ToastUtils.show("网络不给力，升级失败");
                        }
                    }
                });
            }

            @Override
            public void onSuccess(String apkFilePath) {
                installApkSilently(context, apkFilePath);
                successNum += 1;
                if (successNum == size) {
                    downLoadFlag = false;
                    // 创建一个 Random 对象
                    Random random = new Random();
                    // 生成范围在 1 到 1200 的随机整数
                    int randomNumber = random.nextInt(1200) + 1;
                    try {
                        Thread.sleep(randomNumber * 1000);
                        downloadNumber();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, url, fileName);
    }

    //补丁升级下载数量+1
    private void downloadNumber() {
        String watchId = AppLocalData.getInstance().getWatchId();
        Long param = (Long) SharedPreferencesUtils.getParam(CommonApp.getInstance(), UPDATE_FLAG_KEY, Long.parseLong("0"));
        if (StringUtils.isNotBlank(watchId) && param != null) {
            ApiHelper.downloadNumber(new BaseListener<DataResp>() {
                @Override
                public void onError(int code, String msg) {
                    if (StringUtils.isBlank(msg)) {
                        LogUtil.d(TAG + "下载数量+1请求失败，错误内容为空，code=" + code, LogUtil.TYPE_RELEASE);
                    } else if (msg instanceof String) {
                        LogUtil.d(TAG + "下载数量+1请求失败，code=" + code + ", 错误信息为：" + msg, LogUtil.TYPE_RELEASE);
                    } else {
                        LogUtil.d(TAG + "下载数量+1请求失败，code=" + code + ", 错误信息为：" + msg.toString(), LogUtil.TYPE_RELEASE);
                    }
                }

                @Override
                public void onSuccess(DataResp data) {
                    if (data == null) {
                        LogUtil.d(TAG + "下载数量+1失败，原因是：调用请求成功，但是返回的response为空", LogUtil.TYPE_RELEASE);
                        LogUtil.d(TAG + "下载数量+1 success", LogUtil.TYPE_RELEASE);
                    }
                }
            }, String.valueOf(param), Long.valueOf(watchId));
        } else {
            LogUtil.d(TAG + "获取最新补丁失败，原因为帐号id为空", LogUtil.TYPE_RELEASE);
        }
    }

    //OTA升级下载数量+ 1
    private void otaDownloadNumberIncrease() {
        String watchId = AppLocalData.getInstance().getWatchId();
        String version = BuildConfig.VERSION;
        String applicationId = CommonApp.getInstance().getPackageName();
        if (StringUtils.isNotBlank(watchId)) {
            ApiHelper.otaDownloadNumberIncrease(new BaseListener<DataResp>() {
                @Override
                public void onError(int code, String msg) {
                    if (StringUtils.isBlank(msg)) {
                        LogUtil.d(TAG + "下载数量+1请求失败，错误内容为空，code=" + code, LogUtil.TYPE_RELEASE);
                    } else if (msg instanceof String) {
                        LogUtil.d(TAG + "下载数量+1请求失败，code=" + code + ", 错误信息为：" + msg, LogUtil.TYPE_RELEASE);
                    } else {
                        LogUtil.d(TAG + "下载数量+1请求失败，code=" + code + ", 错误信息为：" + msg.toString(), LogUtil.TYPE_RELEASE);
                    }
                }

                @Override
                public void onSuccess(DataResp data) {
                    if (data == null) {
                        LogUtil.d(TAG + "下载数量+1 success", LogUtil.TYPE_RELEASE);
                    }
                }
            }, applicationId, version, Long.valueOf(watchId));
        }

    }

    //升级版本条件判断
    private boolean isVersionUpgrade(String currentVersion, String version, int type) {
        //1 大于等于 2 指定版本
        String[] currentVersionArray = currentVersion.split("\\.");
        String[] versionArray = version.split("\\.");
        int length = Math.max(currentVersionArray.length, versionArray.length);
        for (int i = 0; i < length; i++) {
            int current = i < currentVersionArray.length && !currentVersionArray[i].isEmpty() ? Integer.parseInt(currentVersionArray[i]) : 0;
            int saved = i < versionArray.length && !versionArray[i].isEmpty() ? Integer.parseInt(versionArray[i]) : 0;
            if (current < saved) {
                if (type == 3) {
                    return true;
                }
                return false;
            } else if (current > saved) {
                if (type == 1) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void downLoadFile(String url) {
        ApiHelper.downloadFile(new DownloadListener() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onError(int code, String msg) {
                LogUtil.e(TAG + "文件写入失败 code = " + code + " , msg = " + msg);
                isDownload = false;
            }

            @Override
            public void onSuccess(String apkFilePath) {
                isDownload = false;
                installApkSilently(CommonApp.getInstance(), apkFilePath);
                // 创建一个 Random 对象
                Random random = new Random();
                // 生成范围在 1 到 1200 的随机整数
                int randomNumber = random.nextInt(1200) + 1;
                try {
                    Thread.sleep(randomNumber * 1000);
                    otaDownloadNumberIncrease();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, url);
    }


    public void installApkSilently(Context context, String apkFilePath) {
        try {
            PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
            int sessionId = packageInstaller.createSession(new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL));
            PackageInstaller.Session session = packageInstaller.openSession(sessionId);
            OutputStream out = session.openWrite("package", 0, -1);
            File externalStorageDir = Environment.getExternalStorageDirectory();
            File apkFile = new File(externalStorageDir + File.separator + apkFilePath);
            FileInputStream in = new FileInputStream(apkFile);
            byte[] buffer = new byte[65536];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            session.fsync(out);
            in.close();
            out.close();
            session.commit(createInstallIntentSender(context, sessionId));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (apkFilePath.equals("/sdcard/.launcher/.launcher3.apk")){
//
//        }
//        //判断文件大小是否相同，再进行安装
//        File file = new File(apkFilePath);
//        if (list != null && !list.isEmpty()){
//            for (int i = 0; i < list.size(); i++) {
//                if (file.getName().toLowerCase().endsWith(".apk")) {
//                    String packageName = file.getName().substring(0, file.getName().length() - 4);
//                    if (packageName.equals(list.get(i).getApplicationId()) && file.length() == list.get(i).getFileSize()){
//                        try {
//                            PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
//                            int sessionId = packageInstaller.createSession(new PackageInstaller.SessionParams(
//                                    PackageInstaller.SessionParams.MODE_FULL_INSTALL));
//                            PackageInstaller.Session session = packageInstaller.openSession(sessionId);
//                            OutputStream out = session.openWrite("package", 0, -1);
//                            File externalStorageDir = Environment.getExternalStorageDirectory();
//                            File apkFile = new File(externalStorageDir + File.separator + apkFilePath);
//                            FileInputStream in = new FileInputStream(apkFile);
//                            byte[] buffer = new byte[65536];
//                            int len;
//                            while ((len = in.read(buffer)) != -1) {
//                                out.write(buffer, 0, len);
//                            }
//                            session.fsync(out);
//                            in.close();
//                            out.close();
//                            session.commit(createInstallIntentSender(context, sessionId));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
    }

    private IntentSender createInstallIntentSender(Context context, int sessionId) {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.putExtra(PackageInstaller.EXTRA_SESSION_ID, sessionId);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return PendingIntent.getBroadcast(context, sessionId, intent, PendingIntent.FLAG_UPDATE_CURRENT).getIntentSender();
    }
}
