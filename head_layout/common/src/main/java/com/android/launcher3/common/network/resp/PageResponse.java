package com.android.launcher3.common.network.resp;

import java.util.List;

/**
 * @Author: shensl
 * @Description：分页返回
 * @CreateDate：2023/12/6 15:37
 * @UpdateUser: shensl
 */
public class PageResponse<T> {

    private int pageNum;
    private int pageSize;
    private int pages;
    private int total;
    private List<T> list;

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPages() {
        return pages;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", pages=" + pages +
                ", total=" + total +
                ", list=" + list +
                '}';
    }
}
