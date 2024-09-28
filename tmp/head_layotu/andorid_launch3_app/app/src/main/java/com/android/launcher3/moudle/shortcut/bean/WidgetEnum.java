package com.android.launcher3.moudle.shortcut.bean;

public enum WidgetEnum {

    BATTERY("0"),        // 电量
    WIFI("1"),           // WIFI
    MOBILE_NETWORK("2"), // 移动数据
    BLUETOOTH("3"),      // 蓝牙
    SCREENSHOT("4"),     // 截图
    CLEAR("5"),          // 清理
    SETTINGS("6"),       // 设置
    TASK("7"),           // 最近任务
    BRIGHTNESS("8"),     // 亮度
    MUTE("9"),           // 静音
    ALIPAY("A");         // 支付宝

    WidgetEnum(String s) {
    }

    public static WidgetEnum fromStr(String id) {
        switch (id) {
            case "0":
                return BATTERY;
            case "1":
                return WIFI;
            case "2":
                return MOBILE_NETWORK;
            case "3":
                return BLUETOOTH;
            case "4":
                return SCREENSHOT;
            case "5":
                return CLEAR;
            case "6":
                return SETTINGS;
            case "7":
                return TASK;
            case "8":
                return BRIGHTNESS;
            case "9":
                return MUTE;
            case "A":
                return ALIPAY;
        }
        return WIFI;
    }

    public static WidgetEnum fromInt(int type) {
        switch (type) {
            case 0:
                return BATTERY;
            case 1:
                return WIFI;
            case 2:
                return MOBILE_NETWORK;
            case 3:
                return BLUETOOTH;
            case 4:
                return SCREENSHOT;
            case 5:
                return CLEAR;
            case 6:
                return SETTINGS;
            case 7:
                return TASK;
            case 8:
                return BRIGHTNESS;
            case 9:
                return MUTE;
            case 10:
                return ALIPAY;
        }
        return WIFI;
    }

    public static boolean matchOrNot(int type, WidgetEnum widgetEnum) {
        return fromInt(type) == widgetEnum;
    }

    public static String formEnum(WidgetEnum widgetEnum) {
        switch (widgetEnum) {
            case BATTERY:
                return "0";
            case WIFI:
                return "1";
            case MOBILE_NETWORK:
                return "2";
            case BLUETOOTH:
                return "3";
            case SCREENSHOT:
                return "4";
            case CLEAR:
                return "5";
            case SETTINGS:
                return "6";
            case TASK:
                return "7";
            case BRIGHTNESS:
                return "8";
            case MUTE:
                return "9";
            case ALIPAY:
                return "A";
        }
        return "0";
    }
}
