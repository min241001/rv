package com.baehug.callui.phone.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import com.android.launcher3.R
import com.android.launcher3.common.utils.LogUtil
import com.baehug.callui.phone.interfaces.IPhoneCallListener
import com.baehug.callui.phone.utils.ContactUtil
import com.baehug.callui.phone.utils.MobileNumberUtils
import java.util.*

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 悬浮框
 */
class FloatingWindow(context: Context?, callListener: IPhoneCallListener?) {

    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private var mContext: Context? = context
    private var mCallListener = callListener

    //悬浮窗view
    private lateinit var phoneCallView: View
    private lateinit var tvCallNumber: TextView
    private lateinit var tvPhoneHangUp: TextView
    private lateinit var tvPhonePickUp: TextView
    private lateinit var tvCallRemark: TextView
    private var tvCallingTime: TextView? = null

    // 电话状态判断
    private var hasShown = false
    private var isCallingIn = false
    private var onGoingCallTimer = Timer()
    //呼叫时长
    private var callingTime = 0

    init {
        initView()
        initListener()
    }

    private fun initView() {
        windowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams()
        //高版本适配 全面/刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        params.gravity = Gravity.CENTER
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        params.format = PixelFormat.TRANSLUCENT
        // 设置 Window flag 为系统级弹框 | 覆盖表层
        params.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        // 去掉FLAG_NOT_FOCUSABLE隐藏输入 全面屏隐藏虚拟物理按钮办法
        params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        params.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN

        val interceptorLayout: FrameLayout = object : FrameLayout(mContext!!) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                        return true
                    }
                }
                return super.dispatchKeyEvent(event)
            }
        }
        phoneCallView = LayoutInflater.from(mContext).inflate(R.layout.view_phone_call, interceptorLayout)
        tvCallNumber = phoneCallView.findViewById(R.id.tv_call_number)
        tvPhoneHangUp = phoneCallView.findViewById(R.id.tv_phone_hang_up)
        tvPhonePickUp = phoneCallView.findViewById(R.id.tv_phone_pick_up)
        tvCallingTime = phoneCallView.findViewById(R.id.tv_phone_calling_time)
        tvCallRemark = phoneCallView.findViewById(R.id.tv_call_remark)


    }

    private fun initListener() {
        //接听电话
        tvPhonePickUp.setOnClickListener {
            mCallListener?.onAnswer()
            tvPhonePickUp.visibility = View.GONE
            tvCallingTime?.visibility = View.VISIBLE
            startTimer()
        }
        //挂断电话
        tvPhoneHangUp.setOnClickListener {
            mCallListener?.onDisconnect()
            onGoingCallTimer.cancel()
            callingTime = 0

            dismiss()
        }
    }

    private fun startTimer() {
        callingTime = 0
        onGoingCallTimer.schedule(object : TimerTask() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                tvCallingTime?.post {
                    callingTime++
//                    tvCallingTime?.text = "通话中：${PhoneUtil.getCallingTime(callingTime)}"
                }
            }
        }, 0, 1000)
    }

    /**
     * 显示顶级弹框展示通话信息
     */
    fun show(phoneNumber: String?, callIn: Boolean) {
        try {
            initPhoneView(phoneNumber)
            if (!hasShown) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(mContext)) {
                    if (phoneCallView.parent == null) {
                        //RingManager.setMuteRing()
                        tvPhonePickUp.visibility = if (callIn) View.VISIBLE else View.GONE
                        windowManager.addView(phoneCallView, params)
                        hasShown = true
                    }
                }
            }
        } catch (e: Exception) {
            LogUtil.e("FloatingWindows show()$e", LogUtil.TYPE_RELEASE)
        }
    }

    /**
     * 取消显示
     */
    fun dismiss() {
        try {
            if (hasShown) {
                if (phoneCallView.parent != null) {
                    //RingManager.resetRingVolume()
                    windowManager.removeView(phoneCallView)
                    isCallingIn = false
                    hasShown = false
                }
            }
        } catch (e: Exception) {
            LogUtil.e("FloatingWindows dismiss()$e", LogUtil.TYPE_RELEASE)
        }
    }

    /**
     * 根据电话号码 设置布局
     */
    @SuppressLint("SetTextI18n")
    private fun initPhoneView(phoneNumber: String?) {
        phoneNumber?.let {
            val numberPerson = ContactUtil.getContentCallLog(mContext, it)
            if (numberPerson != null) tvCallNumber.text = numberPerson.toString()
            else tvCallNumber.text = phoneNumber

            val city = MobileNumberUtils.getGeo(phoneNumber)
            val com = MobileNumberUtils.getCarrier(mContext, phoneNumber, 86)
            tvCallRemark.text = "$city $com"
        }
    }
}