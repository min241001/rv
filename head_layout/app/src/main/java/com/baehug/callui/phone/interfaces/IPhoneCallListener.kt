package com.baehug.callui.phone.interfaces

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话操作接口
 */
interface IPhoneCallListener {

    /**
     * 用户点击接听按钮
     */
    fun onAnswer()

    /**
     * 用户点击免提按钮
     */
    fun onOpenSpeaker()

    /**
     * 用户点击挂断按钮
     */
    fun onDisconnect()
}