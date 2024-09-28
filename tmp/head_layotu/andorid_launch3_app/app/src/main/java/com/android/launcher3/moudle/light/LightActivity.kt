package com.android.launcher3.moudle.light

import android.animation.LayoutTransition
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseActivity
import com.android.launcher3.common.utils.ShortCutUtils.getSystemBrightness
import com.android.launcher3.common.utils.ShortCutUtils.setSystemBrightness
import kotlinx.android.synthetic.main.activity_light.iv_light
import kotlinx.android.synthetic.main.activity_light.rl_view

/**
 * 手电筒
 */
class LightActivity: BaseActivity() {

    companion object {
        lateinit var instance: LightActivity
    }

    private val LIGHT_MSG: Int = 1

    private var bightness: Int = getSystemBrightness()

    override fun getResourceId(): Int {
        return R.layout.activity_light
    }

    override fun onPause() {
        super.onPause()
        try {
            mHandler.removeMessages(LIGHT_MSG)
            setSystemBrightness(bightness)
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun initData() {
        instance = this
    }

    override fun initEvent() {
        iv_light.setOnClickListener{
            iv_light.isSelected = !iv_light.isSelected

            if (iv_light.isSelected) {
                iv_light.visibility = View.GONE

                // 设置布局变化的动画
                rl_view.layoutTransition = LayoutTransition()

                rl_view.setBackgroundColor(Color.WHITE)
                setSystemBrightness(100)
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            } else {
                // 取消布局变化的动画
                rl_view.layoutTransition = null

                rl_view.setBackgroundColor(Color.BLACK)
                mHandler.removeMessages(LIGHT_MSG)
                setSystemBrightness(bightness)
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        mHandler.removeMessages(LIGHT_MSG)
        mHandler.sendEmptyMessageDelayed(LIGHT_MSG, 3000)

        if (iv_light.visibility == View.GONE) {
            iv_light.visibility = View.VISIBLE
        }

        return super.onTouchEvent(event)
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                LIGHT_MSG -> {
                    if (iv_light.isSelected) {
                        iv_light.visibility = View.GONE
                    }
                }
            }
        }
    }

}