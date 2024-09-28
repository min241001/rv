package com.android.launcher3.common.dialog
import android.content.Context
import android.os.Handler
import android.os.Looper

class Loading private constructor(){

    private var mContext:Context? = null
    private var mAlert: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null

    private val handler = Handler(Looper.getMainLooper())

    private constructor(mContext:Context?) : this() {
        this.mContext = mContext
        init()
    }

    companion object {

        fun init(mContext:Context?): Loading {
            return Loading(mContext)
        }
    }

    fun setCancelable(isCancel:Boolean){
        mAlert?.setCancelable(isCancel)
    }

    private fun init(){

    }

    fun startPregress(layoutId: Int,delayMillis : Long = 3500L){
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(layoutId)
            ?.fullHeight()
            ?.fullWidth()
        mAlertDialog = mAlert?.show()
        handler.postDelayed(runnable, delayMillis)
    }
    fun startDialPregress(layoutId: Int){
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(layoutId)
            ?.fullHeight()
            ?.fullWidth()
        mAlertDialog = mAlert?.show()
    }

    private var runnable = Runnable {
        stopPregress()
    }

    fun stopPregress(){
        mAlertDialog?.dismiss()
        handler.removeCallbacks(runnable)
    }
}