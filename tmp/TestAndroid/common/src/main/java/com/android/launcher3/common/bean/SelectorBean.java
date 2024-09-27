package com.android.launcher3.common.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * @Author: shensl
 * @Description：选择器实体
 * @CreateDate：2023/12/15 18:37
 * @UpdateUser: shensl
 */
public class SelectorBean implements Serializable {

    private int id;
    private String name;
    private Drawable thumb;

    private String filepath;
    private String className;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public SelectorBean(int id, String name, Drawable thumb) {
        this.id = id;
        this.name = name;
        this.thumb = thumb;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Drawable getThumb() {
        return thumb;
    }

    @Override
    public String toString() {
        return "SelectorBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", thumb=" + thumb +
                '}';
    }
}
