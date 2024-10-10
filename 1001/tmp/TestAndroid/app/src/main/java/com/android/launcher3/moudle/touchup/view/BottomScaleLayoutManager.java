package com.android.launcher3.moudle.touchup.view;

import static androidx.core.view.ViewCompat.dispatchNestedScroll;
import static androidx.core.view.ViewCompat.hasNestedScrollingParent;
import static androidx.core.view.ViewCompat.isNestedScrollingEnabled;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by pengmin on 2024/8/31.
 */

public class BottomScaleLayoutManager extends LinearLayoutManager {

    private static final String TAG = "Over";

    private @FloatRange(from = 0.01, to = 1.0)
    float edgePercent = 0.15f;//触发边缘动画距离百分比

    private @FloatRange(from = 1)
    float slowTimes = 3;//到达此距离后放慢倍数

    private int orientation = OrientationHelper.VERTICAL;
    private boolean offsetUseful = false;
    private int overDist;
    private int totalHeight = 0;
    private int verticalScrollOffset;
    private boolean topOver;
    private int viewWidth, viewHeight;

    public BottomScaleLayoutManager(Context context) {
        this(context,OrientationHelper.VERTICAL);
    }

    public BottomScaleLayoutManager(Context context,int orientation) {
        this(context,orientation, true);
    }

    public BottomScaleLayoutManager(Context context, int orientation, boolean topOver) {
        super(context);
        this.orientation = orientation;
        this.topOver = topOver;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        // 先把所有的View先从RecyclerView中detach掉，然后标记为"Scrap"状态，表示这些View处于可被重用状态(非显示中)。
        // 实际就是把View放到了Recycler中的一个集合中。
        if (getItemCount() == 0) {//没有Item
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout()) {
            return;
        }
        reset();
        detachAndScrapAttachedViews(recycler);
        calculateChildrenSite(recycler, state);
    }

    private void reset() {
        totalHeight = 0;
        if (!offsetUseful) {
            verticalScrollOffset = 0;
        }
        offsetUseful = false;
    }

    private void calculateChildrenSite(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (orientation == OrientationHelper.VERTICAL) {
            calculateChildrenSiteVertical(recycler, state);
            addAndLayoutViewVertical(recycler, state, verticalScrollOffset);
        }

    }

    private void calculateChildrenSiteVertical(RecyclerView.Recycler recycler, RecyclerView.State state) {
        View view = recycler.getViewForPosition(0);//暂时这么解决，不能layout出所有的子View
        measureChildWithMargins(view, 0, 0);
        calculateItemDecorationsForChild(view, new Rect());
        viewHeight = getDecoratedMeasuredHeight(view);
        overDist = (int) (slowTimes * viewHeight);
        totalHeight = getItemCount() * viewHeight;//+getDecoratedMeasuredHeight(view0);
        //Log.d(TAG, "childCountI = " + getChildCount() + "  itemCount= " + recycler.getScrapList().size());
    }

    /*@Override
    public boolean canScrollHorizontally() {
        // 返回true表示可以横向滑动
        return orientation == OrientationHelper.HORIZONTAL;
    }*/

    @Override
    public boolean canScrollVertically() {
        super.canScrollVertically();
        // 返回true表示可以纵向滑动
        return orientation == OrientationHelper.VERTICAL;
    }
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.scrollVerticallyBy(dy,recycler,state);

        int tempDy = dy;
            //列表向下滚动dy为正，列表向上滚动dy为负，这点与Android坐标系保持一致。
            if (verticalScrollOffset <= totalHeight - getVerticalSpace()) {
                verticalScrollOffset += dy;
                //将竖直方向的偏移量+travel
            }
            if (verticalScrollOffset > totalHeight - getVerticalSpace()) {
                verticalScrollOffset = totalHeight - getVerticalSpace();
                tempDy = 0;
            } else if (verticalScrollOffset < 0) {
                verticalScrollOffset = 0;
                tempDy = 0;
            }

            detachAndScrapAttachedViews(recycler);
            addAndLayoutViewVertical(recycler, state, verticalScrollOffset); //从新布局位置、显示View
        return tempDy;
    }

    private void addAndLayoutViewVertical(RecyclerView.Recycler recycler, RecyclerView.State state, int offset) {
        int itemCount = getItemCount();
        if (itemCount <= 0 || state.isPreLayout()) {
            return;
        }
        int displayHeight = getVerticalSpace();
        //float percent = 1 - edgePercent;
        float scale = 1.0f;
        float alpha = 1.0f;
        for (int i = itemCount - 1; i >= 0; i--) {
            // 遍历Recycler中保存的View取出来
            int bottomOffset = (i + 1) * viewHeight - offset;
            int topOffset = i * viewHeight - offset;
            boolean needAdd = true;
            if (bottomOffset - displayHeight >= overDist) {
                needAdd = false;
            }
            if (needAdd) {
                View view = recycler.getViewForPosition(i);
                int interval = view.getHeight() / 6;
                addView(view); // 因为刚刚进行了detach操作，所以现在可以重新添加
                measureChildWithMargins(view, 0, 0); // 通知测量view的margin值
                int width = getDecoratedMeasuredWidth(view); // 计算view实际大小，包括了ItemDecorator中设置的偏移量。
                int height = getDecoratedMeasuredHeight(view);
                //调用这个方法能够调整ItemView的大小，以除去ItemDecorator。
                calculateItemDecorationsForChild(view, new Rect());
                view.setScaleX(scale);
                view.setScaleY(scale);
                view.setAlpha(alpha);
                int realBottomOffset = bottomOffset;
                if (displayHeight - bottomOffset- ((height) * edgePercent) <= height * edgePercent) {
                    if(i != itemCount - 1) {
                        adjustScale(view, slowTimes, getChildCount(), i, true);
                    }else{
                        adjustScale(view, slowTimes, getChildCount(), i, false);
                    }
                }
                layoutDecoratedWithMargins(view, 0, realBottomOffset - height, width, realBottomOffset);
                //layoutDecoratedWithMargins(view, 0, realBottomOffset, width, realBottomOffset+ height);
            }
        }
        Log.d(TAG, "childCount = " + getChildCount() + "  itemCount= " + itemCount);
    }

    private void adjustScale(View itemView, float slowTimes,int cc,int i,boolean flag) {
        float scale = 1.0f;
        float alpha = 1.0f;
        scale -= (slowTimes-cc+2)* 0.1f;
        alpha -= (slowTimes-cc+2)*0.15f;
        if (scale <0.0f) {
            scale = 0.0f;
        }
        if (alpha < 0.0f) {
            alpha = 0.0f;
        }
            if(flag) {
                itemView.setScaleX(scale);
                itemView.setScaleY(scale);
            }else{
                itemView.setScaleX(0.9f);
                itemView.setScaleY(0.9f);
            }
            itemView.setAlpha(alpha);
    }


    private int getVerticalSpace() {
        // 计算RecyclerView的可用高度，除去上下Padding值
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }


    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


    public void setEdgePercent(@FloatRange(from = 0.01, to = 1.0) float edgePercent) {
        this.edgePercent = edgePercent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View findViewByPosition(int position) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return null;
        }
        final int firstChild = getPosition(getChildAt(0));
        final int viewPosition = position - firstChild;
        if (viewPosition >= 0 && viewPosition < childCount) {
            final View child = getChildAt(viewPosition);
            if (getPosition(child) == position) {
                return child; // in pre-layout, this may not match
            }
        }
        return super.findViewByPosition(position);
    }

    @Override
    public void scrollToPosition(int position) {

        offsetUseful = true;
        if (orientation == OrientationHelper.VERTICAL && viewHeight != 0) {
            verticalScrollOffset = position * viewHeight;
        }
        // Log.i(TAG, "=========position:" + position);
        requestLayout();
    }

    public void setSlowTimes(@IntRange(from = 1) int slowTimes) {
        this.slowTimes = slowTimes;
    }


}

