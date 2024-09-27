package com.android.launcher3.common.network.resp;

import java.util.List;

public class UpgradeDetailResp {

    private String patchSid;
    //升级标题
    private String upgradeTitle;
    //升级方式，1弹框 2静默
    private int upgradeType;
    //是否强制升级，1是2否
    private int ifCompelUpgrade;
    //升级时间，格式：开始时间-结束时间 HH:mm
    private String upgradeTime;
    //1大于等于 2指定版本
    private int versionUpgradeType;
    //外部版本号
    private String outerVersion;
    //升级内容
    private String upgradeInfo;
    //文件集合
    private List<WatchAppPatchVersionVOSBean> watchAppPatchVersionVOS;

    @Override
    public String toString() {
        return "UpgradeDetailResp{" +
                "patchSid='" + patchSid + '\'' +
                ", upgradeTitle='" + upgradeTitle + '\'' +
                ", upgradeType=" + upgradeType +
                ", ifCompelUpgrade=" + ifCompelUpgrade +
                ", upgradeTime='" + upgradeTime + '\'' +
                ", versionUpgradeType=" + versionUpgradeType +
                ", outerVersion='" + outerVersion + '\'' +
                ", upgradeInfo='" + upgradeInfo + '\'' +
                ", watchAppPatchVersionVOS=" + watchAppPatchVersionVOS +
                '}';
    }

    public String getPatchSid() {
        return patchSid;
    }

    public void setPatchSid(String patchSid) {
        this.patchSid = patchSid;
    }

    public String getUpgradeTitle() {
        return upgradeTitle;
    }

    public void setUpgradeTitle(String upgradeTitle) {
        this.upgradeTitle = upgradeTitle;
    }

    public int getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(int upgradeType) {
        this.upgradeType = upgradeType;
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

    public int getVersionUpgradeType() {
        return versionUpgradeType;
    }

    public void setVersionUpgradeType(int versionUpgradeType) {
        this.versionUpgradeType = versionUpgradeType;
    }

    public String getOuterVersion() {
        return outerVersion;
    }

    public void setOuterVersion(String outerVersion) {
        this.outerVersion = outerVersion;
    }

    public String getUpgradeInfo() {
        return upgradeInfo;
    }

    public void setUpgradeInfo(String upgradeInfo) {
        this.upgradeInfo = upgradeInfo;
    }

    public List<WatchAppPatchVersionVOSBean> getWatchAppPatchVersionVOS() {
        return watchAppPatchVersionVOS;
    }

    public void setWatchAppPatchVersionVOS(List<WatchAppPatchVersionVOSBean> watchAppPatchVersionVOS) {
        this.watchAppPatchVersionVOS = watchAppPatchVersionVOS;
    }

    public UpgradeDetailResp(String patchSid, String upgradeTitle, int upgradeType, int ifCompelUpgrade, String upgradeTime, int versionUpgradeType, String outerVersion, String upgradeInfo, List<WatchAppPatchVersionVOSBean> watchAppPatchVersionVOS) {
        this.patchSid = patchSid;
        this.upgradeTitle = upgradeTitle;
        this.upgradeType = upgradeType;
        this.ifCompelUpgrade = ifCompelUpgrade;
        this.upgradeTime = upgradeTime;
        this.versionUpgradeType = versionUpgradeType;
        this.outerVersion = outerVersion;
        this.upgradeInfo = upgradeInfo;
        this.watchAppPatchVersionVOS = watchAppPatchVersionVOS;
    }

    public UpgradeDetailResp() {
    }

    public static class WatchAppPatchVersionVOSBean{
        //应用包名
        private String applicationId;
        //版本号
        private String appVersion;
        //app文件信息
        private String fileUrl;

        private String fileMD5;

        private int fileSize;

        @Override
        public String toString() {
            return "WatchAppPatchVersionVOSBean{" +
                    "applicationId='" + applicationId + '\'' +
                    ", appVersion='" + appVersion + '\'' +
                    ", fileUrl='" + fileUrl + '\'' +
                    ", fileMD5='" + fileMD5 + '\'' +
                    ", fileSize=" + fileSize +
                    '}';
        }

        public WatchAppPatchVersionVOSBean(String applicationId, String appVersion, String fileUrl, String fileMD5, int fileSize) {
            this.applicationId = applicationId;
            this.appVersion = appVersion;
            this.fileUrl = fileUrl;
            this.fileMD5 = fileMD5;
            this.fileSize = fileSize;
        }

        public WatchAppPatchVersionVOSBean() {
        }

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileMD5() {
            return fileMD5;
        }

        public void setFileMD5(String fileMD5) {
            this.fileMD5 = fileMD5;
        }

        public int getFileSize() {
            return fileSize;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }
    }

}
