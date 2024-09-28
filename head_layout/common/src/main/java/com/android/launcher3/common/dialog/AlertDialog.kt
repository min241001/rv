package com.android.launcher3.common.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.android.launcher3.common.R


class AlertDialog(context: Context?, themeResId: Int) :
    Dialog(context!!, themeResId) {
    private val mAlertConterller: AlertController = AlertController(this, window!!)

    fun setText(viewId: Int, text: CharSequence?) {
        mAlertConterller.setText(viewId, text)
    }

    fun <T : View?> getView(viewId: Int): T? {
        return mAlertConterller.getView<T>(viewId)
    }

    fun setOnclickListener(viewId: Int, listener: View.OnClickListener?) {
        mAlertConterller.setOnClickListener(viewId, listener)
    }

    fun setOnclickListeners(viewIds: IntArray, listener: View.OnClickListener?) {
        for (i in viewIds.indices) {
            mAlertConterller.setOnClickListener(viewIds[i], listener)
        }
    }

    class Builder @JvmOverloads constructor(
        context: Context?,
        themeResId: Int = R.style.AlertDialog
    ) {
        private val P: AlertController.AlertParams

        fun setContentView(view: View?): Builder {
            P.mContentView = view
            P.mLayoutResId = 0
            return this
        }

        fun setContentView(layoutId: Int): Builder {
            P.mContentView = null
            P.mLayoutResId = layoutId
            return this
        }

        fun setText(viewId: Int, text: CharSequence?): Builder {
            P.mTextArray.put(viewId, text)
            return this
        }

        fun setGravity(mGravity: Int): Builder {
            P.mGravity = mGravity
            return this
        }

        fun setOnClickListener(view: Int, listener: View.OnClickListener?): Builder {
            P.mClickArray.put(view, listener)
            return this
        }

        fun fullWidth(): Builder {
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT
            return this
        }

        fun fullHeight(): Builder {
            P.mHeight = ViewGroup.LayoutParams.MATCH_PARENT
            return this
        }

        fun formBottom(isAnimation: Boolean): Builder {
            if (isAnimation) {
                P.mAnimation = R.style.dialog_from_bottom_anim
            }
            P.mGravity = Gravity.BOTTOM
            return this
        }

        fun addDefaultAnimation(): Builder {
            P.mAnimation = R.style.dialog_scale_anim
            P.mGravity = Gravity.CENTER
            return this
        }

        fun setAnimations(styleAnimation: Int): Builder {
            P.mAnimation = styleAnimation
            return this
        }

        fun setWidthAndHeight(width: Int, height: Int): Builder {
            P.mWidth = width
            P.mHeight = height
            return this
        }


        fun setCancelable(cancelable: Boolean): Builder {
            P.mCancelable = cancelable
            return this
        }


        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?): Builder {
            P.mOnCancelListener = onCancelListener
            return this
        }


        fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?): Builder {
            P.mOnDismissListener = onDismissListener
            return this
        }


        fun setOnKeyListener(onKeyListener: DialogInterface.OnKeyListener?): Builder {
            P.mOnKeyListener = onKeyListener
            return this
        }


        private fun create(): AlertDialog {
            val dialog = AlertDialog(P.mContext, P.mThemeResId)
            P.apply(dialog.mAlertConterller)
            dialog.setCancelable(P.mCancelable)
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true)
            }
            dialog.setOnCancelListener(P.mOnCancelListener)
            dialog.setOnDismissListener(P.mOnDismissListener)
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener)
            }
            return dialog
        }

        fun show(): AlertDialog {
            val dialog = create()
            val layoutParams = dialog.window!!.attributes
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            dialog.window!!.attributes = layoutParams
            dialog.show()
            return dialog
        }

        init {
            P = AlertController.AlertParams(context!!, themeResId)
        }
    }

}