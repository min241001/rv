package com.android.launcher3.desktop4.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.desktop4.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AppleWatchView extends View {

    private ArrayList<AppInfo> mAppInfos;
    private Paint mPaint;
    private GestureDetector mGestureDetector;
    private WatchGestureListener mGestureListener;
    private Activity mActivity;
    private float mLastX;
    private float mLastY;
    private int mLineAccount;
    private int[] mIconsArrange;
    private int midLine;
    private int mRadius;
    private int mCenterX;
    private int mCenterY;
    private int mWidth;
    private int mHeight;
    //最右边Item的下标
    private int rightItemIndex;
    //最左边Item的下标
    private int leftItemIndex;
    private int topItemIndex;
    private int bottomItemIndex;

    //记录双指距离标识
    private boolean recordFlag = true;
    //双指初始落下的距离
    private float oldDistance;
    //放大缩小比例
    private static float SCALE_MAX = 1.5f;
    private static float SCALE_Min = 1.0f;

    public AppleWatchView(Context context) {
        super(context);
    }

    public AppleWatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppleWatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context, List<AppBean> appBeans) {
        if (mAppInfos == null) {
            mAppInfos = new ArrayList<>();
        }
        mAppInfos.clear();
        rightItemIndex = 0;
        leftItemIndex = 0;
        topItemIndex = 0;
        bottomItemIndex = 0;
        mLineAccount = 0;
        mActivity = (Activity) context;
        for (AppBean packageInfo : appBeans) {
            AppInfo info = new AppInfo();
            info.appName = packageInfo.getName();
            info.pkgName = packageInfo.getPackageName();
            info.appIcon = packageInfo.getIcon();
            info.appIntent = mActivity.getPackageManager().getLaunchIntentForPackage(packageInfo.getPackageName());
            mAppInfos.add(info);
        }
        mIconsArrange = Utils.generateNums(mAppInfos.size(), 2);
        mLineAccount = mIconsArrange.length;
        midLine = mLineAccount / 2;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);
        mGestureListener = new WatchGestureListener();
        mGestureDetector = new GestureDetector(context, mGestureListener);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        int midLine = mLineAccount / 2;
        for (int i = 0; i <= midLine; i++) {
            if (i == midLine) {
                rightItemIndex += mIconsArrange[i];
                rightItemIndex--;
            } else {
                leftItemIndex += mIconsArrange[i];
                rightItemIndex += mIconsArrange[i];
            }
        }
        topItemIndex = mIconsArrange[0] / 2;
        for (int i = 0; i < mLineAccount - 1; i++) {
            bottomItemIndex += mIconsArrange[i];
        }
        bottomItemIndex = bottomItemIndex + mIconsArrange[mLineAccount - 1] / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = getWidth();
        mHeight = getHeight();
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        mRadius = mWidth / 7;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        for (int i = 0; i < mLineAccount; i++) {
            drawCircles(i, mIconsArrange[i], canvas);
        }
    }

    private void drawCircles(int line, int account, Canvas canvas) {
        initLocationParam(line, account);
        for (int i = 0; i < account; i++) {
            int index = getIndex(line, i);
            AppInfo info = mAppInfos.get(index);
            mPaint.setShader(info.bitmapShader);
            canvas.drawCircle(info.x, info.y, mRadius * Math.min(info.narrowX, info.narrowY), mPaint);
        }
    }

    private void initLocationParam(int line, int account) {

        int centerY = mCenterY + (line - midLine) * mRadius * 2;
        int midNum = account / 2;
        for (int i = 0; i < account; i++) {
            int centerX;
            if (account % 2 == 0) {
                centerX = mCenterX + (i - midNum) * mRadius * 2 + mRadius;
            } else {
                centerX = mCenterX + (i - midNum) * mRadius * 2;
            }

            int index = getIndex(line, i);
            AppInfo info = mAppInfos.get(index);

            Drawable icon = info.appIcon;
            if (icon instanceof BitmapDrawable) {
                Bitmap bitmap;
                if (info.icon == null) {
                    bitmap = ((BitmapDrawable) icon).getBitmap();
                    info.icon = bitmap;
                } else {
                    bitmap = info.icon;
                }

                if (info.bitmapShader == null) {
                    if (info.scale <= 0) {
                        info.scale = (mRadius * 2.0f) / bitmap.getWidth();
                    }

                    Matrix matrix = new Matrix();
                    matrix.setScale(info.scale, info.scale);
                    matrix.postTranslate(centerX - mRadius, centerY - mRadius);
                    BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                    bitmapShader.setLocalMatrix(matrix);
                    info.bitmapShader = bitmapShader;
                }

                float narrowX;
                float halfWidth = mWidth / 2.0f;
                if ((centerX - getScrollX()) < halfWidth) {
                    narrowX = (centerX - getScrollX()) / halfWidth;
                } else {
                    int disX = mWidth - centerX + getScrollX();
                    narrowX = disX / halfWidth;
                }

                narrowX = narrowX * 0.5f + 0.5f;

                float narrowY;
                float halfHeight = mHeight / 2.0f;
                if ((centerY - getScrollY()) < halfHeight) {
                    narrowY = (centerY - getScrollY()) / halfHeight;
                } else {
                    int disY = mHeight - centerY + getScrollY();
                    narrowY = disY / halfHeight;
                }
                narrowY = narrowY * 0.5f + 0.5f;
                info.x = centerX;
                info.y = centerY;
                info.narrowX = narrowX;
                info.narrowY = narrowY;
                float radius = mRadius * Math.min(info.narrowX, info.narrowY);
                info.content = new RectF(info.x - radius, info.y - radius, info.x + radius, info.y + radius);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d("event.getAction() === " + event.getAction(), LogUtil.TYPE_DEBUG);
        //解决自定义View中的滑动事件冲突问题
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                recordFlag = true;
                //记录按下时的坐标
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    if (recordFlag) {
                        oldDistance = getDistance(event);
                        recordFlag = false;
                    }
                    float newDistance = getDistance(event);
                    LogUtil.d("两指之间的距离 ==== " + newDistance, LogUtil.TYPE_RELEASE);
                    double space = newDistance - oldDistance;
                    float scale = (float) (getScaleX() + space / mWidth);
                    LogUtil.d("当前放大缩小的比例是 === " + scale, LogUtil.TYPE_RELEASE);
                    if (scale > SCALE_Min && scale < SCALE_MAX) {
                        setScale(scale);
                    } else if (scale <= SCALE_Min) {
                        setScale(SCALE_Min);
                    }
                    return true;
                }
                int disX = (int) (event.getX() - mLastX);
                int disY = (int) (event.getY() - mLastY);
                int scrollX = getScrollX();
                int scrollY = getScrollY();
                if (disX < 0) {
                    if ((mAppInfos.get(rightItemIndex).x - scrollX) <= mCenterX / 2) {
                        return true;
                    }
                } else {
                    if ((mAppInfos.get(leftItemIndex).x - scrollX) >= mCenterX / 2) {
                        return true;
                    }
                }
                if (disY < 0) {
                    if ((mAppInfos.get(bottomItemIndex).y - scrollY) <= mCenterY / 2) {
                        return true;
                    }
                } else {
                    if ((mAppInfos.get(topItemIndex).y - scrollY) >= mCenterY / 2) {
                        return true;
                    }
                }
                scrollBy(-disX, -disY);
                mLastX = event.getX();
                mLastY = event.getY();
                break;
        }
        if (recordFlag) {
            // 让 GestureDetector 处理触摸事件
            mGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    /**
     * 设置放大缩小
     */
    public void setScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    private float getDistance(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 获取第line行，第i个元素在集合中的位置
     */
    private int getIndex(int line, int i) {
        int num = 0;
        if (line == 0) {
            num = i;
        } else {
            for (int k = 0; k < line; k++) {
                num += mIconsArrange[k];
            }
            num += i;
        }
        return num;
    }

    static class AppInfo {
        RectF content;
        String appName;
        String pkgName;
        Drawable appIcon;
        Intent appIntent;
        Bitmap icon;
        int x;
        int y;
        float narrowX;
        float narrowY;
        float scale;
        BitmapShader bitmapShader;
    }

    private class WatchGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            try {
                float clickX = e.getX() + getScrollX();
                float clickY = e.getY() + getScrollY();
                for (AppInfo info : mAppInfos) {
                    if (info.content.contains(clickX, clickY)) {
                        AppBean appBean = new AppBean();
                        appBean.setPackageName(info.pkgName);
                        LauncherAppManager.launcherApp(mActivity, appBean);
                        break;
                    }
                }
            } catch (Exception exception) {
                LogUtil.d(exception.getMessage(), LogUtil.TYPE_RELEASE);
                invalidate();
            }
            return true;
        }

    }
}