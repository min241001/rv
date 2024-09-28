package com.baehug.callui.phone.impl

import android.text.TextUtils
import android.util.Log
import com.baehug.callui.phone.interfaces.IPhoneStateAction
import com.baehug.callui.phone.manager.FloatingWindowManager

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话状态实现类
 */
class PhoneStateActionImpl private constructor() : IPhoneStateAction {

    private val TAG: String = "PhoneStateActionImpl"

    private var mLastCallTime:Long = 0

    companion object {
        val instance: PhoneStateActionImpl by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PhoneStateActionImpl()
        }
    }

    override
    fun onCallOut(phoneNumber: String?) {
        Log.i(TAG, "onCallOut: phoneNumber $phoneNumber")
        if (!TextUtils.isEmpty(phoneNumber) && System.currentTimeMillis() > mLastCallTime + 1500) {
            mLastCallTime = System.currentTimeMillis()
//            FloatingWindowManager.instance.show(phoneNumber, false)
        }
    }

    override fun onRinging(phoneNumber: String?) {
        Log.i(TAG, "onRinging: phoneNumber $phoneNumber")
        if (!TextUtils.isEmpty(phoneNumber) && System.currentTimeMillis() > mLastCallTime + 1500) {
            mLastCallTime = System.currentTimeMillis()
//            FloatingWindowManager.instance.show(phoneNumber, true)
        }
    }

    override fun onPickUp(phoneNumber: String?) {
    }

    override fun onHandUp() {
        FloatingWindowManager.instance.dismiss()
    }

}
