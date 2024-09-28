package com.android.launcher3.moudle.island;

import static com.android.launcher3.moudle.island.IslandFloat.APP_ICON_FLOAT;
import static com.android.launcher3.moudle.island.IslandFloat.AUDIO_FLOAT;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.utils.AudioPlayer;
import com.android.launcher3.common.utils.DensityUtils;
import com.android.launcher3.common.utils.ScreenUtils;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * 电话，短信，微聊 消息提示
 */
public class MsgIslandDialog {

    private AudioPlayer audioPlayer;
    private ScreenUtils screenUtils;
    public final static String SINGLE_MSG_FLOAT = "SINGLE_MSG_FLOAT";
    private Handler handler = new Handler(Looper.getMainLooper());
    private Drawable drawable;
    private String hint;

    private static MsgIslandDialog instance;

    public static MsgIslandDialog getInstance() {
        if (instance == null) {
            instance = new MsgIslandDialog();
        }
        return instance;
    }

    public void init(Drawable drawable, String str) {

        this.drawable = drawable;
        this.hint = str;

        //当前音频悬浮存在 隐藏音频悬浮
        IslandDiffNotification islandDiffNotification = IslandDiffNotification.getInstance();
        if (islandDiffNotification != null && islandDiffNotification.hasAudio() && EasyFloat.isShow(AUDIO_FLOAT)) {
            EasyFloat.hide(AUDIO_FLOAT);
        }

        audioPlayer = AudioPlayer.Companion.getInstance(CommonApp.getInstance());
        screenUtils = new ScreenUtils(CommonApp.getInstance());
        show();
        play();
        screenUtils.acquireScreenLock();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EasyFloat.dismiss(SINGLE_MSG_FLOAT);
                stop();
                handler.removeCallbacksAndMessages(null);
                screenUtils.releaseScreenLock();
                EasyFloat.show(AUDIO_FLOAT);
            }
        }, 3000);
    }

    public void show() {

        EasyFloat.with(CommonApp.getInstance())
                .setTag(SINGLE_MSG_FLOAT)
                .setLayout(R.layout.dialog_island_hint_msg, new OnInvokeView() {
                    @Override
                    public void invoke(View view) {
                        TextView tv_text = view.findViewById(R.id.tv_text);
                        RoundedImageView iv_icon = view.findViewById(R.id.iv_icon);
                        if (hint.length() > 6) {
                            tv_text.setSingleLine(true);
                            tv_text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            tv_text.setMarqueeRepeatLimit(-1);
                            tv_text.setOverScrollMode(TextView.OVER_SCROLL_NEVER);
                            tv_text.setFocusable(true);
                            tv_text.setHorizontallyScrolling(true);
                            tv_text.setFreezesText(true);
                            tv_text.setSelected(true);
                            tv_text.requestFocus();
                        }

                        tv_text.setText(hint);
                        if (drawable != null) {
                            iv_icon.setImageDrawable(drawable);
                        }
                    }

                })

                .setShowPattern(ShowPattern.ALL_TIME)
                .setAnimator(null)
                .setGravity(Gravity.CENTER_HORIZONTAL, 0, DensityUtils.dip2px(10, CommonApp.getInstance()))
                .show();

    }

    private void play() {
        AudioManager audioManager = (AudioManager) CommonApp.getInstance().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            return;
        }

        if (audioPlayer != null && audioPlayer.isPlaying()) {
            return;
        }

        if (audioPlayer != null) {
            audioPlayer.play(com.android.launcher3.common.R.raw.fluorine, false);
        }

    }

    private void stop() {
        audioPlayer.stop();
    }

}
