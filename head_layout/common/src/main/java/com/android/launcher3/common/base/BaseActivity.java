package com.android.launcher3.common.base;

import static com.android.launcher3.common.CompileConfig.ALL_SLIDE_BACK;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.BaseActivityManager;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ThreadPoolUtils;

import java.lang.ref.WeakReference;

/**
 * @Description：基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = getClass().getSimpleName() + "--->>>";

    private static final int UI_SYNC_DATA = 1;

    protected static final int SPLASH = 6 * 1000;

    protected Handler uiHandler = null;

    protected boolean calenderNeedSlideBack = false;

    private static class UIHandler extends Handler {

        private WeakReference<Activity> weakReference;

        public UIHandler(Activity fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UI_SYNC_DATA:
                    Activity fragment = weakReference.get();
                    if (fragment != null && fragment instanceof BaseActivity) {
                        ((BaseActivity) fragment).updateView();
                    }
                    break;

            }
        }
    }

    private Activity mActivity;

    public Activity getActivity() {
        return mActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && AppUtils.isThemeDial()) {
            super.onCreate(null);
        } else {
            super.onCreate(savedInstanceState);
        }
        BaseActivityManager.getInstance().addActivity(this);
        this.mActivity = BaseActivity.this;
        // 设置布局
        setContentView(getResourceId());
        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 隐藏导航栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().getDecorView().setBackgroundColor(getBackgroundColor());
        // 创建手势检测器
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        // 初始化控件、数据及事件
        initView();
        initData();
        initEvent();
        // 进行一些初始化操作，如获取参数
        uiHandler = new UIHandler(this);
        ThreadPoolUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                syncData();
                uiHandler.sendEmptyMessage(UI_SYNC_DATA);
            }
        });
        // 侧滑退出的区域
        calcSwipeThreshold();
    }

    private int swipeThreshold;

    private void calcSwipeThreshold() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        swipeThreshold = screenWidth / 3;
    }

    protected abstract int getResourceId();

    protected int getBackgroundColor() {
        return getColor(R.color.background_black);
    }

    protected void initView() {
        // 由子类覆盖重写
    }

    protected void initData() {
        // 由子类覆盖重写
    }

    protected void initEvent() {
        // 由子类覆盖重写
    }

    protected void syncData() {
        // 由子类覆盖重写
    }

    protected void updateView() {
        // 由子类覆盖重写
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放线程池
        ThreadPoolUtils.shutdown();
        this.uiHandler.removeCallbacksAndMessages(null);
        this.uiHandler = null;
        this.mActivity = null;
        BaseActivityManager.getInstance().removeActivity(this);
    }

    public void forward() {
        // 前往事件，子类可重新该方法，实现自己的功能
    }

    public void spacesWard() {
        // 前往事件，子类可重新该方法，实现自己的功能
    }

    public void backward() {
        // 返回事件，子类可重新该方法，实现自己的功能
    }

//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        LogUtil.d(TAG + "onKeyDown --- " + keyCode);
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:// 按下返回键
//                LogUtil.d(TAG + "按下返回键");
////                backward();
//                break;
//            case KeyEvent.KEYCODE_HOME:// 按下home键
//                LogUtil.d(TAG + "按下home键");
//                break;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private GestureDetector gestureDetector;
    private boolean enableSwipeToExit = true;// 默认支持侧滑删除

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 判断是否启用侧滑退出功能
        if (getSwipeToExitEnabled() && gestureDetector != null) {
            gestureDetector.onTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    protected void useGestureDetector(MotionEvent event) {
        if (getSwipeToExitEnabled() && gestureDetector != null) {
            gestureDetector.onTouchEvent(event);
        }
    }

    // 设置是否启用侧滑退出功能
    protected void setSwipeToExitEnabled(boolean enabled) {
        this.enableSwipeToExit = enabled;
    }

    // 获取是否启用侧滑退出功能
    protected boolean getSwipeToExitEnabled() {
        return this.enableSwipeToExit;
    }

    // 自定义SwipeGestureListener监听器
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 75;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (getSwipeToExitEnabled() && e1.getX() < swipeThreshold) {// 把侧滑退出的范围限定在1/6区域
                    float deltaX = e2.getX() - e1.getX();
                    float deltaY = e2.getY() - e1.getY();
                    // 判断是向左滑动还是向右滑动
                    if (Math.abs(deltaX) > Math.abs(deltaY)
                            && Math.abs(deltaX) > SWIPE_THRESHOLD
                            && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (deltaX > 0 && ALL_SLIDE_BACK) {
                            // 向右滑动，返回上一页
                            onBackPressed();
                        } else if (deltaX > 0 && calenderNeedSlideBack) {
                            // 日历向右滑动，返回上一页
                            onBackPressed();
                        }
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            } catch (Exception e) {
                LogUtil.d(e.getMessage(), LogUtil.TYPE_RELEASE);
            }
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSwipeToExitEnabled()) {
            super.onBackPressed();
        }
    }

    protected void jumpToTargetActivity(Class<? extends Activity> clazz) {
        if (clazz == null)
            return;
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
    }

    protected void jumpToTargetActivityFinish(Class<? extends Activity> clazz) {
        if (clazz == null)
            return;
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
        finish();
    }


    protected void startActivityAction(String action) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动应用
     *
     * @param app
     */
    protected void lunchApp(AppBean app) {
        LauncherAppManager.launcherApp(getActivity(), app);
    }


    @Override
    public Resources getResources() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Resources res = super.getResources();
            float fontSize = Settings.System.getFloat(getContentResolver(), Settings.System.FONT_SCALE, 1.3f);
            Configuration configuration = res.getConfiguration();
            DisplayMetrics displayMetrics = res.getDisplayMetrics();
            if (fontSize != 1.0f) {
                configuration.fontScale = fontSize;
            } else {
                configuration.fontScale = 1.3f;
            }
            res.updateConfiguration(configuration, displayMetrics);
            return res;
        } else {
            return super.getResources();
        }
    }
}
