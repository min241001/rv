package com.android.launcher3.moudle.shortcut.callback;

/**
 * @Author: shensl
 * @Description：开关变化回调
 * @CreateDate：2024/1/11 11:52
 * @UpdateUser: shensl
 */
public interface SwitchChangeCallBack extends BaseCallBack {

    enum SwitchStateType {
        SIM,
        WIFI,
        NETWORK,
        BLUETOOTH,
        SOUND
    }

    void onSwitchStateTypeChange(SwitchStateType type, boolean state);

}
