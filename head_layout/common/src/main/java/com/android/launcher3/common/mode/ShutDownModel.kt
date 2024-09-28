package com.android.launcher3.common.mode

import android.os.CountDownTimer

class ShutDownModel(val callback:(data:Int) ->Unit) {

    private lateinit var countDown :CountDownTimer

    fun countdown(){
        var remainSecond = 10L
        countDown =  object : CountDownTimer(remainSecond * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainSecond--
                callback.invoke(remainSecond.toInt())
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
}