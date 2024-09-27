package com.android.launcher3.common.bean;

public class SearchUserBean {

    private String tempUserId;
    private String name;
    private String avatar;
    //是否是好友，1是 2否
    private Integer ifFriend;

    @Override
    public String toString() {
        return "SearchUserBean{" +
                "tempUserId='" + tempUserId + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", ifFriend=" + ifFriend +
                '}';
    }

    public String getTempUserId() {
        return tempUserId;
    }

    public void setTempUserId(String tempUserId) {
        this.tempUserId = tempUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getIfFriend() {
        return ifFriend;
    }

    public void setIfFriend(Integer ifFriend) {
        this.ifFriend = ifFriend;
    }

    public SearchUserBean(String tempUserId, String name, String avatar, Integer ifFriend) {
        this.tempUserId = tempUserId;
        this.name = name;
        this.avatar = avatar;
        this.ifFriend = ifFriend;
    }

    public SearchUserBean() {
    }
}
