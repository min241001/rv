package com.android.launcher3.moudle.shortcut.callback;

/**
 * @Author: shensl
 * @Description：电量回调
 * @CreateDate：2024/1/11 11:57
 * @UpdateUser: shensl
 */
public interface BatteryCallBack extends BaseCallBack {

    void onBatteryChange(int battery);

}
