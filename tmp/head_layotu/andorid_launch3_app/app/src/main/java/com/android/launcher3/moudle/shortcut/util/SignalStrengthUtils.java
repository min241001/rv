package com.android.launcher3.moudle.shortcut.util;

import com.android.launcher3.R;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: shensl
 * @Description：信号强度工具类
 * @CreateDate：2024/1/10 10:00
 * @UpdateUser: shensl
 */
public class SignalStrengthUtils {

    private static List<Integer> mSignalResources = Arrays.asList(
            R.drawable.svg_3_signal_0,
            R.drawable.svg_3_signal_1,
            R.drawable.svg_3_signal_2,
            R.drawable.svg_3_signal_3
    );

    private static List<Integer> mWifiResources = Arrays.asList(
            R.drawable.svg_3_wifi_0,
            R.drawable.svg_3_wifi_1,
            R.drawable.svg_3_wifi_2,
            R.drawable.svg_3_wifi_white
    );

    /**
     * 获取信号强度
     *
     * @param signalStrength
     * @return
     */
    private static int getSignalStrengthLevel(int signalStrength) {
        //        if (signalStrength >= -110) {
//            return 1;
//        } else if (signalStrength >= -125) {
//            return 2;
//        } else if (signalStrength >= -130) {
//            return 3;
//        } else if (signalStrength >= -135) {
//            return 4;
//        } else {
//            return 0;
//        }

        if (signalStrength == -1 || signalStrength == 0){
            return 1;
        }else {
            return signalStrength;
        }
    }

    /**
     * 获取信号强度
     * 0 rssi<=-100
     * 1 (-100, -88]
     * 2 (-88, -77]
     * 3 (-66, -55]
     * 4 rssi>=-55
     *
     * @param signalStrength
     * @return
     */
    private static int getWifiSignalStrengthLevel(int signalStrength) {
        if (signalStrength >= -55) {
            return 3;
        } else if (signalStrength >= -66) {
            return 2;
        } else if (signalStrength >= -77) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 获取信号强度资源文件
     *
     * @param signalStrength 信号强度
     * @return 对应信号强度的图标
     */
    public static int getSignalResourceId(int signalStrength) {
        if (signalStrength < 0) {
            signalStrength = 0;
        } else if (signalStrength > 3) {
            signalStrength = 3;
        }
        return mSignalResources.get(getSignalStrengthLevel(signalStrength));
    }

    /**
     * 获取移动网络信号强度资源文件
     *
     * @param signalStrength 信号强度
     * @return 对应信号强度的图标
     */
    public static int getMobileSignalResourceId(int signalStrength) {
        if (signalStrength < 0) {
            signalStrength = 0;
        } else if (signalStrength > 3) {
            signalStrength = 3;
        }
        return mSignalResources.get(getSignalStrengthLevel(signalStrength));
    }

    /**
     * 获取wifi信号强度资源文件
     *
     * @param signalStrength 信号强度
     * @return 对应信号强度的图标
     */
    public static int getWifiResourceId(int signalStrength) {
        return mWifiResources.get(getWifiSignalStrengthLevel(signalStrength));
    }

    /**
     * 获取信号强度资源文件
     *
     * @param level 信号强度
     * @return
     */
    public static int getWifiLevelResourceId(int level) {
        if (level < 0) {
            level = 0;
        } else if (level > 3) {
            level = 3;
        }
        return mWifiResources.get(level);
    }

}
