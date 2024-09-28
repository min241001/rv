package com.android.launcher3.common.dialog
import android.content.Context
import com.android.launcher3.common.R
import com.android.launcher3.common.mode.ShutDownModel
import com.android.launcher3.common.utils.ShutdownUtils.shutdown

class ShutdownDialog private constructor() {

    private var mContext:Context? = null
    private var mAlert: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null

    private val model by lazy { ShutDownModel{
        mAlertDialog?.setText(R.id.tv_shutdown,it.toString())
        if (it == 0){
            shutDown()
        }
    }}

    private constructor(mContext:Context) : this() {
        this.mContext = mContext
        init()
    }

    companion object {

        fun init(mContext:Context): ShutdownDialog {
            return ShutdownDialog(mContext)
        }
    }

    private fun init(){

    }

    fun startPregress(){
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(R.layout.dialog_shutdown)
            ?.fullWidth()
            ?.fullHeight()
        mAlert?.setCancelable(false)
        mAlertDialog = mAlert?.show()
        model.countdown()
    }

    fun isShow() : Boolean?{
       return mAlertDialog?.isShowing
    }

    fun stopPregress(){
        mAlertDialog?.dismiss()
    }

    private fun shutDown(){
        shutdown()
    }
}