package com.android.launcher3.moudle.toolapp.view

import com.android.launcher3.common.bean.Course

interface CourseView {

    fun onCourseSuccess(resp : Course)

    fun onCourseFail(code: Int, msg: String)
}