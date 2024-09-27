package com.android.launcher3.common.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.android.launcher3.common.CommonApp;

/**
 * 音频焦点获取与释放
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "AudioFocusManager";
    private final AudioManager mAudioManager;

    // 播放标记
    private int mFlag;

    private AudioFocusRequest mFocusRequest;

    private static AudioFocusManager instance;

    // 是否省电模式
    private boolean mIsPowerModel;

    private Handler mHandler = new Handler();

    private AudioFocusManager(){
        mAudioManager = (AudioManager) CommonApp.getInstance().getSystemService(Context.AUDIO_SERVICE);
    }
    public static AudioFocusManager getInstance() {
        if (instance == null) {
            instance = new AudioFocusManager();
        }
        return instance;
    }

    public void getFlag() {
        if (mAudioManager.isMusicActive()) {
            mFlag = 1;//正在播放
        } else {
            mFlag = 2;//暂停播放
        }
        Log.e(TAG, "initData: mFlag $mFlag isMusicActive " + mAudioManager.isMusicActive());
    }

    /**
     * 暂时获取音频焦点
     */
    public void requestAudioFocus() {
        Log.i(TAG, "setAudioStatus: mFlag $mFlag isMusicActive " + mAudioManager.isMusicActive());
        requestAudioFocus(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    /**
     * 暂时获取音频焦点
     * @param focusGain 获取焦点参数
     */
    public void requestAudioFocus(int focusGain) {
        Log.i(TAG, "setAudioStatus: isMusicActive " + mAudioManager.isMusicActive() + " focusGain " + focusGain);
        if (mFlag == 1) {
            mFlag = 2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // AudioAttributes 配置(多媒体场景，申请的是音乐流)
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                // 初始化AudioFocusRequest
                mFocusRequest = new AudioFocusRequest.Builder(focusGain)
                        .setAudioAttributes(audioAttributes)
                        .setAcceptsDelayedFocusGain(true) // 设置AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK会暂停，系统不会压低声音
                        .setWillPauseWhenDucked(true) // 设置焦点监听回调
                        .setOnAudioFocusChangeListener(this::onAudioFocusChange)
                        .build();
                //申请焦点
                mAudioManager.requestAudioFocus(mFocusRequest);
            } else {
                //android8.0之前申请焦点方式
                mAudioManager.requestAudioFocus(
                        this,
                        AudioManager.STREAM_MUSIC,
                        focusGain
                );
            }
        }
    }

    @Override
    public void onAudioFocusChange(int i) {

    }

    /**
     * 释放音频焦点
     */
    public void freedAudioFocus() {
        Log.i(TAG, "freedAudioFocus: " + mIsPowerModel);
        freedAudioFocus(5000);
    }

    /**
     * 释放音频焦点
     */
    public void freedAudioFocusNoDelay() {
        Log.i(TAG, "freedAudioFocus: " + mIsPowerModel);
        freedAudioFocus(0);
    }

    /**
     * 释放音频焦点
     */
    private void freedAudioFocus(long delayMillis) {
        Log.i(TAG, "freedAudioFocus: " + mIsPowerModel);
        if (mIsPowerModel) {
            return;
        }
        mHandler.postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mFocusRequest != null) {
                    mAudioManager.abandonAudioFocusRequest(mFocusRequest);
                    mFocusRequest = null;
                }
            } else {
                mAudioManager.abandonAudioFocus(AudioFocusManager.this);
            }
        }, delayMillis);

    }

    /**
     * 设置是否省电模式标记
     *
     * @param model 模式
     */
    public void setPowerModel(boolean model) {
        this.mIsPowerModel = model;
    }
}
