package com.android.launcher3.desktop4.cellular.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;

import androidx.core.view.GestureDetectorCompat;

import com.android.launcher3.common.mode.AppMode;
import com.android.launcher3.common.utils.AppUtils;

import java.util.ArrayList;

/**
 * Author : yanyong
 * Date : 2024/7/11
 * Details : 棋盘风格
 */
public class CheckerBoardStyleView<T extends Adapter> extends AdapterView<T> {

    private static final String TAG = "CheckerBoardStyleView_123123";
    private T mAdapter;
    public int lastX;
    public int lastY;
    public int downX;
    public int downY;
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
    private int mScreenW; // 屏幕宽
    private int mScreenH; // 屏幕高
    private int mCenterW; // 中心坐标x
    private int mCenterH; // 中心坐标y
    private ArrayList<XY> mXYList = new ArrayList<>();
    // 圆半径
    private int mHexR;
    // 控件大小
    private int mItemSize;
    // 边距
    private int mEdgeSize;
    // 缩放比例
    private float iconDistance;
    private float animAlpha = 1;
    private static final int SCROLL_RANGE_X = 1;
    private static final int SCROLL_RANGE_Y = 1;
    // 最大坐标
    private int mMaxCoordinate;
    // 最小坐标
    private int mMinCoordinate;
    // xy轴最边缘的坐标数据
    private int mMinX, mMinY, mMaxX, mMaxY;
    private GestureDetector.SimpleOnGestureListener mListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e1 != null && e2 != null) {
                if (e1.getX() < 30 && Math.abs(e2.getY() - e1.getY()) < 50) {
                    Log.d(TAG, ",滑动事件状态 x1 = " + e1.getX() + ",x2 = " + e2.getX() + " x " + Math.abs(e2.getY() - e1.getY()));
                    return true;
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };
    private GestureDetectorCompat mGestureDetector;
    private Handler mHandler = new Handler();

    public CheckerBoardStyleView(Context context) {
        super(context);
        init(context);
    }

    public CheckerBoardStyleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckerBoardStyleView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        downX = (int) event.getX();
        downY = (int) event.getY();
        deltaX = 0;
        deltaY = 0;
        scrollMoveX += deltaX;
        scrollMoveY += deltaY;
        scrollX = scrollMoveX;
        scrollY = scrollMoveY;
        mTouchState = TOUCH_STATE_CLICK;
    }

    private void endTouch() {
        // 需要回弹的距离
        int resilience = 2 * mHexR;
        int tagX = scrollX > 0 ? scrollX - resilience : scrollX + resilience;
        int tagY = scrollY > 0 ? scrollY - resilience : scrollY + resilience;
        ValueAnimator endAnim = ValueAnimator.ofFloat(0, 1);
        endAnim.addUpdateListener(animation -> {
            scrollMoveX = scrollX;
            scrollMoveY = scrollY;
            setScrollValue(scrollX, tagX);
            setScrollValue(scrollY, tagY);
            iconMapRefresh(scrollX, scrollY, iconDistance);
            requestLayout();
        });
        endAnim.setDuration(1000);
        endAnim.start();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: " + endAnim.isRunning());
                for (XY xy : mXYList) {
                    if (xy.scale > 0.5f) {
                        scrollCalibration((int) xy.x, (int) xy.y);
                        return;
                    }
                }
            }
        }, 1050);
        mTouchState = TOUCH_STATE_RESTING;
    }

    /**
     * 设置回弹动画的差值
     *
     * @param scroll 数据
     * @param tag    回弹位置标记
     */
    private void setScrollValue(int scroll, int tag) {
        if (scroll < 0) {
            if (scroll < tag) {
                scroll += Math.abs(tag) / 50;
                if (scroll > tag) {
                    scroll = tag;
                }
            }
        } else {
            if (scroll > tag) {
                scroll -= Math.abs(tag) / 50;
                if (scroll < tag) {
                    scroll = tag;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理滑动冲突
//        getParent().requestDisallowInterceptTouchEvent(true);
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

        boolean event1 = mGestureDetector.onTouchEvent(event);
//        return !event1 || super.onTouchEvent(event);
        if (event1) {
            return super.onTouchEvent(event);
        }
        return true;
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
        int xx = 0, yy = 0;
        XY checkedXY = new XY();
        XY checkedXY2 = new XY();
        int type = 0;
        for (XY xy : mXYList) {
            if (xy.scale >= 0.7f) {
                checkedXY = xy;
                xx = (int) xy.x;
                yy = (int) xy.y;
                if (mMinX >= xy.sX && mMinY >= xy.sY) { // 左上
                    xx = (int) xy.x;
                    yy = (int) xy.y;
                    type = 1;
                    break;
                } else if (mMinX >= xy.sX && mMaxY <= xy.sY) { // 左下
                    xx = (int) xy.x;
                    yy = (int) xy.y;
                    type = 2;
                    break;
                } else if (mMaxX <= xy.sX && mMinY >= xy.sY) { // 右上
                    xx = (int) xy.x;
                    yy = (int) xy.y;
                    type = 3;
                    break;
                } else if (mMaxX <= xy.sX && mMaxY <= xy.sY) { // 右下
                    xx = (int) xy.x;
                    yy = (int) xy.y;
                    type = 4;
                    break;
                } else if (mMinX >= xy.sX) { // 左边
                    type = 5;
                    if (checkedXY2.sX > xy.sX) {
                        checkedXY2 = xy;
                    }
                } else if (mMaxX <= xy.sX) {
                    type = 6;
                    if (checkedXY2.sX < xy.sX) { // 右边
                        checkedXY2 = xy;
                    }
                } else if (mMinY >= xy.sY) {
                    type = 7;
                    if (checkedXY2.sY > xy.sY) { // 上边
                        checkedXY2 = xy;
                    }
                } else if (mMaxY <= xy.sY) {
                    type = 8;
                    if (checkedXY2.sY < xy.sY) { // 下边
                        checkedXY2 = xy;
                    }
                } else { //
                    xx = (int) xy.x;
                    yy = (int) xy.y;
                }
            }
        }
        int minx = xx;
        int miny = yy;
        switch (type) {
            case 1: // 左上角
                minx = mMaxCoordinate + xx;
                miny = mMaxCoordinate + yy;
                scrollContainer(x, y, -minx, -miny);
                break;
            case 2: // 左下角
                minx = mMaxCoordinate + xx;
                miny = mMaxCoordinate - yy;
                scrollContainer(x, y, -minx, miny);
                break;
            case 3: // 右上角
                minx = mMaxCoordinate - xx;
                miny = mMaxCoordinate + yy;
                scrollContainer(x, y, minx, -miny);
                break;
            case 4: // 右下角
                minx = mMaxCoordinate - xx;
                miny = mMaxCoordinate - yy;
                scrollContainer(x, y, minx, miny);
                break;
            case 5:
                xx = (int) checkedXY2.x;
                yy = (int) checkedXY2.y;
                if (xx <= mMinCoordinate) {
                    minx = mMaxCoordinate + xx;
                } else if (xx <= 0) {
                    minx = mMaxCoordinate + xx;
                } else {
                    minx = mMaxCoordinate + xx;
                }
                if (yy <= mMinCoordinate) {
                    miny = mMinCoordinate - yy;
                } else if (yy <= 0) {
                    miny = -yy;
                } else {
                    miny = mMaxCoordinate - yy;
                }
                scrollContainer(x, y, -minx, miny);
                break;
            case 6: // 右边
                xx = (int) checkedXY2.x;
                yy = (int) checkedXY2.y;
                if (xx <= mMinCoordinate) {
                    minx = mMaxCoordinate - xx;
                } else if (xx <= 0) {
                    minx = mMaxCoordinate - xx;
                } else {
                    minx = mMaxCoordinate - xx;
                }
                if (yy <= mMinCoordinate) {
                    miny = mMinCoordinate - yy;
                } else if (yy <= 0) {
                    miny = -yy;
                } else {
                    miny = mMaxCoordinate - yy;
                }
                scrollContainer(x, y, minx, miny);
                break;
            case 7: // 上边
                xx = (int) checkedXY2.x;
                yy = (int) checkedXY2.y;
                if ((xx <= mMinCoordinate)) {
                    minx = xx - mMinCoordinate;
                } else if (xx <= 0) {
                    minx = xx;
                } else {
                    minx = xx - mMaxCoordinate;
                }
                if (yy <= mMinCoordinate) {
                    miny = mMinCoordinate - yy;
                } else if (yy <= 0) {
                    miny = mMinCoordinate - yy;
                } else {
                    miny = mMinCoordinate - yy;
                }
                scrollContainer(x, y, -minx, miny);
                break;
            case 8: // 下边
                xx = (int) checkedXY2.x;
                yy = (int) checkedXY2.y;
                if ((xx <= mMinCoordinate)) {
                    minx = xx - mMinCoordinate;
                } else if (xx <= 0) {
                    minx = xx;
                } else {
                    minx = xx - mMaxCoordinate;
                }
                if (yy <= mMinCoordinate) {
                    miny = mMinCoordinate + yy;
                } else if (yy <= 0) {
                    miny = mMinCoordinate + yy;
                } else {
                    miny = mMinCoordinate + yy;
                }
                scrollContainer(x, y, -minx, -miny);
                break;
            default:
                if ((xx <= mMinCoordinate)) {
                    minx = xx - mMinCoordinate;
                } else if (xx <= 0) {
                    minx = xx;
                } else {
                    minx = xx - mMaxCoordinate;
                }

                if (yy <= mMinCoordinate) {
                    miny = mMinCoordinate - yy;
                } else if (yy <= 0) {
                    miny = -yy;
                } else {
                    miny = mMaxCoordinate - yy;
                }
                scrollContainer(x, y, -minx, miny);
                break;
        }
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

        // 编码器缩小到最小时回弹
        if (iconDistance <= 0.5f) {
            if (scrollMoveX < -mHexR || scrollMoveY < -mHexR || scrollMoveX > mHexR || scrollMoveY > mHexR) {
                endTouch();
                return;
            }
        }

        // 计算最大滑动距离，超出最大无缺外圈加一个的半径范围
        int distance = mHexR * mMaxX + mHexR;
        // 超出最外层item范围回弹
        if (scrollMoveX < -distance || scrollMoveY < -distance || scrollMoveX > distance || scrollMoveY > distance) {
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
        int distance = mHexR * mMaxX + 2 * mHexR;
        if (scrollMoveX < -distance || scrollMoveY < -distance || scrollMoveX > distance || scrollMoveY > distance) {
            endTouch();
            return;
        }
        scrollX = scrollMoveX;
        scrollY = scrollMoveY;
        Log.i(TAG, "scrollContainer: scrollX " + scrollX + " scrollY " + scrollY);
        iconMapRefresh(scrollX, scrollY, iconDistance);
        requestLayout();
    }

    //编码器放大
    public void makeBigger() {
        Log.d("makeBigger", "makeBigger ==== " + iconDistance);
        if (iconDistance > 1.09f) {
            iconDistance = 1.0f;
            scrollContainer(lastX, lastY);
        } else {
            iconDistance += 0.1f;
            scrollContainer(lastX, lastY);
        }
    }

    //编码器缩小
    public void makeSmaller() {
        Log.d("makeSmaller", "makeSmaller ==== " + iconDistance);
        if (iconDistance < 0.15f) {
            iconDistance = 0.5f;
            scrollContainer(lastX, lastY);
        } else {
            iconDistance -= 0.1f;
            scrollContainer(lastX, lastY);
        }
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
     * 整数4层的坐标如下
     * [-3,-3] [-2,-3] [-1,-3] [0,-3] [1,-3] [2,-3] [3,-3]
     * [-3,-2] [-2,-2] [-1,-2] [0,-2] [1,-2] [2,-2] [3,-2]
     * [-3,-1] [-2,-1] [-1,-1] [0,-1] [1,-1] [2,-1] [3,-1]
     * [-3,0]  [-2,0]  [-1,0]  [0,0]  [1,0]  [2,0]  [3,0]
     * [-3,1]  [-2,1]  [-1,1]  [0,1]  [1,1]  [2,1]  [3,1]
     * [-3,2]  [-2,2]  [-1,2]  [0,2]  [1,2]  [2,2]  [3,2]
     * [-3,3]  [-2,3]  [-1,3]  [0,3]  [1,3]  [2,3]  [3,3]
     */
    private void init(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        // screenW 328 screenH 401 hexR 96 edgeSize 10 dpi 200 widthP 410 heightP 502 centerW 164 centerH 200
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

        // 获取屏幕宽高等参数
        iconDistance = 1.0f;
        mScreenW = (int) (displayMetrics.widthPixels / multiple);
        mScreenH = (int) (displayMetrics.heightPixels / multiple);
        mEdgeSize = 10;
        mItemSize = (mScreenW - mEdgeSize * 2) / 4;
        mHexR = (int) (mItemSize * multiple);
//        mCenterW = (mScreenW - mEdgeSize * 2) / 2/* - 10*/; // 此处10为补偿分辨率为240*296的机型
//        mCenterH = (mScreenH - mEdgeSize * 2) / 2;
        mCenterW = mScreenW / 2; // 此处10为补偿分辨率为240*296的机型
        mCenterH = mScreenH / 2;
        Log.i(TAG, "init: screenW " + mScreenW + " screenH " + mScreenH + " hexR " + mHexR
                + " edgeSize " + mEdgeSize + " dpi " + dpi + " widthP "
                + displayMetrics.widthPixels + " heightP " + displayMetrics.heightPixels
                + " centerW " + mCenterW + " centerH " + mCenterH
        );
        mGestureDetector = new GestureDetectorCompat(getContext(), mListener);

        mMinCoordinate = -mHexR;
        mMaxCoordinate = mHexR;

        int sum = new AppMode(context).loadDefaultData().size();
        // 棋盘层数
        int layer = AppUtils.getCheckerboardAppLayer(sum);
        // 最外层不够一圈余数
        int remainder = AppUtils.getRemainder(sum);
        int itemSum = AppUtils.getSideItemSum(sum);
        if (itemSum > 2) {
            itemSum -= 2;
        } else {
            itemSum = 0;
        }
        // 无缺最大层数
        int minLayer = layer;
        if (remainder > 0) {
            minLayer = layer - 1;
        }
        int count = 0;
        for (int y = -layer; y <= layer; y++) {
            for (int x = -layer; x <= layer; x++) {
                // 计算最外层的X轴坐标
                if (Math.abs(y) > minLayer && count < remainder) {
                    if (Math.abs(x) == layer) {
                        if (remainder > itemSum) {
                            createXY(x, y);
                            count++;
                        }
                    } else {
                        createXY(x, y);
                        count++;
                    }
                } else {
                    // 计算最外层的Y轴坐标
                    if (Math.abs(x) == layer && count < remainder) {
                        createXY(x, y);
                        count++;
                    }
                }
            }
            // 计算无缺最大内圈所有坐标
            if (Math.abs(y) < layer) {
                for (int x = -minLayer; x <= minLayer; x++) {
                    // 记录无缺最外层边界xy轴参数
                    mMinX = Math.min(mMinX, x);
                    mMinY = Math.min(mMinY, y);
                    mMaxX = Math.max(mMaxX, x);
                    mMaxY = Math.max(mMaxY, y);
                    createXY(x, y);
                }
            }
        }
        iconMapRefresh(0, 0, iconDistance);
    }

    /**
     * 创建item的xy轴对应的数据对象
     *
     * @param x x轴像素点
     * @param y y轴像素点
     */
    private void createXY(int x, int y) {
        XY xy = new XY();
        xy.sX = x;
        xy.sY = y;
        xy.x = x;
        xy.y = y;
        xy._x = x * mHexR;
        xy._y = y * mHexR;
        mXYList.add(xy);
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

    /**
     * 就算缩放比例
     *
     * @param x x像素点位置
     * @param y y像素点位置
     * @return 缩放比例
     */
    private float computeScale(float x, float y) {
        float absX = Math.abs(x);
        float absY = Math.abs(y);
        if (absX <= 1.25f * mHexR && absY <= 1.25f * mHexR) {
            return iconDistance - 0.1f;
        } else if (absX <= 1.25f * mHexR && absY <= 1.35f * mHexR) {
            return iconDistance - 0.1f;
        } else if (absX <= 1.292f * mHexR && absY <= 1.396f * mHexR) {
            return iconDistance - 0.3f;
        } else if (absX <= 1.33f * mHexR && absY <= 1.458f * mHexR) {
            return iconDistance - 0.35f;
        } else if (absX <= 1.375f * mHexR && absY <= 1.5f * mHexR) {
            return iconDistance - 0.4f;
        } else if (absX <= 1.416f * mHexR && absY <= 1.542f * mHexR) {
            return iconDistance - 0.45f;
        } else if (absX <= 1.458f * mHexR && absY <= 1.583f * mHexR) {
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
        float sX;       // 初始位置二位坐标 x
        float sY;       // 初始位置二位坐标 y
        float x;        // x坐标
        float _x;       // x坐标
        float y;        // y坐标
        float _y;       // y坐标
        float scale;    // 比列

        @Override
        public String toString() {
            return "XY{" +
                    "scale=" + scale +
                    ", sX=" + sX +
                    ", sY=" + sY +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
