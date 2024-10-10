package com.renny.contractgridview.bean;

import android.graphics.drawable.Drawable;

/**
 * @Author: pengmin
 * @CreateDate：2024/8/28 10:27
 */
public class AppInfoBean {
    private int id = 0;
    public boolean editMode = false;
    private int favIndex = 0;//编辑状态
    private String app_name = "";//应用名称
    private String package_name = "";//应用包名
    private Drawable app_icon = null;//应用app_icon
    private int type = 0;//item 类型
    private boolean hide = false;//是否隐藏

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public Drawable getApp_icon() {
        return app_icon;
    }

    public void setApp_icon(Drawable app_icon) {
        this.app_icon = app_icon;
    }
    public boolean getEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    /*public int getFavIndex() {
        return favIndex;
    }

    public void setFavIndex(int favIndex) {
        this.favIndex = favIndex;
    }*/

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public boolean getHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }
}