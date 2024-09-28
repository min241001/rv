package com.android.launcher3.netty.constant;

import com.baehug.lib.nettyclient.constant.CmdConstant;

/**
 * 协议指令名常量
 */
public interface XmxxCmdConstant extends CmdConstant {

    /**
     * 找手表
     */
    String FIND = "FIND";
    /**
     * 远程关机
     */
    String POWER_OFF = "POWER_OFF";
    /**
     * 修改SOS号码
     * 平台发送：[XM*334588000000156*001F*OVR_SOS,00000000000,00000000000]
     * 终端回复：[XM*334588000000156*001F*OVR_SOS,00000000000,00000000000]
     */
    String OVR_SOS = "OVR_SOS";
    /**
     * SOS 号码查询
     * 平台发送：[XM*334588000000156*0008*SOS_LIST]
     * 终端回复：[XM*334588000000156*001B*SOS_LIST,00000000000,00000000000]
     *
     */
    String SOS_LIST = "SOS_LIST";
    /**
     * SOS 短信报警开关
     * 平台发送：[XM*334588000000156*0007*SOS_SMS]
     * 终端回复：[XM*334588000000156*0009*SOS_SMS,1]
     *
     */
    String SOS_SMS = "SOS_SMS";
    /**
     * SOS 短信报警开关
     * 平台发送：[XM*334588000000156*0014*MOD_SOS_SMS_SWITCH,1]
     * 终端回复：[XM*334588000000156*0014*MOD_SOS_SMS_SWITCH,2]
     * 1 开启、2 关闭
     */
    String MOD_SOS_SMS_SWITCH = "MOD_SOS_SMS_SWITCH";
    /**
     * 添加电话本+白名单（累计 100 条）
     * 平台发送：[XM*334588000000156*003F*ADD_PHL,1509000777,110,&#24352;&#19977;http://www.baidu.com,110]
     * 终端回复：[XM*334588000000156*0012*ADD_PHL,1509000777]
     */
    String ADD_PHL = "ADD_PHL";
    /**
     * 修改电话本+白名单（累计 100 条）
     * 平台发送：[XM*334588000000156*003F*ADD_PHL,1509000777,110,&#24352;&#19977;http://www.baidu.com,110]
     * 终端回复：[XM*334588000000156*0012*ADD_PHL,1509000777]
     */
    String MOD_PHL = "MOD_PHL";
    /**
     * 查询电话本+白名单（累计 100 条）
     * 平台发送：[XM*334588000000156*0012*PHL_LIST,1509000777]
     * 终端回复：[XM*334588000000156*003E*PHL_LIST,110,&#24352;&#19977;http://www.baidu.com,110,121212121]
     */
    String PHL_LIST = "PHL_LIST";
    /**
     * 删除电话本+白名单（累计 100 条）
     * 平台发送：[XM*334588000000156*0012*DEL_PHL,1509000777]
     * 终端回复：[XM*334588000000156*0012*DEL_PHL,1509000777]
     */
    String DEL_PHL = "DEL_PHL";
    /**
     * 白名单开关
     * 平台发送：[XM*334588000000156*0012*MOD_WLKEY_SWITCH,1]
     * 终端回复：[XM*334588000000156*00012*MOD_WLKEY_SWITCH,1]
     * ONOFF: 1 开启、2 关闭、3 30秒后自动接听
     */
    String MOD_WLKEY_SWITCH = "MOD_WLKEY_SWITCH";

    /**
     * 版本查询
     * 平台发送：[XM*334588000000156*0006*VER_NO]
     * 终端回复：[XM*334588000000156*0041*VER_NO,1.2.1,1.0.2,240*240,android 12,1024,4096,c6:18:bb:b1:ab:a5]
     */
    String VER_NO = "VER_NO";

    /**
     * 设置定时开机
     * 平台发送：[XM*334588000000156*000F*TIMER_ON,8:00,1]
     *
     */
    String MOD_POWER_ON = "MOD_POWER_ON";

    /**
     * 设置定时关机
     * 平台发送：[XM*334588000000156*0010*TIMER_OFF,18:00,1]
     *
     */
    String MOD_POWER_OFF = "MOD_POWER_OFF";


    /**
     * 获取设备定时开关机的状态
     *
     * [XM*334588000000156*000C*POWER_ON_OFF]
     * [XM*334588000000156*001b*POWER_ON_OFF,8:00,1,18:00,2]
     * 1 开启、2 关闭
     */
    String POWER_ON_OFF = "POWER_ON_OFF";


    /**
     * 手表电量
     * 平台发送：[XM*334588000000156*0008*POWER]
     * 终端回复：[XM*334588000000156*000a*POWER,80]
     */
    String POWER = "POWER";

    /**
     *
     * 获取步数
     */
    String STEP = "STEP";

    /**
     * 发送定位
     */
    String CR = "CR";

    /**
     * 获取手表应用列表
     */
    String APP_LIST = "APP_LIST";

    /**
     * 修改手表应用列表状态
     */
    String OVR_APP_LIST = "OVR_APP_LIST";

    /**
     * 获取指定app的版本号
     */
    String APP_VERSION = "APP_VERSION";

    /**
     * 获取设备当前定位
     * [XM*334588000000156*0008*LOCATION]
     * [XM*334588000000156*0063*LOCATION,1,1,121.49884033194,31.22569656361,50,广东省,深圳市,龙华区,龙华街道,吉华路,利金城科技工业园,利金城科技工业园内,1702974038]
     */
    String LOCATION = "LOCATION";

    /**
     * 定位回复指令(本指令三合一，wifi、基站、GPS)
     */
    String UD = "UD";

    /**
     * 设置定位监测频率
     * [XM*334588000000156*000A*MOD_LOCATION_FRQ,300]
     * [XM*334588000000156*000A*MOD_LOCATION_FRQ,300]
     *
     */
    String MOD_LOCATION_FRQ = "MOD_LOCATION_FRQ";

    String CHECK_NETTY_STATE = "CHECK_NETTY_STATE";

    // 上课禁用通用标签
    String CLASS_BAN_START = "CLASS_BAN_STATE";

    /**
     * 查询定位频率
     */
    String LOCATION_FRQ = "LOCATION_FRQ";

    /**
     * 上课禁用时间段设置（自定义日期）
     * 平台发送：[XM*334588000000156*0050*OVR_SILENCE_TIME]
     * 终端回复： [XM*334588000000156*00010*OVR_SILENCE_TIME]
     */
    String OVR_SILENCE_TIME = "OVR_SILENCE_TIME";

    /**
     * 查询上课禁用时间段设置（自定义日期）
     * 平台发送：[XM*334588000000156*0000C*SILENCE_TIME]
     * 终端回复： [XM*334588000000156*004C*SILENCE_TIME,7:30-21:10-1-0111110]
     */
    String SILENCE_TIME = "SILENCE_TIME";

    /**
     * 腾讯微聊消息通知
     */
    String TX_IM_PUSH = "TX_IM_PUSH";

    /**
     * 链路保持
     */
    String OTA_LK = "LK";

    /**
     * 链路保持(LK不执行走KA)
     */
    String KA = "KA";

    /**
     * 同步服务器时间
     */
    String TIME = "TIME";
    /**
     * 定位频率requestCode
     */
    int LocationTaskCode = 10008;
    /**
     * 定时关机requestCode
     */
    int PowerOffTaskCode = 10009;
}
