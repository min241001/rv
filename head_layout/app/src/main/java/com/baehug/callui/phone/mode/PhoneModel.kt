package com.baehug.callui.phone.mode

import com.android.launcher3.App
import com.baehug.callui.phone.bean.PhoneMsg
import com.baehug.callui.phone.utils.MobileNumberUtils

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : phonemodel
 */
object PhoneModel : BaseModel() {

    fun searchMsgByPhoneNumber(content: String, callBack: Callback<PhoneMsg>?) {
        val pm = PhoneMsg()
        pm.city = MobileNumberUtils.getGeo(content)
        pm.type = MobileNumberUtils.getCarrier(App.getInstance(), content, 86)
        safeCallSuccess(callBack, pm)
    }

}

