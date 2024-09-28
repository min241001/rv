package com.baehug.callui.phone.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.telecom.Call
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.launcher3.App
import com.android.launcher3.R
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.moudle.shortcut.util.BaseUtil
import com.baehug.callui.phone.interfaces.ICanAddCallChangedListener
import com.baehug.callui.phone.interfaces.IPhoneCallInterface
import com.baehug.callui.phone.manager.PhoneCallManager
import com.baehug.callui.phone.manager.PhoneRecordManager
import kotlinx.android.synthetic.main.activity_phone_call.*

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 接听电话界面
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class PhoneCallActivity : AppCompatActivity(), View.OnClickListener, SensorEventListener,
    ICanAddCallChangedListener {

    private val TAG_ = "PhoneCallActivity"

    companion object {
        private const val EXTRA_MAIN_CALL_ID = "extra_main_call_id"
        private const val EXTRA_SUB_CALL_ID = "extra_sub_call_id"
        private const val EXTRA_IS_FOREGROUND = "extra_is_foreground"

        fun actionStart(context: Context, mainCallId: String?, isForeground: Boolean?) {
            val intent = Intent(context, PhoneCallActivity::class.java)
            if (context !is Activity) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            intent.putExtra(EXTRA_MAIN_CALL_ID, mainCallId)
            intent.putExtra(EXTRA_SUB_CALL_ID, PhoneCallManager.instance.getSubCallId(mainCallId))
            intent.putExtra(EXTRA_IS_FOREGROUND, isForeground)
            context.startActivity(intent)
        }
    }

    private var mNearFace = false
    private var mSensor: Sensor? = null
    private var mSensorManager: SensorManager? = null
    private var mWakeLock: PowerManager.WakeLock? = null

    private val innerAnimationSet by lazy { AnimationSet(true) }
    private val outerAnimationSet by lazy { AnimationSet(true) }

    private var mMainCallId = ""
        set(value) {
            field = value
            PhoneCallManager.instance.mainCallId = field
        }
    private var mSubCallId: String? = null

    private var isForeground = false
    private var recordManager: PhoneRecordManager? = null
    private var mCallStateListener = object : IPhoneCallInterface {
        override fun onCallStateChanged(call: Call?, state: Int) {
            LogUtil.e(
                "onCallStateChaned: $mMainCallId, $mSubCallId state = $state",
                LogUtil.TYPE_RELEASE
            )
            updateActionState()
            when (state) {
                Call.STATE_RINGING -> toggleCallInView(true)
                Call.STATE_ACTIVE -> {
                    toggleCallInView(false)
                    call_hold_container.handleHold(mSubCallId)
                }

                Call.STATE_HOLDING -> {
                }

                //断开连接
                Call.STATE_DISCONNECTED -> {
                    if (recordManager?.isRecording == true) {
                        recordManager?.stopRecord()
                    }
                    if (PhoneCallManager.instance.getCurrentCallSize() <= 1) {
                        call_hold_container.hide()
                    }
                    checkFinish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_call)
        mSensorManager =
            App.getInstance().getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        if (mSensorManager != null) {
            mSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        }
        val powerManager =
            App.getInstance().getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (powerManager != null) {
            mWakeLock = powerManager.newWakeLock(
                PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                "cs:phoneCallActivityTag"
            )
        }
        if (mSensor != null) {
            mSensorManager?.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        initView()
        initData()
        setViewListener()
    }

    private fun initView() {
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //hide navigationBar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.decorView.systemUiVisibility = uiOptions

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        call_hold_container.callSwitchListener = object : CallHoldView.CallSwitchListener {
            override fun onCallSwitch(callId: String) {
                mSubCallId = mMainCallId
                mMainCallId = callId
                call_hold_container.handleHold(mSubCallId)
                initCall(false)
            }
        }
    }

    private fun initData() {
        PhoneCallManager.instance.registerCanAddCallChangedListener(this)
        parseIntent(intent)
        initCall(true)
        PhoneCallManager.instance.setSpeakPhoneOn(true)
    }

    @SuppressLint("LongLogTag")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i(TAG_, "onNewIntent: ")
        parseIntent(intent)
        initCall(true)
    }

    private inline fun parseIntent(intent: Intent?) {
        intent?.let {
            mMainCallId = it.getStringExtra(EXTRA_MAIN_CALL_ID) ?: ""
            mSubCallId = it.getStringExtra(EXTRA_SUB_CALL_ID)
        }
    }

    override fun onCanAddCallChanged(canAddCall: Boolean) {
        updateActionState(canAddCall)
    }

    private fun setViewListener() {
        iv_incall_hang_up.setOnClickListener(this)
        iv_incall_pick_up.setOnClickListener(this)
        iv_hand_up.setOnClickListener(this)
        cb_mute.setOnClickListener(this)
        iv_more.setOnClickListener(this)
        tv_hide_keyboard.setOnClickListener(this)
    }

    private fun initCall(isAddCall: Boolean) {
        if (PhoneCallManager.instance.getCallById(mMainCallId) == null) {
            checkFinish()
            return
        }
        if (mMainCallId == mSubCallId) {
            return
        }
        with(PhoneCallManager.instance) {
            hold(mMainCallId, false)
            unregisterCallStateListener(mSubCallId, mCallStateListener)
            registerCallStateListener(mMainCallId, mCallStateListener)
            val phoneNum = getNumberByCallId(mMainCallId)
            updateActionState()
            caller_header_container.bindInfo(phoneNum, mMainCallId, isAddCall)
            if (recordManager?.isRecording == true) {
                recordManager?.stopRecord()
            }
            recordManager = PhoneRecordManager(phoneNum)
            LogUtil.e("initCall: mMainCallId = $mMainCallId, mSubCallId = " +
                    "$mSubCallId, size = ${getCurrentCallSize()}", LogUtil.TYPE_RELEASE
            )
        }
    }

    override fun onDestroy() {
        caller_header_container.unbindInfo()
        PhoneCallManager.instance.unregisterCallStateListener(mMainCallId, mCallStateListener)
        PhoneCallManager.instance.unregisterCanAddCallChangedListener(this)
        PhoneCallManager.instance.mainCallId = null
        PhoneCallManager.instance.release()
        if (mSensorManager != null) {
            mSensorManager?.unregisterListener(this)
        }
        if (mWakeLock?.isHeld == true) {
            mWakeLock?.release()
        }
        mWakeLock = null
        super.onDestroy()
    }

    override fun finishAndRemoveTask() {
        super.finishAndRemoveTask()
        if (!isForeground) {
            this.let { act ->
                act.moveTaskToBack(true).also {
                    Log.e(TAG_, "moveTaskToBack:  $it")
                }
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            if (!BaseUtil.repeatedClicks(v)) {
                updateActionState()
                return
            }
            when (it.id) {
                R.id.iv_incall_hang_up -> PhoneCallManager.instance.disconnect(mMainCallId)

                R.id.iv_incall_pick_up -> PhoneCallManager.instance.answer(mMainCallId)

//                R.id.iv_more -> showCallerKeyboard()
                R.id.iv_more -> {
                    val intent = Intent(this@PhoneCallActivity, InputNumberActivity::class.java)
                    intent.putExtra("callid", mMainCallId)
                    startActivity(intent)
                }

                R.id.tv_hide_keyboard -> hideCallerKeyboard()

                R.id.iv_hand_up -> {
                    PhoneCallManager.instance.disconnect(mMainCallId).also {
                        if (!it) { // 执行挂断失败
                            toggleCallInView(false)
                            checkFinish()
                        }
                    }
                }
                R.id.cb_mute -> {
                    val isMicrophoneMute = PhoneCallManager.instance.isMicrophoneMute()
                    PhoneCallManager.instance.setSpeakPhoneOn(isMicrophoneMute)
                    PhoneCallManager.instance.setMicrophoneMute(!isMicrophoneMute)
                }
                else -> {
                }
            }
        }
    }

    private inline fun updateActionState(canAddCall: Boolean? = null) {
        with(PhoneCallManager.instance) {
            cb_mute.isChecked = isMicrophoneMute()
        }
    }

    private inline fun checkFinish() {
        if (mSubCallId != null) {
            mMainCallId = mSubCallId!!
            mSubCallId = null
            initCall(false)
            return
        }
        finishAndRemoveTask()
    }

    private fun toggleCallInView(showCallInView: Boolean) {
        if (showCallInView) {
            group_caller_hide_keyboard.visibility = View.GONE
            group_caller_show_keyboard.visibility = View.GONE
            iv_hand_up.visibility = View.GONE
            ll_incall.visibility = View.VISIBLE
            startWaveAnimation()
        } else {
            group_caller_hide_keyboard.visibility = View.VISIBLE
            group_caller_show_keyboard.visibility = View.GONE
            iv_hand_up.visibility = View.VISIBLE
            ll_incall.visibility = View.GONE
            clearWaveAnimation()
        }
    }

    private inline fun hideCallerKeyboard() {
        // 隐藏拨号键盘
        group_caller_hide_keyboard.visibility = View.VISIBLE
        caller_header_container.visibility = View.VISIBLE
        group_caller_show_keyboard.visibility = View.GONE
        ll_incall.visibility = View.GONE
    }

    private fun startWaveAnimation() {
        //缩放动画，以中心从原始放大到1.4倍
        val innerScaleAnimation = ScaleAnimation(
            1.0f, 1.4f, 1.0f, 1.4f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 800
            repeatCount = Animation.INFINITE
        }
        //渐变动画
        val innerAlphaAnimation = AlphaAnimation(1.0f, 0.5f).apply {
            repeatCount = Animation.INFINITE
        }
        innerAnimationSet.duration = 800
        innerAnimationSet.addAnimation(innerScaleAnimation)
        innerAnimationSet.addAnimation(innerAlphaAnimation)
        iv_wave_inner.startAnimation(innerAnimationSet)

        //缩放动画，以中心从1.4倍放大到1.6倍
        val outerScaleAnimation = ScaleAnimation(
            1.4f, 1.6f, 1.4f, 1.6f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 800
            repeatCount = Animation.INFINITE
        }
        //渐变动画
        val outerAlphaAnimation = AlphaAnimation(0.5f, 0.1f).apply {
            repeatCount = Animation.INFINITE
        }
        outerAnimationSet.duration = 800
        outerAnimationSet.addAnimation(outerScaleAnimation)
        outerAnimationSet.addAnimation(outerAlphaAnimation)
        iv_wave_outer.startAnimation(outerAnimationSet)
    }

    private fun clearWaveAnimation() {
        innerAnimationSet.cancel()
        innerAnimationSet.reset()

        outerAnimationSet.cancel()
        outerAnimationSet.reset()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode === KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.also {
            mNearFace = it.values[0] < it.sensor?.maximumRange ?: 0f
            if (mNearFace) {
                turnOnProximitySensor()
            } else {
                turnOffProximitySensor()
            }
        }
    }

    private fun turnOnProximitySensor() {
        if (mWakeLock != null && mWakeLock?.isHeld == false) {
            mWakeLock?.acquire()
        }
    }

    private fun turnOffProximitySensor() {
        if (mWakeLock != null && mWakeLock?.isHeld == true) {
            mWakeLock?.release(0)
        }
    }

}