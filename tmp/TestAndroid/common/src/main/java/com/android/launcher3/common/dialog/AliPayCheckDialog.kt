package com.android.launcher3.common.dialog

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.android.launcher3.common.R
import com.android.launcher3.common.utils.AliPayUtils

class AliPayCheckDialog() {

    private var mContext: Context? = null
    private var mAlert: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null
    private var str: TextView? = null
    private var cancel: Button? = null
    private var result: Button? = null
    private var ok: Button? = null


    private constructor(mContext: Context) : this() {
        this.mContext = mContext
        init()
    }

    companion object {
        fun init(mContext: Context): AliPayCheckDialog {
            return AliPayCheckDialog(mContext)
        }
    }

    private fun init() {

    }

    fun startProgress(imei: String) {
        if (isShow() == true) {
            return
        }
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(R.layout.alipay_check_dialog)
        mAlertDialog?.setCancelable(false)
        mAlertDialog = mAlert?.show()
        mAlertDialog?.getView<Button>(R.id.alipay_check_cancel)?.setOnClickListener {
            stopProgress()
        }
        mAlertDialog?.getView<Button>(R.id.alipay_check_result)?.setOnClickListener {
            stopProgress()
        }
        mAlertDialog?.getView<Button>(R.id.alipay_check_ok)?.setOnClickListener {
            AliPayUtils.addAliPayRegister(imei)
        }
    }

    fun resetUI(checkResult: Int) {
        str = mAlertDialog?.getView<TextView>(R.id.alipay_check_str)
        cancel = mAlertDialog?.getView<Button>(R.id.alipay_check_cancel)
        ok = mAlertDialog?.getView<Button>(R.id.alipay_check_ok)
        result = mAlertDialog?.getView<Button>(R.id.alipay_check_result)
        //激活状态 1处理中 2激活失败 4未申请
        when (checkResult) {
            1 -> {
                str?.text = mContext!!.getString(R.string.registering)
                cancel?.visibility = View.GONE
                ok?.visibility = View.GONE
                result?.visibility = View.VISIBLE
                result?.text = mContext!!.getString(R.string.know)
            }
            2 -> {
                str?.text = mContext!!.getString(R.string.register_fail)
                cancel?.visibility = View.GONE
                ok?.visibility = View.GONE
                result?.visibility = View.VISIBLE
                result?.text = mContext!!.getString(R.string.sure)
            }
            4 -> {
                str?.text = mContext!!.getString(R.string.check_alipay)
                cancel?.visibility = View.VISIBLE
                ok?.visibility = View.VISIBLE
                result?.visibility = View.GONE
            }
        }
    }

    private fun stopProgress() {
        mAlertDialog?.dismiss()
    }

    private fun isShow(): Boolean? {
        return mAlertDialog?.isShowing
    }

    fun result(){
        resetUI(1)
    }

}