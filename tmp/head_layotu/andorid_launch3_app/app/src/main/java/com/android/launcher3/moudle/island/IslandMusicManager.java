package com.android.launcher3.moudle.island;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.launcher3.moudle.bloodoxygen.model.MusicInfo;
import com.android.launcher3.moudle.notification.notity.NotificationListener;

import java.util.ArrayList;
import java.util.List;

public class IslandMusicManager {

    private Context context;
    private MediaSessionManager mediaSessionManager;
    private ComponentName mNotifyReceiveService;
    private MusicInfo musicInfo;

    public IslandMusicManager(Context context) {
        this.context = context;
        init();
    }

    public void init() {

        mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        mNotifyReceiveService = new ComponentName(context, NotificationListener.class);
        loadMusicControl();
        mediaSessionManager.addOnActiveSessionsChangedListener(new MediaSessionManager.OnActiveSessionsChangedListener() {
            @Override
            public void onActiveSessionsChanged(@Nullable List<MediaController> controllers) {
                registerListener();
            }
        }, mNotifyReceiveService);
    }

    private void registerListener() {

        try {
            List<MediaController> controllers = mediaSessionManager.getActiveSessions(mNotifyReceiveService);
            Log.e("TAG", controllers.size() + "");
            for (MediaController controller : controllers) {
                MediaControllerCompat controllerCompat = new MediaControllerCompat(context, MediaSessionCompat.Token.fromToken(controller.getSessionToken()));
                Log.e("TAG", controllerCompat.getPlaybackState().toString());
                controllerCompat.registerCallback(mediaCompactCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void unRegisterListener() {
        try {
            List<MediaController> controllers = mediaSessionManager.getActiveSessions(mNotifyReceiveService);
            for (MediaController controller : controllers) {
                MediaControllerCompat controllerCompat = new MediaControllerCompat(context, MediaSessionCompat.Token.fromToken(controller.getSessionToken()));
                controllerCompat.unregisterCallback(mediaCompactCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadMusicControl() {
        try {
            List<MediaController> mediaControllers = mediaSessionManager.getActiveSessions(mNotifyReceiveService);
            List<MusicInfo> musicInfos = new ArrayList<>();
            for (MediaController controller : mediaControllers) {
                MediaControllerCompat controllerCompat = new MediaControllerCompat(context, MediaSessionCompat.Token.fromToken(controller.getSessionToken()));
                MusicInfo itemMusicInfo = new MusicInfo();
                String pkgName = controllerCompat.getPackageName();

                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(pkgName, 0);
                itemMusicInfo.setAppName(applicationInfo.loadLabel(context.getPackageManager()).toString());
                itemMusicInfo.setPkgName(pkgName);
                PlaybackStateCompat playbackStateCompat = controllerCompat.getPlaybackState();
                if (playbackStateCompat == null) {
                    Log.e("TAG", "播放状态： PlaybackStateCompat 为空");
                }
                itemMusicInfo.setMusicState(playbackStateCompat != null && playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING);
                itemMusicInfo.setTitle("");
                MediaMetadataCompat mediaMetadataCompat = controllerCompat.getMetadata();
                if (mediaMetadataCompat == null) {
                    Log.e("TAG", "播放数据： MediaMetadataCompat 为空");
                }
                if (mediaMetadataCompat != null) {
                    MediaDescriptionCompat descriptionCompat = mediaMetadataCompat.getDescription();
                    if (descriptionCompat != null) {
                        CharSequence musicTitle = descriptionCompat.getTitle();
                        if (!TextUtils.isEmpty(musicTitle)) {
                            Log.e("TAG", "当前播放：" + descriptionCompat.getTitle().toString());
                            itemMusicInfo.setTitle(musicTitle.toString());

                        }

                    }
                }
                musicInfos.add(itemMusicInfo);
                musicInfo = itemMusicInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void preMusic() {

        MediaControllerCompat controllerCompat = findMediaControl(musicInfo);
        if (controllerCompat != null) {
            Log.i("TAG", "lastMusic->pkgName:" + controllerCompat.getPackageName());
            boolean isDown = controllerCompat.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS, KeyEvent.ACTION_DOWN));
            boolean isUp = controllerCompat.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS, KeyEvent.ACTION_UP));
            boolean isSucc = isDown && isUp;
            if (!isSucc) {
                MediaControllerCompat.TransportControls transportControls = controllerCompat.getTransportControls();
                if (transportControls != null)
                    transportControls.skipToPrevious();
            }
        }
    }

    public void playOrPause() {


/*        MediaControllerCompat controllerCompat = findMediaControl(musicInfo);
        boolean isPlay = musicInfo.isMusicState();
        Log.e("TAG", "isPlay: " + musicInfo.isMusicState());
        if (controllerCompat != null) {
            Log.i("TAG", "playOrPause->pkgName:" + controllerCompat.getPackageName());
            boolean isDown = controllerCompat.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.ACTION_DOWN));
            boolean isUp = controllerCompat.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.ACTION_UP));
            boolean isSucc = isDown && isUp;
            if (!isSucc) {
                MediaControllerCompat.TransportControls transportControls = controllerCompat.getTransportControls();
                if (transportControls != null) {
                    Log.e("TAG", "----transportControls-----");
                    if (isPlay)
                        transportControls.pause();
                    else
                        transportControls.play();
                }
            }
        }*/

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
    }

    public void nexMusic() {

        MediaControllerCompat controllerCompat = findMediaControl(musicInfo);
        if (controllerCompat != null) {
            Log.i("TAG", "nextMusic->pkgName:" + controllerCompat.getPackageName());
            boolean isDown = controllerCompat.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT, KeyEvent.ACTION_DOWN));
            boolean isUp = controllerCompat.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT, KeyEvent.ACTION_UP));
            boolean isSucc = isDown && isUp;
            if (!isSucc) {
                MediaControllerCompat.TransportControls transportControls = controllerCompat.getTransportControls();
                if (transportControls != null)
                    transportControls.skipToNext();
            }
        }
    }

    public MediaControllerCompat findMediaControl(MusicInfo musicInfo) {
        try {
            List<MediaController> mediaControllers = mediaSessionManager.getActiveSessions(mNotifyReceiveService);
            for (MediaController controller : mediaControllers) {
                MediaControllerCompat controllerCompat = new MediaControllerCompat(context, MediaSessionCompat.Token.fromToken(controller.getSessionToken()));
                if (musicInfo.getPkgName().equals(controllerCompat.getPackageName()))
                    return controllerCompat;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public MediaControllerCompat.Callback mediaCompactCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onSessionReady() {
            Log.i("TAG", "onSessionReady");
            loadMusicControl();
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            //播放状态发生改变
            if (state != null) {
                boolean isPlaying = state.getState() == state.STATE_PLAYING;
                Log.e("TAG", "isPlaying: " + isPlaying);
            }
            loadMusicControl();
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            //播放内容发生改变
            if (metadata != null) {
                String trackName =
                        metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
                String artistName =
                        metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
                String albumArtistName =
                        metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
                String albumName =
                        metadata.getString(MediaMetadata.METADATA_KEY_ALBUM);
                Log.e("TAG", "trackName:  " + trackName + "  artistName  " + artistName + "  albumArtistName  " + albumArtistName + "  albumName  " + albumName);

                loadMusicControl();
            }
        }
    };

    public String getPlayContent() {

        String str = "";

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (audioManager.isMusicActive()) {


        }

        return str;
    }

}
