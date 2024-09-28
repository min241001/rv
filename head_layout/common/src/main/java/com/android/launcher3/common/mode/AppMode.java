package com.android.launcher3.common.mode;

import static com.android.launcher3.common.CompileConfig.FACE_UNLOCK;
import static com.android.launcher3.common.CompileConfig.SYSTEM_DEFAULT_APP_JSON;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.utils.ImageUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppMode extends BaseMode<AppBean> implements IAppMode<AppBean> {

    public static final String SOUGOU = "com.sogou.ime.wear";

    public static final String CALENDAR = "com.android.calendar";

    public static final String CALCULATOR = "com.android.calculator2";

    public AppMode(Context context) {
        super(context);
    }

    @Override
    public List<AppBean> loadDefaultData() {
        //ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
        ArrayList<AppBean> apps = new ArrayList<>();
        HashMap<String, AppBean> hashMap = new HashMap<>();

        //预设的全部装载
        ArrayList<AppBean> appDefault = (ArrayList<AppBean>) loadDefaultConfig();
        if (appDefault != null && appDefault.size() > 0) {
            for (AppBean app : appDefault) {
                if (app.getPreinstall() == 1) {
                    apps.add(app);
                    hashMap.put(app.getPackageName(), app);
                }
            }
        }

        //得到PackageManager对象
        PackageManager pm = context.getPackageManager();
        //得到系统安装的所有程序包的PackageInfo对象
        List<PackageInfo> packs = pm.getInstalledPackages(0);

        for (PackageInfo pi : packs) {
            // 应用包名
            String packageName = pi.packageName;

            // 判断是否是launcher应用
            if (packageName.equals(context.getPackageName())) {
                LogUtil.d("过滤launcher包", LogUtil.TYPE_RELEASE);
                continue;
            }

            if (SOUGOU.equals(packageName)) {
                continue;
            }

            if (CALENDAR.equals(packageName)) {
                continue;
            }

            if (CALCULATOR.equals(packageName)) {
                continue;
            }

            // 判断是系统应用还是第三方应用
            if ((pi.applicationInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) { // 系统应用
                AppBean appBean = hashMap.get(packageName);
                if (appBean != null) {
                    // 遍历获取是哪一个
                    for (int i = 0; i < apps.size(); i++) {
                        AppBean app = apps.get(i);
                        if (app.getPackageName().equals(packageName)) {
                            // 应用图标
                            Drawable icon;
                            if (app.getIconFlag() == -1) {
                                icon = pi.applicationInfo.loadIcon(pm);
                            } else {
                                icon = app.getIcon();
                            }
                            app.setIcon(icon);
                            // 应用安装包的路径
                            String sourceDir = pi.applicationInfo.sourceDir;
                            app.setSourceDir(sourceDir);
                            // 应用安装apk的大小
                            long apkSize;
                            try {
                                File file = new File(sourceDir);
                                if (file != null && file.isFile()) {
                                    apkSize = file.length();
                                    app.setApkSize(apkSize);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // 直接替换
                            apps.set(i, app);
                            break;
                        }
                    }
                }
            } else if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) { // 安装的应用程序
                AppBean appBean = hashMap.get(packageName);
                if (appBean != null) {
                    // 遍历获取是哪一个
                    for (int i = 0; i < apps.size(); i++) {
                        AppBean app = apps.get(i);
                        if (app.getPackageName().equals(packageName)) {
                            // 应用图标
                            Drawable icon;
                            if (app.getIconFlag() == -1) {
                                icon = pi.applicationInfo.loadIcon(pm);
                            } else {
                                icon = app.getIcon();
                            }
                            app.setIcon(icon);
                            // 应用安装包的路径
                            String sourceDir = pi.applicationInfo.sourceDir;
                            app.setSourceDir(sourceDir);
                            // 应用安装apk的大小
                            long apkSize;
                            try {
                                File file = new File(sourceDir);
                                if (file != null && file.isFile()) {
                                    apkSize = file.length();
                                    app.setApkSize(apkSize);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // 直接替换
                            apps.set(i, app);
                            break;
                        }
                    }
                } else {
                    // 应用名称
                    String appName = (String) pi.applicationInfo.loadLabel(pm);
                    // 应用图标
                    Drawable icon = pi.applicationInfo.loadIcon(pm);
                    // 应用安装包的路径
                    String sourceDir = pi.applicationInfo.sourceDir;
                    // 应用安装apk的大小
                    long apkSize = 0L;
                    try {
                        File file = new File(sourceDir);
                        if (file != null && file.isFile()) {
                            apkSize = file.length();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (packageName.equals("com.tencent.wechatkids")){
                        icon = ContextCompat.getDrawable(context, R.drawable.icon_chat);
                    }

                    if (packageName.equals("com.eg.android.AlipayGphone")){
                        icon = ContextCompat.getDrawable(context, R.drawable.icon_alipay);
                    }

                    if (packageName.equals("org.chromium.webview_shell")){
                        icon = ContextCompat.getDrawable(context, R.drawable.icon_browser);
                    }

                    // 构造数据
                    AppBean bean = new AppBean(appName, icon, packageName, sourceDir, apkSize,"1");
                    // 添加到集合里面
                    apps.add(bean);
                    hashMap.put(packageName,bean);
                }
            }
        }
//        SharedPreferencesUtils.setParam(CommonApp.getInstance(),"apps_size",apps.size());
        return apps;
    }

    @SuppressLint("ResourceType")
    @Override
    public List<AppBean> loadDefaultConfig() {
        ArrayList<AppBean> data = new ArrayList<>();

        LogUtil.i(TAG, "loadDefaultConfig: " + SYSTEM_DEFAULT_APP_JSON, LogUtil.TYPE_RELEASE);
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<AppBean>>(){}.getType();
            data = gson.fromJson(SYSTEM_DEFAULT_APP_JSON, type);
            for (int i = 0; i < data.size(); i++) {
                AppBean bean = data.get(i);
                bean.setId(i);
                bean.setIcon(ImageUtil.getDrawable(bean.getIconId()));
                bean.setName(StringUtils.getStr(bean.getNameId()));
                if (bean.getPackageName().equals("com.baehug.facerecognition") && FACE_UNLOCK) {
                    bean.setIcon(ImageUtil.getDrawable(R.drawable.ic_facerecognition));
                    bean.setName(CommonApp.getInstance().getString(R.string.app_name_face));
                }
            }
        } catch (JsonSyntaxException e) {
            LogUtil.e(TAG, "loadDefaultConfig: ", e, LogUtil.TYPE_RELEASE);
        }

        return data;
    }

    @Override
    public AppBean getAppByPackageName(String packageName) {
        List<AppBean> appBeans = loadDefaultData();
        if (appBeans != null && appBeans.size() > 0) {
            for (AppBean appBean : appBeans) {
                if (appBean.getPackageName().equals(packageName)) {
                    return appBean;
                }
            }
        }
        return null;
    }

}