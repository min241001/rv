package com.renny.contractgridview.adapter;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by pengmin on 2024/8/31.
 */

public class OverlayLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "Over";

    private @FloatRange(from = 0.01, to = 1.0)
    float edgePercent = 0.2f;//触发边缘动画距离百分比

    private @FloatRange(from = 1)
    float slowTimes = 3;//到达此距离后放慢倍数

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

    public OverlayLayoutManager() {
        this(OrientationHelper.VERTICAL);
    }

    public OverlayLayoutManager(int orientation) {
        this(orientation, true);
    }

    public OverlayLayoutManager(int orientation, boolean topOver) {
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
        calculateChildrenSite(recycler, state);
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

    private void calculateChildrenSite(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (orientation == OrientationHelper.VERTICAL) {
            calculateChildrenSiteVertical(recycler, state);
            addAndLayoutViewVertical(recycler, state, verticalScrollOffset);
        } else {
            calculateChildrenSiteHorizontal(recycler, state);
            addAndLayoutViewHorizontal(recycler, state, horizontalScrollOffset);
        }

    }

    private void calculateChildrenSiteVertical(RecyclerView.Recycler recycler, RecyclerView.State state) {
        View view = recycler.getViewForPosition(0);//暂时这么解决，不能layout出所有的子View
        measureChildWithMargins(view, 0, 0);
        calculateItemDecorationsForChild(view, new Rect());
        viewHeight = getDecoratedMeasuredHeight(view);
        overDist = (int) (slowTimes * viewHeight);
        //overDist = (int) (viewHeight);
        totalHeight = getItemCount() * viewHeight;//+getDecoratedMeasuredHeight(view0);
        //Log.d(TAG, "childCountI = " + getChildCount() + "  itemCount= " + recycler.getScrapList().size());
    }

    private void calculateChildrenSiteHorizontal(RecyclerView.Recycler recycler, RecyclerView.State state) {
        View view0 = recycler.getViewForPosition(0);
        View view = recycler.getViewForPosition(1);//暂时这么解决，不能layout出所有的子View
        measureChildWithMargins(view0, 0, 0);
        calculateItemDecorationsForChild(view0, new Rect());
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
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.scrollVerticallyBy(dy,recycler,state);
        //列表向下滚动dy为正，列表向上滚动dy为负，这点与Android坐标系保持一致。
        int tempDy = dy;

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

    private void addAndLayoutViewVertical(RecyclerView.Recycler recycler, RecyclerView.State state, int offset) {
        int itemCount = getItemCount();
        if (itemCount <= 0 || state.isPreLayout()) {
            return;
        }
        int displayHeight = getVerticalSpace();
        //float percent = 1 - edgePercent;
        float scale = 1.0f;
        float alpha = 1.0f;
        float interval = 0.0f;
        float interval2= 0.0f;
        for (int i = itemCount - 1; i >= 0; i--) {
            // 遍历Recycler中保存的View取出来
            int bottomOffset = (i + 1) * viewHeight - offset;
            int topOffset = i * viewHeight - offset;
            boolean needAdd = true;
            if (bottomOffset - displayHeight >= overDist) {
                needAdd = false;
            }
            if (topOffset < -overDist && i != 0 && topOver
                    || topOffset < -overDist && !topOver) {
                needAdd = false;
            }

            if (needAdd) {
                View view = recycler.getViewForPosition(i);
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

                //if (i != itemCount - 1) {//除最后一个外的底部慢速动画
                    if (displayHeight - bottomOffset <= height * 0.2) {
                        //int edgeDist = (int) (displayHeight - height * edgePercent);//减去比例
                        //int bottom = (int) (edgeDist + (bottomOffset - edgeDist) / slowTimes);//

                        //显示高度-view高度//相当于一个item视图
                        Log.i(TAG, "displayheight:" + displayHeight);
                        Log.i(TAG, "height:" + height);
                        Log.i(TAG, "i:" + i);
                        //int edgeDist = (int) (displayHeight - height * (getChildCount()*0.1));//减去比例
                        //int bottom = (int) (edgeDist + (bottomOffset - edgeDist) / slowTimes );
                       // Log.i(TAG, "edgeDist:" + edgeDist);
                        //Log.i(TAG, "bottom:" + bottom);
                        Log.i(TAG, "bottomOffset:" + bottomOffset);
                        //bottom = Math.min(bottom, displayHeight);
                        int bottom = (int)(displayHeight);
                        interval+=0.15;
                        interval2+=0.1;
                        if((displayHeight - bottomOffset <= height * 0.2)&&(displayHeight - bottomOffset >= height * 0.05)){
                            //bottom =(int)(displayHeight-height * (getChildCount()*0.05));
                            //bottom =(int)(displayHeight-height *interval);

                        }
                        if(displayHeight - bottomOffset < height * 0.05){
                            //int edgeDist = (int) (displayHeight - height * (getChildCount()*0.1));
                            //bottom = (int) (edgeDist + (bottomOffset - edgeDist) /slowTimes );///
                           // bottom =(int)(displayHeight-height * (getChildCount()*0.2));

                            //bottom =(int)(displayHeight-height-bottomOffset);//
                            //bottom =(int)(displayHeight-height *interval);
                        }
                        //bottom =(int)(displayHeight);
                        bottom =(int)(displayHeight-height *interval);
                        realBottomOffset = bottom;
                        adjustScale(view, interval,slowTimes, getChildCount(),i,true);
                    } else {
                       view.setScaleX(1.0f);
                    }


                /*} else {
                    realBottomOffset = totalHeight > displayHeight ? displayHeight : totalHeight;
                    if (i == itemCount - 1) {
                        adjustScale(view,  slowTimes, getChildCount(),i,false);
                    } else {
                        view.setScaleX(1.0f);
                    }
                }*/
                //realBottomOffset = realBottomOffset-interval*(itemCount-i);
                layoutDecoratedWithMargins(view, 0, realBottomOffset - height, width, realBottomOffset);
            }
        }
        Log.d(TAG, "childCount = " + getChildCount() + "  itemCount= " + itemCount);
    }

    private void adjustScale(View itemView, float interval,float slowTimes,int cc,int i,boolean flag) {
        Log.i(TAG, "----------->i:" + i);
        Log.i(TAG, "-------------->getChildItemCount:" + cc);
        float scale = 1.0f;
        float alpha = 1.0f;
        scale -= (slowTimes-cc+1)* 0.15f;
        alpha -= (slowTimes-cc+1)*0.15f;
        //scale -= interval2;
        //alpha -= interval2;
        if (scale <0.7f) {
            scale = 0.7f;
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

