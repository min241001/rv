package com.baehug.callui.phone.manager

import com.baehug.callui.phone.bean.PhoneMsg
import com.baehug.callui.phone.mode.Callback
import com.baehug.callui.phone.mode.PhoneModel

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话管理类
 */
object PhoneNumberManager {
    private var phoneMsg: PhoneMsg? = null

    /**
     * 根据电话号码 获取电话归属地和 运行商信息
     */
    @JvmStatic
    @JvmOverloads
    fun getStageTaskList(number: String, listener: OnPhoneListener? = null) {
        PhoneModel.searchMsgByPhoneNumber(number, object : Callback<PhoneMsg> {
            override fun onSuccess(response: PhoneMsg?) {
                if (null == response) {
                    return
                }
                phoneMsg = response
                listener?.onSuccess(phoneMsg)
            }

            override fun onFailed(code: Int, message: String?) {
                listener?.onFailed(code, message)
            }

        })
    }

    data class OnPhoneListenerWrapper(val listener: OnPhoneListener)

    interface OnPhoneListener {

        fun onSuccess(obj: PhoneMsg?)

        fun onFailed(code: Int?, errorMsg: String?)

    }

}