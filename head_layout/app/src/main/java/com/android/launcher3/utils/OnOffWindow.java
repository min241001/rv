package com.android.launcher3.utils;
import static com.android.launcher3.receiver.SystemBroadcastReceiver.SOS_ACTION;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.launcher3.App;
import com.android.launcher3.R;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ShortCutUtils;
import com.android.launcher3.widget.SlideSwitch;

/**
 * Author : yanyong
 * Date : 2024/9/9
 * Details : 开关机悬浮框
 */
public class OnOffWindow {

    private static final String TAG = "OnOffWindow1";

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mParams;

    private Context mContext;

    // 悬浮窗view
    private View mView;

    // 悬浮框是否显示
    private boolean mHasShown;

    // Y61E下侧键短按标记
    private boolean mDownFlag;

    private GestureDetector mGestureDetector;
    private SlideSwitch mSsSos;

    public OnOffWindow() {
        LogUtil.i(TAG, "OnOffWindow: init", LogUtil.TYPE_RELEASE);
        this.mContext = App.getInstance();
        initView();
    }

    private void initView() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        //高版本适配 全面/刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        mParams.gravity = Gravity.CENTER;
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        mParams.format = PixelFormat.TRANSLUCENT;
        // 设置 Window flag 为系统级弹框 | 覆盖表层
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        // 去掉FLAG_NOT_FOCUSABLE隐藏输入 全面屏隐藏虚拟物理按钮办法
        mParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mParams.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN;

        // 创建手势检测器
        mGestureDetector = new GestureDetector(mContext, new SwipeGestureListener());

        FrameLayout frameLayout = new FrameLayout(mContext) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mDownFlag) {
                        dismiss();
                    } else {
                        mDownFlag = true;
                    }
                }
                return super.dispatchKeyEvent(event);
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (mGestureDetector != null) {
                    mGestureDetector.onTouchEvent(ev);
                }
                return super.dispatchTouchEvent(ev);
            }
        };

        mView = LayoutInflater.from(mContext).inflate(R.layout.view_onoff_floating, frameLayout);
        ((SlideSwitch) mView.findViewById(R.id.ss_shutdown)).setOnStateChangedListener(state -> {
            LogUtil.i(TAG, "onStateChanged: ss_shutdown " + state, LogUtil.TYPE_RELEASE);
            if (state) {
                ShortCutUtils.INSTANCE.shutDown();
            }
        });

        ((SlideSwitch) mView.findViewById(R.id.ss_reboot)).setOnStateChangedListener(state -> {
            LogUtil.i(TAG, "onStateChanged: ss_reboot " + state, LogUtil.TYPE_RELEASE);
            if (state) {
                Intent intent = new Intent(Intent.ACTION_REBOOT);
                intent.putExtra("nowait", 1);
                intent.putExtra("interval", 1);
                intent.putExtra("window", 0);
                mContext.sendBroadcast(intent);
            }
        });

        mSsSos = mView.findViewById(R.id.ss_sos);
        mSsSos.setOnStateChangedListener(state -> {
            LogUtil.i(TAG, "onStateChanged: ss_sos " + state, LogUtil.TYPE_RELEASE);
            if (state) {
                Intent intent = new Intent(SOS_ACTION);
                mContext.sendBroadcast(intent);
                dismiss();
                mSsSos.resetCurX();
            }
        });
    }

    public void show() {
        try {
            LogUtil.i(TAG, "show: hasShown " + mHasShown + " canDrawOverlays "
                    + Settings.canDrawOverlays(mContext), LogUtil.TYPE_RELEASE);
            if (!mHasShown) {
                if (Settings.canDrawOverlays(mContext)) {
                    if (mView.getParent() == null) {
                        mWindowManager.addView(mView, mParams);
                        mHasShown = true;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "FloatingWindows show()" + e, LogUtil.TYPE_RELEASE);
        }
    }

    /**
     * 取消显示
     */
    public void dismiss() {
        try {
            if (mHasShown) {
                if (mView.getParent() != null) {
                    mDownFlag = false;
                    OnOffFloatingManager.getInstance().setShowing();
                    mWindowManager.removeView(mView);
                    mHasShown = false;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "FloatingWindows dismiss() " + e, LogUtil.TYPE_RELEASE);
        }
    }

    // 自定义SwipeGestureListener监听器
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 55;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            /*try {
                float deltaX = e2.getX() - e1.getX();
                float deltaY = e2.getY() - e1.getY();
                LogUtil.i(TAG, "onFling: deltaX " + deltaX + " deltaY " + deltaY + " velocityX "
                        + velocityX + " velocityY " + velocityY, LogUtil.TYPE_RELEASE);
                // 判断是向左滑动还是向右滑动
                if (Math.abs(deltaX) > Math.abs(deltaY)
                        && Math.abs(deltaX) > SWIPE_THRESHOLD
                        && Math.abs(deltaY) < SWIPE_VELOCITY_THRESHOLD) {
                    LogUtil.i(TAG, "onFling: " + e1.getX() + " deltaX " + deltaX, LogUtil.TYPE_RELEASE);
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            } catch (Exception e){
                LogUtil.d(e.getMessage(), LogUtil.TYPE_RELEASE);
            }*/
            return false;
        }
    }

}
