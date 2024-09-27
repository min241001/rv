package com.android.launcher3.desktop2.cellular.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.android.launcher3.common.base.BaseFragment;
import com.android.launcher3.desktop2.R;

import java.util.ArrayList;

public class BubbleCloudView<T extends Adapter> extends AdapterView<T> {

    private static final String TAG = "BubbleCloudView_123123";
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
    int scrollRangeX = 30;
    int scrollRangeY = 10;


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
        endAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

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

                if (scrollX > scrollRangeX) {
                    scrollX -= (scrollX - scrollRangeX) / 4;
                } else if (scrollX < -scrollRangeX) {
                    scrollX -= (scrollX + scrollRangeX) / 4;
                }

                if (scrollY > scrollRangeY) {
                    scrollY -= (scrollY - scrollRangeY) / 4;
                } else if (scrollY < -scrollRangeY) {
                    scrollY -= (scrollY + scrollRangeY) / 4;
                }
                iconMapRefresh(hexR, scrollX, scrollY);
                requestLayout();

            }
        });
        endAnim.setDuration(300);
        endAnim.start();

        mTouchState = TOUCH_STATE_RESTING;
    }


    public BubbleCloudView(Context context) {
        super(context);
        init();
    }

    public BubbleCloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleCloudView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
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

    private void scrollContainer(int x, int y) {

        deltaX = x - lastX;
        deltaY = y - lastY;

        lastX = x;
        lastY = y;

        scrollMoveX += deltaX;
        scrollMoveY += deltaY;

//        if (scrollMoveX < -222 || scrollMoveY < -222 || scrollMoveX > 222 || scrollMoveY > 222) {
//            endTouch();
//            return;
//        }

        if (Math.abs(scrollMoveX) > 222 || Math.abs(scrollMoveY) > 222) {
            endTouch();
            return;
        }


        scrollX = scrollMoveX;
        scrollY = scrollMoveY;
        if (scrollMoveX > scrollRangeX) {
            scrollX = scrollRangeX + (scrollMoveX - scrollRangeX) / 2;
        } else if (scrollX < -scrollRangeX) {
            scrollX = -scrollRangeX + (scrollMoveX + scrollRangeX) / 2;
        }
        if (scrollMoveY > scrollRangeY) {
            scrollY = scrollRangeY + (scrollMoveY - scrollRangeY) / 2;
        } else if (scrollY < -scrollRangeY) {
            scrollY = -scrollRangeY + (scrollMoveY + scrollRangeY) / 2;
        }
        iconMapRefresh(hexR, scrollX, scrollY);
        requestLayout();
    }

    private void clickChildAt(final int x, final int y) {
        final int index = getContainingChildIndex(x, y);
        if (index != INVALID_INDEX) {
            final View itemView = getChildAt(index);
            final int position = index;
            final long id = mAdapter.getItemId(position);
            performItemClick(itemView, position, id);
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
            params = new LayoutParams(itemSize, itemSize);
        }
        addViewInLayout(child, -1, params, true);
        child.measure(MeasureSpec.EXACTLY | itemSize, MeasureSpec.EXACTLY | itemSize);
    }

    private void positionItems() {

        for (int index = 0; index < getChildCount(); index++) {
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

    private int num;

    private void init() {
        this.hexCube = new ArrayList<>();
        num = BaseFragment.mAppLayer;
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
        this.edgeSize = getResources().getDimensionPixelSize(R.dimen.edge_size);
        this.centerW = (this.screenW + hexR) / 2;
        this.centerH = (this.screenH + hexR) / 2;
        iconMapRefresh(hexR, 0, 0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //开启动画
        startEnterAnim();
    }

    /**
     * @param hexR
     * @param scrollX
     * @param scrollY
     */
    private void iconMapRefresh(float hexR, float scrollX, float scrollY) {
        hexCubeOrtho.clear();
        for (int i = 0; i < hexCube.size(); i++) {
            final Integer[] integers = this.hexCube.get(i);
            XY xy = new XY();
            xy.x = ((integers[1] + integers[0] / 2f) * hexR + scrollX) * 1.2f;
            xy.y = (float) ((Math.sqrt(3) / 2 * integers[0] * hexR + scrollY) * 1.2f);
            if (Math.abs(xy.x) <= 90 && Math.abs(xy.y) <= 106) {
                xy.scale = 0.95f;
            } else if (Math.abs(xy.x) <= 93 && Math.abs(xy.y) <= 112) {
                xy.scale = 0.85f;
            } else if (Math.abs(xy.x) <= 96 && Math.abs(xy.y) <= 113) {
                xy.scale = 0.8f;
            } else if (Math.abs(xy.x) <= 99 && Math.abs(xy.y) <= 117) {
                xy.scale = 0.75f;
            } else if (Math.abs(xy.x) <= 102 && Math.abs(xy.y) <= 120) {
                xy.scale = 0.7f;
            } else if (Math.abs(xy.x) <= 105 && Math.abs(xy.y) <= 124) {
                xy.scale = 0.65f;
            } else if (Math.abs(xy.x) <= 108 && Math.abs(xy.y) <= 127) {
                xy.scale = 0.6f;
            } else {
                xy.scale = 0.3f;
            }
            hexCubeOrtho.add(xy);
        }
    }

    private void startEnterAnim() {
        ValueAnimator startAnim = ValueAnimator.ofFloat(0, 1);
        startAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float v = easeOutCubic((float) animation.getAnimatedValue() * 36, hexR * 2, -hexR, 36f);
                iconMapRefresh(v, 0, 0);
                animAlpha = animation.getAnimatedFraction();
                requestLayout();
            }
        });
        startAnim.setDuration(300);
        startAnim.start();
    }

    private float easeOutCubic(float t, float b, float c, float d) {
        return c * ((t = t / d - 1) * t * t + 1) + b;
    }

    private static class XY {
        float x; // x坐标
        float y; // y坐标
        float scale; // 比列

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
