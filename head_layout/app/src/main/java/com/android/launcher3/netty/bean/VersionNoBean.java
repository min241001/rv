package com.android.launcher3.netty.bean;

import com.jeremyliao.liveeventbus.core.LiveEvent;

public class VersionNoBean implements LiveEvent {
    /**
     * 对外版本号
     */
    private String s1OutVersion;
    /**
     * launch版本号
     */
    private String s2LauncherVersion;
    /**
     * setting版本号
     */
    private String s3SettingVersion;
    /**
     * 屏幕分辨率
     */
    private String s4ScreenResolution;
    /**
     * 系统版本
     */
    private String s5SystemVersion;
    /**
     * RAM 整数 单位MB
     */
    private Integer s6Ram;
    /**
     * ROM 整数 单位MB
     */
    private Integer s7Rom;
    /**
     * mac地址
     */
    private String s8Mac;

    /**
     * 固件信息
     */
    private String s9firmware;

    public String getS1OutVersion() {
        return s1OutVersion;
    }

    public void setS1OutVersion(String s1OutVersion) {
        this.s1OutVersion = s1OutVersion;
    }

    public String getS2LauncherVersion() {
        return s2LauncherVersion;
    }

    public void setS2LauncherVersion(String s2LauncherVersion) {
        this.s2LauncherVersion = s2LauncherVersion;
    }

    public String getS3SettingVersion() {
        return s3SettingVersion;
    }

    public void setS3SettingVersion(String s3SettingVersion) {
        this.s3SettingVersion = s3SettingVersion;
    }

    public String getS4ScreenResolution() {
        return s4ScreenResolution;
    }

    public void setS4ScreenResolution(String s4ScreenResolution) {
        this.s4ScreenResolution = s4ScreenResolution;
    }

    public String getS5SystemVersion() {
        return s5SystemVersion;
    }

    public void setS5SystemVersion(String s5SystemVersion) {
        this.s5SystemVersion = s5SystemVersion;
    }

    public Integer getS6Ram() {
        return s6Ram;
    }

    public void setS6Ram(Integer s6Ram) {
        this.s6Ram = s6Ram;
    }

    public Integer getS7Rom() {
        return s7Rom;
    }

    public void setS7Rom(Integer s7Rom) {
        this.s7Rom = s7Rom;
    }

    public String getS8Mac() {
        return s8Mac;
    }

    public void setS8Mac(String s8Mac) {
        this.s8Mac = s8Mac;
    }

    public String getS9firmware() {
        return s9firmware;
    }

    public void setS9firmware(String s9firmware) {
        this.s9firmware = s9firmware;
    }

    @Override
    public String toString() {
        return "VersionNoBean{" +
                "s1OutVersion='" + s1OutVersion + '\'' +
                ", s2LauncherVersion='" + s2LauncherVersion + '\'' +
                ", s3SettingVersion='" + s3SettingVersion + '\'' +
                ", s4ScreenResolution='" + s4ScreenResolution + '\'' +
                ", s5SystemVersion='" + s5SystemVersion + '\'' +
                ", s6Ram=" + s6Ram +
                ", s7Rom=" + s7Rom +
                ", s8Mac='" + s8Mac + '\'' +
                ", s9firmware='" + s9firmware + '\'' +
                '}';
    }
}
