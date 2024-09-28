package com.android.launcher3.moudle.calendar.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class CustomMonthView(context: Context) : MonthView(context) {
    private val mProgressPaint = Paint()
    private val mNoneProgressPaint = Paint()
    private var mRadius = 0

    init {
        mProgressPaint.isAntiAlias = true
        mProgressPaint.style = Paint.Style.STROKE
        mProgressPaint.strokeWidth = dipToPx(context, 3f).toFloat()
        mProgressPaint.color = -0x440ab600
        mNoneProgressPaint.isAntiAlias = true
        mNoneProgressPaint.style = Paint.Style.STROKE
        mNoneProgressPaint.strokeWidth = dipToPx(context, 3f).toFloat()
        mNoneProgressPaint.color = -0x6f303031
    }

    override fun onPreviewHook() {
        mRadius = mItemWidth.coerceAtMost(mItemHeight) / 11 * 4 + 2
    }

    override fun onDrawSelected(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean {
        val cx = x + (mItemWidth / 2 + mItemWidth % 2)
        val cy = y + mItemHeight / 2
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSelectedPaint)
        return false
    }

    override fun onDrawCurrentDayTheme(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int
    ): Boolean {
        val cx = x + mItemWidth - mItemWidth / 5
        val cy = y + mItemHeight - mItemHeight / 6
        canvas.drawRoundRect(
            x.toFloat() + mItemWidth / 5, y.toFloat() + mItemHeight / 6, cx.toFloat(), cy.toFloat(),
            dipToPx(context, 8.0f).toFloat(), dipToPx(context, 8.0f).toFloat(),
            mCurDayThemePaint
        )
        return false
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        val angle = getAngle(calendar.scheme.toInt())
        val progressRectF = RectF(
            (cx - mRadius).toFloat(),
            (cy - mRadius).toFloat(),
            (cx + mRadius).toFloat(),
            (cy + mRadius).toFloat()
        )
        canvas.drawArc(progressRectF, -90f, angle.toFloat(), false, mProgressPaint)
        val noneRectF = RectF(
            (cx - mRadius).toFloat(),
            (cy - mRadius).toFloat(),
            (cx + mRadius).toFloat(),
            (cy + mRadius).toFloat()
        )
        canvas.drawArc(
            noneRectF,
            (angle - 90).toFloat(),
            (360 - angle).toFloat(),
            false,
            mNoneProgressPaint
        )
    }

    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2
        if (isSelected) {
            canvas.drawText(
                calendar.day.toString(),
                cx.toFloat(),
                baselineY,
                mSelectTextPaint
            )
        } else if (hasScheme) {
            canvas.drawText(
                calendar.day.toString(),
                cx.toFloat(),
                baselineY,
                if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mSchemeTextPaint else mOtherMonthTextPaint
            )
        } else {
            if (calendar.isWeekend) {
                canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), baselineY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mCurMonthWeekendTextPaint else mOtherMonthTextPaint
                )
            } else {
                canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), baselineY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mCurMonthTextPaint else mOtherMonthTextPaint
                )
            }
        }
    }

    companion object {
        private fun getAngle(progress: Int): Int {
            return (progress * 3.6).toInt()
        }

        private fun dipToPx(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}