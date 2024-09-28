package com.android.launcher3.moudle.breathe.activity

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.view.View
import android.view.WindowManager
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseActivity
import com.android.launcher3.moudle.breathe.model.BreatheModel
import kotlinx.android.synthetic.main.activity_breathe_anima.*
import kotlinx.android.synthetic.main.activity_breathe_content.*

class BreatheAnimaActivity : BaseActivity() {

    private val model by lazy { BreatheModel() }

    private var frame = 75

    private val duration by lazy { model.getDurationValue() }

    private val anim = AnimationDrawable()

    private val handler = Handler()

    private var currentFrameIndex = 0

    private var current = true

    override fun getResourceId(): Int {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        return R.layout.activity_breathe_anima
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        val rhythmValue = model.getRhythmValue()
        frame = when(rhythmValue){
            "缓慢" ->{
                125
            }

            "稍快" ->{
                75
            }

            else ->{
                100
            }
        }

        model.countdown(4) {
            tv_countdown.text = "$it"
            if (it == 0) {
                tv_countdown.visibility = View.GONE
                tv_tips.visibility = View.GONE
                iv_breathe.visibility = View.VISIBLE
                model.cancel()
                startAnim()
                tv_breathe_state.text = "吸气"
            }
        }
    }

    private fun startAnim() {
        for (i in 1..74) {
            val id = resources.getIdentifier("ic_breathe_$i", "drawable", packageName)
            val drawable = resources.getDrawable(id)
            anim.addFrame(drawable, frame)
        }
        anim.isOneShot = false
        iv_breathe.setImageDrawable(anim)
        anim.selectDrawable(0)
        handler.postDelayed(runnable, anim.getDuration(0).toLong())
        model.countdown(duration * 60) {
            if (it == 0)
              finish()
        }
    }

    private val runnable = object : Runnable {
        override fun run() {
            if (currentFrameIndex == anim.numberOfFrames - 1) {
                currentFrameIndex = 0
                if (current){
                    tv_breathe_state.text = "呼气"
                }else{
                    tv_breathe_state.text = "吸气"
                }
                current = !current
            } else {
                currentFrameIndex++
            }
            anim.selectDrawable(currentFrameIndex)
            handler.postDelayed(this, anim.getDuration(currentFrameIndex).toLong())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        model.cancel()
        handler.removeCallbacks(runnable)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}