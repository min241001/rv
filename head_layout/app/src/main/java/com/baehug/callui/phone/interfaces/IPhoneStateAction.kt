package com.baehug.callui.phone.interfaces

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话状态接口
 */
interface IPhoneStateAction {

    /**
     * 拨打电话
     */
    fun onCallOut(phoneNumber: String?)

    /**
     * 响铃
     */
    fun onRinging(phoneNumber: String?)

    /**
     * 接听电话
     */
    fun onPickUp(phoneNumber: String?)

    /**
     * 挂断电话
     */
    fun onHandUp()

}