package com.android.launcher3.receiver.core;

import android.content.Context;
import android.content.Intent;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/5 09:34
 * @UpdateUser: jamesfeng
 */
public interface ReceiverListener {
    void onReceive(Context context, Intent intent);
}
