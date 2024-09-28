package com.android.launcher3.common.bean;

import android.graphics.drawable.Drawable;

/**
 * @Author: shensl
 * @Description：选择器实体
 * @CreateDate：2023/12/15 18:37
 * @UpdateUser: shensl
 */
public class WorkspaceBean extends SelectorBean {

    private int iconId;

    private int nameId;

    // 表盘类型
    private int type;

    public WorkspaceBean(int id, String name, Drawable thumb) {
        super(id, name, thumb);
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
