package com.baehug.callui.base

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details :
 */
open class BasePresenter<T : BaseView> : BasePre<T> {
    private  var mView: T? = null

    override fun detachView() {
        mView = null
    }

    override fun attachView(view: T?) {
        mView = view
    }
}
