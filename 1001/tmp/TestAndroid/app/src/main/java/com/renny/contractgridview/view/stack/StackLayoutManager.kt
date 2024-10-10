package com.android.launcher3.moudle.touchup.view;

import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by pengmin on 2024/9/6 .
 */

class StackCardLayoutTestManager : RecyclerView.LayoutManager() {
    private val TAG = "StackCardLayoutTestManager"

    //目前就只考虑纵向滑动了
    private val mScrollDirection = LinearLayout.VERTICAL

    //子视图的高宽
    private var mChildHeight = 0
    private var mChildWidth = 0

    //基准中心坐标
    private var mBaseCenterX = 0
    private var mBaseCenterY = 0

    //每个视图的偏移量
    private var mBaseOffSetY = 50

    //滑动的总距离
    private var mTotalScrollY = 0

    //当前的卡片位置
    private var mCurrentPosition = 0

    //当前卡片滑动的百分比
    private var mCurrentRatio = 0f

    //基础的透明度
    private val mBaseAlpha = 1.0f

    //基础的缩放值
    private val mBaseScale = 1.0f

    //当卡片滑动出去时的缩放值
    private val mOutCardScale = 0.8f

    //当卡片滑动出去时的透明度
    private val mOutCardAlpha = 0.0f

    //每张堆叠卡片的透明度变化
    private val mBaseAlphaChange = 0.3f

    //每张堆叠卡片的缩放变化
    private val mBaseScaleChange = 0.05f

    //每张卡片绘制时的Y轴位移
    private val mBaseOffSetYChange = 60

    //必须要实现此方法
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        initialize(recycler)
        drawChildren(recycler, state)
    }

    //是否能够水平滑动
    override fun canScrollHorizontally(): Boolean {
        return mScrollDirection == LinearLayoutManager.HORIZONTAL
    }

    //是否能够竖向滑动
    override fun canScrollVertically(): Boolean {
        return mScrollDirection == LinearLayoutManager.VERTICAL
    }

    private fun initialize(recycler: RecyclerView.Recycler) {
        //移除所绘制的所有view
        detachAndScrapAttachedViews(recycler)
        //这里视图里面默认每个子视图都是相同的高宽大小
        var itemView = recycler.getViewForPosition(0)
        addView(itemView)
        measureChildWithMargins(itemView, 0, 0)
        mChildHeight = getDecoratedMeasuredHeight(itemView)
        mChildWidth = getDecoratedMeasuredWidth(itemView)
        mBaseCenterX = width / 2
        mBaseCenterY = height / 2
        detachAndScrapAttachedViews(recycler)
    }

    private fun drawChildren(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        dy: Int = 0
    ): Int {
        detachAndScrapAttachedViews(recycler)
        //向上滑动，滑动距离为负数
        mTotalScrollY += dy * -1
        //第一张图禁止向下滑动
        if (mTotalScrollY >= 0) mTotalScrollY = 0
        //最后一张图禁止向上滑动
        if (mTotalScrollY <= -(state.itemCount - 1) * mChildHeight) mTotalScrollY =
            -(state.itemCount - 1) * mChildHeight
        mCurrentPosition = Math.abs(mTotalScrollY / mChildHeight)
        //偏移量
        var offSetY = 0
        //透明度
        var alpha = 1.0f
        //缩放大小
        var scale = 1.0f
        //百分比,当前卡片剩余进行长度占总长度的比例
        mCurrentRatio =
            1 - Math.abs((mTotalScrollY + mCurrentPosition * mChildHeight).toFloat() / mChildHeight.toFloat())
        /**
         * 这里后绘制的视图会重叠在先绘制的视图之上
         * 所以这里采用倒序，先绘制后面的视图，再绘制之前的
         * 关于回收问题，直接仅绘制所在位置以及之后的四张即可
         */
        for (i in Math.min(mCurrentPosition + 4, state.itemCount - 1) downTo mCurrentPosition) {
            if (i == mCurrentPosition) {
                offSetY = mTotalScrollY - -1 * mChildHeight * i
                alpha = mBaseAlpha
                scale = mOutCardScale + (mBaseScale - mOutCardScale) * mCurrentRatio
            } else if (i < mCurrentPosition) {
                offSetY = mTotalScrollY - -1 * mChildHeight * i
                alpha = mOutCardAlpha
                scale = mOutCardScale
            } else {
                alpha =
                    mBaseAlpha - mBaseAlphaChange * (i - 1) - mCurrentRatio * mBaseAlphaChange + mCurrentPosition * mBaseAlphaChange
                scale =
                    mBaseScale - mBaseScaleChange * (i - 1) - mCurrentRatio * mBaseScaleChange + (mCurrentPosition) * mBaseScaleChange
                offSetY =
                    (mBaseOffSetYChange * (i - 1) - mCurrentRatio * mBaseOffSetYChange * -1 + (mCurrentPosition) * mBaseOffSetYChange * -1).toInt()
            }
            var view = recycler.getViewForPosition(i)
            measureChildWithMargins(view, 0, 0)
            addView(view)
            layoutDecoratedWithMargins(
                view,
                mBaseCenterX - mChildWidth / 2,
                mBaseCenterY - mChildHeight / 2 + offSetY,
                mBaseCenterX + mChildWidth / 2,
                mBaseCenterY + mChildHeight / 2 + offSetY
            )

            view.alpha = alpha
            view.scaleX = scale
            view.scaleY = scale
        }
        //位置滑动到最底部的时候不再进行滑动处理,返回值为0
        return if (mTotalScrollY == 0 || mTotalScrollY == -(state.itemCount - 1) * mChildHeight) 0 else dy
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        return drawChildren(recycler, state, dy)
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        return super.scrollHorizontallyBy(dx, recycler, state)
    }
}


