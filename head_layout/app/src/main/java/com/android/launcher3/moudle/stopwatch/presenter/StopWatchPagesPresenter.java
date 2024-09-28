package com.android.launcher3.moudle.stopwatch.presenter;

import android.app.Activity;

import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.moudle.stopwatch.view.StopWatchPagesView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class StopWatchPagesPresenter extends BasePresenter<StopWatchPagesView> {
    private Activity activity;

    private boolean isRunning;
    private ArrayList<String> lapList = new ArrayList<>();
    private long pausedTime;
    private long startTime;
    private Timer timer;
    private long elapsedTime;//已用时


    public StopWatchPagesPresenter(Activity activity) {
        this.activity = activity;
    }

    public void startPauseTimer() {
        if (isRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }


    public void startTimer() {

        if (!isRunning) {
            isRunning = true;
            getView().startTimer();//开始计时 点击按钮后 显示暂停按钮
            if (pausedTime == 0L) {
                startTime = System.currentTimeMillis();
            } else {
                long pausedDuration = System.currentTimeMillis() - pausedTime;
                startTime += pausedDuration;
            }
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    getView().updateTimer(elapsedTime);//表盘时间设置
                    updateTimerText();
                }
            }, 0, 10);

            if (elapsedTime > 30 * 60 * 1000) {
                //超过30分钟 结束
                removeTimer();
            }
        }
    }

    public void pauseTimer() {

        if (isRunning) {
            isRunning = false;
            getView().pauseTimer();
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            pausedTime = System.currentTimeMillis();
        }
    }

    public void resetLapTimer() {
        if (isRunning) {
            //秒表计时中 左下侧显示分段图标 计时列表添加
            lapTimer();
        } else {
            //秒表暂停计时 左下侧显示重置
            resetTimer();
        }
    }

    public void resetTimer() {
        isRunning = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        elapsedTime = 0L;
        startTime = 0L;
        pausedTime = 0L;
        lapList.clear();
        getView().resetTimer();


    }

    public void lapTimer() {
        if (isRunning) {
            String lapTime = getFormattedTime();
            lapList.add(lapTime);
            Collections.reverse(lapList);
            getView().updateLapText(lapList);

        } else {
            getView().lapTimer();
        }
    }

    private void updateTimerText() {
        String timeText = getFormattedTime();
        if (getView() != null) {
            getView().updateTimerText(timeText);
        }
    }

    private String getFormattedTime() {
        long milliseconds = elapsedTime % 1000;
        long seconds = elapsedTime / 1000 % 60;
        long minutes = elapsedTime / (1000 * 60) % 60;
        long hours = elapsedTime / (1000 * 60 * 60) % 24;

        String millisecondsString = String.format("%03d", milliseconds);
        if (millisecondsString.length() > 2) {
            millisecondsString = millisecondsString.substring(0, 2);
        }

        return String.format("%02d:%02d.%s", minutes, seconds, millisecondsString);
    }

    public void removeTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
