package com.android.launcher3.common.network.resp;

/**
 * @Author: shensl
 * @Description：主题详情
 * @CreateDate：2023/12/6 15:47
 * @UpdateUser: shensl
 */
public class ThemeDetailsResp {

    private int themeId;// 主题id
    private String themeIconUrl;// 主题图标
    private int ifBought;// 1 已购买 2未购买
    private String fileUrl;
    private String fileMD5;
    private int fileSize;

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getThemeId() {
        return themeId;
    }

    public String getThemeIconUrl() {
        return themeIconUrl;
    }

    public int getIfBought() {
        return ifBought;
    }

    @Override
    public String toString() {
        return "ThemeDetails{" +
                "themeId=" + themeId +
                ", themeIconUrl='" + themeIconUrl + '\'' +
                ", ifBought=" + ifBought +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileMD5='" + fileMD5 + '\'' +
                ", fileSize='" + fileSize + '\'' +
                '}';
    }
}
