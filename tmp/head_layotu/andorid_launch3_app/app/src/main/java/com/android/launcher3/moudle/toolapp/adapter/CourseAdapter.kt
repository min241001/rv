package com.android.launcher3.moudle.toolapp.adapter


import android.text.TextUtils
import android.widget.TextView
import com.android.launcher3.R
import com.android.launcher3.common.bean.AfternoonCourse
import com.android.launcher3.common.bean.MorningCourse
import com.android.launcher3.common.utils.CommonUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class CourseAdapter(mData: ArrayList<*>, layout: Int) :
    BaseQuickAdapter<Any, BaseViewHolder>(layout, mData) {

    override fun convert(helper: BaseViewHolder, item: Any) {

        if (item is MorningCourse){
            helper.setText(R.id.tv_courseName,item.courseName)
                .setText(R.id.tv_sections,"第${CommonUtils.transformStr(helper.layoutPosition + 1)}节课")
        }

        if ( item is AfternoonCourse){
            helper.setText(R.id.tv_courseName,item.courseName)
                .setText(R.id.tv_sections,"第${CommonUtils.transformStr(helper.layoutPosition + 1)}节课")
        }

        helper.getView<TextView>(R.id.tv_courseName).apply{
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.MARQUEE
            marqueeRepeatLimit = -1
            isFocusable = true
            isFocusableInTouchMode = true
            isSelected = true
        }
    }
}