package com.renny.contractgridview.view.stack;


/**
 * Create by pengmin on 2024/9/6 .
 */

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
//import org.jetbrains.annotations.NotNull;
//import kotlin.jvm.internal.Intrinsics;

public final class StackLayoutManager extends RecyclerView.LayoutManager {
    private final String TAG = "StackCardLayoutTestManager";
    private final int mScrollDirection = 1;
    private int mChildHeight;
    private int mChildWidth;
    private int mBaseCenterX;
    private int mBaseCenterY;
    private int mBaseOffSetY = 50;
    private int mTotalScrollY;
    private int mCurrentPosition;
    private float mCurrentRatio;
    private final float mBaseAlpha = 1.0F;
    private final float mBaseScale = 1.0F;
    //private final float mOutCardScale = 0.8F;
    private final float mOutCardScale = 1.0F;//外部元素缩放量
    private final float mOutCardAlpha = 0f;
    private final float mBaseAlphaChange = 0.3F;
    private final float mBaseScaleChange = 0.05F;
    private final int mBaseOffSetYChange = 60;

    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }

    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //Intrinsics.checkNotNullParameter(recycler, "recycler");
        //Intrinsics.checkNotNullParameter(state, "state");
        this.initialize(recycler);
        //drawChildrenDefault(recycler, state, 0, 4, (Object) null);
    }

    public boolean canScrollHorizontally() {
        return this.mScrollDirection == 0;
    }

    public boolean canScrollVertically() {
        return this.mScrollDirection == 1;
    }

    private void initialize(RecyclerView.Recycler recycler) {
        this.detachAndScrapAttachedViews(recycler);
        View var3 = recycler.getViewForPosition(0);
        //Intrinsics.checkNotNullExpressionValue(var3, "recycler.getViewForPosition(0)");
        View itemView = var3;
        this.addView(itemView);
        this.measureChildWithMargins(itemView, 0, 0);
        this.mChildHeight = this.getDecoratedMeasuredHeight(itemView);
        this.mChildWidth = this.getDecoratedMeasuredWidth(itemView);
        this.mBaseCenterX = this.getWidth() / 2;
        this.mBaseCenterY = this.getHeight() / 2;
        this.detachAndScrapAttachedViews(recycler);
    }

    private int drawChildren2(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        this.detachAndScrapAttachedViews(recycler);
        this.mTotalScrollY -= dy;// * -1;
       if (this.mTotalScrollY >= 0) {
            this.mTotalScrollY = 0;
       }

        if (this.mTotalScrollY <= -(state.getItemCount() - 1) * this.mChildHeight) {
            this.mTotalScrollY = -(state.getItemCount() - 1) * this.mChildHeight;
        }

        this.mCurrentPosition = Math.abs(this.mTotalScrollY / this.mChildHeight);
        int offSetY = 0;
        float alpha = 1.0F;
        float scale = 1.0F;
        //当前元素
        this.mCurrentRatio = (float) 1 - Math.abs((float) (this.mTotalScrollY + this.mCurrentPosition * this.mChildHeight) / (float) this.mChildHeight);
        // 层叠数量
        int i = Math.min(this.mCurrentPosition + 4, state.getItemCount() - 1);
        int var8 = this.mCurrentPosition;
        //if (var8 <= i) {
            while (true) {
                if (i == this.mCurrentPosition) {
                    //offSetY = this.mTotalScrollY - -1 * this.mChildHeight * i;
                    offSetY = this.mTotalScrollY -1 * this.mChildHeight;//// * i;
                    alpha = this.mBaseAlpha;
                    scale = this.mOutCardScale + (this.mBaseScale - this.mOutCardScale) * this.mCurrentRatio;
                } else if (i < this.mCurrentPosition) {
                   //offSetY = this.mTotalScrollY - -1 * this.mChildHeight * i;
                   //alpha = this.mOutCardAlpha;
                    //scale = this.mOutCardScale;
                } else {
                    alpha = this.mBaseAlpha - this.mBaseAlphaChange * (float) (i - 1) - this.mCurrentRatio * this.mBaseAlphaChange + (float) this.mCurrentPosition * this.mBaseAlphaChange;
                    scale = this.mBaseScale - this.mBaseScaleChange * (float) (i - 1) - this.mCurrentRatio * this.mBaseScaleChange + (float) this.mCurrentPosition * this.mBaseScaleChange;
                    offSetY = (int) ((float) (this.mBaseOffSetYChange * (i - 1)) - this.mCurrentRatio * (float) this.mBaseOffSetYChange * (float) -1 + (float) (this.mCurrentPosition * this.mBaseOffSetYChange * -1));
                }

                View var10 = recycler.getViewForPosition(i);
                //Intrinsics.checkNotNullExpressionValue(var10, "recycler.getViewForPosition(i)");
                View view = var10;
                this.measureChildWithMargins(view, 0, 0);
                this.addView(view);
                this.layoutDecoratedWithMargins(view, this.mBaseCenterX - this.mChildWidth / 2, this.mBaseCenterY - this.mChildHeight / 2 + offSetY, this.mBaseCenterX + this.mChildWidth / 2, this.mBaseCenterY + this.mChildHeight / 2 + offSetY);
                view.setAlpha(alpha);
                view.setScaleX(scale);
                view.setScaleY(scale);
                if (i == var8) {
                    break;
                }

                --i;
          //  }
        }

        return this.mTotalScrollY != 0 && this.mTotalScrollY != -(state.getItemCount() - 1) * this.mChildHeight ? dy : 0;
    }

    // synthetic method
    private int drawChildrenDefault(RecyclerView.Recycler var1, RecyclerView.State var2, int var3, int var4, Object var5) {
        if ((var4 & 4) != 0) {
            var3 = 0;
        }

        return drawChildren2(var1, var2, var3);
    }

    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //Intrinsics.checkNotNullParameter(recycler, "recycler");
        //Intrinsics.checkNotNullParameter(state, "state");
        return this.drawChildren2(recycler, state, dy);
    }

    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //Intrinsics.checkNotNullParameter(recycler, "recycler");
        //Intrinsics.checkNotNullParameter(state, "state");
        return super.scrollHorizontallyBy(dx, recycler, state);
    }
}
