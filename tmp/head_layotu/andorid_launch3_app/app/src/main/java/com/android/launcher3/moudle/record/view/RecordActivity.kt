package com.android.launcher3.moudle.record.view

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.telephony.TelephonyManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseMvpActivity
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.common.utils.ToastUtils
import com.android.launcher3.moudle.notification.util.TimeUtils
import com.android.launcher3.moudle.record.audio.AudioManagerXM
import com.android.launcher3.moudle.record.base.RecordConstant
import com.android.launcher3.moudle.record.presenter.RecordInterface
import com.android.launcher3.moudle.record.presenter.RecordPresenter
import com.android.launcher3.moudle.shortcut.util.BaseUtil
import com.android.launcher3.moudle.shortcut.util.IntentUtil
import com.baehug.callui.phone.view.PhoneActivity
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.activity_wallet.*

/**
 * Author : yanyong
 * Date : 2024/6/12
 * Details : 录音应用首页
 */
@Suppress("UNREACHABLE_CODE")
class RecordActivity : BaseMvpActivity<RecordInterface, RecordPresenter>(), View.OnClickListener,
    RecordInterface {

    private val TAG_: String = "RecordActivity_123123"
    private var mRecordingTime: Long = 0
    // 余数
    private var mRemainder: Long = 0
    private var mIsRecord: Boolean = false
    private var mAlertDialog: AlertDialog? = null
    private var mLastTime: Long = 0
    private var mStartTime: Long = 0;
    private var mTime: Long = 0;
    private var mBroadcastReceiver: BroadcastReceiver? = null

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // 更新UI显示录音时长
            tv_time.text = TimeUtils.getRecordTime(mRecordingTime)
        }
    }

    override fun getResourceId(): Int {
        return R.layout.activity_record
    }

    override fun createPresenter(): RecordPresenter {
        return RecordPresenter()
    }

    override fun initView() {
        super.initView()
        LogUtil.i("initView: ", LogUtil.TYPE_RELEASE)
        iv_pause.visibility = View.GONE
        iv_list.visibility = View.VISIBLE

        iv_pause.setOnClickListener(this)
        iv_start_stop.setOnClickListener(this)
        iv_list.setOnClickListener(this)
        textView.setOnClickListener {
            if (BaseUtil.continuousClicks()) {
                startActivity(Intent(this@RecordActivity, PhoneActivity::class.java))
            }
        }
    }

    override fun initData() {
        super.initData()
        mPresenter.setRecordStreamListener()

        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // 接收到呼入电弧或者微聊通话
                if (intent.action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
                    || intent.action.equals(RecordConstant.ACTION_WEILIAO_STATE_CHANGED)) {
                    LogUtil.i("initData: action " + intent.action, LogUtil.TYPE_RELEASE)
                    mIsRecord = false
                    val status = mPresenter.getStatus()
                    // 如果录音是在开始或暂停状态则删除该录音
                    if (status == AudioManagerXM.Status.STATUS_START || status == AudioManagerXM.Status.STATUS_PAUSE) {
                        iv_start_stop.setImageResource(R.drawable.svg_record_red_circle)
                        iv_pause.visibility = View.GONE
                        iv_list.visibility = View.VISIBLE
                        tv_time.text = "00:00"
                        mPresenter.stopRecord()
                        mPresenter.deleteRecordFile()
                        mPresenter.cancelRecord()
                    }
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        intentFilter.addAction(RecordConstant.ACTION_WEILIAO_STATE_CHANGED)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("onResume: ", LogUtil.TYPE_RELEASE)
    }

    override fun onClick(v: View?) {
        if (!BaseUtil.repeatedClicks(v, BaseUtil.DURATION_300)) {
            LogUtil.i("onClick: repeated click", LogUtil.TYPE_RELEASE)
            return
        }
        when (v!!.id) {
            // 暂停/重新开始
            R.id.iv_pause -> {
                LogUtil.i("onClick: iv_pause mIsRecord $mIsRecord Status " + mPresenter.getStatus(), LogUtil.TYPE_RELEASE)
                if (mPresenter.getStatus() == AudioManagerXM.Status.STATUS_START) {
                    mTime += (System.currentTimeMillis() - mStartTime)
                    mIsRecord = false
                    iv_pause.setImageResource(R.drawable.svg_record_play_white)
                } else {
                    mIsRecord = true
                    iv_pause.setImageResource(R.drawable.svg_record_pause_white)
                }
                mPresenter.recodeStartOrPause()
            }
            // 开始/停止
            R.id.iv_start_stop -> {
                LogUtil.i("onClick: iv_start_stop mIsRecord $mIsRecord", LogUtil.TYPE_RELEASE)
                if (mPresenter.getStatus() == AudioManagerXM.Status.STATUS_NO_READY) {
                    mIsRecord = true
                    iv_start_stop.setImageResource(R.drawable.svg_record_red_square)
                    iv_pause.setImageResource(R.drawable.svg_record_pause_white)
                    iv_pause.visibility = View.VISIBLE
                    iv_list.visibility = View.GONE
                    mRecordingTime = 0
                    tv_time.text = TimeUtils.getRecordTime(mRecordingTime)
                    mPresenter.startRecord()
                } else {
                    stopRecord()
                }
            }
            // 跳转录音列表
            R.id.iv_list -> {
                LogUtil.i("onClick: iv_list", LogUtil.TYPE_RELEASE)
                IntentUtil.startActivity(this, RecordListActivity::class.java)
            }
        }
    }

    /**
     * 开始计时
     */
    override fun startTimingListener() {
        mStartTime = System.currentTimeMillis();
        startTiming()
    }

    private fun startTiming() {
        handler.postDelayed(timerRunnable, 100) // 每隔一秒执行一次
    }

    override fun saveFileListener(save: Boolean) {
        LogUtil.i(TAG_, "saveFile: save $save", LogUtil.TYPE_RELEASE)
        runOnUiThread {
            ToastUtils.show(R.string.save_success)
        }
    }

    private var timerRunnable = Runnable {
        if (mIsRecord) {
            mRecordingTime = (System.currentTimeMillis() - mStartTime  + mTime) / 1000
            val time = (System.currentTimeMillis() - mStartTime  + mTime) / 100
            mRemainder = (System.currentTimeMillis() - mStartTime  + mTime) % 1000
            // LogUtil.i(TAG_, "timerRunnable: mRecordingTime $mRecordingTime mTime $mTime mStartTime $mStartTime $time", LogUtil.TYPE_RELEASE)
            if (time >= 5995) { // 录音最长10分钟自动停止，599.5秒
                stopRecord()
            } else {
                handler.sendEmptyMessage(0) // 通知UI更新
                startTiming() // 继续下一次定时
            }
        }
    }

    /**
     * 停止录音dialog
     */
    private fun showDialog() {
        mTime = 0
        val view: View = layoutInflater.inflate(R.layout.layout_dialog_view_record, null, false)
        val etName = view.findViewById<AppCompatEditText>(R.id.et_name)
        etName.setText(mPresenter.getFileName())
        etName.setSelection(mPresenter.getFileName().length)
        mAlertDialog = AlertDialog.Builder(this).setView(view).create()
        @SuppressLint("NonConstantResourceId")
        val listener = View.OnClickListener { v: View ->
                when (v.id) {
                    R.id.btn_cancel -> {
                        mAlertDialog!!.dismiss()
                        mPresenter.deleteRecordFile()
                    }
                    R.id.btn_connect -> {
                        val text = etName.text.toString()
                        if (mPresenter.searchFileName(this, text)) {
                            ToastUtils.show(R.string.record_file_name_equals)
                            return@OnClickListener
                        }
                        if (mPresenter.matchesSpecialCharacters(text)) {
                            ToastUtils.show(R.string.record_file_name_hint)
                            return@OnClickListener
                        }
                        LogUtil.i(TAG_, "saveFile: mRemainder $mRemainder text $text", LogUtil.TYPE_RELEASE)
                        if (mRemainder > 50) {
                            mRecordingTime++
                        }
                        mPresenter.saveRecordFile(this, text, mRecordingTime)
                        mAlertDialog!!.dismiss()
                    }
                }
            }
        view.findViewById<View>(R.id.btn_cancel).setOnClickListener(listener)
        view.findViewById<View>(R.id.btn_connect).setOnClickListener(listener)
        if (!mAlertDialog!!.isShowing()) {
            mAlertDialog!!.show()
        }
    }

    /**
     * 停止录音
     */
    private fun stopRecord() {
        mIsRecord = false
        iv_start_stop.setImageResource(R.drawable.svg_record_red_circle)
        iv_pause.visibility = View.GONE
        iv_list.visibility = View.VISIBLE
        tv_time.text = "00:00"
        mPresenter.stopRecord()
        showDialog()
    }

    override fun onBackPressed() {
        LogUtil.i(TAG, "onBackPressed: " + (System.currentTimeMillis() - mLastTime), LogUtil.TYPE_RELEASE)
        if (System.currentTimeMillis() - mLastTime > 2000) {
            mLastTime = System.currentTimeMillis()
            val status = mPresenter.getStatus()
            LogUtil.i(TAG, "onBackPressed: status $status", LogUtil.TYPE_RELEASE)
            if (status == AudioManagerXM.Status.STATUS_START || status == AudioManagerXM.Status.STATUS_PAUSE) {
                stopRecord()
            } else {
                ToastUtils.show(R.string.save_record_quit_record_hint)
            }
        } else {
            if (mAlertDialog != null) {
                mAlertDialog!!.dismiss()
                mPresenter.deleteRecordFile()
            }
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i(TAG, "onDestroy: cancelRecord", LogUtil.TYPE_RELEASE)
        unregisterReceiver(mBroadcastReceiver)
        mIsRecord = false
        mPresenter.cancelRecord()
    }
}