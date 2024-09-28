package com.baehug.callui.phone.view.callheader

import android.content.Context
import android.os.Build
import android.telecom.Call
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.android.launcher3.R
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.common.utils.StringUtils
import com.baehug.callui.phone.interfaces.IPhoneCallInterface
import com.baehug.callui.phone.manager.PhoneCallManager

import com.baehug.callui.phone.utils.ContactUtil
import kotlinx.android.synthetic.main.view_caller_header.view.*

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 通话头部组合类
 */
@RequiresApi(Build.VERSION_CODES.M)
class CallHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), CallHeaderContract.View {

    private val TAG = "CallHeaderView"

    private val INCOME_TEXT = StringUtils.getStrById(R.string.incoming_call)

    private var presenter: CallHeaderContract.Presenter? = null

    private val mCallStateListener = object : IPhoneCallInterface {
        override fun onCallStateChanged(call: Call?, state: Int) {
            when (state) {
                //正在呼叫(呼出)
                Call.STATE_DIALING -> {
                    Log.i(TAG, "onCallStateChanged: STATE_DIALING 正在呼叫")
                    tv_sim_card.visibility = View.VISIBLE
                    tv_sim_card.text = StringUtils.getStrById(R.string.call_calling_in)
                }

                Call.STATE_CONNECTING -> {
                    tv_sim_card.visibility = View.VISIBLE
                    tv_sim_card.text = StringUtils.getStrById(R.string.call_connecting)
                }

                Call.STATE_RINGING -> {
                    tv_sim_card.visibility = View.GONE
                }

                //正在通话
                Call.STATE_ACTIVE -> {
                    Log.i(TAG, "onCallStateChanged: STATE_ACTIVE 正在通话")
                    mCallId?.let {
                        tv_sim_card.visibility = View.VISIBLE
                        presenter?.startTimer(it)
                    }
                }

                //断开连接中
                Call.STATE_DISCONNECTING -> {
                    Log.i(TAG, "onCallStateChanged: STATE_DISCONNECTING 断开连接中")
//                    tv_sim_card.visibility = View.GONE
                }

                //断开连接
                Call.STATE_DISCONNECTED -> {
//                    mCallId?.let {
//                        presenter?.stopTimer(it, "STATE_DISCONNECTED")
//                    }
                    call?.let {
                        presenter?.removeTime(PhoneCallManager.instance.getCallId(it))
                    }
                }
            }
        }
    }

    private var mCallId: String? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_caller_header, this, true)
        presenter = CallHeaderPresenter(this)
    }

    fun bindInfo(phoneNumber: String, callId: String, isAddCall: Boolean) {
        LogUtil.e("bind info: from $mCallId to $callId", LogUtil.TYPE_RELEASE)
        if (mCallId == callId) {
            return
        }
        PhoneCallManager.instance.unregisterCallStateListener(mCallId, mCallStateListener)
        PhoneCallManager.instance.registerCallStateListener(callId, mCallStateListener)

        if (!isAddCall || PhoneCallManager.instance.getCallById(callId)?.state === Call.STATE_ACTIVE) {
            tv_sim_card.visibility = View.VISIBLE
            presenter?.startTimer(callId)
        }
        mCallId = callId

        tv_call_number.text = presenter?.formatPhoneNumber(phoneNumber)

        presenter?.queryLocalContactInfo(context, phoneNumber)
        presenter?.queryPhoneInfo(phoneNumber)
    }

    fun unbindInfo() {
        presenter?.stopTimer(mCallId)
    }

    override fun onQueryLocalContactInfoSuccessful(info: ContactUtil.ContactInfo?) {
        info?.let {
            if (it.displayName != null) {
                Log.i(TAG, "onQueryLocalContactInfoSuccessful: ${it.displayName}")
                tv_call_number.text = it.displayName
            }
        }
    }

    /**
     * 来电显示信息
     *
     * @param city 来电归属地信息
     * @param type 运营商名称
     */
    override fun onQueryPhoneInfoSuccessful(city: String?, type: String?) {
        if (city == null && type == null) {
            tv_call_number_info.visibility = View.GONE
            return
        }
        tv_call_number_info.visibility = View.VISIBLE

        // 通话类型  来电/去电
        val flag = PhoneCallManager.instance.getCallById(mCallId)?.let {
            if (it.state === Call.STATE_RINGING) {
                return@let INCOME_TEXT
            }
            return@let ""
        }
        Log.i(TAG, "onQueryPhoneInfoSuccessful: city $city type $type flag $flag")
        tv_call_number_info?.text = "$city $type"
        tv_sim_card.text = "$flag"
        tv_sim_card.visibility = View.VISIBLE
    }

    override fun updateCallingTime(callId: String, time: String) {
        LogUtil.e("updateCallingTime: $mCallId, $callId, $time", LogUtil.TYPE_RELEASE)
        if (mCallId != callId) {
            return
        }
        Log.i(TAG, "updateCallingTime: text " + tv_call_number_info.text)
        tv_call_number_info.text = tv_call_number_info.text.toString().replace(INCOME_TEXT, "")
        tv_sim_card.visibility = View.VISIBLE
        tv_sim_card.text = time
    }

}