package com.android.launcher3.moudle.shortcut.bean;

import java.util.Objects;

public class Widget {

    private String id;          // 控件id

    private String name;        // 控件名称

    private boolean selected;   // 是否被选中

    private int resId;          // 是否被选中

    public Widget() {
    }

    public Widget(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    @Override
    public String toString() {
        return "Widget{" +
                "name='" + WidgetEnum.fromStr(id) + '\'' +
                ", name=" + name +
                ", selected=" + selected +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        Widget bean = (Widget) o;
        return Objects.equals(bean.getId(), this.getId());
    }
}
