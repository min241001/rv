package com.android.launcher3.common.constant;

/**
 * @Author:   pengmin
 * @CreateDate：2024/8/31 11:30
 */
public class Constants {
    public static final boolean logFlag = true;//关掉提示
    public static final String temp_tag1 = "apps0";
    public static final String SET_VIEW_PAGER_ACTION = "com.android.launcher3.moudle.touchup.view.action";
    public class msgWhat{
        public static final int OPEN_EDITMODE_STATUS= 0x01;
        public static final int CLOSE_EDITMODE_STATUS= 0x02;
        public static final int UPDATE_RV_ITEM = 0x03;
        public static final int UPDATE_APPS_DATA = 0x04;
        public static final int UPDATE_APPS_DATA2 = 0x05;
        public static final int UPDATE_PACKAGE_DELAYED = 0X06;
    }
    public class eventWhat{
        public static final int SET_PACKAGE_DATA= 0x01;
    }

}
