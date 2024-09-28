package com.android.launcher3.common.dialog

import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.android.launcher3.common.R

class IslandAlertController(val dialog: IslandAlertDialog, val window: Window) {
    private var mDialogViewHelper: DialogViewHelper? = null
    fun setViewHelper(mDialogViewHelper: DialogViewHelper?) {
        this.mDialogViewHelper = mDialogViewHelper
    }

    fun setText(id: Int, sequence: CharSequence?) {
        mDialogViewHelper!!.setText(id, sequence)
    }

    fun <T : View?> getView(viewID: Int): T? {
        return mDialogViewHelper!!.getView<T>(viewID)
    }

    /**
     * View的单击事件
     * @param id id
     * @param listener listener
     */
    fun setOnClickListener(id: Int, listener: View.OnClickListener?) {
        mDialogViewHelper!!.setOnClickListener(id, listener)
    }

    internal class AlertParams(var mContext: Context, var mThemeResId: Int) {
        var mCancelable = true //点击其他区域是否可以取消，默认可以取消

        // dialog Cancel监听
        var mOnCancelListener: DialogInterface.OnCancelListener? = null

        // dialog Dismiss监听
        var mOnDismissListener: DialogInterface.OnDismissListener? = null

        // dialog Key监听
        var mOnKeyListener: DialogInterface.OnKeyListener? = null
        var mContentView //布局的View
                : View? = null
        var mLayoutResId = 0

        // 存放字体的修改
        var mTextArray = SparseArray<CharSequence>()

        //存放点击事件
        var mClickArray = SparseArray<View.OnClickListener>()

        //宽度 默认自内部填充
        var mWidth = ViewGroup.LayoutParams.WRAP_CONTENT

        //动画
        var mAnimation = R.style.dialog_scale_anim

        //上中下 位置
        var mGravity = Gravity.CENTER //默认居中

        //高度 默认自内部填充
        var mHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        //组装所有参数
        fun apply(mAlertConterller: IslandAlertController) {
            var mDialogViewHelper: DialogViewHelper? = null
            if (mLayoutResId != 0) {
                mDialogViewHelper = DialogViewHelper(mContext, mLayoutResId)
            }
            if (mContentView != null) {
                mDialogViewHelper = DialogViewHelper()
                mDialogViewHelper.contentView = mContentView
            }
            requireNotNull(mDialogViewHelper) { "mLayoutResId or mContentView is NULL" }
            mAlertConterller.dialog.setContentView(mDialogViewHelper.contentView!!)

            //设置辅助类
            mAlertConterller.setViewHelper(mDialogViewHelper)

            //设置文本
            val textArraySize = mTextArray.size()
            for (i in 0 until textArraySize) {
                if (!TextUtils.isEmpty(mTextArray.valueAt(i))) {
                    mAlertConterller.setText(mTextArray.keyAt(i), mTextArray.valueAt(i))
                    mAlertConterller.getView<View>(mTextArray.keyAt(i))!!.visibility =
                        View.VISIBLE
                }
            }

            // 3.设置点击
            val clickArraySize = mClickArray.size()
            for (i in 0 until clickArraySize) {
                mAlertConterller.setOnClickListener(mClickArray.keyAt(i), mClickArray.valueAt(i))
            }

            // 4.配置自定义的效果  全屏  从底部弹出    默认动画
            val window = mAlertConterller.window
            // 设置位置
            window.setGravity(mGravity)

            // 设置动画
            if (mAnimation != 0) {
                window.setWindowAnimations(mAnimation)
            }

            // 设置宽高
            val params = window.attributes
            params.width = mWidth
            params.height = mHeight
            window.attributes = params
        }
    }
}