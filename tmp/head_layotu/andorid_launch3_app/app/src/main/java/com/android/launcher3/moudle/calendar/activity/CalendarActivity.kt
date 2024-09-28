package com.android.launcher3.moudle.calendar.activity

import android.content.Intent
import android.view.MotionEvent
import androidx.lifecycle.lifecycleScope
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseActivity
import com.android.launcher3.moudle.calendar.calendarview.Calendar
import com.android.launcher3.moudle.calendar.calendarview.CalendarView
import kotlinx.android.synthetic.main.activity_calendar.calendarView
import kotlinx.android.synthetic.main.activity_calendar.tv_calendar_year
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CalendarActivity : BaseActivity(), CalendarView.OnCalendarSelectListener,
    CalendarView.OnMonthChangeListener {

    private var isBack = true

    override fun getResourceId(): Int {
        return R.layout.activity_calendar
    }

    override fun initView() {
        val year = calendarView.curYear
        val month = calendarView.curMonth
        tv_calendar_year.text = getString(R.string.calendar_year, year, month)
        calenderNeedSlideBack = true
        swipeToExitEnabled = true
    }

    override fun initEvent() {
        calendarView.setOnCalendarSelectListener(this)
        calendarView.setOnMonthChangeListener(this)
    }

    override fun onBackPressed() {
        lifecycleScope.launch {
            delay(50)
            if (isBack) {
                super.onBackPressed()
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN &&
            event.x < 20f &&
            event.y > 200f
        ) {
            useGestureDetector(event)
            return true
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
    }

    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        if (isClick) {
            val intent = Intent(this, CalendarDetailsActivity::class.java)
            intent.putExtra("calendar", calendar)
            startActivity(intent)
        }
    }

    override fun onMonthChange(year: Int, month: Int) {
        isBack = false
        tv_calendar_year.text = getString(R.string.calendar_year, year, month)
        lifecycleScope.launch {
            delay(100)
            isBack = true
        }
    }
}