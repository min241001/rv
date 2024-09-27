package com.android.launcher3.common.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.annotation.RawRes
import com.android.launcher3.common.R
import java.io.IOException

class AudioPlayer private constructor(private val mContext: Context) {
    private val mMediaPlayer: MediaPlayer = MediaPlayer()
    private var mIsPrepared = false

    init {
        mMediaPlayer.setVolume(1f, 1f)
        mMediaPlayer.setOnPreparedListener { mp: MediaPlayer? ->
            mIsPrepared = true
        }
        mMediaPlayer.setOnCompletionListener { mp: MediaPlayer? -> mMediaPlayer.stop() }
        mMediaPlayer.setOnErrorListener { mp: MediaPlayer?, what: Int, extra: Int ->
            mIsPrepared = false
            false
        }
        mMediaPlayer.setOnSeekCompleteListener { mp: MediaPlayer -> mp.stop() }
    }

    fun play(uri: Uri?,loop : Boolean = false) {
        try {
            mMediaPlayer.reset()
            mMediaPlayer.setDataSource(mContext, uri!!)
            mMediaPlayer.prepare()
            mMediaPlayer.isLooping = loop
            mMediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
            mIsPrepared = false
        }
    }


    fun play(@RawRes id : Int, loop : Boolean = false) {
        try {
            mMediaPlayer.reset()
            mMediaPlayer.setDataSource(mContext.resources.openRawResourceFd(id))
            mMediaPlayer.prepare()
            mMediaPlayer.isLooping = loop
            mMediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
            mIsPrepared = false
        }
    }

    fun pause() {
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.pause()
        }
    }

    fun isPlaying() : Boolean{
        return mMediaPlayer.isPlaying
    }


    fun resume() {
        if (mIsPrepared && !mMediaPlayer.isPlaying) {
            mMediaPlayer.start()
        }
    }

    fun stop() {
        if (mMediaPlayer.isPlaying || mIsPrepared) {
            mMediaPlayer.stop()
            mIsPrepared = false
        }
    }

    companion object {
        private var sInstance: AudioPlayer? = null
        fun getInstance(context: Context): AudioPlayer? {
            if (sInstance == null) {
                synchronized(AudioPlayer::class.java) {
                    if (sInstance == null) {
                        sInstance =
                            AudioPlayer(context.applicationContext)
                    }
                }
            }
            return sInstance
        }
    }
}
