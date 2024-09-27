package com.android.launcher3.common.network.resp;

import java.util.List;

public class SearchFriendResp {

    private int pageNum;
    private int pageSize;
    private int pages;
    private int total;
    private List<FriendResp.FriendBean> list;

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

    public List<FriendResp.FriendBean> getList() {
        return list;
    }

    public void setList(List<FriendResp.FriendBean> list) {
        this.list = list;
    }

    public SearchFriendResp(int pageNum, int pageSize, int pages, int total, List<FriendResp.FriendBean> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.total = total;
        this.list = list;
    }

    public SearchFriendResp() {
    }
}
