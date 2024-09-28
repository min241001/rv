package com.android.launcher3.moudle.shortcut.view;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2024/1/4 15:35
 * @UpdateUser: shensl
 */
public interface IShortCutMenuView {

    void setOperatorName(String operatorName);

    void setNetworkType(String networkType);

    void setSignalImageResource(int resourceId);

    void setWifiImageResource(int resourceId);

    void setSIMCardInserted();

    void setNoSIMCardInserted();

    void setWifiOpen();

    void setWifiClose();

}
