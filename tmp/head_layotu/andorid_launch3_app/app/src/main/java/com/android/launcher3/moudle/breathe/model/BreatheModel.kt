package com.android.launcher3.moudle.breathe.model

import android.os.CountDownTimer
import androidx.annotation.ArrayRes
import com.android.launcher3.App
import com.android.launcher3.common.data.AppLocalData

class BreatheModel {

    private lateinit var countDown : CountDownTimer

    fun countdown(second: Long, callback: (data: Int) -> Unit) {
        countDown = object : CountDownTimer(second * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                callback.invoke(secondsLeft.toInt())
            }

            override fun onFinish() {
                callback.invoke(0)
            }
        }
        countDown.start()
    }

    fun cancel(){
        countDown.cancel()
    }

    fun getRhythmValue() : String{
        val position = AppLocalData.getInstance().rhythm
        return getArrayRes(com.android.launcher3.common.R.array.rhythm)[position]

    }

    fun getDurationValue() : Long{
        val position = AppLocalData.getInstance().duration
        return getArrayRes(com.android.launcher3.common.R.array.duration)[position].toLong()
    }

    private fun getArrayRes(@ArrayRes id :Int) : List<String>{
       return App.getInstance().resources.getStringArray(id).toList()
    }
}