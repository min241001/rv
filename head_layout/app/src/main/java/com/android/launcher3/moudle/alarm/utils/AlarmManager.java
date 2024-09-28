package com.android.launcher3.moudle.alarm.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

/**
 * 闹钟铃声
 */
public class AlarmManager {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Context context;

    public AlarmManager(Context context) {
        mediaPlayer = new MediaPlayer();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.context = context;
    }

    public void startAlarm() {
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setDataSource(context.getResources().openRawResourceFd(com.android.launcher3.common.R.raw.beep));
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

            vibrator.vibrate(new long[]{1000, 1000, 1000, 1000}, 0); // Example vibration pattern
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        vibrator.cancel();
    }
}
