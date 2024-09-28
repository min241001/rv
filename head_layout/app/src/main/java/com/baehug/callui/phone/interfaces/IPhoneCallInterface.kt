package com.baehug.callui.phone.interfaces

import android.telecom.Call

/**
 * Author : yanyong
 * Date : 2024/8/19
 * Details : 电话状态变化监听
 */
interface IPhoneCallInterface {

    fun onCallStateChanged(call: Call?, state: Int)

}