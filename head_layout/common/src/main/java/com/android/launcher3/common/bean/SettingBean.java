package com.android.launcher3.common.bean;

/**
 * @Author: shensl
 * @Description：设置实体
 * @CreateDate：2023/12/15 14:32
 * @UpdateUser: shensl
 */
public class SettingBean {

    private int id;
    private int icon;
    private String name;
    private String clazz;

    public SettingBean(int id, int icon, String name, String clazz) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.clazz = clazz;
    }

    public int getId() {
        return id;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "SettingBean{" +
                "id=" + id +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", clazz='" + clazz + '\'' +
                '}';
    }
}
