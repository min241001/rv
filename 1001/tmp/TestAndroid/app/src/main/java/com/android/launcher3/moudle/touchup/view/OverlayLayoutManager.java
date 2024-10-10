package com.android.launcher3.moudle.touchup.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.touchup.view.ViewInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by pengmin on 2024/8/31.
 */

public class OverlayLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "Over";

    private @FloatRange(from = 0.01, to = 1.0)
    float edgePercent = 0.1f;//触发边缘动画距离百分比

    private @FloatRange(from = 1)
    float slowTimes = 1.60f;//到达此距离后放慢倍数
    float layer_offset = 0.24f;//层距

    private int orientation = OrientationHelper.VERTICAL;
    private boolean offsetUseful = false;
    private int overDist;
    private int totalHeight = 0;
    private int totalWidth = 0;
    private int verticalScrollOffset;
    private int horizontalScrollOffset;

    //头部是否也要层叠，默认需要
    private boolean topOver;
    private int viewWidth, viewHeight;

    public OverlayLayoutManager(Context context) {
        this(context, OrientationHelper.VERTICAL);
    }

    public OverlayLayoutManager(Context context, int orientation) {
        this(context, orientation, true);
    }

    public OverlayLayoutManager(Context context, int orientation, boolean topOver) {
        // super(context);
        this.orientation = orientation;
        this.topOver = topOver;
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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
        CalcChildView(recycler, state, getChildCount(), false);
    }

    private void reset() {
        totalHeight = 0;
        totalWidth = 0;
        if (!offsetUseful) {
            verticalScrollOffset = 0;
            horizontalScrollOffset = 0;
        }
        offsetUseful = false;
    }


    private void CalcChildView(RecyclerView.Recycler recycler, RecyclerView.State state, int index, boolean flag) {

        if (orientation == OrientationHelper.VERTICAL) {
            calculateChildrenSiteVertical(recycler, state, index, flag);
            addAndLayoutViewVertical(recycler, state, verticalScrollOffset);
        } else {
            calculateChildrenSiteHorizontal(recycler, state, index);
            addAndLayoutViewHorizontal(recycler, state, horizontalScrollOffset);
        }

    }

    public static HashMap<Integer, ViewInfo> hs = new HashMap<Integer, ViewInfo>();

    private void calculateChildrenSiteVertical(RecyclerView.Recycler recycler, RecyclerView.State state, int index, boolean scrollFlag) {
        if (!hs.isEmpty() && hs.containsKey(index)) {
            ViewInfo vi = hs.get(index);
            viewHeight = vi.height;
            overDist = vi.overDist;
            //overDist = (int) (viewHeight);
            totalHeight = vi.totalHeight;
        } else {
            View view = recycler.getViewForPosition(index);
            measureChildWithMargins(view, 0, 0);
            calculateItemDecorationsForChild(view, new Rect());
            viewHeight = getDecoratedMeasuredHeight(view);
            overDist = (int) (slowTimes * viewHeight);
            //overDist = (int) (viewHeight);
            totalHeight = getItemCount() * viewHeight;//+getDecoratedMeasuredHeight(view0);
            //Log.d(TAG, "childCountI = " + getChildCount() + "  itemCount= " + recycler.getScrapList().size());
            ViewInfo vi = new ViewInfo();
            vi.position = index;
            vi.height = viewHeight;
            vi.overDist = overDist;
            vi.totalHeight = totalHeight;//+getDecoratedMeasuredHeight(view0);
            if (!hs.containsKey(index)) {
                hs.put(index, vi);
            }

        }
    }

    private void calculateChildrenSiteHorizontal(RecyclerView.Recycler recycler, RecyclerView.State state, int index) {
        LogUtil.i(Constants.tm, "calculateChildrenSiteHorizontal");
        View view = recycler.getViewForPosition(index);
        measureChildWithMargins(view, 0, 0);
        calculateItemDecorationsForChild(view, new Rect());
        viewWidth = getDecoratedMeasuredWidth(view);
        overDist = (int) (slowTimes * viewWidth);
        //overDist =  viewWidth;
        totalWidth = getItemCount() * viewWidth;
        //Log.d(TAG, "childCountI = " + getChildCount() + "  itemCount= " + recycler.getScrapList().size());
    }

    @Override
    public boolean canScrollHorizontally() {
        // 返回true表示可以横向滑动
        return orientation == OrientationHelper.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        super.canScrollVertically();
        // 返回true表示可以纵向滑动
        return orientation == OrientationHelper.VERTICAL;
    }

    /*public boolean canScrollVertically(int direction) {
       final int offset = computeVerticalScrollOffset();
       final int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
       if (range == 0) return false;
       if (direction < 0) {
           return offset > 0;
       } else {
           return offset < range - 1;
       }
   }*/
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.scrollVerticallyBy(dy, recycler, state);
        //列表向下滚动dy为正，列表向上滚动dy为负，这点与Android坐标系保持一致。
        int tempDy = dy;
        //int index = getItemCount()-(getChildCount() - 1);
        int index = getChildCount() - 1;
        LogUtil.i(Constants.tm, "index:" + index + "   dy:" + dy);
        LogUtil.i(Constants.tm, "height:" + viewHeight + "   totalHeight:" + totalHeight);
        CalcChildView(recycler, state, index, true);
        ////calculateChildrenSite(recycler, state);
        if (verticalScrollOffset <= totalHeight - getVerticalSpace()) {
            verticalScrollOffset += dy;
            horizontalScrollOffset += dy;
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

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

        int tempDx = dx;
        if (horizontalScrollOffset <= totalWidth - getHorizontalSpace()) {
            horizontalScrollOffset += dx / 8;
            //将竖直方向的偏移量+travel
        }
        if (horizontalScrollOffset > totalWidth - getHorizontalSpace()) {
            horizontalScrollOffset = totalWidth - getHorizontalSpace();
            tempDx = 0;
        } else if (horizontalScrollOffset < 0) {
            horizontalScrollOffset = 0;
            tempDx = 0;
        }
        detachAndScrapAttachedViews(recycler);
        addAndLayoutViewHorizontal(recycler, state, horizontalScrollOffset); //从新布局位置、显示View
        return tempDx;

    }

    public static HashMap<Integer, ViewInfo> hv = new HashMap<Integer, ViewInfo>();

    private void addAndLayoutViewVertical(RecyclerView.Recycler recycler, RecyclerView.State state, int offset) {

        int itemCount = getItemCount();
        if (itemCount <= 0 || state.isPreLayout()) {
            return;
        }
        int displayHeight = getVerticalSpace(0);
        //float percent = 1 - edgePercent;
        float scale = 1.0f;
        float alpha = 1.0f;
        float interval = 0.0f;
        float interval2 = 0.0f;
        View view0 = recycler.getViewForPosition(0);
        measureChildWithMargins(view0, 0, 0); // 通知测量view的margin值
        //调用这个方法能够调整ItemView的大小，以除去ItemDecorator。
        calculateItemDecorationsForChild(view0, new Rect());
        int width0 = getDecoratedMeasuredWidth(view0); // 计算view实际大小，包括了ItemDecorator中设置的偏移量。
        int height0 = getDecoratedMeasuredHeight(view0);
        removeAllViews();
        for (int i = itemCount - 1; i >= 0; i--) {
            int width = 0; // 计算view实际大小，包括了ItemDecorator中设置的偏移量。
            int height = 0;
            int tHeight = 0;
            int tod = 0;
            View view;
            if (hv.containsKey(i)) {
                ViewInfo vi = hv.get(i);
                width = vi.width;
                height = vi.height;
                tHeight = vi.totalHeight;
                tod = vi.overDist;
                view = vi.v;
                //// view = recycler.getViewForPosition(i);
            } else {
                view = recycler.getViewForPosition(i);
                measureChildWithMargins(view, 0, 0); // 通知测量view的margin值
                //调用这个方法能够调整ItemView的大小，以除去ItemDecorator。
                calculateItemDecorationsForChild(view, new Rect());
                width = getDecoratedMeasuredWidth(view); // 计算view实际大小，包括了ItemDecorator中设置的偏移量。
                height = getDecoratedMeasuredHeight(view);
                tod = (int) (slowTimes * viewHeight);
                ViewInfo vi = new ViewInfo();
                vi.position = i;
                vi.height = height;
                vi.width = width;
                vi.overDist = tod;
                //vi.totalHeight = tHeight;//+getDecoratedMeasuredHeight(view0);
                vi.totalHeight = height * (i) + height0;
                if (i == 0) {
                    // vi.totalHeight = height0;
                }
                tHeight = vi.totalHeight;
                vi.v = view;
                removeView(vi.v);
                hv.put(i, vi);
            }
            // 遍历Recycler中保存的View取出来
            int bottomOffset = height * (i) + height0 - offset;
            int topOffset = (i - 1) * height + height0 - offset;
            boolean needAdd = true;
            if (bottomOffset - displayHeight >= tod) {
                needAdd = false;
            }
            if (topOffset < -tod && i != 0 && topOver
                    || topOffset < -tod && !topOver) {
                needAdd = false;
            }

            if (needAdd) {
                /*if(recycler.getViewForPosition(i)!=null) {
                    removeView(view);
                }*/
                addView(view); // 因为刚刚进行了detach操作，所以现在可以重新添加
                view.setScaleX(scale);
                view.setScaleY(scale);
                view.setAlpha(alpha);
                int realBottomOffset = bottomOffset;
                //
                if (topOver) {
                    if (i != 0) {//除第一个外的顶部黏性动画
                        if (topOffset <= height * edgePercent) {//到达顶部边界了
                            int edgeDist = (int) (height * edgePercent);//边界触发距离
                            viewWidth = (int) (viewWidth + edgePercent);
                            int top = (int) (edgeDist - (edgeDist - topOffset) / slowTimes);//到达边界后速度放慢到原来5分之一
                            top = Math.max(top, 0);
                            realBottomOffset = top + height;
                        }
                    } else {
                        realBottomOffset = height;
                    }
                }
                interval2 += layer_offset;
                if (interval2 > 1) {
                    interval2 = 1;
                }
                if (i != itemCount - 1) {//除最后一个外的底部慢速动画
                    if ((displayHeight - bottomOffset) - ((height) * interval2 / 1.5) <= (height * layer_offset)) {
                        interval += layer_offset;
                        if (interval > 1) {
                            interval = 1;
                        }
                        //显示高度-view高度//相当于一个item视图
                        //StringUtil.i(TAG, "displayheight:" + displayHeight);
                        //StringUtil.i(TAG, "height:" + height);
                        //StringUtil.i(TAG, "i:" + i);
                        //StringUtil.i(TAG, "bottomOffset:" + bottomOffset);
                        realBottomOffset = (int) (displayHeight - height * interval);
                        adjustScale(view, scale, alpha, slowTimes, getChildCount(), i, true);
                    } else {
                        view.setScaleX(1.0f);
                    }
                } else {
                    realBottomOffset = tHeight > displayHeight ? displayHeight : tHeight;
                    //realBottomOffset = tHeight;
                    if (i == itemCount - 1) {
                        adjustScale(view, scale, alpha, slowTimes, getChildCount(), i, false);
                    } else {
                        view.setScaleX(1.0f);
                    }
                }
                layoutDecoratedWithMargins(view, 0, realBottomOffset - height, width, realBottomOffset);
            }
        }
        Log.d(TAG, "childCount = " + getChildCount() + "  itemCount= " + itemCount);
    }

    private void adjustScale(View itemView, float scale, float alpha, float slowTimes, int ChildCount, int i, boolean flag) {
        LogUtil.i("oscale", "ChildCount = " + ChildCount + "  i " + i);
        scale = 1.0f;
        alpha = 1.0f;
        scale += (ChildCount - 3) * 0.15f;
        alpha += (ChildCount - 3) * 0.20f;

        if (scale > 0.8f) {
            //scale = 1.0f;
        }
        if (scale < 0.0f) {
            scale = 0.0f;
        }
        if (scale > 1.0f) {
            scale = 1.0f;
        }
        if (alpha < 0.0f) {
            alpha = 0.0f;
        }
        if (alpha > 1.0f) {
            alpha = 1.0f;
        }

        if (flag) {
            itemView.setScaleX(scale);
            itemView.setScaleY(scale);
            itemView.setAlpha(alpha);
        } else {
            itemView.setScaleX(1.0f);
            itemView.setScaleY(1.0f);
            itemView.setAlpha(1.0f);
        }
    }

    private void addAndLayoutViewHorizontal(RecyclerView.Recycler recycler, RecyclerView.State state, int offset) {
        int itemCount = getItemCount();
        if (itemCount <= 0 || state.isPreLayout()) {
            return;
        }
        int displayWidth = getHorizontalSpace();

        for (int i = itemCount - 1; i >= 0; i--) {
            int rightOffset = (i + 1) * viewWidth - offset;
            int leftOffset = i * viewWidth - offset;
            boolean needAdd = true;
            if (rightOffset - displayWidth >= overDist) {
                needAdd = false;
            }
            if (leftOffset < -overDist && i != 0
                    || leftOffset < -overDist && !topOver) {
                needAdd = false;
            }
            if (needAdd) {
                // 遍历Recycler中保存的View取出来
                View view = recycler.getViewForPosition(i);
                addView(view); // 因为刚刚进行了detach操作，所以现在可以重新添加
                measureChildWithMargins(view, 0, 0); // 通知测量view的margin值
                int width = getDecoratedMeasuredWidth(view); // 计算view实际大小，包括了ItemDecorator中设置的偏移量。
                int height = getDecoratedMeasuredHeight(view);

                //调用这个方法能够调整ItemView的大小，以除去ItemDecorator。
                calculateItemDecorationsForChild(view, new Rect());
                int realRightOffset = rightOffset;
                if (topOver) {//除第一个外的左边缘慢速动画
                    if (i != 0) {
                        if (leftOffset <= width * edgePercent) {//到达边界了
                            int edgeDist = (int) (width * edgePercent);//边界触发距离
                            int left = (int) (edgeDist - (edgeDist - leftOffset) / slowTimes);///到达边界后速度放慢到原来5分之一
                            left = Math.max(0, left);
                            if (left < 0) {
                                left = 0;
                            }
                            realRightOffset = left + width;
                        }
                    } else {
                        realRightOffset = width;
                    }
                }
                if (i != itemCount - 1) {//除最后一个外的右边缘慢速动画
                    if (displayWidth - rightOffset <= width * edgePercent) {
                        int edgeDist = (int) (displayWidth - width * edgePercent);
                        int right = (int) (edgeDist + (rightOffset - edgeDist) / slowTimes);
                        if (right >= displayWidth) {
                            right = displayWidth;
                        }
                        realRightOffset = right;
                    }
                } else {
                    realRightOffset = totalWidth > displayWidth ? displayWidth : totalWidth;
                }
                layoutDecoratedWithMargins(view, realRightOffset - width, 0, realRightOffset, height);
            }
        }

        Log.d(TAG, "childCount = " + getChildCount() + "  itemCount= " + itemCount);
    }

    private int getVerticalSpace() {
        // 计算RecyclerView的可用高度，除去上下Padding值
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private int getVerticalSpace(int height) {
        // 计算RecyclerView的可用高度，除去上下Padding值
        return getHeight() - getPaddingBottom() - getPaddingTop() + 45;//+120
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
        } else if (orientation == OrientationHelper.HORIZONTAL && viewWidth != 0) {
            horizontalScrollOffset = position * viewWidth;
        }
        // Log.i(TAG, "=========position:" + position);
        requestLayout();
    }

    public void setSlowTimes(@IntRange(from = 1) int slowTimes) {
        this.slowTimes = slowTimes;
    }


}

