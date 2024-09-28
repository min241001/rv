package com.android.launcher3.common.utils;

import android.content.Context;
import android.os.PowerManager;


public class ScreenUtils {

    private Context context;
    private PowerManager.WakeLock wakeLock;

    public ScreenUtils(Context context) {
        this.context = context;
    }

    public void acquireScreenLock() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag");
        wakeLock.acquire();
    }

    public void releaseScreenLock() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
