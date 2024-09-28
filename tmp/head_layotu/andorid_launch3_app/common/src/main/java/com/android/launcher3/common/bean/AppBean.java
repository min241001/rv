package com.android.launcher3.common.bean;
import android.graphics.drawable.Drawable;

import com.baehug.lib.nettyclient.annotation.MyUnicodeSign;

public class AppBean {
    private int id;
    private String name;
    private Drawable icon;
    @MyUnicodeSign
    private String packageName;
    private String sourceDir;
    private int flag = 0;  //0：系统应用；1：用户安装应用；2：配置应用;
    private int iconFlag = -1; //-1:配置中无图片；0：配置中有图片；1：使用系统默认图片
    private int preinstall = 0; //是否为预装(0:否，1:预装)
    private long apkSize;// 应用大小
    @MyUnicodeSign
    private String type;// 应用开关 1 开、2 关
    private int iconId;
    private int nameId;

    public AppBean(){

    }

    public AppBean(int id, String name, Drawable icon, String packageName,String type){
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
        this.type = type;
    }

    public AppBean(String name, Drawable icon, String packageName, String sourceDir,long apkSize,String type) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
        this.sourceDir = sourceDir;
        this.apkSize = apkSize;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getIconFlag() {
        return iconFlag;
    }

    public void setIconFlag(int iconFlag) {
        this.iconFlag = iconFlag;
    }

    public int getPreinstall() {
        return preinstall;
    }

    public void setPreinstall(int preinstall) {
        this.preinstall = preinstall;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    @Override
    public String toString() {
        return "AppBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", flag=" + flag +
                ", preinstall=" + preinstall +
                ", type=" + type +
                '}';
    }
}