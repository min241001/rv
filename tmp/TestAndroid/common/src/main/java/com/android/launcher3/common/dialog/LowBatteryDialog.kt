package com.android.launcher3.common.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Html
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.launcher3.common.R

class LowBatteryDialog private constructor() {

    private var mContext: Context? = null
    private var mAlert: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null
    private val handler = Handler(Looper.getMainLooper())

    private constructor(mContext: Context) : this() {
        this.mContext = mContext
        init()
    }

    companion object {
        fun init(mContext: Context): LowBatteryDialog {
            return LowBatteryDialog(mContext)
        }
    }

    private fun init() {

    }

    fun startPregress(battery: Int) {
        if (isShow() == true) {
            return
        }
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(R.layout.layout_low_battery)
            ?.fullWidth()
            ?.fullHeight()
        mAlertDialog = mAlert?.show()
        mAlertDialog?.setText(R.id.tv_battery,mContext?.getString(R.string.battery,battery))
        val battery = "<font color=\"#FFFFFF\">剩余电量 </font><font color=\"#FF921B\">$battery</font><font color=\"#FFFFFF\"> % </font>"
        mAlertDialog?.setText(R.id.tv_battery,Html.fromHtml(battery))
        mAlertDialog?.getView<ConstraintLayout>(R.id.layout_battery)?.setOnClickListener {
            stopPregress()
        }
        handler.postDelayed(runnable, 3500L)
    }

    fun stopPregress() {
        mAlertDialog?.dismiss()
        handler.removeCallbacks(runnable)
    }

    private var runnable = Runnable {
        stopPregress()
    }

    private fun isShow(): Boolean? {
        return mAlertDialog?.isShowing
    }
}