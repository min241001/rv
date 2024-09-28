package com.android.launcher3.common.network.resp;

public class AddFriendCountResp {

    private int count;

    @Override
    public String toString() {
        return "AddFriendCountResp{" +
                "count=" + count +
                '}';
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public AddFriendCountResp(int count) {
        this.count = count;
    }

    public AddFriendCountResp() {
    }
}
