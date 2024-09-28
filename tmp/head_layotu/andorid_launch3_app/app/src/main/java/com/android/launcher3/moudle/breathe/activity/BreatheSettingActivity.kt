package com.android.launcher3.moudle.breathe.activity

import android.os.Bundle
import android.view.View
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseActivity
import com.android.launcher3.common.utils.AppLauncherUtils

class BreatheSettingActivity : BaseActivity() {

    override fun getResourceId(): Int {
        return R.layout.activity_breathe_setting
    }

    fun clickRhythm(view: View) {
        val bundle  = Bundle()
        bundle.putInt("status",0)
        AppLauncherUtils.launchActivity(this,BreatheContentActivity::class.java,bundle)
    }

    fun clickDuration(view: View) {
        val bundle  = Bundle()
        bundle.putInt("status",1)
        AppLauncherUtils.launchActivity(this,BreatheContentActivity::class.java,bundle)
    }
}