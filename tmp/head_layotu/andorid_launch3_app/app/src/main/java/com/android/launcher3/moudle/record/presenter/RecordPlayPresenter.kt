package com.android.launcher3.moudle.record.presenter

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.launcher3.common.base.BasePresenter
import com.android.launcher3.common.utils.LogUtil

/**
 * Author : yanyong
 * Date : 2024/6/14
 * Details : 录音播放功能presenter
 */
class RecordPlayPresenter : BasePresenter<RecordPlayInterface>() {

    private var mMediaPlayer: MediaPlayer? = null
    // 记录暂停播放时，已播放的时长
    private var mTime: Long = 0;
    // 记录开始播放的时间
    private var mStartTime: Long = 0
    // sp中保存的录音总时长，会比mMediaPlayer.duration时间短
    private var mTotalTime: Int = 0
    private val mHandler = Handler(Looper.getMainLooper())
    // 是否开始播放
    private var mIsStart: Boolean = false
    // 录音文件播放出错
    private var mRecordError: Boolean = false

    // 限制log打印次数
    private var mLastLogTime: Long = 0;

    /**
     * 初始化MediaPlayer
     */
    fun initMediaPlayer(file: String, totalTime: Int) {
        mRecordError = false
        mTotalTime = totalTime
        try {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer!!.setDataSource(file)
            mMediaPlayer!!.prepare()
            mMediaPlayer!!.setOnTimedTextListener { mp, text ->
                val curPos = mp.currentPosition
                LogUtil.i(TAG, "setOnTimedTextListener: curPos $curPos text $text", LogUtil.TYPE_RELEASE)
                if (isViewAttached) {
                    view.onProgressListener(curPos, (mTime / 1000).toInt())
                }
            }
            mMediaPlayer!!.setOnPreparedListener { mp ->
                val curPos = mp.currentPosition
                LogUtil.i(TAG, "setOnPreparedListener: curPos $curPos", LogUtil.TYPE_RELEASE)
                if (isViewAttached) {
                    view.onProgressListener(curPos, (mTime / 1000).toInt())
                }
            }
        } catch (e: Exception) {
            mRecordError = true
            LogUtil.e(TAG, "initMediaPlayer: ", e, LogUtil.TYPE_RELEASE)
        }
    }

    /**
     * 暂停/播放
     */
    fun playOrPause() {
        // 录音出错
        if (mRecordError) {
            LogUtil.i(TAG, "playOrPause: mRecordError $mRecordError", LogUtil.TYPE_RELEASE)
            if (isViewAttached) {
                view.onRecordError()
            }
            return
        }
        if (isPlay()) {
            mTime += System.currentTimeMillis() - mStartTime
            Log.i(TAG, "playOrPause: mTime $mTime, duration ${mMediaPlayer!!.duration}")
            if (mTime < mMediaPlayer!!.duration - 1000) {
                mMediaPlayer!!.pause()
                mIsStart = false
            }
        } else {
            mMediaPlayer!!.start()
            mIsStart = true
            mStartTime = System.currentTimeMillis()
            LogUtil.i(TAG, "startTimer: mTime $mTime", LogUtil.TYPE_RELEASE)
            if (mTime === 0L) {
                if (isViewAttached) {
                    view.onProgressListener(0, 0)
                }
            }
            startTimer()
        }
    }

    /**
     * 是否播放中
     */
    fun isPlay(): Boolean {
        if (mMediaPlayer == null) {
            return false
        }
        return mMediaPlayer!!.isPlaying
    }

    /**
     * 停止播放
     */
    fun stop() {
        mIsStart = false
        mTime = 0
        if (mMediaPlayer == null) {
            return
        }
        mMediaPlayer!!.stop()
        mMediaPlayer!!.release()
        mMediaPlayer = null
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null)
        }
    }

    private var mTimerRunnable = Runnable {
        if (mIsStart) {
            if (mMediaPlayer != null) {
                try {
                    val duration = mMediaPlayer!!.duration
                    val curPos = mMediaPlayer!!.currentPosition
                    if (System.currentTimeMillis() - mLastLogTime > 1000) {
                        mLastLogTime = System.currentTimeMillis();
                        LogUtil.i(TAG, "startTimer: curPos $curPos duration $duration " +
                                "mStartTime $mStartTime mTime $mTime mTotalTime $mTotalTime mRecordError $mRecordError", LogUtil.TYPE_DEBUG)
                    }
                    if (curPos == 0 || mTime >= duration) {
                        mTime = 0
                    }
                    var time = (System.currentTimeMillis() - mStartTime + mTime).toInt()
                    if (time >= duration || time >= mTotalTime * 1000) {
                        time = mTotalTime
                    } else {
                        if (time >= mTotalTime * 1000 - 100) {
                            time = mTotalTime
                        } else {
                            time /= 1000
                        }
                    }
                    if (isViewAttached && !mRecordError) {
                        view.onProgressListener(curPos * 100 / duration, time)
                    }
                } catch (e: Exception) {
                    LogUtil.e(TAG, "startTimer: ${e.message}", LogUtil.TYPE_RELEASE)
                }
            }
            startTimer()
        }
    }

    /**
     * 开始计时
     */
    private fun startTimer() {
        if (mHandler != null) {
            mHandler.postDelayed(mTimerRunnable, 50)
        }
    }

    /**
     * 取消计时
     */
    fun cancelTimer() {
        mIsStart = false
        mTime = 0
    }

    /**
     * 设置媒体音量
     */
    fun setMediaVolume(context: Context, volume: Int) {
        val audioManager = context.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max * volume / 100, AudioManager.FLAG_PLAY_SOUND)
    }

    /**
     * 获取媒体音量
     */
    fun getMediaVolume(context: Context): Float {
        val audioManager = context.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        return current.toFloat() / max.toFloat() * 100
    }
}