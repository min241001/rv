package com.android.launcher3.common.network.resp;

import java.util.List;

public class FriendResp {

    private List<FriendBean> list;

    @Override
    public String toString() {
        return "FriendResp{" +
                "list=" + list +
                '}';
    }

    public List<FriendBean> getList() {
        return list;
    }

    public void setList(List<FriendBean> list) {
        this.list = list;
    }

    public FriendResp(List<FriendBean> list) {
        this.list = list;
    }

    public FriendResp() {
    }

    public static class FriendBean{
        private int waAcctId;
        private String name;
        private String avatar;
        private int ifFriend;//是否是好友，1是 2否

        @Override
        public String toString() {
            return "FriendBean{" +
                    "waAcctId=" + waAcctId +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", ifFriend=" + ifFriend +
                    '}';
        }

        public int getWaAcctId() {
            return waAcctId;
        }

        public void setWaAcctId(int waAcctId) {
            this.waAcctId = waAcctId;
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

        public int getIfFriend() {
            return ifFriend;
        }

        public void setIfFriend(int ifFriend) {
            this.ifFriend = ifFriend;
        }

        public FriendBean(int waAcctId, String name, String avatar, int ifFriend) {
            this.waAcctId = waAcctId;
            this.name = name;
            this.avatar = avatar;
            this.ifFriend = ifFriend;
        }

        public FriendBean() {
        }
    }
}
