package com.android.launcher3.common.network.resp;

import java.util.List;

public class FriendApplyResp {

    private int pageNum;
    private int pageSize;
    private int pages;
    private int total;
    private List<FriendApplyBean> list;

    @Override
    public String toString() {
        return "SearchFriendResp{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", pages=" + pages +
                ", total=" + total +
                ", list=" + list +
                '}';
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<FriendApplyBean> getList() {
        return list;
    }

    public void setList(List<FriendApplyBean> list) {
        this.list = list;
    }

    public FriendApplyResp(int pageNum, int pageSize, int pages, int total, List<FriendApplyBean> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.total = total;
        this.list = list;
    }

    public FriendApplyResp() {
    }

    public static class FriendApplyBean{
        private int fromWaAcctId;
        private String name;
        private String avatar;
        private int applyId;//申请记录id

        @Override
        public String toString() {
            return "FriendApplyBean{" +
                    "fromWaAcctId=" + fromWaAcctId +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", applyId=" + applyId +
                    '}';
        }

        public int getFromWaAcctId() {
            return fromWaAcctId;
        }

        public void setFromWaAcctId(int fromWaAcctId) {
            this.fromWaAcctId = fromWaAcctId;
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

        public int getApplyId() {
            return applyId;
        }

        public void setApplyId(int applyId) {
            this.applyId = applyId;
        }

        public FriendApplyBean(int fromWaAcctId, String name, String avatar, int applyId) {
            this.fromWaAcctId = fromWaAcctId;
            this.name = name;
            this.avatar = avatar;
            this.applyId = applyId;
        }

        public FriendApplyBean() {
        }
    }

}
