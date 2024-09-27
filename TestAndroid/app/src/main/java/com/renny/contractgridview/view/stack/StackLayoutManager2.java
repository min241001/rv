package com.renny.contractgridview.view.stack;

/*import android.content.res.Resources;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StackLayoutManager2 extends RecyclerView.LayoutManager {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
    private int mDecoratedChildWidth;
    private int mDecoratedChildHeight;
    private int mLeftMargin;
    private int mTopMargin;
    private int mBottomMargin;
    private int mMaxElementsInStack;
    private int mItemHeightInStackInPx;
    @NotNull
    private final SparseArray mViewCache = new SparseArray();
    @NotNull
    private ScrollState mScrollState;
    @NotNull
    private final SpringForce mSpringForce;
    @NotNull
    private final ArrayList mAnimations;
    private static final int ONE_ELEMENT_OFFSET = 1;
    private static final int ITEM_HEIGHT_IN_STACK_IN_DP = 20;
    private static final int RELATIVE_SCREEN_PART_TO_STACK = 6;

    public StackLayoutManager2() {
        this.mScrollState = StackLayoutManager2.ScrollState.SCROLL_NA;
        this.mSpringForce = new SpringForce();
        this.mAnimations = new ArrayList();
        this.mSpringForce.setDampingRatio(0.5F).setStiffness(200.0F);
    }

    private final View getAnchorView() {
        View view = null;
        int i = 0;

        for(int var3 = this.mViewCache.size(); i < var3; ++i) {
            if (this.getDecoratedTop((View)this.mViewCache.valueAt(i)) >= this.getBottomEdgeOfTopStack()) {
                view = (View)this.mViewCache.valueAt(i);
                break;
            }
        }

        return view;
    }

    private final View getFirstViewBeforeBottomStack() {
        View view = null;

        for(int i = this.mViewCache.size() - ONE_ELEMENT_OFFSET; -1 < i; --i) {
            if (this.getDecoratedTop((View)this.mViewCache.valueAt(i)) <= this.getTopEdgeOfBottomStack()) {
                view = (View)this.mViewCache.valueAt(i);
                break;
            }
        }

        return view;
    }

    private final int getBottomEdgeOfTopStack() {
        return this.getHeight() / RELATIVE_SCREEN_PART_TO_STACK;
    }

    private final int getTopStackSize() {
        int cnt = 0;
        int i = 0;

        for(int var3 = this.mViewCache.size(); i < var3; ++i) {
            View view = (View)this.mViewCache.valueAt(i);
            if (view.getTop() < this.getBottomEdgeOfTopStack()) {
                ++cnt;
            }
        }

        return cnt;
    }

    private final int getBottomStackSize() {
        int cnt = 0;
        int i = 0;

        for(int var3 = this.mViewCache.size(); i < var3; ++i) {
            View view = (View)this.mViewCache.valueAt(i);
            if (view.getTop() >= this.getTopEdgeOfBottomStack()) {
                ++cnt;
            }
        }

        return cnt;
    }

    private final int getTopEdgeOfBottomStack() {
        return this.getHeight() - this.getHeight() / RELATIVE_SCREEN_PART_TO_STACK;
    }

    @NotNull
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-1, -2);
    }

    public void onLayoutChildren(@Nullable RecyclerView.Recycler recycler, @Nullable RecyclerView.State state) {
        if (this.getItemCount() == 0) {
            if (recycler != null) {
                this.detachAndScrapAttachedViews(recycler);
            }

        } else {
            if (this.getChildCount() == 0) {
                Intrinsics.checkNotNull(state);
                if (state.isPreLayout()) {
                    return;
                }
            }

            if (this.getChildCount() == 0) {
                Intrinsics.checkNotNull(recycler);
                View var4 = recycler.getViewForPosition(0);
                Intrinsics.checkNotNullExpressionValue(var4, "recycler!!.getViewForPosition(0)");
                View scrap = var4;
                this.addView(scrap);
                this.measureChildWithMargins(scrap, 0, 0);
                this.mDecoratedChildWidth = this.getDecoratedMeasuredWidth(scrap);
                this.mDecoratedChildHeight = this.getDecoratedMeasuredHeight(scrap);
                Resources var7 = scrap.getResources();
                Intrinsics.checkNotNullExpressionValue(var7, "scrap.resources");
                this.mItemHeightInStackInPx = this.convertDpToPx(var7, ITEM_HEIGHT_IN_STACK_IN_DP);
                this.mMaxElementsInStack = this.getBottomEdgeOfTopStack() / this.mItemHeightInStackInPx;
                ViewGroup.LayoutParams var5 = scrap.getLayoutParams();
                Intrinsics.checkNotNull(var5, "null cannot be cast to non-null type androidx.recyclerview.widget.RecyclerView.LayoutParams");
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)var5;
                this.mLeftMargin = layoutParams.leftMargin;
                this.mTopMargin = layoutParams.topMargin;
                this.mBottomMargin = layoutParams.bottomMargin;
                this.detachAndScrapView(scrap, recycler);
                recycler.recycleView(scrap);
            }

            this.fillViewCache();
            int startViewPosition = this.getPaddingTop();
            int startAdapterPosition = -ONE_ELEMENT_OFFSET;
            if (recycler != null) {
                this.detachAndScrapAttachedViews(recycler);
            }

            if (this.mViewCache.size() != 0) {
                View view = this.getAnchorView();
                if (view != null) {
                    this.expandStack(view);
                    view = (View)this.mViewCache.valueAt(0);
                    Intrinsics.checkNotNull(view);
                    startViewPosition = this.getDecoratedTop(view) - this.mTopMargin;
                    startAdapterPosition = this.mViewCache.keyAt(0) - ONE_ELEMENT_OFFSET;
                }

                this.mViewCache.clear();
            }

            Intrinsics.checkNotNull(recycler);
            this.addItemsUpperAdapterPos(recycler, startAdapterPosition, startViewPosition);
            this.createTopStackScrollDown(recycler);
            this.createBottomStackScrollDown();
            this.attachViewCache();
            this.mScrollState = StackLayoutManager2.ScrollState.SCROLL_NA;
        }
    }

    public boolean canScrollVertically() {
        return true;
    }

    public int scrollVerticallyBy(int dy, @Nullable RecyclerView.Recycler recycler, @Nullable RecyclerView.State state) {
        int delta;
        if (this.getChildCount() == 0 && dy == 0) {
            return 0;
        } else if (!this.mAnimations.isEmpty()) {
            return dy;
        } else if (this.moveViewWhenScrollEnd(dy)) {
            return 0;
        } else {
            this.fillViewCache();
            if (dy > 0) {
                Intrinsics.checkNotNull(recycler);
                delta = this.scrollDown(dy, recycler);
                this.mScrollState = delta == 0 ? StackLayoutManager2.ScrollState.SCROLL_DOWN_END : StackLayoutManager2.ScrollState.SCROLL_NA;
            } else {
                Intrinsics.checkNotNull(recycler);
                delta = this.scrollUp(dy, recycler);
                this.mScrollState = delta == 0 ? StackLayoutManager2.ScrollState.SCROLL_UP_END : StackLayoutManager2.ScrollState.SCROLL_NA;
            }

            this.attachViewCache();
            return dy;
        }
    }

    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == 0) {
            this.animateAllMovedView();
        }

    }

    private final boolean moveViewWhenScrollEnd(int dy) {
        boolean result = false;
        if (this.mScrollState == StackLayoutManager2.ScrollState.SCROLL_UP_END && dy < 0) {
            this.moveWhenScrollUpEnd(dy);
            result = true;
        } else if (this.mScrollState == StackLayoutManager2.ScrollState.SCROLL_DOWN_END && dy > 0) {
            this.moveWhenScrollDownEnd(dy);
            result = true;
        } else {
            this.animateAllMovedView();
        }

        return result;
    }

    private final void moveWhenScrollDownEnd(int dy) {
        int topLimit = this.getPaddingTop() + this.mTopMargin + this.mItemHeightInStackInPx + this.mItemHeightInStackInPx / 2 * (this.getChildCount() - 2);

        for(int i = this.getChildCount() - ONE_ELEMENT_OFFSET; 0 < i; --i) {
            View view = this.getChildAt(i);
            Intrinsics.checkNotNull(view);
            if (this.isViewNeedToMove(view, i)) {
                this.calcAndSetTranslation(dy, topLimit, view);
            }

            if (i == this.getChildCount() - ONE_ELEMENT_OFFSET) {
                topLimit -= this.mItemHeightInStackInPx;
            } else {
                topLimit -= this.mItemHeightInStackInPx / 2;
            }
        }

    }

    private final void moveWhenScrollUpEnd(int dy) {
        int topLimit = this.getHeight() - this.mItemHeightInStackInPx - this.mItemHeightInStackInPx / 2 * (this.getChildCount() - ONE_ELEMENT_OFFSET);
        int i = 0;

        for(int var4 = this.getChildCount(); i < var4; ++i) {
            View view = this.getChildAt(i);
            if (view != null && this.isViewNeedToMove(view, i)) {
                this.calcAndSetTranslation(dy, topLimit, view);
            }

            if (i == 0) {
                topLimit += this.mItemHeightInStackInPx;
            } else {
                topLimit += this.mItemHeightInStackInPx / 2;
            }
        }

    }

    private final void calcAndSetTranslation(int dy, int topLimit, View view) {
        int currentTop = this.getDecoratedTop(view);
        int currentTranslation = Math.round(view.getTranslationY());
        int futureTranslation = currentTranslation - dy;
        int translation;
        if (dy < 0) {
            translation = currentTop + futureTranslation >= topLimit ? topLimit - currentTop : futureTranslation;
        } else {
            translation = currentTop + futureTranslation <= topLimit ? topLimit - currentTop : futureTranslation;
        }

        view.setTranslationY((float)translation);
    }

    private final void animateAllMovedView() {
        int i = 0;

        for(int var2 = this.getChildCount(); i < var2; ++i) {
            View view = this.getChildAt(i);
            if (view != null && view.getTranslationY() != 0.0F) {
                this.startReturnAnimation(view);
            }
        }

    }

    private final boolean isViewNeedToMove(View view, int childPosition) {
        boolean result = false;
        ScrollState var4 = this.mScrollState;
        int var5 = StackLayoutManager2.WhenMappings.EnumSwitchMapping0[var4.ordinal()];
        switch (var5) {
            case 1:
                result = true;
                if (childPosition < this.getChildCount() - ONE_ELEMENT_OFFSET) {
                    View var10001 = this.getChildAt(childPosition + ONE_ELEMENT_OFFSET);
                    Intrinsics.checkNotNull(var10001);
                    result = this.getDecoratedTop(var10001) - this.getDecoratedTop(view) >= this.mItemHeightInStackInPx;
                }
                break;
            case 2:
                result = true;
                if (childPosition > 0) {
                    int var10000 = this.getDecoratedTop(view);
                    View var10002 = this.getChildAt(childPosition - ONE_ELEMENT_OFFSET);
                    Intrinsics.checkNotNull(var10002);
                    result = var10000 - this.getDecoratedTop(var10002) >= this.mItemHeightInStackInPx;
                }
        }

        return result;
    }

    private final void startReturnAnimation(View view) {
        SpringAnimation springAnimation = new SpringAnimation(view, (FloatPropertyCompat)DynamicAnimation.TRANSLATION_Y, 0.0F);
        this.mSpringForce.setFinalPosition(0.0F);
        springAnimation.setSpring(this.mSpringForce);
        this.mAnimations.add(springAnimation);
        springAnimation.addEndListener(StackLayoutManager2::startReturnAnimationlambda0);
        springAnimation.start();
    }

    private final void fillViewCache() {
        this.mViewCache.clear();
        int i = 0;

        int var2;
        for(var2 = this.getChildCount(); i < var2; ++i) {
            View view = this.getChildAt(i);
            if (view != null) {
                int position = this.getPosition(view);
                this.mViewCache.put(position, view);
            }
        }

        i = 0;

        for(var2 = this.mViewCache.size(); i < var2; ++i) {
            this.detachView((View)this.mViewCache.valueAt(i));
        }

    }

    private final void attachViewCache() {
        int i = 0;

        for(int var2 = this.mViewCache.size(); i < var2; ++i) {
            this.attachView((View)this.mViewCache.valueAt(i));
        }

    }

    private final int scrollDown(int dy, RecyclerView.Recycler recycler) {
        int currentPosition;
        int delta = -dy;
        View view = this.getAnchorView();
        if (view == null) {
            return 0;
        } else {
            this.expandStack(view);
             currentPosition = this.mViewCache.keyAt(this.mViewCache.size() - ONE_ELEMENT_OFFSET);
            int baseItemDecoratedBottom;
            int i;
            if (this.mViewCache.keyAt(this.mViewCache.size() - ONE_ELEMENT_OFFSET) == this.getItemCount() - ONE_ELEMENT_OFFSET) {
                i = this.getDecoratedBottom((View)this.mViewCache.get(this.getItemCount() - ONE_ELEMENT_OFFSET)) + this.mBottomMargin;
                baseItemDecoratedBottom = i + delta;
                delta = baseItemDecoratedBottom - this.getHeight() >= 0 ? delta : this.getHeight() - i;
            }

            i = 0;

            for(baseItemDecoratedBottom = this.mViewCache.size(); i < baseItemDecoratedBottom; ++i) {
                ((View)this.mViewCache.valueAt(i)).offsetTopAndBottom(delta);
            }

            view = (View)this.mViewCache.get(currentPosition);
            baseItemDecoratedBottom = this.getDecoratedBottom(view);
            this.addItemsUpperAdapterPos(recycler, currentPosition, baseItemDecoratedBottom);
            this.createTopStackScrollDown(recycler);
            this.createBottomStackScrollDown();
            return delta;
        }
    }

    private final int scrollUp(int dy, RecyclerView.Recycler recycler) {
        int currentPosition;
        int delta = -dy;
        View view = this.getAnchorView();
        if (view == null) {
            return 0;
        } else {
            this.expandStack(view);
            currentPosition = this.mViewCache.keyAt(0);
            int baseItemDecoratedTop;
            int i;
            if (this.mViewCache.get(0) != null) {
                i = this.getDecoratedTop((View)this.mViewCache.get(0));
                baseItemDecoratedTop = i + delta;
                delta = this.mTopMargin + this.getPaddingTop() - baseItemDecoratedTop >= 0 ? delta : this.mTopMargin + this.getPaddingTop() - i;
            }

            i = 0;

            for(baseItemDecoratedTop = this.mViewCache.size(); i < baseItemDecoratedTop; ++i) {
                ((View)this.mViewCache.valueAt(i)).offsetTopAndBottom(delta);
            }

            view = (View)this.mViewCache.get(currentPosition);
            baseItemDecoratedTop = this.getDecoratedTop(view);
            this.addItemsLowerAdapterPos(recycler, currentPosition, baseItemDecoratedTop);
            this.createTopStackScrollUp();
            this.createBottomStackScrollUp(recycler);
            return delta;
        }
    }

    private final void createTopStackScrollUp() {
        int edgeLimit = this.mTopMargin + this.getPaddingTop();
        int startPosIdx = 0;
        int topStackSize = this.getTopStackSize();
        if (topStackSize > this.mMaxElementsInStack) {
            startPosIdx = topStackSize - this.mMaxElementsInStack;
        }

        int topEdge;
        for(int i = startPosIdx; -1 < i; --i) {
            topEdge = edgeLimit - this.getDecoratedTop((View)this.mViewCache.valueAt(i)) >= 0 ? edgeLimit - this.getDecoratedTop((View)this.mViewCache.valueAt(i)) : 0;
            ((View)this.mViewCache.valueAt(i)).offsetTopAndBottom(topEdge);
        }

        View anchorView = (View)this.mViewCache.valueAt(topStackSize);
        topEdge = this.getDecoratedTop(anchorView);
        edgeLimit = this.mTopMargin + this.getPaddingTop() + (Math.min(topStackSize, this.mMaxElementsInStack) - ONE_ELEMENT_OFFSET) * this.mItemHeightInStackInPx;
        edgeLimit = topEdge - edgeLimit > this.mItemHeightInStackInPx ? edgeLimit : topEdge - this.mItemHeightInStackInPx;
        int i = topStackSize - ONE_ELEMENT_OFFSET;
        int var7 = startPosIdx;
        if (startPosIdx <= i) {
            while(true) {
                int offset = edgeLimit - this.getDecoratedTop((View)this.mViewCache.valueAt(i)) >= 0 ? edgeLimit - this.getDecoratedTop((View)this.mViewCache.valueAt(i)) : 0;
                ((View)this.mViewCache.valueAt(i)).offsetTopAndBottom(offset);
                if (topStackSize <= this.mMaxElementsInStack) {
                    edgeLimit = this.mTopMargin + this.getPaddingTop() + (i - ONE_ELEMENT_OFFSET) * this.mItemHeightInStackInPx;
                } else {
                    edgeLimit = this.getDecoratedTop((View)this.mViewCache.valueAt(i)) - this.mItemHeightInStackInPx;
                }

                if (i == var7) {
                    break;
                }

                --i;
            }
        }

    }

    private final void createBottomStackScrollUp(RecyclerView.Recycler recycler) {
        int bottomStackSize = this.getBottomStackSize();
        int edgeLimit;
        int startPosIdx;
        View var6 = this.getFirstViewBeforeBottomStack();
        if (var6 != null) {
            int topEge = this.getDecoratedTop(var6);
            startPosIdx = this.mViewCache.indexOfValue(var6) + ONE_ELEMENT_OFFSET;
            int maxVisibleStackIdx = Math.min(startPosIdx + this.mMaxElementsInStack, this.mViewCache.size());
            edgeLimit = this.getHeight() - this.mItemHeightInStackInPx * (maxVisibleStackIdx - startPosIdx);
            edgeLimit = edgeLimit - topEge > this.mItemHeightInStackInPx ? edgeLimit : topEge + this.mItemHeightInStackInPx;

            int i;
            int currentTop;
            for(i = startPosIdx; i < maxVisibleStackIdx; ++i) {
                currentTop = this.getDecoratedTop((View)this.mViewCache.valueAt(i));
                int offset = edgeLimit - currentTop >= 0 ? 0 : edgeLimit - currentTop;
                ((View)this.mViewCache.valueAt(i)).offsetTopAndBottom(offset);
                if (bottomStackSize >= this.mMaxElementsInStack) {
                    edgeLimit = this.getDecoratedTop((View)this.mViewCache.valueAt(i)) + this.mItemHeightInStackInPx;
                } else {
                    edgeLimit = this.getHeight() - this.mItemHeightInStackInPx * (this.mViewCache.size() - i - ONE_ELEMENT_OFFSET);
                }
            }

            i = startPosIdx + this.mMaxElementsInStack;

            for(currentTop = this.mViewCache.size(); i < currentTop; ++i) {
                recycler.recycleView((View)this.mViewCache.valueAt(i));
                this.mViewCache.remove(this.mViewCache.keyAt(i));
            }

        }
    }

    private final void createBottomStackScrollDown() {
        View anchorView = null;
        int edgeLimit;
        int anchorPos;
        anchorView = this.getFirstViewBeforeBottomStack();
        if (anchorView != null) {
            anchorPos = this.getPosition(anchorView);
            SparseArray stack = new SparseArray();
            int prevItemBottom = this.getDecoratedBottom(anchorView) + this.mBottomMargin + this.mTopMargin;
            StackLayoutManager2 $this$createBottomStackScrollDown_u24lambda_u241 = (StackLayoutManager2)this;
            int var8;

            int currentTop;
            for(currentTop = anchorPos + ONE_ELEMENT_OFFSET; currentTop <= anchorPos + $this$createBottomStackScrollDown_u24lambda_u241.mMaxElementsInStack && currentTop < $this$createBottomStackScrollDown_u24lambda_u241.getItemCount(); ++currentTop) {
                stack.put(currentTop, $this$createBottomStackScrollDown_u24lambda_u241.mViewCache.get(currentTop));
            }

            edgeLimit = this.getHeight() - this.mItemHeightInStackInPx * stack.size();
            edgeLimit = edgeLimit - this.getDecoratedTop(anchorView) > this.mItemHeightInStackInPx ? edgeLimit : this.getDecoratedTop(anchorView) + this.mItemHeightInStackInPx;
            int bottomStackSize = this.getBottomStackSize();
            int i = 0;

            for(int var14 = stack.size(); i < var14; ++i) {
                currentTop = this.getDecoratedTop((View)stack.valueAt(i));
                int offset = edgeLimit - prevItemBottom >= 0 ? 0 : edgeLimit - currentTop;
                ((View)stack.valueAt(i)).offsetTopAndBottom(offset);
                if (bottomStackSize < this.mMaxElementsInStack) {
                    edgeLimit = this.getHeight() - this.mItemHeightInStackInPx * (stack.size() - i - ONE_ELEMENT_OFFSET);
                } else {
                    edgeLimit = this.getDecoratedTop((View)stack.valueAt(i)) + this.mItemHeightInStackInPx;
                }

                prevItemBottom = this.getDecoratedBottom((View)stack.valueAt(i));
            }
        }

    }

    private final void createTopStackScrollDown(RecyclerView.Recycler recycler) {
        View anchorView = null;
        int edgeLimit;
        anchorView = this.getAnchorView();
        if (anchorView != null) {
            int anchorPos = this.getPosition(anchorView);
            int firstPosInStack;
            int offset;
            int i;
            if (anchorPos > this.mMaxElementsInStack) {
                edgeLimit = this.mTopMargin + this.getPaddingTop();
                firstPosInStack = anchorPos - this.mMaxElementsInStack - ONE_ELEMENT_OFFSET;
                offset = edgeLimit - this.getDecoratedTop((View)this.mViewCache.get(firstPosInStack));
                ((View)this.mViewCache.get(firstPosInStack)).offsetTopAndBottom(offset);
                edgeLimit = this.getPaddingTop() + this.mTopMargin + this.mItemHeightInStackInPx * (this.mMaxElementsInStack - ONE_ELEMENT_OFFSET);
                offset = edgeLimit - this.getDecoratedTop((View)this.mViewCache.get(anchorPos - ONE_ELEMENT_OFFSET)) >= 0 ? edgeLimit - this.getDecoratedTop((View)this.mViewCache.get(anchorPos - ONE_ELEMENT_OFFSET)) : 0;
                ((View)this.mViewCache.get(anchorPos - ONE_ELEMENT_OFFSET)).offsetTopAndBottom(offset);
                i = anchorPos - 2;
                int var8 = firstPosInStack + 1;
                if (var8 <= i) {
                    while(true) {
                        edgeLimit = this.getDecoratedTop((View)this.mViewCache.get(i + ONE_ELEMENT_OFFSET)) - this.mItemHeightInStackInPx;
                        offset = edgeLimit - this.getDecoratedTop((View)this.mViewCache.get(i)) >= 0 ? edgeLimit - this.getDecoratedTop((View)this.mViewCache.get(i)) : 0;
                        ((View)this.mViewCache.get(i)).offsetTopAndBottom(offset);
                        if (i == var8) {
                            break;
                        }

                        --i;
                    }
                }

                for(i = 0; i < firstPosInStack; ++i) {
                    if (this.mViewCache.get(i) != null) {
                        recycler.recycleView((View)this.mViewCache.get(i));
                        this.mViewCache.remove(i);
                    }
                }
            } else {
                edgeLimit = this.mTopMargin + this.getPaddingTop();
                firstPosInStack = 0;

                for(offset = this.getTopStackSize(); firstPosInStack < offset; ++firstPosInStack) {
                    i = edgeLimit - this.getDecoratedTop((View)this.mViewCache.valueAt(firstPosInStack)) >= 0 ? edgeLimit - this.getDecoratedTop((View)this.mViewCache.valueAt(firstPosInStack)) : 0;
                    ((View)this.mViewCache.valueAt(firstPosInStack)).offsetTopAndBottom(i);
                    edgeLimit = this.getDecoratedTop((View)this.mViewCache.valueAt(firstPosInStack)) + this.mItemHeightInStackInPx;
                }
            }

        }
    }

    private final void addItemsUpperAdapterPos(RecyclerView.Recycler recycler, int currentAdapterPosition, int startViewPosition) {

        while(true) {
            ++currentAdapterPosition;
            if (currentAdapterPosition >= this.getItemCount()) {
                return;
            }

            if (startViewPosition < this.getHeight() + this.mMaxElementsInStack * this.mDecoratedChildHeight) {
                View var7 = recycler.getViewForPosition(currentAdapterPosition);
                Intrinsics.checkNotNullExpressionValue(var7, "recycler.getViewForPosit…n(currentAdapterPosition)");
                View view = var7;
                this.addView(view);
                this.measureChildWithMargins(view, 0, 0);
                int leftEdge = this.mLeftMargin;
                int rightEdge = leftEdge + this.mDecoratedChildWidth;
                int bottomEdge = this.mDecoratedChildHeight;
                int topEdge = startViewPosition + this.mTopMargin;
                this.layoutDecorated(view, leftEdge, topEdge, rightEdge, topEdge + bottomEdge);
                this.detachView(view);
                this.mViewCache.put(currentAdapterPosition, view);
                startViewPosition = topEdge + bottomEdge + this.mBottomMargin;
            }
        }
    }

    private final void addItemsLowerAdapterPos(RecyclerView.Recycler recycler, int currentAdapterPosition, int startViewPosition) {

        while(true) {
            --currentAdapterPosition;
            if (currentAdapterPosition < 0 || currentAdapterPosition >= this.getItemCount() || startViewPosition <= -this.mMaxElementsInStack * this.mDecoratedChildHeight) {
                return;
            }

            View var7 = recycler.getViewForPosition(currentAdapterPosition);
            Intrinsics.checkNotNullExpressionValue(var7, "recycler.getViewForPosit…n(currentAdapterPosition)");
            View view = var7;
            this.addView(view);
            this.measureChildWithMargins(view, 0, 0);
            int leftEdge = this.mLeftMargin;
            int rightEdge = leftEdge + this.mDecoratedChildWidth;
            int bottomEdge = startViewPosition - this.mBottomMargin;
            int topEdge = bottomEdge - this.mTopMargin - this.mDecoratedChildHeight;
            this.layoutDecorated(view, leftEdge, topEdge, rightEdge, bottomEdge);
            this.detachView(view);
            this.mViewCache.put(currentAdapterPosition, view);
            startViewPosition = topEdge + bottomEdge;
        }
    }

    private final void expandStack(View anchorView) {
        int baseItemDecoratedTop = this.getDecoratedTop(anchorView);

        int baseItemDecoratedBottom;
        int edge;
        int edge;
        for(baseItemDecoratedBottom = this.mViewCache.indexOfValue(anchorView) - ONE_ELEMENT_OFFSET; -1 < baseItemDecoratedBottom; --baseItemDecoratedBottom) {
            View prevView = (View)this.mViewCache.valueAt(baseItemDecoratedBottom);
            edge = baseItemDecoratedTop - this.mTopMargin;
            int prevViewBottom = this.getDecoratedBottom(prevView) + this.mBottomMargin;
            edge = edge - prevViewBottom;
            ((View)this.mViewCache.valueAt(baseItemDecoratedBottom)).offsetTopAndBottom(edge);
            baseItemDecoratedTop = this.getDecoratedTop(prevView);
        }

        baseItemDecoratedBottom = this.getDecoratedBottom(anchorView);
        int i = this.mViewCache.indexOfValue(anchorView) + ONE_ELEMENT_OFFSET;

        for(edge = this.mViewCache.size(); i < edge; ++i) {
            View nextView = (View)this.mViewCache.valueAt(i);
            edge = baseItemDecoratedBottom + this.mBottomMargin;
            int nextViewTop = this.getDecoratedTop(nextView) - this.mTopMargin;
            int offset = edge - nextViewTop;
            ((View)this.mViewCache.valueAt(i)).offsetTopAndBottom(offset);
            baseItemDecoratedBottom = this.getDecoratedBottom(nextView);
        }

    }

    private final int convertDpToPx(Resources resources, int dp) {
        return Math.round(TypedValue.applyDimension(1, (float)dp, resources.getDisplayMetrics()));
    }

    private static void startReturnAnimationlambda0(StackLayoutManager2 s, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        Intrinsics.checkNotNullParameter(s, "this$0");
        Collection var5 = (Collection)s.mAnimations;
        TypeIntrinsics.asMutableCollection(var5).remove(animation);
    }
    public static final class Companion {
        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    public static enum ScrollState {
        SCROLL_NA,
        SCROLL_DOWN_END,
        SCROLL_UP_END;

        // $FF: synthetic method
        private static final ScrollState[] $values() {
            ScrollState[] var0 = new ScrollState[]{SCROLL_NA, SCROLL_DOWN_END, SCROLL_UP_END};
            return var0;
        }
    }

    // $FF: synthetic class
    @Metadata(
            mv = {1, 7, 0},
            k = 3,
            xi = 48
    )
    public class WhenMappings {
        // $FF: synthetic field
        static int[] EnumSwitchMapping0= new int[StackLayoutManager2.ScrollState.values().length];


        EnumSwitchMapping0[StackLayoutManager2.ScrollState.SCROLL_DOWN_END.ordinal()] = 1;



        EnumSwitchMapping0[StackLayoutManager2.ScrollState.SCROLL_UP_END.ordinal()] = 2;


    }
}
*/