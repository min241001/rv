package com.android.launcher3.utils;

import android.app.Instrumentation;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ThreadPoolUtils;

public class KeyCodeUtil {

    /**
     * 发送模拟按键
     *
     * @param keyCode 按键指令
     */
    public static void sendKeyCode(int keyCode) {
        ThreadPoolUtils.getExecutorService().execute(() -> {
            try {
                Instrumentation ins = new Instrumentation();
                ins.sendKeyDownUpSync(keyCode);
            } catch (Exception e) {
                LogUtil.e("sendKeyCode: " + e.getMessage(), LogUtil.TYPE_RELEASE);
            }
        });
    }

}
