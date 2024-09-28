package com.android.launcher3.common.bean;

import com.baehug.lib.nettyclient.annotation.MyUnicodeSign;
import com.jeremyliao.liveeventbus.core.LiveEvent;

public class WatchAppBean implements LiveEvent {

    @MyUnicodeSign
    private String s01PackageName;//包名
    @MyUnicodeSign
    private String s02appName;//应用名
    @MyUnicodeSign
    private String s03MType;// 应用开关 1 开、2 关

    @Override
    public String toString() {
        return "ReceiveAppBean{" +
                "s01PackageName='" + s01PackageName + '\'' +
                ", s02appName='" + s02appName + '\'' +
                ", s03MType='" + s03MType + '\'' +
                '}';
    }

    public String getS01PackageName() {
        return s01PackageName;
    }

    public void setS01PackageName(String s01PackageName) {
        this.s01PackageName = s01PackageName;
    }

    public String getS02appName() {
        return s02appName;
    }

    public void setS02appName(String s02appName) {
        this.s02appName = s02appName;
    }

    public String getS03MType() {
        return s03MType;
    }

    public void setS03MType(String s03MType) {
        this.s03MType = s03MType;
    }

    public WatchAppBean(String s01PackageName, String s02appName, String s03MType) {
        this.s01PackageName = s01PackageName;
        this.s02appName = s02appName;
        this.s03MType = s03MType;
    }

    public WatchAppBean() {
    }
}
