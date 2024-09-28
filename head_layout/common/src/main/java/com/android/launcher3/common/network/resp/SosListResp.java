package com.android.launcher3.common.network.resp;

import java.util.List;

/**
 * @Author: mahz
 * @Description：分页返回
 * @CreateDate：2024/2/3 16:39
 * @UpdateUser: mahz
 */
public class SosListResp {
    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "SosListResp{" +
                "list=" + list +
                '}';
    }
}
