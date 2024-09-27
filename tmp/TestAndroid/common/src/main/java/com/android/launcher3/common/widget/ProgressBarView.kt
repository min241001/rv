package com.android.launcher3.common.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import com.android.launcher3.common.R

class ProgressBarView : ProgressBar {

    private var mRadius = dp2px(30)
    private val DEFAULT_TEXT_COLOR = -0x3ff2f
    private val DEFAULT_COLOR_UNREACHED_COLOR = -0x2c2926
    private var mPaint: Paint = Paint()
    private var mReachedProgressBarHeight = dp2px(4)
    private var mReachedBarColor = DEFAULT_TEXT_COLOR
    private var mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR
    private var mUnReachedProgressBarHeight = mReachedProgressBarHeight
    private var mMaxPaintWidth = 0f
    private var mStart = 0f

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) :this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :super(context, attrs, defStyleAttr){
        obtainStyledAttributes(attrs)
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.strokeCap = Paint.Cap.ROUND
    }

    @Synchronized
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        mMaxPaintWidth = mReachedProgressBarHeight.coerceAtLeast(mUnReachedProgressBarHeight)
        val expect = (mRadius * 2 + mMaxPaintWidth + paddingLeft
                + paddingRight).toInt()
        val width = resolveSize(expect, widthMeasureSpec)
        val height = resolveSize(expect, heightMeasureSpec)
        val realWidth = width.coerceAtMost(height)
        mRadius = (realWidth - paddingLeft - paddingRight - mMaxPaintWidth) / 2
        setMeasuredDimension(realWidth, realWidth)
    }

    @SuppressLint("CustomViewStyleable")
    private fun obtainStyledAttributes(attrs: AttributeSet?) {
        val attributes: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ProgressBar
        )
        mReachedBarColor = attributes
            .getColor(
                R.styleable.ProgressBar_progress_reached_color,
                DEFAULT_COLOR_UNREACHED_COLOR
            )
        mUnReachedBarColor = attributes
            .getColor(
                R.styleable.ProgressBar_progress_unreached_color,
                DEFAULT_COLOR_UNREACHED_COLOR
            )
        mReachedProgressBarHeight = attributes
            .getDimension(
                R.styleable.ProgressBar_progress_reached_bar_height,
                mReachedProgressBarHeight
            )
        mUnReachedProgressBarHeight = attributes
            .getDimension(
                R.styleable.ProgressBar_progress_unreached_bar_height,
                mUnReachedProgressBarHeight
            )
        mRadius = attributes.getDimension(
            R.styleable.ProgressBar_progress_radius, mRadius
        )
        mStart = attributes.getFloat(
            R.styleable.ProgressBar_progress_start, mStart
        )

        attributes.recycle()
    }

    @SuppressLint("DrawAllocation")
    @Synchronized
    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(
            paddingLeft + mMaxPaintWidth / 2, paddingTop
                    + mMaxPaintWidth / 2
        )
        mPaint.style = Paint.Style.STROKE
        mPaint.color = mUnReachedBarColor
        mPaint.strokeWidth = mUnReachedProgressBarHeight
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint)
        mPaint.color = mReachedBarColor
        mPaint.strokeWidth = mReachedProgressBarHeight

        val sweepAngle = progress * 1.0f / max * 360
        canvas.drawArc(
            RectF(0f, 0f, (mRadius * 2), (mRadius * 2)), mStart,
            sweepAngle, false, mPaint
        )
        mPaint.style = Paint.Style.FILL
        canvas.restore()
    }

    private fun dp2px(dpVal: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal.toFloat(), resources.displayMetrics
        )
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun setProgressAnimation(progress: Int , end: () -> Unit) {
        var currentProgress = 0
        val animator = ObjectAnimator.ofInt(this, "progress", currentProgress, progress)
        animator.duration = 3000
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            currentProgress = animation.animatedValue as Int
            invalidate()
        }

        animator.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator?) {

            }

            override fun onAnimationEnd(animation: android.animation.Animator?) {
                end()
            }

            override fun onAnimationCancel(animation: android.animation.Animator?) {

            }

            override fun onAnimationRepeat(animation: android.animation.Animator?) {

            }
        })

        animator.start()
    }

    fun setReachedBarColor(color: Int) {
        mReachedBarColor = color
        invalidate()
    }

    fun setUnReachedBarColor(color: Int) {
        mUnReachedBarColor = color
    }
}