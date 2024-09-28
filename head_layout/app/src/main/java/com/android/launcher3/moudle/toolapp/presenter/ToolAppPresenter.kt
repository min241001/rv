package com.android.launcher3.moudle.toolapp.presenter

import com.android.launcher3.R
import com.android.launcher3.common.base.BasePresenter
import com.android.launcher3.moudle.toolapp.entity.App
import com.android.launcher3.moudle.toolapp.view.ToolAppView

class ToolAppPresenter : BasePresenter<ToolAppView>() {

   fun initAppData(): List<App> {
      val apps = mutableListOf(
         App("闹钟", R.drawable.icon_app_clock, "com.android.deskclock"),
         App("日历", R.drawable.icon_app_calendar, "com.android.calendar"),
         App("计算器", R.drawable.icon_app_calculator, "com.android.calculator2"),
         App("秒表", R.drawable.icon_app_stopwatch, "")
      )
//         App("日志上传",R.mipmap.log,"com.android.logger"))
      return apps.toList()
   }

}