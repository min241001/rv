package com.android.launcher3.moudle.calendar.activity

import com.android.launcher3.R
import com.android.launcher3.common.base.BaseActivity
import com.android.launcher3.moudle.calendar.calendarview.Calendar
import com.android.launcher3.moudle.calendar.calendarview.CalendarUtil
import com.android.launcher3.moudle.calendar.calendarview.TrunkBranchAnnals
import kotlinx.android.synthetic.main.activity_calendar_details.*

class CalendarDetailsActivity : BaseActivity() {

    private val calendar by lazy { intent.getSerializableExtra("calendar") as Calendar }

    override fun getResourceId(): Int {
        return R.layout.activity_calendar_details
    }

    override fun initView() {
        tv_year.text = "${calendar.year}"
        tv_week.text =  CalendarUtil.getWeekTetx(this,calendar.week)
        tv_month.text = getString(R.string.calendar_month,calendar.month,calendar.day)
        val lunarMonth = CalendarUtil.getMonthTetx(this,calendar.lunarCalendar.month,R.array.lunar_month)
        val dayMonth = CalendarUtil.getDayTetx(this,calendar.lunarCalendar.day)
        tv_lunar_month.text = getString(R.string.lunar_month,lunarMonth,dayMonth)
        tv_lunar_year.text = getString(R.string.lunar_year, TrunkBranchAnnals.getTrunkBranchYear(calendar.lunarCalendar.year))
    }

    override fun initEvent() {
        ib_calendar.setOnClickListener {
            finish()
        }
    }
}