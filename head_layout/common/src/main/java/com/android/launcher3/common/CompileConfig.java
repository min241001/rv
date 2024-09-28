package com.android.launcher3.common;

import android.os.Build;

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

    // 不需要加打包配置 是否显示喜马拉雅
    boolean DISPLAY_XIMALAYA = Build.MODEL.equals("Y09K");

    // 不需要加打包配置 是否显示咪咕音乐
    boolean DISPLAY_MIGUYINYUE = Build.MODEL.equals("Y09K");

    // Workspace
    String PKG_NAME_WORKSPACE = "com.android.launcher3.moudle.selector.view.WorkspaceSelectorActivity";

    // 系统默认应用json字符串 name:应用名称  packageName:包名   preinstall:是否预安装   type:应用开关  iconId:图片id，只能用drawable  nameId:名称id
    String SYSTEM_DEFAULT_APP_JSON = "[" +
            "{'name':'拨号','packageName':'com.baehug.dialer','iconId':" + R.drawable.icon_dialer + ",'nameId':" + R.string.app_name_dialer + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'天气','packageName':'com.baehug.weather','iconId':" + R.drawable.icon_weather + ",'nameId':" + R.string.app_name_weather + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'通讯录','packageName':'com.baehug.contacts','iconId':" + R.drawable.icon_contacts + ",'nameId':" + R.string.app_name_contacts + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'微聊','packageName':'com.baehug.watch.wechat','iconId':" + R.drawable.icon_wechat + ",'nameId':" + R.string.app_name_micro_chat + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'视频','packageName':'com.baehug.video','iconId':" + R.drawable.icon_video + ",'nameId':" + R.string.app_name_video + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'相册','packageName':'com.baehug.album','iconId':" + R.drawable.icon_gallery3d + ",'nameId':" + R.string.app_name_album + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'课程表','packageName':'com.baehug.course','iconId':" + R.drawable.icon_course + ",'nameId':" + R.string.app_name_course + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'短信','packageName':'com.baehug.sms','iconId':" + R.drawable.icon_mms + ",'nameId':" + R.string.app_name_sms + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'录音','packageName':'com.baehug.record','iconId':" + R.drawable.icon_record + ",'nameId':" + R.string.app_name_record + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'西蒙主题','packageName':'com.baehug.theme','iconId':" + R.drawable.icon_theme + ",'nameId':" + R.string.app_name_theme + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'视频通话','packageName':'com.baehug.videochat','iconId':" + R.drawable.icon_videocall + ",'nameId':" + R.string.app_name_videochat + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'应用市场','packageName':'com.baehug.appstore','iconId':" + R.drawable.icon_appstore + ",'nameId':" + R.string.app_name_appstore + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'西萌钱包','packageName':'com.baehug.wallet','iconId':" + R.drawable.icon_wallet + ",'nameId':" + R.string.app_name_wallet + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'风格','packageName':'com.android.launcher3.moudle.selector.view.WorkspaceSelectorActivity','iconId':" + R.drawable.icon_style + ",'nameId':" + R.string.app_name_style + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'相机','packageName':'com.baehug.camera','iconId':" + R.drawable.icon_hwcamera + ",'nameId':" + R.string.app_name_camera + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'加好友','packageName':'com.baehug.meet','iconId':" + R.drawable.icon_friend + ",'nameId':" + R.string.app_name_meet + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'二维码','packageName':'com.ximeng.qrcode','iconId':" + R.drawable.icon_qrcode + ",'nameId':" + R.string.app_name_qrcode + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'工具','packageName':'com.baehug.util','iconId':" + R.drawable.icon_util + ",'nameId':" + R.string.app_name_util + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'密码锁屏','packageName':'com.baehug.facerecognition','iconId':" + R.drawable.icon_facerecognition + ",'nameId':" + R.string.app_name_password_lock_screen + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'设置','packageName':'com.baehug.settings','iconId':" + R.drawable.icon_settings + ",'nameId':" + R.string.app_name_settings + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'超级省电','packageName':'com.baehug.power','iconId':" + R.drawable.icon_super_power + ",'nameId':" + R.string.app_name_power + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'儿童微信','packageName':'com.tencent.wechatkids','iconId':" + R.drawable.icon_chat + ",'nameId':" + R.string.app_name_wechatkids + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            (DISPLAY_BROWSER ? ",{'name':'via','packageName':'mark.via','iconId':" + R.drawable.icon_via + ",'nameId':" + R.string.app_name_via + ",'iconFlag':0,'preinstall':1,'type':'1'}" : "") +
            (DISPLAY_XIMALAYA ? ",{'name':'喜马拉雅','packageName':'com.ximalayaos.wearkid','iconId':" + R.drawable.icon_xmly + ",'nameId':" + R.string.app_name_xmly + ",'iconFlag':0,'preinstall':1,'type':'1'}" : "") +
            (DISPLAY_MIGUYINYUE ? ",{'name':'咪咕音乐','packageName':'com.fzbosun.watch.migumusic','iconId':" + R.drawable.icon_mgyy + ",'nameId':" + R.string.app_name_mgyy + ",'iconFlag':0,'preinstall':1,'type':'1'}" : "") +
            (CUSTOM_LOCK_SCREEN ? ",{'name':'密码锁屏','packageName':'com.baehug.lockscreen','iconId':" + R.drawable.icon_facerecognition + ",'nameId':" + R.string.app_name_lockscreen + ",'iconFlag':0,'preinstall':1,'type':'1'}" : "") +
            ",{'name':'呼吸','packageName':'com.baehug.breathe','iconId':" + R.drawable.icon_breathe + ",'nameId':" + R.string.app_name_breathe + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'手电筒','packageName':'com.baehug.light','iconId':" + R.drawable.icon_flashlight + ",'nameId':" + R.string.app_name_flashlight + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'血氧','packageName':'com.baehug.blood','iconId':" + R.drawable.icon_blood + ",'nameId':" + R.string.app_name_blood + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'心率','packageName':'com.baehug.heart','iconId':" + R.drawable.icon_heart + ",'nameId':" + R.string.app_name_heart + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'体温','packageName':'com.baehug.temperature','iconId':" + R.drawable.icon_temperature + ",'nameId':" + R.string.app_name_temperature + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            ",{'name':'计时器','packageName':'com.baehug.timer','iconId':" + R.drawable.icon_stopwatch + ",'nameId':" + R.string.app_name_stopwatch + ",'iconFlag':0,'preinstall':1,'type':'1'}" +
            "]";

    // TODO 不用配置 表盘类型  0是圆款、1是方款、2是通用
    int WATCH_INDEX_DIAL = 1;

    // 系统默认风格json字符串 id:id  type:表盘类型(0是圆款、1是方款、2是通用)   iconId:表盘缩略图id   nameId:表盘名称id
    String SYSTEM_DEFAULT_THEME_JSON = "[" +
            "{'id':1,'type':2,'iconId':" + R.drawable.thumb_style1 + ",'nameId':" + R.string.index_dial_1 + "}" + // 九宫格
            ",{'id':2,'type':2,'iconId':" + R.drawable.thumb_style2 + ",'nameId':" + R.string.index_dial_2 + "}" + // 蜂窝表盘
            ",{'id':3,'type':2,'iconId':" + R.drawable.thumb_style3 + ",'nameId':" + R.string.index_dial_3 + "}" + // 四宫格
            ",{'id':4,'type':2,'iconId':" + R.drawable.thumb_style4 + ",'nameId':" + R.string.index_dial_4 + "}" + // 棋盘表盘
            ",{'id':5,'type':2,'iconId':" + R.drawable.thumb_style5 + ",'nameId':" + R.string.index_dial_5 + "}" + // 竖屏列表
            ",{'id':6,'type':2,'iconId':" + R.drawable.thumb_style6 + ",'nameId':" + R.string.index_dial_6 + "}" + // 齿轮风格
            ",{'id':7,'type':2,'iconId':" + R.drawable.thumb_style7 + ",'nameId':" + R.string.index_dial_7 + "}" + // 3x4
            "]";
}
