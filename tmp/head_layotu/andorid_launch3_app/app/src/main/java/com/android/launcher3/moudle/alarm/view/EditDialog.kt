package com.android.launcher3.moudle.alarm.view

import android.content.Context
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.android.launcher3.R
import com.android.launcher3.common.dialog.AlertDialog
import com.android.launcher3.common.utils.ToastUtils
import com.baehug.toputils.view.TitleRelativeLayout

class EditDialog() {

    private var mContext: Context? = null
    private var mAlert: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null

    private constructor(mContext: Context) : this() {
        this.mContext = mContext
    }

    companion object {
        fun init(mContext: Context): EditDialog {
            return EditDialog(mContext)
        }
    }

    fun startProgress(str: String) {
        if (isShow() == true) {
            return
        }
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(R.layout.dialog_edit)
            ?.fullHeight()
            ?.fullWidth()
        mAlertDialog?.setCancelable(false)
        mAlertDialog = mAlert?.show()
        val editText = mAlertDialog?.getView<EditText>(R.id.et_text)
        editText?.setText(str)
        editText?.setSelection(editText.text.length);

        mAlertDialog?.getView<TitleRelativeLayout>(R.id.view_top)?.setOnLeftClickListener {
            stopProgress()
        }
        mAlertDialog?.getView<Button>(R.id.btn_ok)?.setOnClickListener {
            // 修改标签成功
            val tag = editText?.text?.trim().toString()
            if (TextUtils.isEmpty(tag)) {
                ToastUtils.show("标签不能为空")
                return@setOnClickListener
            }
            closeOnClick?.closeClick(tag)
            stopProgress()
        }
    }

    private fun stopProgress() {
        mAlertDialog?.dismiss()
    }

    private fun isShow(): Boolean? {
        return mAlertDialog?.isShowing
    }

    private var closeOnClick: CloseOnClick? = null
    interface CloseOnClick{
        fun closeClick(str: String) {
        }
    }
    fun setCloseOnClick(click: CloseOnClick) {
        this.closeOnClick = click
    }
}