package com.android.launcher3.common.network.resp;

/**
 * @Author: shensl
 * @Description：主题下载
 * @CreateDate：2023/12/6 15:50
 * @UpdateUser: shensl
 */
public class ThemeDownloadResp {

    private int ifBought;// 1已购买 2余额不足
    private String fileUrl;
    private String fileMD5;
    private int fileSize;

    public int getIfBought() {
        return ifBought;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public int getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        return "ThemeDownloadResp{" +
                "ifBought=" + ifBought +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileMD5='" + fileMD5 + '\'' +
                ", fileSize='" + fileSize + '\'' +
                '}';
    }
}
