package com.renny.contractgridview.bean;

import androidx.annotation.DrawableRes;

import java.io.Serializable;

public class AppInfoBean implements Serializable {
    private int id = 0;
    private boolean editMode = false;
    private String app_name = "";
    private String package_name = "";
    private int type = 0;
    private boolean hide = false;
    private int level = 0;//1为default//2为系统app
    
    private int resourceId; //应用对应图片资源id

    public @DrawableRes int getResourceId() {
        return resourceId;
    }

    public void setResourceId(@DrawableRes int resourceId) {
        this.resourceId = resourceId;
    }
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
    public boolean getEditMode() {
        return editMode;
    }
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
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
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

}