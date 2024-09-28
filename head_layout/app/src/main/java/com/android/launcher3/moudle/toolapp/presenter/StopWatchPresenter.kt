package com.android.launcher3.moudle.toolapp.presenter

import com.android.launcher3.common.base.BasePresenter
import com.android.launcher3.moudle.toolapp.view.StopWatchView
import java.util.*

class StopWatchPresenter : BasePresenter<StopWatchView>() {

    private val lapList = ArrayList<String>()
    private var pausedTime =  0L

    private var isRunning =false
    private var startTime = 0L
    private var timer: Timer? = null
    private var elapsedTime = 0L

    private  fun startTimer() {
        if (!isRunning) {
            isRunning = true

            view.startTimer()
            if (pausedTime == 0L) {
                startTime = System.currentTimeMillis()
            } else {
                val pausedDuration = System.currentTimeMillis() - pausedTime
                startTime += pausedDuration
            }
            timer = Timer()
            timer!!.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    elapsedTime = System.currentTimeMillis() - startTime
                    updateTimerText()
                }
            }, 0, 10)
        }
    }

    private fun pauseTimer() {
        if (isRunning) {
            isRunning = false

            view.pauseTimer()
            if (timer != null) {
                timer!!.cancel()
                timer = null
            }
            pausedTime = System.currentTimeMillis()
        }
    }

     fun startPauseTimer() {
        if (isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

      fun lapTimer() {
        if (isRunning){
            val lapTime = getFormattedTime()
            lapList.add(lapTime)
            view.updateLapText(lapList.reversed())
        }
    }

     fun resetTimer() {
        isRunning = false
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
        elapsedTime = 0
        startTime = 0
        pausedTime = 0
        lapList.clear()
        view.resetTimer()
    }

    private  fun updateTimerText() {
        val timeText = getFormattedTime()
        if (view != null){
            view.updateTimerText(timeText)
        }
    }

    private  fun getFormattedTime(): String {
        val milliseconds = elapsedTime % 1000
        val seconds = elapsedTime / 1000 % 60
        val minutes = elapsedTime / (1000 * 60) % 60
        val hours = elapsedTime / (1000 * 60 * 60) % 24

        var millisecondsString = String.format("%03d", milliseconds)
        if (millisecondsString.length > 2) {
            millisecondsString = millisecondsString.substring(0, 2)
        }

        return String.format("%02d:%02d:%02d.%s", hours, minutes, seconds, millisecondsString)
    }

    fun removeTimer(){
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

}