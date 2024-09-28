package com.android.launcher3.common.mode

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.android.launcher3.common.R
import com.android.launcher3.common.utils.BaseActivityManager
import com.android.launcher3.common.utils.AudioPlayer
import kotlin.math.roundToInt

class LookWatchModel(val mContext:Context) {

    private val mediaPlayer = mContext.let { AudioPlayer.getInstance(it) }

    val mAudioManager = mContext.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager

    private val volume by lazy { getStream() }

    fun play(){
        if (mediaPlayer!!.isPlaying()){
            return
        }
        setStream(100)
        mediaPlayer.play(R.raw.cygnus)
    }

    fun stop(){
        setStream(volume)
        mediaPlayer?.stop()
    }

    private fun getStream(): Int {
        val max = mAudioManager.getStreamMaxVolume(STREAM_MUSIC)
        val current = mAudioManager.getStreamVolume(STREAM_MUSIC)
        return (current.toFloat() / max.toFloat() * 100).roundToInt()
    }

    private fun setStream(volume :Int){
        val max = mAudioManager.getStreamMaxVolume(STREAM_MUSIC)
        mAudioManager.setStreamVolume(STREAM_MUSIC, max * volume/100 , AudioManager.FLAG_PLAY_SOUND)
    }

    fun setLight(status: Boolean) {
        val activity = BaseActivityManager.getInstance().currentActivity
        val layoutParams = activity.window.attributes
        if (status) {
            layoutParams.flags =
                layoutParams.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        } else {
            layoutParams.flags =
                layoutParams.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
        }
        activity.window.attributes = layoutParams
    }
}