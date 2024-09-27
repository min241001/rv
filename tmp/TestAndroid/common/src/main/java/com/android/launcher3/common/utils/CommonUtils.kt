package com.android.launcher3.common.utils

import android.content.Context
import android.net.ConnectivityManager
import java.util.*



object CommonUtils{

    fun getCourseWeek():Int{
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val week = calendar.get(Calendar.DAY_OF_WEEK) - 1
        return if (week == 0)  7 else week
    }

    fun transformWeek(position: Int) : String{
        return  when(position){
            1 -> "星期一"
            2 -> "星期二"
            3 -> "星期三"
            4 -> "星期四"
            5 -> "星期五"
            6 -> "星期六"
            7 -> "星期天"
            else -> {"星期一"}
        }
    }

    fun transformStr(position: Int) : String{
        return  when(position){
            1 -> "一"
            2 -> "二"
            3 -> "三"
            4 -> "四"
            5 -> "五"
            6 -> "六"
            7 -> "七"
            8 -> "八"
            9 -> "九"
            10 -> "十"
            else -> {""}
        }
    }

     fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}