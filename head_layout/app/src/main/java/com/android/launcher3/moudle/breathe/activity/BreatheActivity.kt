package com.android.launcher3.moudle.breathe.activity

import android.view.View
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseActivity
import com.android.launcher3.common.utils.AppLauncherUtils

class BreatheActivity : BaseActivity() {

    override fun getResourceId(): Int {
        return R.layout.activity_breathe
    }

    fun settingClick(view: View) {
        AppLauncherUtils.launchActivity(this,BreatheSettingActivity::class.java)
    }

    fun start(view: View) {
        AppLauncherUtils.launchActivity(this,BreatheAnimaActivity::class.java)
    }
}