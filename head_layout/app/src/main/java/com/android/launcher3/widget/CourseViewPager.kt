package com.android.launcher3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

class CourseViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    private var mDownX: Int = 0
    private var mSlop: Int = 0

    init {
        mSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> mDownX = ev.x.toInt()
            MotionEvent.ACTION_MOVE -> {
                val dx = (ev.x - mDownX).toInt()
                if (abs(dx) >= mSlop) {
                    requestFocusFromTouch()
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mDownX = 0
        }

        return super.onInterceptTouchEvent(ev)
    }
}