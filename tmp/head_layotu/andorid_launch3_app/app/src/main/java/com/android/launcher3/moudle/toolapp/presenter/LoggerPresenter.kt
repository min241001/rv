package com.android.launcher3.moudle.toolapp.presenter

import android.content.Context
import com.android.launcher3.common.base.BasePresenter
import com.android.launcher3.common.network.api.ApiHelper
import com.android.launcher3.common.network.listener.BaseListener
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.common.utils.ToastUtils
import com.android.launcher3.moudle.toolapp.view.LoggerView

class LoggerPresenter: BasePresenter<LoggerView>() {

    //打开日志
    fun openLogger(context: Context,watchId : String,filePath:String){
        ApiHelper.loggerUpload( object : BaseListener<Boolean> {
            override fun onError(code: Int, msg: String) {
                LogUtil.d(
                    "$code\t\t$msg",
                    LogUtil.TYPE_RELEASE
                )
                ToastUtils.show("日志上传失败\t$msg")
            }
            override fun onSuccess(t: Boolean?) {
                LogUtil.d(
                    "是否请求成功 === $t",
                    LogUtil.TYPE_RELEASE
                )
                ToastUtils.show("日志上传成功")
            }

        },watchId,filePath)
    }

}