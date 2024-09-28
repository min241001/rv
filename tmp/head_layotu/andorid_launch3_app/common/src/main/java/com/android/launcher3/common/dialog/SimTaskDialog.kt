package com.android.launcher3.common.dialog
import android.content.Context
import android.text.Html
import com.android.launcher3.common.CommonApp
import com.android.launcher3.common.R
import com.android.launcher3.common.mode.ShutDownModel
import com.android.launcher3.common.mode.SimModel
import com.android.launcher3.common.utils.ScreenUtils
import com.android.launcher3.common.utils.ShutdownUtils.shutdown

class SimTaskDialog private constructor() {

    private var mContext:Context? = null
    private var mAlert: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null

    private val simModel by lazy { SimModel(CommonApp.getInstance()) }

    private val model by lazy { ShutDownModel{
        val task = "<font color=\"#FFFFFF\">手表未插卡,手表<br>将在</font><font color=\"#FF921B\">$it</font><font color=\"#FFFFFF\">秒后关机</font>"
        mAlertDialog?.setText(R.id.tv_sim_reminder, Html.fromHtml(task))
        if (it == 0){
            shutDown()
        }
    }}

    private constructor(mContext:Context) : this() {
        this.mContext = mContext
        init()
    }

    companion object {

        fun init(mContext:Context): SimTaskDialog {
            return SimTaskDialog(mContext)
        }
    }

    private fun init(){

    }

    fun startPregress(){
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(R.layout.layout_sim_shutdown)
            ?.fullWidth()
            ?.fullHeight()
        mAlert?.setCancelable(false)
        mAlertDialog = mAlert?.show()
        model.countdown()
        ScreenUtils(mContext).acquireScreenLock()
        mAlertDialog?.setOnclickListener(R.id.ib_cancel){
            model.cancel()
            simModel.isCheckSim()
            stopPregress()
        }
        mAlertDialog?.setOnclickListener(R.id.ib_ok){
            stopPregress()
            shutDown()
        }
    }

    fun stopPregress(){
        mAlertDialog?.dismiss()
        ScreenUtils(mContext).releaseScreenLock()
    }

    private fun shutDown(){
        shutdown()
    }
}