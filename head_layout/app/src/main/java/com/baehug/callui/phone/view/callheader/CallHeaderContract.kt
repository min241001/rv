package com.baehug.callui.phone.view.callheader

import android.content.Context
import com.baehug.callui.base.BasePre
import com.baehug.callui.base.BaseView
import com.baehug.callui.phone.utils.ContactUtil

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话头部信息类
 */
class CallHeaderContract {

    interface View : BaseView {

        fun onQueryLocalContactInfoSuccessful(info: ContactUtil.ContactInfo?)

        fun updateCallingTime(callId: String, time: String)

        fun onQueryPhoneInfoSuccessful(city: String?, type: String?)
    }

    interface Presenter : BasePre<View> {

        fun startTimer(s: String?)

        fun removeTime(callId: String?)

        fun stopTimer(callId: String?)

        fun queryLocalContactInfo(context: Context, phoneNum: String)

        fun queryPhoneInfo(phoneNum: String)

        fun formatPhoneNumber(phoneNum: String?): String?
    }
}
