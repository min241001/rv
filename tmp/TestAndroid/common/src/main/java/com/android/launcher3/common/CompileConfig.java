package com.android.launcher3.common;

/**
 * 编译配置文件
 */
public interface CompileConfig {

    // 1 是否使用系统录音
    boolean SYSTEM_RECORD = false;

    // 2 是否使用系统WIFI
    boolean SYSTEM_WIFI = false;

    // 3 下拉编辑页面是否支持优化返回
    boolean PULL_ADDED_SLIDE_BACK = false;

    int STYLE_DEFAULT = 0;

    int STYLE_Y09W = 1;

    int STYLE_Y29W = 2;

    int STYLE_147 = 3;

    /** 4
     * 风格边距类型 默认为0
     * 默认: 0
     * y09w: 1
     * y29w: 2
     */
    int STYLE_TYPE = STYLE_147;

    // 5 全局右滑返回
    boolean ALL_SLIDE_BACK = false;

    // 6 是否支持人脸解锁屏幕（不支持就是密码锁屏）
    boolean FACE_UNLOCK = false;

    // 7 下拉状态栏顶部左右边距
    int STATUS_BAR_PADDING = 0;

    // 尊瑞 137 143 160
    int MANUFACTURER_ZUNRUI = 0;

    // 一科 Y09BH
    int MANUFACTURER_YIKE = 1;

    // 8 厂商类型
    int FACE_MANUFACTURER_TYPE = MANUFACTURER_YIKE;

    int WATCH_TYPE_DEFAULT = 0;

    int WATCH_TYPE_137 = 1;

    int WATCH_TYPE_Y29W = 2;

    // 9 是否使用自定义锁屏
    boolean CUSTOM_LOCK_SCREEN = false;

    // 10 手表型号，根据手表型号匹配桌面显示
    int WATCH_TYPE = WATCH_TYPE_DEFAULT;

    // 11 是否显示内置浏览器
    boolean DISPLAY_BROWSER = false;
}
