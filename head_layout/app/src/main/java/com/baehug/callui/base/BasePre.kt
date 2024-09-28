package com.baehug.callui.base

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 基类接口
 */
interface BasePre<T : BaseView> {

    /**
     * 注入View
     *
     * @param view view
     */
    fun attachView(view: T?)

    /**
     * 回收View
     */
    fun detachView()

}