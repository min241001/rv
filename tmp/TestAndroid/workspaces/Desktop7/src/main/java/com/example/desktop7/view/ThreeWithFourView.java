package com.example.desktop7.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.android.launcher3.common.mode.AppMode;

import java.util.ArrayList;

/**
 * Author : yanyong
 * Date : 2024/7/11
 * Details : 343列表风格，调试手表分辨率 410 * 502。屏幕密度 200。半径 96
 * 基础参数
 * x坐标像素 3列：-96  0  96  4列：-144  -48  48  144
 * y坐标像素 -192  -96  0  96  192
 */
public class ThreeWithFourView<T extends Adapter> extends AdapterView<T> {

    private static final String TAG = "ThreeWithFourView";
    private T mAdapter;
    public int lastX;
    public int mDownX;
    public int lastY;
    private int deltaX;
    private int deltaY;
    private int scrollMoveX;
    private int scrollMoveY;
    private int scrollX;
    private int scrollY;

    private int mTouchState = TOUCH_STATE_RESTING;
    private static final int TOUCH_STATE_RESTING = 0;
    private static final int TOUCH_STATE_CLICK = 1;
    private static final int TOUCH_STATE_SCROLL = 2;
    private static final int INVALID_INDEX = -1;
    private static final int TOUCH_SCROLL_THRESHOLD = 10;

    private Rect mRect;
    private int mScreenW;
    private int mScreenH;
    private int mCenterW;
    private int mCenterH;
    private ArrayList<XY> mXYList = new ArrayList<>();
    private int mHexR;
    private int mItemSize;
    private int mEdgeSize;
    private float iconDistance;
    private float animAlpha = 1;
    // 最大坐标
    private int mMaxCoordinate;
    // 最小坐标
    private int mMinCoordinate;
    // xy轴最边缘的坐标数据
    private int mMinX, mMaxX, mMinY, mMaxY;

    public ThreeWithFourView(Context context) {
        super(context);
        init(context);
    }

    public ThreeWithFourView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ThreeWithFourView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public T getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(T adapter) {
        mAdapter = adapter;
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int position) {
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        //处理滑动冲突
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(event);
                return false;
            case MotionEvent.ACTION_MOVE:
                return startScrollIfNeeded(event);
            default:
                endTouch();
                return false;
        }
    }

    private void startTouch(final MotionEvent event) {
        lastX = (int) event.getX();
        lastY = (int) event.getY();
        mDownX = (int) event.getX();
        deltaX = 0;
        deltaY = 0;
        scrollMoveX += deltaX;
        scrollMoveY += deltaY;
        scrollX = scrollMoveX;
        scrollY = scrollMoveY;
        mTouchState = TOUCH_STATE_CLICK;
    }

    private void endTouch() {
        XY tagXY = new XY();
        for (XY xy : mXYList) {
            if (xy.scale > 0.5f) {
                if (xy.sY == mMinY) {
                    tagXY = xy;
                    break;
                } else if (xy.sY == mMaxY) {
                    tagXY = xy;
                    break;
                }
            }
        }
        // 需要回弹的距离
        int scrY = 0;
        if (tagXY.sY == mMinY) {
            // 列表在顶部，向下滑动。
            if (tagXY.sY <= -mHexR * 2) {
                // TODO: 2024/7/11 当前参数范围小，扩大外围滑动范围需要添加参数
            } else if (tagXY.sY <= -mHexR) {
                scrY = (int) (3 * mHexR + tagXY.sY);
            } else if (tagXY.sY <= 0) {
                scrY = (int) (2 * mHexR + tagXY.sY);
            } else if (tagXY.sY <= mHexR) {
                scrY = (int) (mHexR + tagXY.sY);
            } else {
                scrY = (int) tagXY.sY;
            }
        } else if (tagXY.sY == mMaxY) {
            // 列表在底部，向上滑动   -192  -96  0  96  192
            if (tagXY.sY <= -mHexR * 2) {
                // TODO: 2024/7/11 当前参数范围小，扩大外围滑动范围需要添加参数
            } else if (tagXY.sY <= -mHexR) {
                // TODO: 2024/7/11 当前参数范围小，扩大外围滑动范围需要添加参数
            } else if (tagXY.sY <= 0) {
                scrY = (int) (mHexR - tagXY.sY);
            } else if (tagXY.sY <= mHexR) {
                scrY = (int) (2 * mHexR - tagXY.sY);
            } else {
                scrY = (int) (2 * mHexR - tagXY.sY);
            }
        }

        int tag = scrollY > 0 ? scrollY - scrY : scrollY + scrY;
        ValueAnimator endAnim = ValueAnimator.ofFloat(0, 1);
        endAnim.addUpdateListener(animation -> {
            scrollMoveX = scrollX;
            scrollMoveY = scrollY;
            if (scrollY < 0) {
                if (scrollY < tag) {
                    scrollY += Math.abs(tag) / 50;
                    if (scrollY > tag) {
                        scrollY = tag;
                    }
                }
            } else {
                if (scrollY > tag) {
                    scrollY -= Math.abs(tag) / 50;
                    if (scrollY < tag) {
                        scrollY = tag;
                    }
                }
            }
            iconMapRefresh(scrollX, scrollY, iconDistance);
            requestLayout();
        });
        endAnim.setDuration(1000);
        endAnim.start();
        mTouchState = TOUCH_STATE_RESTING;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理滑动冲突
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_CLICK) {
                    startScrollIfNeeded(event);
                }
                if (mTouchState == TOUCH_STATE_SCROLL) {
                    scrollContainer((int) event.getX(), (int) event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                scrollCalibration((int) event.getX(), (int) event.getY());
                if (mTouchState == TOUCH_STATE_CLICK) {
                    clickChildAt((int) event.getX(), (int) event.getY());
                }
                break;
            default:
                endTouch();
                break;
        }
        return true;
    }

    private boolean startScrollIfNeeded(final MotionEvent event) {
        final int xPos = (int) event.getX();
        final int yPos = (int) event.getY();
        if (xPos < lastX - TOUCH_SCROLL_THRESHOLD
                || xPos > lastX + TOUCH_SCROLL_THRESHOLD
                || yPos < lastY - TOUCH_SCROLL_THRESHOLD
                || yPos > lastY + TOUCH_SCROLL_THRESHOLD) {
            mTouchState = TOUCH_STATE_SCROLL;
            return true;
        }
        return false;
    }

    public void scrollContainer(int x, int y) {
        deltaX = x - lastX;
        deltaY = y - lastY;

        lastX = x;
        lastY = y;

        scrollMoveX += deltaX;
        scrollMoveY += deltaY;

        if (iconDistance <= 0.5f) {
            endTouch();
        }
        if (scrollMoveY < -(mHexR * mMaxY) || scrollMoveY > -(mHexR * mMinY)) {
            endTouch();
            return;
        }

        scrollX = scrollMoveX;
        scrollY = scrollMoveY;

        iconMapRefresh(scrollX, scrollY, iconDistance);
        requestLayout();
    }

    public void scrollContainer(int x, int y, int xx, int yy) {
        x += xx;
        y += yy;
        deltaX = x - lastX;
        deltaY = y - lastY;

        lastX = x - xx;
        lastY = y - yy;

        scrollMoveX += deltaX;
        scrollMoveY += deltaY;

        if (scrollMoveY < -(mHexR * mMaxY) || scrollMoveY > -(mHexR * mMinY)) {
            endTouch();
            return;
        }
        scrollX = scrollMoveX;
        scrollY = scrollMoveY;
        iconMapRefresh(scrollX, scrollY, iconDistance);
        requestLayout();
    }

    /**
     * 坐标回弹法，滑动校准
     *
     * @param x x坐标
     * @param y y坐标
     */
    private void scrollCalibration(int x, int y) {
        lastX = x;
        lastY = y;
        XY leftXY = new XY();
        XY rightXY = new XY();
        int type = 0;
        for (XY xy : mXYList) {
            if (xy.scale >= 0.7f) {
                if (x - mDownX > 0) {
                    type = 1;
                    if (mMinX >= xy.sX && leftXY.sX > xy.sX && xy.flag) { // 左边
                        leftXY = xy;
                    }
                } else {
                    type = 2;
                    if (mMaxX <= xy.sX && rightXY.sX < xy.sX && xy.flag) { // 右边
                        rightXY = xy;
                    }
                }
            }
        }
        int xx = 0, yy = 0;
        int minx = xx;
        int miny = yy;
        switch (type) {
            case 1: // 左边 -144 -48  48  144   -96  0  96
                xx = (int) leftXY.x;
                yy = (int) leftXY.y;
                if (xx <= mMinCoordinate) { // -144
                    minx = mMaxCoordinate + xx;
                } else if (xx <= mMinCoordinate + mHexR) { // -48
                    minx = mMaxCoordinate + xx;
                } else if (xx <= mMaxCoordinate - mHexR) { // 48
                    minx = mMaxCoordinate + xx;
                } else {
                    minx = mMaxCoordinate + xx;
                }
                if (yy <= -mHexR) {
                    miny = -mHexR - yy;
                } else if (yy <= 0) {
                    miny = -yy;
                } else {
                    miny = mHexR - yy;
                }
                scrollContainer(x, y, -minx, miny);
                break;
            case 2: // 右边 -144 -48  48  144   -96  0  96
                xx = (int) rightXY.x;
                yy = (int) rightXY.y;
                if (xx <= mMinCoordinate) {
                    minx = mMaxCoordinate - xx;
                } else if (xx <= mMinCoordinate + mHexR) {
                    minx = mMaxCoordinate - xx;
                } else if (xx <= mMaxCoordinate - mHexR) {
                    minx = mMaxCoordinate - xx;
                } else {
                    minx = mMaxCoordinate - xx;
                }
                if (yy <= -mHexR) {
                    miny = -mHexR - yy;
                } else if (yy <= 0) {
                    miny = -yy;
                } else {
                    miny = mHexR - yy;
                }
                scrollContainer(x, y, minx, miny);
                break;
        }
    }

    //编码器放大
    public void makeBigger(Context context) {
        if (iconDistance >= 1.0f) {
            // 获取屏幕中心坐标
            Point centerPoint = getScreenCenter(context);
            int centerX = centerPoint.x;
            int centerY = centerPoint.y;
            Log.d("makeBigger = ", "makeBigger === " + centerX);
            Log.d("makeBigger = ", "makeBigger === " + centerY);
            return;
        }
        iconDistance += 0.05f;
        scrollContainer(lastX, lastY);
        requestLayout();
    }

    public static Point getScreenCenter(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        int centerX = screenSize.x / 2;
        int centerY = screenSize.y / 2;
        return new Point(centerX, centerY);
    }

    //编码器缩小
    public void makeSmaller() {
        if (iconDistance <= 0.5f) {
            return;
        }
        iconDistance -= 0.05f;
        scrollContainer(lastX, lastY);
        requestLayout();
    }

    private void clickChildAt(final int x, final int y) {
        final int index = getContainingChildIndex(x, y);
        if (index != INVALID_INDEX) {
            final View itemView = getChildAt(index);
            final long id = mAdapter.getItemId(index);
            performItemClick(itemView, index, id);
        }
    }

    private int getContainingChildIndex(final int x, final int y) {
        if (mRect == null) {
            mRect = new Rect();
        }
        for (int index = 0; index < getChildCount(); index++) {
            getChildAt(index).getHitRect(mRect);
            if (mRect.contains(x, y)) {
                return index;
            }
        }
        return INVALID_INDEX;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mAdapter == null) {
            return;
        }

        if (getChildCount() == 0) {
            int position = 0;
            while (position < mAdapter.getCount()) {
                View newBottomChild = mAdapter.getView(position, null, this);
                addAndMeasureChild(newBottomChild);
                position++;
            }
        }

        positionItems();
    }

    private void addAndMeasureChild(View child) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(mItemSize, mItemSize);
        }
        addViewInLayout(child, -1, params, true);
        child.measure(mScreenW | mItemSize, mScreenH | mItemSize);
    }

    private void positionItems() {
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);
            XY xy = mXYList.get(index);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int offsetX = mCenterW - mEdgeSize;
            int offsetY = mCenterH - mEdgeSize;

            child.layout((int) xy.x + offsetX, (int) xy.y + offsetY, (int) xy.x + offsetX + width, (int) xy.y + offsetY + height);
            child.setScaleX(xy.scale);
            child.setScaleY(xy.scale);
            child.setAlpha(animAlpha);
        }
    }

    /**
     * 343风格数据结构
     * <p>
     * [-1,-3] [0,-3] [1,-3]
     * [-1,-2] [0,-2] [1,-2] [2,-2]
     * [-1,-1] [0,-1] [1,-1]
     * [-1,0]  [0,0]  [1,0]  [2,0]
     * [-1,1]  [0,1]  [1,1]
     * [-1,2]  [0,2]  [1,2]  [2,2]
     * [-1,3]  [0,3]  [1,3]
     */
    private void init(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int dpi = displayMetrics.densityDpi;
        float multiple;
        // 适配不同分辨率设备
        if (dpi <= 120) {
            multiple = dpi / 120f;
        } else if (dpi < 240) {
            multiple = dpi / 160f;
        } else {
            multiple = dpi / 240f;
        }
        this.iconDistance = 1.0f;
        mScreenW = (int) (displayMetrics.widthPixels / multiple);
        mScreenH = (int) (displayMetrics.heightPixels / multiple);
        mEdgeSize = 10;
        mItemSize = (mScreenW - mEdgeSize * 2) / 4;
        mHexR = (int) (mItemSize * multiple);

        mMinCoordinate = -mHexR - mHexR / 2;
        mMaxCoordinate = mHexR + mHexR / 2;

        mCenterW = this.mScreenW / 2;
        mCenterH = this.mScreenH / 2;

        //传入具体app总数
        int sum = new AppMode(context).loadDefaultData().size();
        // 总行数
        int lines = sum / 7 + (sum % 7 == 0 ? 0 : 1);
        int count = 0;
        // 标记当前行是否为3个，首行为3个
        boolean flag = true;

        for (int y = -lines; y <= lines; y++) {
            for (int x = -1; x < 3; x++) {
                if (count >= sum) {
                    break;
                }
                mMinY = Math.min(mMinY, y);
                mMaxY = Math.max(mMaxY, y);
                if (flag) {
                    // 一行3个
                    if (x == 2) {
                        continue;
                    }
                    XY xy = createXY(false, x, y);
                    mXYList.add(xy);
                } else {
                    mMinX = Math.min(mMinX, x);
                    mMaxX = Math.max(mMaxX, x);
                    // 一行4个
                    XY xy = createXY(true, x, y);
                    mXYList.add(xy);
                }
                count++;
            }
            flag = !flag;
        }
        iconMapRefresh(0, 0, iconDistance);
    }

    /**
     * 创建xy对象
     *
     * @param flag 是否4列标记
     * @param x    x坐标
     * @param y    y坐标
     * @return xy对象
     */
    private XY createXY(boolean flag, int x, int y) {
        XY xy = new XY();
        xy.flag = flag;
        xy.sX = x;
        xy.sY = y;
        xy.x = x;
        xy.y = y;
        xy._x = x * mHexR;
        // 4列的x坐标左移半个hexR
        if (flag) {
            xy._x -= mHexR / 2f;
        }
        xy._y = y * mHexR;
        return xy;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startEnterAnim();
    }

    /**
     * 滑动后位置数据适配
     *
     * @param scrollX       x轴滑动距离
     * @param scrollY       y轴滑动距离
     * @param mDefaultValue 缩放比例
     */
    private void iconMapRefresh(float scrollX, float scrollY, float mDefaultValue) {
        for (XY xy : mXYList) {
            if (iconDistance <= 0.5f) {
                xy.x = (xy._x + scrollX) * mDefaultValue;
                xy.y = (xy._y + scrollY) * mDefaultValue;
                xy.scale = mDefaultValue;
            } else {
                xy.x = (xy._x + scrollX) * mDefaultValue;
                xy.y = (xy._y + scrollY) * mDefaultValue;
                xy.scale = computeScale(xy.x, xy.y);
            }
        }
    }

    private float computeScale(float x, float y) {
        float absX = Math.abs(x);
        float absY = Math.abs(y);

        if (absY <= 2.125f * mHexR) {
            return iconDistance - 0.1f;
        } else if (absY <= 2.156f * mHexR) {
            return iconDistance - 0.1f;
        } else if (absY <= 2.188f * mHexR) {
            return iconDistance - 0.3f;
        } else if (absY <= 2.219f * mHexR) {
            return iconDistance - 0.35f;
        } else if (absY <= 2.25f * mHexR) {
            return iconDistance - 0.4f;
        } else if (absY <= 2.281f * mHexR) {
            return iconDistance - 0.45f;
        } else if (absY <= 2.312f * mHexR) {
            return iconDistance - 0.5f;
        } else {
            return iconDistance - 0.55f;
        }
    }

    private void startEnterAnim() {
        ValueAnimator startAnim = ValueAnimator.ofFloat(0, 1);
        startAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                iconMapRefresh(0, 0, iconDistance);
                animAlpha = animation.getAnimatedFraction();
                requestLayout();
            }
        });
        startAnim.setDuration(300);
        startAnim.start();
    }

    private static class XY {
        float sX;       // 初始位置二维坐标 x
        float sY;       // 初始位置二维坐标 y
        float x;        // x坐标
        float _x;       // x坐标
        float y;        // y坐标
        float _y;       // y坐标
        float scale;    // 比列
        boolean flag;   // 此标记区分3列和4列，true为4列

        @Override
        public String toString() {
            return "XY{" +
                    "scale=" + scale +
                    ", sX=" + sX +
                    ", sY=" + sY +
                    ", x=" + x +
                    ", y=" + y +
                    ", flag=" + flag +
                    '}';
        }
    }
}
