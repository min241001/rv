package com.android.launcher3.desktop2.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.android.launcher3.common.base.BaseFragment;
import com.android.launcher3.desktop2.R;

import java.util.ArrayList;

/**
 * Author : zhangjiankang
 * Date : 2024/6/12
 * Details : 蜂窝风格
 */
public class HoneycombView<T extends Adapter> extends AdapterView<T> {

    private static final String TAG = "HoneycombView";

    private T mAdapter;
    private int lastX;
    private int lastY;
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
    private static final int TOUCH_STATE_SCALE = 3;
    private static final int INVALID_INDEX = -1;
    private static final int TOUCH_SCROLL_THRESHOLD = 10;

    private Rect mRect;
    private int screenW;
    private int screenH;
    private int centerW;
    private int centerH;
    private ArrayList<Integer[]> hexCube;
    private ArrayList<XY> hexCubeOrtho = new ArrayList<>();
    private int hexR;
    private int itemSize;
    private int edgeSize;
    private float animAlpha = 1;
    private int SCROLL_RANGE_X = 30;
    private int SCROLL_RANGE_Y = 10;
    private int menu_w;
    private int menu_h;
    private int menu_index;

    private ScaleGestureDetector gestureDetector;
    private Matrix mScaleTransMatrix; // 缓存了上次的矩阵值，所以需要计算每次变化量
    private float mStartCenterX, mStartCenterY, mLastCenterX, mLastCenterY, centerX, centerY;
    private float mStartSpan, mLastSpan, mCurrentSpan;
    private float[] mMatrixValue = new float[9];
    private float mMinScale = 0.5F, mMaxScale = 1.5F; //缩放倍数
    private float scaleX = 1f, scaleY = 1f, translateX = 0f, translateY = 0f; //缩放后，缩放因子值和平移量

    public HoneycombView(Context context) {
        super(context);
        init();
    }

    public HoneycombView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HoneycombView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void setScaleGestureDetectorListener() {

        gestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mCurrentSpan = detector.getCurrentSpan();
                centerX = detector.getFocusX();
                centerY = detector.getFocusY();
                if (processOnScale(detector)) {
                    mLastCenterX = centerX;
                    mLastCenterY = centerY;
                    mLastSpan = mCurrentSpan;
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mTouchState = TOUCH_STATE_SCALE;
                if (mScaleTransMatrix == null) {
                    mScaleTransMatrix = new Matrix(getMatrix());
                    onScaleMatrixUpdate(mScaleTransMatrix);
                }
                mStartCenterX = detector.getFocusX();
                mStartCenterY = detector.getFocusY();
                mStartSpan = detector.getCurrentSpan();

                mLastCenterX = mStartCenterX;
                mLastCenterY = mStartCenterY;
                mLastSpan = mStartSpan;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                if (mScaleTransMatrix != null) {
                    float[] mMatrixValue = new float[9];
                    mScaleTransMatrix.getValues(mMatrixValue);
                    scaleX = mMatrixValue[Matrix.MSCALE_X];
                    scaleY = mMatrixValue[Matrix.MSCALE_Y];
                    translateX = mMatrixValue[Matrix.MTRANS_X];
                    translateY = mMatrixValue[Matrix.MTRANS_Y];
                    if (scaleX > 1f || (scaleX >= 0.7 && scaleX < 1f)) {
                        reboundToOriginal();
                    }
                }
                super.onScaleEnd(detector);
            }
        });
    }

    private void reboundToOriginal() {
        ValueAnimator animator = ValueAnimator.ofFloat(scaleX, 1f);
        animator.setDuration(300); // 动画持续时间
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scaleX = (float) animation.getAnimatedValue();
                scaleY = (float) animation.getAnimatedValue();
                mScaleTransMatrix.setScale(scaleX, scaleY);
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mScaleTransMatrix == null) {
            super.dispatchDraw(canvas);
            return;
        }
        canvas.save();
        canvas.concat(mScaleTransMatrix);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    private boolean processOnScale(ScaleGestureDetector detector) {
        float diffScale = mCurrentSpan / mLastSpan;
        if (mScaleTransMatrix != null) {
            postScale(mScaleTransMatrix, diffScale, mStartCenterX, mStartCenterY);
            mScaleTransMatrix.postTranslate(detector.getFocusX() - mLastCenterX,
                    detector.getFocusY() - mLastCenterY);
            onScaleMatrixUpdate(mScaleTransMatrix);
            return true;
        }
        return false;
    }

    private void postScale(Matrix matrix, float scale, float x, float y) {
        matrix.getValues(mMatrixValue);
        float curScale = mMatrixValue[Matrix.MSCALE_X];
        if (scale < 1 && Math.abs(curScale - mMinScale) < 0.001F) {
            scale = 1;
        } else if (scale > 1 && Math.abs(curScale - mMaxScale) < 0.001F) {
            scale = 1;
        } else {
            curScale *= scale;
            if (scale < 1 && curScale < mMinScale) {
                curScale = mMinScale;
                scale = curScale / mMatrixValue[Matrix.MSCALE_X];
            } else if (scale > 1 && curScale > mMaxScale) {
                curScale = mMaxScale;
                scale = curScale / mMatrixValue[Matrix.MSCALE_X];
            }
            matrix.postScale(scale, scale, x, y);
        }
    }

    private void onScaleMatrixUpdate(Matrix matrix) {
        invalidate();
    }


    @Override
    public T getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(T adapter) {
        mAdapter = adapter;
        menu_index = mAdapter.getCount()-1;
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
        deltaX = 0;
        deltaY = 0;
        scrollMoveX += deltaX;
        scrollMoveY += deltaY;
        scrollX = scrollMoveX;
        scrollY = scrollMoveY;
        mTouchState = TOUCH_STATE_CLICK;
    }

    private void endTouch() {
        int veloX = deltaX;
        int veloY = deltaY;
        final int distanceX = veloX * 10;
        final int distanceY = veloY * 10;

        ValueAnimator endAnim = ValueAnimator.ofFloat(0, 1);
        endAnim.addUpdateListener(animation -> {

            scrollMoveX = scrollX;
            scrollMoveY = scrollY;
            int steps = 16;
            int step = (int) (steps * animation.getAnimatedFraction());

            int inertiaX = (int) (easeOutCubic
                    (step, 0, distanceX, steps) - easeOutCubic((step - 1), 0, distanceX, steps));
            int inertiaY = (int) (easeOutCubic
                    (step, 0, distanceY, steps) - easeOutCubic((step - 1), 0, distanceY, steps));

            scrollX += inertiaX;
            scrollY += inertiaY;

            if (scrollX > SCROLL_RANGE_X) {
                scrollX -= (scrollX - SCROLL_RANGE_X) / 4;
            } else if (scrollX < -SCROLL_RANGE_X) {
                scrollX -= (scrollX + SCROLL_RANGE_X) / 4;
            }

            if (scrollY > SCROLL_RANGE_Y) {
                scrollY -= (scrollY - SCROLL_RANGE_Y) / 4;
            } else if (scrollY < -SCROLL_RANGE_Y) {
                scrollY -= (scrollY + SCROLL_RANGE_Y) / 4;
            }
            iconMapRefresh(hexR, scrollX, scrollY);
            requestLayout();

        });
        endAnim.setDuration(300);
        endAnim.start();

        mTouchState = TOUCH_STATE_RESTING;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        if (event.getPointerCount() == 2) {
            gestureDetector.onTouchEvent(event);
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_CLICK) {
                    startScrollIfNeeded(event);
                }
                if (mTouchState == TOUCH_STATE_SCROLL) {
                    scrollContainer((int) event.getX(), (int) event.getY(), 1);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mTouchState == TOUCH_STATE_CLICK) {
                    clickChildAt((int) event.getX(), (int) event.getY());
                }
//                else {
//                    scroll3(event);
//                }
                break;
            default:
                endTouch();
                break;
        }
        return true;
    }

    // 坐标回弹法
    private void scroll3(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int xx = 0, yy = 0;
        for (XY xy : hexCubeOrtho) {
            if (xy.scale > 0.9f) {
                Log.i(TAG, "onTouchEvent:25 " + xy.toString());
                xx = (int) xy.x;
                yy = (int) xy.y;
                break;
            }
        }
        int x1 = -155, x2 = -50, x3 = 55;
        int y1 = -144, y2 = -39, y3 = 66;
        int minx = 0;
        int miny = 0;

        if (xx < -102) {
            minx = xx - x1;
        } else if (xx < -3) {
            minx = xx - x2;
        } else {
            minx = xx - x3;
        }

        if (yy < -102) {
            miny = yy - y1;
        } else if (yy < -3) {
            miny = yy - y2;
        } else {
            miny = yy - y3;
        }

        Log.i(TAG, "onTouchEvent:26 ==================== " + " minx " + minx + " miny " + miny);
//                scrollContainer(x, y, minx, miny);
        scrollContainer(x - minx, y - miny, 1);
        for (XY xy : hexCubeOrtho) {
            if (xy.scale > 0.5f) {
                Log.i(TAG, "onTouchEvent:27 " + xy.toString());
            }
        }
        Log.i(TAG, "onTouchEvent:28 ========================================== ");
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

    private void scrollContainer(int x, int y, int flag) {
        deltaX = (int) ((x - lastX) / scaleX);
        deltaY = (int) ((y - lastY) / scaleY);

        lastX = x;
        lastY = y;

        scrollMoveX += deltaX;
        scrollMoveY += deltaY;

        if (scrollMoveX < -300 || scrollMoveY < -450 || scrollMoveX > 300 || scrollMoveY > 300) {
            endTouch();
            return;
        }

        scrollX = scrollMoveX;
        scrollY = scrollMoveY;

        if (flag == 1) {
            iconMapRefresh(hexR, scrollX, scrollY);
        } else {

        }
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
        float[] touchPoint = new float[]{x, y};
        Matrix matrixInverse = new Matrix();
        if (mRect == null) {
            mRect = new Rect();
        }
        if (mScaleTransMatrix != null) {
            mScaleTransMatrix.invert(matrixInverse);
            matrixInverse.mapPoints(touchPoint);
        }

        for (int index = 0; index < getChildCount(); index++) {
            getChildAt(index).getHitRect(mRect);
            // 缩放后，视图点击范围根据缩放因子，平移量调整， 然后缩放坐标系的转成原始坐标系。保障触摸事件坐标正确
            if (mScaleTransMatrix != null && scaleX != 1f) {
                mRect.left = (int) (mRect.left * scaleX + translateX);
                mRect.top = (int) (mRect.top * scaleY + translateY);
                mRect.right = (int) (mRect.right * scaleX + translateX);
                mRect.bottom = (int) (mRect.bottom * scaleY + translateY);
                RectF scaledRect = new RectF(mRect);
                matrixInverse.mapRect(scaledRect);
                if (scaledRect.contains(touchPoint[0], touchPoint[1])) {
                    return index;
                }
            } else {
                if (mRect.contains(x, y)) {
                    return index;
                }
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
                addAndMeasureChild(newBottomChild,position,mAdapter.getCount());
                position++;
            }
        }

        positionItems();
    }

    private void addAndMeasureChild(View child,int position,int sum) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            if(mAdapter.getItemId(position)==menu_index) {
                params = new LayoutParams(menu_w, menu_h);
            }else{
                params = new LayoutParams(itemSize, itemSize);
            }
        }
        addViewInLayout(child, -1, params, true);
        if(mAdapter.getItemId(position)==menu_index){
            child.measure(MeasureSpec.EXACTLY | menu_w, MeasureSpec.EXACTLY | menu_h);
        }else {
            child.measure(MeasureSpec.EXACTLY | itemSize, MeasureSpec.EXACTLY | itemSize);
        }
    }

    private void positionItems() {
        for (int index = 0; index < getChildCount(); index++) {
            if(mAdapter.getItemId(index)!=menu_index){
                View child = getChildAt(index);
                XY xy = hexCubeOrtho.get(index);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                int offsetX = centerW - edgeSize;
                int offsetY = centerH - edgeSize;
                child.layout((int) xy.x + offsetX, (int) xy.y + offsetY, (int) xy.x + offsetX + width, (int) xy.y + offsetY + height);
                child.setScaleX(xy.scale);
                child.setScaleY(xy.scale);
                child.setAlpha(animAlpha);
            }
        }
        View child = getChildAt(menu_index);
        XY xy = hexCubeOrtho.get(menu_index);
        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight()*2;
        int offsetX = centerW - edgeSize*2;
        //// int offsetY = screenH+centerH-itemSize/3;
        Log.i("dv","screenH:centerH:"+screenH+":"+centerH);
        Log.i("dv","itemSize:"+itemSize);
        int offsetY = screenH-(int)(itemSize*3);
        child.layout((int) this.screenW/6, (int) xy.y + offsetY, (int) (this.screenW+centerW)/6*4, (int) xy.y + offsetY + height+menu_h);
        //child.setScaleX(xy.scale);
        //child.setScaleY(xy.scale);
        child.setAlpha(animAlpha);
    }

    private void init() {
        setScaleGestureDetectorListener();
        this.hexCube = new ArrayList<>();
        this.iconDistance = 1.0f;
        int num = BaseFragment.mAppLayer;
        for (int i = 0; i < num; i++)
            for (int j = -i; j <= i; j++)
                for (int k = -i; k <= i; k++)
                    for (int l = -i; l <= i; l++)
                        if (Math.abs(j) + Math.abs(k) + Math.abs(l) == i * 2 && j + k + l == 0) {
                            final Integer[] integers = {j, k, l};
                            this.hexCube.add(integers);
                        }
        this.screenW = getResources().getDimensionPixelSize(R.dimen.screenw);
        this.screenH = getResources().getDimensionPixelSize(R.dimen.screenh);
        this.hexR = getResources().getDimensionPixelSize(R.dimen.hexR);
        this.itemSize = getResources().getDimensionPixelSize(R.dimen.item_size);
        this.menu_w = getResources().getDimensionPixelSize(R.dimen.menu_w);
        this.menu_h = getResources().getDimensionPixelSize(R.dimen.menu_h);
        this.edgeSize = getResources().getDimensionPixelSize(R.dimen.edge_size);
        this.centerW = (this.screenW) / 2;
        this.centerH = (this.screenH) / 2;
        iconMapRefresh(hexR, 0, 0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startEnterAnim();
    }

    private float iconDistance;

    public void makeBigger() {
        if (iconDistance >= 1.0f) {
            return;
        }
        iconDistance += 0.05f;
        scrollContainer(lastX, lastY, 0);
    }

    public void makeSmaller() {
        if (iconDistance <= 0.5f) {
            return;
        }
        iconDistance -= 0.05f;
        scrollContainer(lastX, lastY, 0);
    }

    private void iconMapRefresh(float hexR, float scrollX, float scrollY) {
        hexCubeOrtho.clear();
        for (Integer[] integers : hexCube) {
            XY xy = new XY();
            xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.05f;
            xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.05f);
            xy.scale = computeScale(xy.x, xy.y);
            if (xy.scale == 0.9f) {
                xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.04f;
                xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.04f);
            } else if (xy.scale == 0.88f) {
                xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.03f;
                xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.03f);
            } else if (xy.scale == 0.86f) {
                xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.02f;
                xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.02f);
            } else if (xy.scale == 0.84f) {
                xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.01f;
                xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.01f);
            } else if (xy.scale == 0.82f) {
                xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.01f;
                xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.01f);
            } else if (xy.scale == 0.8f) {
                xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.01f;
                xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.01f);
            } else if (xy.scale == 0.78f) {
                xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.01f;
                xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.01f);
            } else if (xy.scale == 0.5f) {
                xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.0f;
                xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.0f);
            }
            hexCubeOrtho.add(xy);
        }
    }

    private float computeScale(float x, float y) {
        float absX = Math.abs(x);
        float absY = Math.abs(y);
        if (scaleX < 0.75 || scaleY < 0.75) {
            //缩放0.75倍一下，图标缩放0.95倍
            return 0.95f;
        } else if (absX <= 170 && absY <= 204) {
            return 0.9f;
        } else if (absX <= 173 && absY <= 207) {
            return 0.88f;
        } else if (absX <= 176 && absY <= 210) {
            return 0.86f;
        } else if (absX <= 179 && absY <= 213) {
            return 0.84f;
        } else if (absX <= 182 && absY <= 216) {
            return 0.82f;
        } else if (absX <= 185 && absY <= 219) {
            return 0.8f;
        } else if (absX <= 188 && absY <= 222) {
            return 0.78f;
        } else {
            return 0.5f;
        }
    }

    private void startEnterAnim() {
        ValueAnimator startAnim = ValueAnimator.ofFloat(0, 1);
        startAnim.addUpdateListener(animation -> {
            final float v = easeOutCubic((float) animation.getAnimatedValue() * 36, hexR * 2, -hexR, 36f);
            iconMapRefresh(v, 0, 0);
            animAlpha = animation.getAnimatedFraction();
            requestLayout();
        });
        startAnim.setDuration(300);
        startAnim.start();
    }

    private float easeOutCubic(float t, float b, float c, float d) {
        return c * ((t = t / d - 1) * t * t + 1) + b;
    }

    private static class XY {
        float x;
        float y;
        float scale;

        @Override
        public String toString() {
            return "XY{" +
                    "scale=" + scale +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

}
