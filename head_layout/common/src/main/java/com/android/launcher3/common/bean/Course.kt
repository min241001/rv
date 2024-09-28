package com.android.launcher3.common.bean

data class Course (val list:ArrayList<CourseList>)

data class CourseList(var week:Int, var morningCourse:ArrayList<MorningCourse>, var afternoonCourse:ArrayList<AfternoonCourse>)

data class MorningCourse(var courseName:String)

data class AfternoonCourse(var courseName:String)