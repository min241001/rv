package com.android.launcher3.common.network.resp;

import java.io.Serializable;

/**
 * @Author: shensl
 * @Description：热门主题返回实体
 * @CreateDate：2023/12/6 15:35
 * @UpdateUser: shensl
 */
public class HotTopicsResp implements Serializable {

    private int themeId;// 主题id
    private String themeName;// 主题名称
    private String themeIconUrl;// 主题图标
    private int diamondPrice;// 钻石价格
    private String className;// 类型名称
    private int classType;// 类型名称

    public int getThemeId() {
        return themeId;
    }

    public String getThemeName() {
        return themeName;
    }

    public String getThemeIconUrl() {
        return themeIconUrl;
    }

    public int getDiamondPrice() {
        return diamondPrice;
    }

    public String getClassName() {
        return className;
    }

    public int getClassType() {
        return classType;
    }

    @Override
    public String toString() {
        return "PopularTopicsResp{" +
                "themeId=" + themeId +
                ", themeName='" + themeName + '\'' +
                ", themeIconUrl='" + themeIconUrl + '\'' +
                ", diamondPrice=" + diamondPrice +
                ", className='" + className + '\'' +
                ", classType='" + classType + '\'' +
                '}';
    }
}
