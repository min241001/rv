package com.android.launcher3.moudle.stopwatch.view;

import java.util.ArrayList;

public interface StopWatchPagesView {
    void updateTimerText(String data);

    void updateLapText(ArrayList<String> list);

    void resetTimer();

    void lapTimer();

    void pauseTimer();

    void startTimer();

    void updateTimer(long l);
}
