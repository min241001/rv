package com.android.launcher3.moudle.toolapp.view

interface StopWatchView {

    fun updateTimerText(data : String)

    fun updateLapText(data: List<String>)

    fun resetTimer()

    fun pauseTimer()

    fun startTimer()
}