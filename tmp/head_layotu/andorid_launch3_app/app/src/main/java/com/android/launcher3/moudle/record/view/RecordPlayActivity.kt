package com.android.launcher3.moudle.record.view

import android.annotation.SuppressLint
import android.os.Handler
import android.view.View
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseMvpActivity
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.common.utils.ToastUtils
import com.android.launcher3.moudle.notification.util.TimeUtils
import com.android.launcher3.moudle.record.base.RecordConstant.KEY_BUNDLE
import com.android.launcher3.moudle.record.modle.RecordBean
import com.android.launcher3.moudle.record.presenter.RecordPlayInterface
import com.android.launcher3.moudle.record.presenter.RecordPlayPresenter
import com.android.launcher3.moudle.record.widget.VertSeekBar.OnStateChangeListener
import com.android.launcher3.moudle.shortcut.util.BaseUtil
import kotlinx.android.synthetic.main.activity_record_list.*
import kotlinx.android.synthetic.main.activity_record_play.*

/**
 * Author : yanyong
 * Date : 2024/6/12
 * Details : 播放录音应
 */
@Suppress("UNREACHABLE_CODE")
class RecordPlayActivity : BaseMvpActivity<RecordPlayInterface, RecordPlayPresenter>(),
    View.OnClickListener, RecordPlayInterface {

    private var mTotalTime: Long = 0

    private val mHandler = Handler()

    private var mStartTime: Long = 0

    private var mSlideTime: Long = 0

    // 上一次显示时间，降低UI刷新效率
    private var mLastTime: Int = 0

    override fun getResourceId(): Int {
        return R.layout.activity_record_play
    }

    override fun createPresenter(): RecordPlayPresenter {
        return RecordPlayPresenter()
    }

    override fun initView() {
        super.initView()
        LogUtil.i("initView: ", LogUtil.TYPE_RELEASE)
        iv_volume.setOnClickListener(this)
        iv_play.setOnClickListener(this)

        // 声音进度条
        vsb.setOnStateChangeListener(object : OnStateChangeListener {
            override fun onStartTouch(view: View) {}

            override fun onStateChangeListener(view: View, progress: Float, indicatorOffset: Float) {}

            override fun onStopTrackingTouch(view: View, progress: Float) {
                LogUtil.i("initView: onStopTrackingTouch progress $progress", LogUtil.TYPE_RELEASE)
                mSlideTime = System.currentTimeMillis()
                mPresenter.setMediaVolume(this@RecordPlayActivity, progress.toInt())
            }
        })
    }

    override fun initData() {
        super.initData()
        val bean: RecordBean = intent.getSerializableExtra(KEY_BUNDLE) as RecordBean
        LogUtil.i(TAG, "initData: $bean", LogUtil.TYPE_RELEASE)
        tv_name.text = bean.name
        mTotalTime = bean.duration
        tv_time.text = covertTime(0)
        mPresenter.initMediaPlayer(bean.file, bean.duration.toInt())
        vsb.setmProgress(mPresenter.getMediaVolume(this))
    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("onResume: ", LogUtil.TYPE_RELEASE)
    }

    override fun onClick(v: View?) {
        if (!BaseUtil.repeatedClicks(v)) {
            LogUtil.i("onClick: repeated click", LogUtil.TYPE_RELEASE)
            return
        }
        when (v!!.id) {
            R.id.iv_volume -> {
                if (vsb.visibility == View.GONE) {
                    vsb.visibility = View.VISIBLE
                    delayHideVolumeSeekbar()
                }
            }
            R.id.iv_play -> {
                val isPlay = mPresenter.isPlay()
                LogUtil.i(TAG, "onClick: iv_play isPlay $isPlay", LogUtil.TYPE_RELEASE)
                vsb.visibility = View.GONE
                if (isPlay) {
                    iv_play.setImageResource(R.drawable.svg_record_start_circle)
                } else {
                    iv_play.setImageResource(R.drawable.svg_record_pause_circle)
                }
                mPresenter.playOrPause()
            }
        }
    }

    /**
     * 延时隐藏音量进度条
     */
    private fun delayHideVolumeSeekbar() {
        mStartTime = System.currentTimeMillis();
        mHandler.postDelayed({
            if (mSlideTime > mStartTime) {
                delayHideVolumeSeekbar()
            } else {
                vsb.visibility = View.GONE
            }
        }, 3000)
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressListener(position: Int, time: Int) {
        runOnUiThread {
            if (time == mTotalTime.toInt()) {
                LogUtil.i(TAG, "onProgressListener: play completed, time $time", LogUtil.TYPE_RELEASE)
                tv_time.text = covertTime(time)
                mLastTime = -1
                iv_play.setImageResource(R.drawable.svg_record_start_circle)
                mPresenter.cancelTimer()
            }
            if (mLastTime != time) {
                LogUtil.i(TAG, "onProgressListener: position $position time $time", LogUtil.TYPE_RELEASE)
                mLastTime = time
                tv_time.text = covertTime(time)
            }
        }
    }

    override fun onRecordError() {
        LogUtil.i("onRecordError: ", LogUtil.TYPE_RELEASE)
        ToastUtils.show(R.string.record_file_error)
        iv_play.setImageResource(R.drawable.svg_record_start_circle)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i("onDestroy: ", LogUtil.TYPE_RELEASE)
        mPresenter.cancelTimer()
        mPresenter.stop()
    }

    /**
     * 根据播放进度转换时间
     *
     * @param position 播放进度
     */
    private fun covertTime(time: Int): String {
        val sb = StringBuilder()
        return sb.append(TimeUtils.getRecordTime(time.toLong()))
            .append("/")
            .append(TimeUtils.getRecordTime(mTotalTime))
            .toString()
    }
}