package com.baehug.callui.phone.mode

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : basemodel
 */
open class BaseModel {

    companion object {
        /**
         * 未知错误状态码
         */
        const val CODE_UNKNOWN = -1000

        /**
         * 未知错误信息
         */
        const val MESSAGE_UNKNOWN = "unknown"

        @JvmStatic
        fun <R> safeCallSuccess(callback: Callback<R>?, response: R) {
            if (null == callback) {
                return
            }
            callback.onSuccess(response)
        }

        @JvmStatic
        fun safeCallFailed(callback: Callback<*>?, message: String?) {
            safeCallFailed(callback, CODE_UNKNOWN, message)
        }

        @JvmOverloads
        @JvmStatic
        fun safeCallFailed(callback: Callback<*>?
                                     , code: Int = CODE_UNKNOWN
                                     , message: String? = MESSAGE_UNKNOWN
        ) {
            callback?.onFailed(code, message)
        }
    }
}