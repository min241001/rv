package com.android.launcher3.moudle.launcher.view;

import static com.android.launcher3.common.constant.AppPackageConstant.VIEW_ACTION;
import static com.android.launcher3.common.constant.SettingsConstant.CHARGE_ACTION;
import static com.android.launcher3.common.constant.SettingsConstant.LS_FLOAT_MENU_ACTION;
import static com.android.launcher3.common.constant.SettingsConstant.LS_LAND_MODE_ACTION;
import static com.android.launcher3.common.constant.SettingsConstant.WALLPAPER_CHANGES_RECEIVER;
import static com.android.launcher3.moudle.island.IslandFloat.APP_ICON_FLOAT;
import static com.android.launcher3.moudle.island.IslandFloat.AUDIO_FLOAT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.launcher3.App;
import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpActivity;
import com.android.launcher3.common.base.PageDesktopFragment;
import com.android.launcher3.common.base.WallpaperUtil;
import com.android.launcher3.common.constant.SettingsConstant;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.dialog.Loading;
import com.android.launcher3.common.mode.SimModel;
import com.android.launcher3.common.utils.AliPayUtils;
import com.android.launcher3.common.utils.AppLauncherUtils;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.BaseActivityManager;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.PhoneSIMCardUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.common.utils.ShutdownUtils;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.desktop2.Desktop2Fragment;
import com.android.launcher3.desktop4.Desktop4Fragment;
import com.android.launcher3.moudle.island.IslandDiffNotification;
import com.android.launcher3.moudle.island.IslandFloat;
import com.android.launcher3.moudle.island.IslandMsgBean;
import com.android.launcher3.moudle.island.IslandMusicManager;
import com.android.launcher3.moudle.launcher.adapter.LauncherPageAdapter;
import com.android.launcher3.moudle.launcher.presenter.LauncherPresenter;
import com.android.launcher3.moudle.menufloat.MenuFloat;
import com.android.launcher3.moudle.notification.bean.NotificationBean;
import com.android.launcher3.moudle.notification.notity.IslandNotifyHelper;
import com.android.launcher3.moudle.notification.notity.NotificationListener;
import com.android.launcher3.moudle.notification.notity.NotifyListener;
import com.android.launcher3.moudle.notification.queue.IslandNotificationLinkedQueue;
import com.android.launcher3.moudle.selector.view.DialSelectorActivity;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.moudle.touchup.utils.AppUtil;
import com.android.launcher3.moudle.touchup.utils.CommonUtil;
import com.android.launcher3.receiver.AppStateBroadcastReceiver;
import com.android.launcher3.receiver.FloatReceiver;
import com.android.launcher3.receiver.LsLangReceiver;
import com.android.launcher3.utils.AppSuperviseManager;
import com.android.launcher3.common.utils.AudioFocusManager;
import com.android.launcher3.utils.ClassNameUtils;
import com.android.launcher3.utils.PluginEnv;
import com.android.launcher3.utils.PluginInstallUtils;
import com.android.launcher3.widget.CustomGesture;
import com.android.launcher3.widget.SlidingDrawerLayout;
import com.android.launcher3.widget.ViewPagerExt;
import com.google.gson.Gson;
import com.lzf.easyfloat.EasyFloat;
import com.taobao.sophix.SophixManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LauncherActivity extends BaseMvpActivity<LauncherView, LauncherPresenter> implements LauncherView {

    public static Context context;

    private boolean isInstall;
    private ViewPagerExt viewPagerExt;
    private SlidingDrawerLayout qspanelContainer;
    private SlidingDrawerLayout notificationContainer;
    private SlidingDrawerLayout touchUpEventContainer;
    private TextView textView;
    private LinearLayout splash;
    private Handler handler = new Handler(Looper.getMainLooper());
    private AppStateBroadcastReceiver receiver;
    private FontBroadcastReceiver fontReceiver;
    private FloatReceiver floatReceiver;
    private WallpaperBroadcastReceiver wallpaperBroadcastReceiver;
    private SetViewPagerBroadcastReceiver setViewPagerBroadcastReceiver;
    private SetWorkspaceBroadcastReceiver setWorkspaceBroadcastReceiver;
    private LsLangReceiver lsLangReceiver;
    private SimModel simModel;
    private ImageView wallpaperImage;
    private static Thread thread = null;
    private Gson gson;

    private IslandDiffNotification islandDiffNotification;
    final CustomGesture.GestureListener mGestureListener = new CustomGesture.GestureListener() {
        @Override
        public void onLeftToRight() {

        }

        @Override
        public void onRightToLeft() {

        }

        @Override
        public void onTopToBottom(boolean left) {
            if (AppLocalData.getInstance().getPower()) {
                LogUtil.i("onTopToBottom: no power", LogUtil.TYPE_RELEASE);
                return;
            }
            LogUtil.i("onTopToBottom:123123 left " + left, LogUtil.TYPE_RELEASE);
            if (left) {
                mPresenter.showNotificationFragment(getSupportFragmentManager());
                notificationContainer.show();
            } else {
                mPresenter.showShortcutMenuFragment(getSupportFragmentManager());
                qspanelContainer.show();
            }
        }

        @Override
        public void onBottomToTop(boolean left) {
            if (AppLocalData.getInstance().getPower()) {
                LogUtil.i("onBottomToTop: no power", LogUtil.TYPE_RELEASE);
                return;
            }
            if (left) {
                mPresenter.showNotificationFragment(getSupportFragmentManager());
                notificationContainer.show();
            } else {
                mPresenter.showTouchUpEventFragment(getSupportFragmentManager());
                touchUpEventContainer.show();
            }

        }

        @Override
        public void onLongPress() {
            if (AppLocalData.getInstance().getPower()) {
                LogUtil.i("onLongPress: no power", LogUtil.TYPE_RELEASE);
                return;
            }

            if (splash.isShown()) {
                return;
            }
            jumpToTargetActivity(DialSelectorActivity.class);
        }
    };

    private LauncherPageAdapter launcherPageAdapter;
    private int position = 0;

    private long mLastAddTaskTime;

    // 滑动事件
    private SlidingDrawerLayout.SlideTouchListener listener = new SlidingDrawerLayout.SlideTouchListener() {
        @Override
        public void onDrawerOpenedOrClosed(boolean isShow) {

            if (isShow) {
                viewPagerExt.setPosition(1);
                if (!splash.isShown()) {
                    EasyFloat.dismiss(AUDIO_FLOAT);
                    EasyFloat.dismiss(APP_ICON_FLOAT);
                }
            } else {
                viewPagerExt.setPosition(0);
                if (!splash.isShown()) {
                    if (AppLocalData.getInstance().getIslandOpenState() == 1) {
                        if (islandDiffNotification.hasAudio() && AppLocalData.getInstance().getIslandOpenState() == 1) {
                            islandFloat.showAudioFloat();
                        } else {
                            if (islandDiffNotification.hasTelMsg() || islandDiffNotification.hasSmsMsg() || islandDiffNotification.hasWechatMsg()) {
                                islandFloat.showAppIconFloat();
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d("keyCode === " + keyCode, LogUtil.TYPE_DEBUG);
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            List<Fragment> fragmentList = mPresenter.getFragmentList();
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof Desktop2Fragment) {
                    ((Desktop2Fragment) fragment).onKeyDown(keyCode);
                    return true;
                }
                if (fragment instanceof Desktop4Fragment) {
                    ((Desktop4Fragment) fragment).onKeyDown(keyCode);
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected LauncherPresenter createPresenter() {
        return new LauncherPresenter(this);
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void initView() {
        super.initView();
        context = this;
        gson = new Gson();
        viewPagerExt = findViewById(R.id.container);
        qspanelContainer = findViewById(R.id.container_qspanel);
        notificationContainer = findViewById(R.id.container_notification);
        touchUpEventContainer = findViewById(R.id.container_pull_down);
        textView = findViewById(R.id.textview);
        splash = findViewById(R.id.splash);
        wallpaperImage = findViewById(R.id.wallpaper_image);
        simModel = new SimModel(this);

        //viewpage 滑动监听
        viewPagerExt.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //滑动切换 悬浮窗隐藏显示
                switch (position) {
                    case 0:
                        //表盘页面
                        if (AppLocalData.getInstance().getIslandOpenState() == 1) {
                            if (islandDiffNotification.hasAudio()) {
                                islandFloat.showAudioFloat();
                            } else {
                                if (islandDiffNotification.hasTelMsg() || islandDiffNotification.hasSmsMsg() || islandDiffNotification.hasWechatMsg()) {
                                    islandFloat.showAppIconFloat();
                                }
                            }
                        }
                        break;
                    case 1:
                        //应用风格
                        EasyFloat.dismiss(AUDIO_FLOAT);
                        EasyFloat.dismiss(APP_ICON_FLOAT);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(SettingsConstant.ANIM_MODE),
                true,
                animContentObserver
        );
    }


    @Override
    protected void initData() {
        super.initData();
        LogUtil.d(TAG + "initData", LogUtil.TYPE_RELEASE);
        boolean shutdown = AppLocalData.getInstance().getShutdown();
        if (shutdown) {
            splash.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            }, SPLASH);
        } else {
            init();
        }
    }


    private void init() {

        Intent intent = new Intent(this, NotificationListener.class);
        startService(intent);
        islandMusicManager = new IslandMusicManager(context);
        islandDiffNotification = IslandDiffNotification.getInstance();
        islandFloat = new IslandFloat(context);
        IslandNotifyHelper.getInstance().setNotifyListener(islandNotifyListener);

        MenuFloat.getInstance().setContext(this);
        MenuFloat.getInstance().updateMenuFloat(MenuFloat.getInstance().initData());

        BaseActivityManager.getInstance().setCurrentActivity(this);
        // 显示页面
        position = getIntent().getIntExtra("NAV", 0);
        LogUtil.d(TAG + "position = " + position, LogUtil.TYPE_RELEASE);
        if (position == 0) {
            AudioFocusManager.getInstance().getFlag();
            AudioFocusManager.getInstance().requestAudioFocus();
        }
        if (position == 0 && AppUtils.isThemeDial() && !splash.isShown()) {
            textView.setVisibility(View.VISIBLE);
        }
        new installTask().execute();
        //支付宝激活判断
        AliPayUtils.aliPayCheck(this, PhoneSIMCardUtil.getInstance().getImei(), true);
        initSetViewPagerBroadcast();
        initSetWorkspaceBroadcast();
        if (AppLocalData.getInstance().getMenuFloatOpenState() == 1) {
            MenuFloat.getInstance().showMenuFloat(this);
        }

        initApps();
    }

   private void  initApps(){
       CommonUtil.InitAppsData(this,gson);
        if(AppUtil.defaultApp.size()==0|| AppUtil.apps.size()==0) {
            if (thread == null) {
                thread = new Thread(new Runnable() {
                    public void run() {
                        AppUtil.GetAppsA(LauncherActivity.this, gson);
                    }
                });
                thread.start();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        sendBroadcast(new Intent(WALLPAPER_CHANGES_RECEIVER));
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 设置手势监听
        viewPagerExt.setGestureListener(mGestureListener);

        // 滑动事件
        qspanelContainer.setSlideTouchListener(listener);
        notificationContainer.setSlideTouchListener(listener);
        touchUpEventContainer.setSlideTouchListener(listener);
    }

    @Override
    protected boolean getSwipeToExitEnabled() {
        return false;// 设置启动页不支持侧滑退出
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: " + viewPagerExt.getCurrentItem());
        if (viewPagerExt.getCurrentItem() == 0) {
            qspanelContainer.close();
            notificationContainer.close();
            touchUpEventContainer.close();
        } else {
            viewPagerExt.setCurrentItem(0, true);
            List<Fragment> fragmentList = mPresenter.getFragmentList();
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof PageDesktopFragment) {
                    ((PageDesktopFragment) fragment).setPageSize(viewPagerExt.getCurrentItem());
                }
            }
        }
    }

    @Override
    public void showLoadFaceError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show("加载表盘失败");
            }
        });
    }

    @Override
    public void showLoadWorkspaceError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show("加载桌面风格失败");
            }
        });
    }

    @Override
    public void setCurrentItem(int position) {
        LogUtil.d(TAG + "当前位置：" + position, LogUtil.TYPE_RELEASE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPagerExt.setCurrentItem(position, true);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }

            if (fontReceiver != null) {
                unregisterReceiver(fontReceiver);
            }
        }
        simModel.unRegister();
        getContentResolver().unregisterContentObserver(animContentObserver);
        if (wallpaperBroadcastReceiver != null) {
            unregisterReceiver(wallpaperBroadcastReceiver);
        }

        if (lsLangReceiver != null) {
            unregisterReceiver(lsLangReceiver);
        }

        if (floatReceiver != null) {
            unregisterReceiver(floatReceiver);
        }

        if (setViewPagerBroadcastReceiver != null) {
            unregisterReceiver(setViewPagerBroadcastReceiver);
        }
        if(setWorkspaceBroadcastReceiver!=null){
            unregisterReceiver(setWorkspaceBroadcastReceiver);
        }
        if (thread != null) {
            thread = null;
        }


    }

    @Override
    public AssetManager getAssets() {
        PluginEnv env = getPluginRunEnv();
        if (isInstall && env != null && !ClassNameUtils.isHostFragmentResourceRequest()) {
            return env.pluginAsset;
        } else {
            return super.getAssets();
        }
    }

    @Override
    public Resources getResources() {
        PluginEnv env = getPluginRunEnv();
        if (isInstall && env != null && !ClassNameUtils.isHostFragmentResourceRequest()) {
            return env.pluginRes;
        } else {
            return super.getResources();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        PluginEnv env = getPluginRunEnv();
        if (isInstall && env != null && !ClassNameUtils.isHostFragmentResourceRequest()) {
            return env.pluginClassLoader;
        } else {
            return super.getClassLoader();
        }
    }

    @Override
    public Resources.Theme getTheme() {
        PluginEnv env = getPluginRunEnv();
        if (isInstall && env != null && !ClassNameUtils.isHostFragmentResourceRequest()) {
            return env.pluginTheme;
        } else {
            return super.getTheme();
        }
    }

    public PluginEnv getPluginRunEnv() {
        if (AppUtils.isThemeDial()) {
            String path = AppLocalData.getInstance().getFaceFilePath();
            return PluginInstallUtils.mPackagesHolder.get(path);
        }
        return null;
    }

    private boolean install() {
        if (AppUtils.isThemeDial()) {
            String path = AppLocalData.getInstance().getFaceFilePath();
            PluginInstallUtils.getInstance(this).installRunEnv(path);
        }
        return true;
    }

    class installTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            isInstall = install();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            long startTime = System.currentTimeMillis();
            splash.setVisibility(View.GONE);
            AppLocalData.getInstance().setShutdown(false);
            // 初始化数据
            mPresenter.initData();
            textView.setVisibility(View.GONE);
            // 设置适配器
            launcherPageAdapter = new LauncherPageAdapter(getSupportFragmentManager(), mPresenter.getFragmentList());
            viewPagerExt.setAdapter(launcherPageAdapter);
            try {
                // 加载页面
                if (!getSupportFragmentManager().isDestroyed()) {
                    mPresenter.loadFragments(getSupportFragmentManager());
                    viewPagerExt.setCurrentItem(position, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mPresenter.setPageTransformer(viewPagerExt);
            }
            simModel.register();
            mPresenter.clearTask(LauncherActivity.this);
            if (ShutdownUtils.INSTANCE.isCharging(LauncherActivity.this)) {
                Intent intent = new Intent(CHARGE_ACTION);
                sendBroadcast(intent);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                initBroadcast();
                initFontBroadcast();
            }

            initWallPaperBroadcast();
            initIslandBroadcast();
            initFloatBroadcast();


//            Boolean spSplash = AppLocalData.getInstance().getSpSplash();
//            if (spSplash){
//                SplashDialog splashDialog = new SplashDialog(App.getInstance());
//                splashDialog.startPregress();
//            }

            long endTime = System.currentTimeMillis();
            LogUtil.d(TAG + "耗时 = " + (endTime - startTime), LogUtil.TYPE_RELEASE);
        }
    }

    private void initBroadcast() {
        receiver = new AppStateBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
    }

    private void initFontBroadcast() {
        fontReceiver = new FontBroadcastReceiver();
        IntentFilter filter = new IntentFilter(VIEW_ACTION);
        registerReceiver(fontReceiver, filter);
    }


    private void initWallPaperBroadcast() {
        wallpaperBroadcastReceiver = new WallpaperBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WALLPAPER_CHANGES_RECEIVER);
        registerReceiver(wallpaperBroadcastReceiver, intentFilter);
    }

    private void initSetViewPagerBroadcast() {
        setViewPagerBroadcastReceiver = new SetViewPagerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.SET_VIEW_PAGER_ACTION);
        registerReceiver(setViewPagerBroadcastReceiver, intentFilter);
    }

    private void initSetWorkspaceBroadcast() {
        setWorkspaceBroadcastReceiver = new SetWorkspaceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.SET_WORK_SPACE_ACTION);
        registerReceiver(setWorkspaceBroadcastReceiver, intentFilter);
    }
    private void initIslandBroadcast() {
        lsLangReceiver = new LsLangReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LS_LAND_MODE_ACTION);
        registerReceiver(lsLangReceiver, intentFilter);
    }

    private void initFloatBroadcast() {
        floatReceiver = new FloatReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LS_FLOAT_MENU_ACTION);
        registerReceiver(floatReceiver, intentFilter);
    }


    public class FontBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            recreate();
        }
    }

    private IslandMusicManager islandMusicManager;
    private IslandFloat islandFloat;

    final NotifyListener islandNotifyListener = new NotifyListener() {
        private List<IslandMsgBean> islandMsgBeans = new ArrayList<>();

        @Override
        public void onReceiveMessage(NotificationBean bean) {

            int islandOpenState = AppLocalData.getInstance().getIslandOpenState();
            Log.e("TAG", "灵动岛设置： " + islandOpenState);
            Log.e("TAG", "接收到： " + bean.getAppName() + " 发送的通知");

            //灵动岛开关是否打开
            if (islandOpenState == 1) {

                boolean hasAudio = false;
                LinkedList<NotificationBean> linkedList = IslandNotificationLinkedQueue.getInstance().getNoRepeatQueues();
                if (linkedList == null || linkedList.size() == 0) {
                    return;
                }

                for (NotificationBean notificationBean : linkedList) {
                    if (notificationBean.getPackageName().equals("com.ximalayaos.wearkid") || notificationBean.getPackageName().equals("bubei.tingshu.wear")) {
                        hasAudio = true;
                    }
                }

                EasyFloat.dismiss(AUDIO_FLOAT);
                EasyFloat.dismiss(APP_ICON_FLOAT);
                if (hasAudio) {
                    islandFloat.showAudioFloat();
                } else {
                    islandFloat.showAppIconFloat();
                }
            } else {
                islandFloat.disMissAllFloat();
            }
        }

        @Override
        public void onRemovedMessage(String packageName) {

            LinkedList<NotificationBean> linkedList = IslandNotificationLinkedQueue.getInstance().getNoRepeatQueues();
            if (linkedList == null || linkedList.size() == 0) {
                return;
            }
            for (NotificationBean bean : linkedList) {
                if (bean.getPackageName().equals(packageName)) {
                    IslandNotificationLinkedQueue.getInstance().removeQueue(bean);
                    break;
                }
            }

        }
    };


    class WallpaperBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setImage();
        }
    }

    class SetViewPagerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SET_VIEW_PAGER_ACTION)) {

                if (!getSupportFragmentManager().isDestroyed()) {
                    mPresenter.loadFragments(getSupportFragmentManager());
                    viewPagerExt.setCurrentItem(1, true);
                    qspanelContainer.close();
                    notificationContainer.close();
                    touchUpEventContainer.close();
                }
            }
        }
    }
    class SetWorkspaceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SET_WORK_SPACE_ACTION)) {
                ReplaceWorkSpace();
            }
        }
    }
    private Loading mLoading;
    private void ReplaceWorkSpace(){
        long lastSwitchTime = (long) SharedPreferencesUtils.getParam(context, "switch_style", 0l);
        if (System.currentTimeMillis() - lastSwitchTime > 4000) {
            SharedPreferencesUtils.setParam(context, "switch_style", System.currentTimeMillis());
            //mPresenter.loadWorkspace();
            //OnOffFloatingManager.getInstance().setNextWorkspace();
            mLoading = Loading.Companion.init(App.getInstance());
            // 开启加载动画
            mLoading.startDialPregress(com.android.launcher3.common.R.layout.loading_view);
            mLoading.setCancelable(true);
            AppLauncherUtils.jumpActivity(App.getInstance(), 1);
            dismissLoading();
        }
    }
    // 关闭加载动画
    public void dismissLoading() {
        if (mLoading != null) {
            mLoading.stopPregress();
        }
    }
    private void setImage() {
        Bitmap bitmap = WallpaperUtil.getWallpaper();
        wallpaperImage.setImageBitmap(bitmap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String key = "KEY_QUERY_AND_LOAD_PATCH";
        long time = (long) SharedPreferencesUtils.getParam(this, key, 0L);
        Log.i(TAG, "onResume: duration " + (System.currentTimeMillis() - time));
        // 间隔最小一个小时请求一次热修复补丁
        if (System.currentTimeMillis() - time > 60 * 60 * 1000) {
            SophixManager.getInstance().queryAndLoadNewPatch();
            SharedPreferencesUtils.setParam(this, key, System.currentTimeMillis());
        }
        // 每分钟添加一次任务
        if (System.currentTimeMillis() > mLastAddTaskTime + 60 * 1000) {
            mLastAddTaskTime = System.currentTimeMillis();
            AppSuperviseManager.getInstance().addClassBanTask();
        }
        if (LauncherAppManager.isForbiddenInClass(App.getInstance(), "")
                && LauncherAppManager.isAppForbidden(App.getInstance())) {
            Log.i(TAG, "onResume: launchHome");
            // 上课禁用中杀掉所有运行的应用
            AppSuperviseManager.getInstance().launchHome();
        }
    }

    private ContentObserver animContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            try {
                mPresenter.setPageTransformer(viewPagerExt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}