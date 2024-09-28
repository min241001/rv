package com.baehug.callui.phone.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.launcher3.R
import com.android.launcher3.moudle.shortcut.util.BaseUtil
import com.baehug.callui.phone.manager.PhoneCallManager
import kotlinx.android.synthetic.main.activity_input_number.*

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 通话时输入数字页
 */
class InputNumberActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {

    private val TAG = "InputNumberActivity_123123"

    private var mMainCallId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_number)
        mMainCallId = intent.getStringExtra("callid") as String
        Log.i(TAG, "onCreate: mMainCallId $mMainCallId")
        if (TextUtils.isEmpty(mMainCallId)) {
            return
        }
        initView()
    }

    private fun initView() {
        iv_back.setOnClickListener(this)
        zl_0.setOnTouchListener(this)
        zl_1.setOnTouchListener(this)
        zl_2.setOnTouchListener(this)
        zl_3.setOnTouchListener(this)
        zl_4.setOnTouchListener(this)
        zl_5.setOnTouchListener(this)
        zl_6.setOnTouchListener(this)
        zl_7.setOnTouchListener(this)
        zl_8.setOnTouchListener(this)
        zl_9.setOnTouchListener(this)
        zl_10.setOnTouchListener(this)
        zl_11.setOnTouchListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val action = event!!.action
        if (action == MotionEvent.ACTION_DOWN) {
            if (!BaseUtil.repeatedClicks(BaseUtil.DURATION_400)) {
                return false
            }
            when (v!!.id) {
                R.id.zl_0 -> {
                    playNumber('0')
                }
                R.id.zl_1 -> {
                    playNumber('1')
                }
                R.id.zl_2 -> {
                    playNumber('2')
                }
                R.id.zl_3 -> {
                    playNumber('3')
                }
                R.id.zl_4 -> {
                    playNumber('4')
                }
                R.id.zl_5 -> {
                    playNumber('5')
                }
                R.id.zl_6 -> {
                    playNumber('6')
                }
                R.id.zl_7 -> {
                    playNumber('7')
                }
                R.id.zl_8 -> {
                    playNumber('8')
                }
                R.id.zl_9 -> {
                    playNumber('9')
                }
                R.id.zl_10 -> {
                    playNumber('*')
                }
                R.id.zl_11 -> {
                    playNumber('#')
                }
            }
            return true
        } else if (action == MotionEvent.ACTION_UP) {
            PhoneCallManager.instance.stopDtmfTone(mMainCallId)
            return true
        }
        return false
    }

    private fun playNumber(digit: Char) {
        tv_number.text = "${tv_number.text}$digit"
        Log.i(TAG, "playNumber: digit $digit text ${tv_number.text}")
        PhoneCallManager.instance.playNumberTone1(mMainCallId, digit)
    }
}
