package com.android.launcher3.moudle.toolapp.activity

import android.text.TextUtils
import android.widget.Button
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseMvpActivity
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.utils.ToastUtils
import com.android.launcher3.moudle.toolapp.presenter.LoggerPresenter
import com.android.launcher3.moudle.toolapp.view.LoggerView

class LoggerActivity: BaseMvpActivity<LoggerView, LoggerPresenter>(), LoggerView {


    private lateinit var logUpload: Button

    override fun getResourceId(): Int {
        return R.layout.activity_logger
    }

    override fun createPresenter(): LoggerPresenter {
        return LoggerPresenter()
    }

    override fun initView() {
        logUpload = findViewById(R.id.logUpload)
    }

    override fun initEvent() {
        logUpload.setOnClickListener{
            openLogger()
        }
    }

    private val CLICK_TIMEL: Long = 24*60*60*1000 // 定义点击的时间间隔，单位为毫秒
    private var lastClickTime: Long = 0

    private fun isFastClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < CLICK_TIMEL) {
            return true
        }
        lastClickTime = currentTime
        return false
    }

    override fun openLogger() {
        if (AppLocalData.getInstance().batterySwitch != 0){
            ToastUtils.show("工具未启用")
            return
        }
        if (TextUtils.isEmpty(AppLocalData.getInstance().watchId)){
            ToastUtils.show("无账号")
            return
        }
        if (isFastClick()){
            ToastUtils.show("上传失败,一天只可以点一次")
            return
        }
        mPresenter.openLogger(this,AppLocalData.getInstance().watchId,"/storage/emulated/0/batteryLogfile.txt")
    }

}