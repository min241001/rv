package com.android.launcher3.common.dialog

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import java.lang.ref.WeakReference

class DialogViewHelper() {
    /**
     * 获取布局View
     *
     * @return View
     */
    /**
     * 设置布局
     * @param mContentView mContentView
     */
    var contentView: View? = null
    private val mViews: SparseArray<WeakReference<View>> = SparseArray()

    constructor(mContext: Context?, layoutID: Int) : this() {
        contentView = LayoutInflater.from(mContext).inflate(layoutID, null)
    }

    fun setText(viewID: Int, sequence: CharSequence?) {
        val tv = getView<View>(viewID)!!
        if (tv is TextView) {
            tv.text = sequence
        }
        if (tv is Button) {
            tv.text = sequence
        }
        if (tv is EditText) {
            tv.setText(sequence)
        }
        if (tv is RadioButton) {
            tv.text = sequence
        }
        if (tv is CheckBox) {
            tv.text = sequence
        }
    }

    /**
     * View 的单击事件
     * @param viewID id
     * @param listener listener
     */
    fun setOnClickListener(viewID: Int, listener: View.OnClickListener?) {
        val view = getView<View>(viewID)
        view?.setOnClickListener(listener)
    }

    fun <T : View?> getView(viewID: Int): T? {
        val weakReference = mViews[viewID]
        var view: View? = null
        if (weakReference != null) {
            view = weakReference.get()
        }
        if (view == null) {
            view = contentView!!.findViewById(viewID)
            if (view != null) {
                mViews.put(viewID, WeakReference(view))
            }
        }
        return view as T?
    }

}