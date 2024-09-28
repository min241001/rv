package com.android.launcher3.moudle.toolapp.adapter

import com.android.launcher3.R
import com.android.launcher3.moudle.toolapp.entity.App
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.util.ArrayList

class ToolAppAdapter(mData: ArrayList<App>, layout: Int) : BaseQuickAdapter<App, BaseViewHolder>(layout, mData){

    override fun convert(helper: BaseViewHolder, item: App) {
        helper.setImageResource(R.id.iv_appIcon,item.appIcon)
            .setText(R.id.iv_appName,item.appName)
    }
}