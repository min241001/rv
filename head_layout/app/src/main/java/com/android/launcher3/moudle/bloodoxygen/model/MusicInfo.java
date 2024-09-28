package com.android.launcher3.moudle.bloodoxygen.model;

public class MusicInfo {
    private String appName;
    private String pkgName;
    private String title;
    private boolean musicState;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMusicState() {
        return musicState;
    }

    public void setMusicState(boolean musicState) {
        this.musicState = musicState;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "appName='" + appName + '\'' +
                ", pkgName='" + pkgName + '\'' +
                ", title='" + title + '\'' +
                ", musicState=" + musicState +
                '}';
    }
}
