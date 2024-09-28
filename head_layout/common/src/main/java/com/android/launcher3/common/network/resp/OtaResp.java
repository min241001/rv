package com.android.launcher3.common.network.resp;

public class OtaResp {
    private String outerVersion;//外部版本号
    private String fileUrl;
    private int ifCompelUpgrade;
    private String upgradeTime;
    private int fileSize;

    public String getOuterVersion() {
        return outerVersion;
    }

    public void setOuterVersion(String outerVersion) {
        this.outerVersion = outerVersion;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getIfCompelUpgrade() {
        return ifCompelUpgrade;
    }

    public void setIfCompelUpgrade(int ifCompelUpgrade) {
        this.ifCompelUpgrade = ifCompelUpgrade;
    }

    public String getUpgradeTime() {
        return upgradeTime;
    }

    public void setUpgradeTime(String upgradeTime) {
        this.upgradeTime = upgradeTime;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "OtaResp{" +
                "outerVersion='" + outerVersion + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", ifCompelUpgrade=" + ifCompelUpgrade +
                ", upgradeTime='" + upgradeTime + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
