package com.android.launcher3.common.network.resp;

public class DealFriendApplyResp {

    private int ifSuccess;
    private String msg;

    @Override
    public String toString() {
        return "DealFriendApplyResp{" +
                "ifSuccess=" + ifSuccess +
                ", msg='" + msg + '\'' +
                '}';
    }

    public int getIfSuccess() {
        return ifSuccess;
    }

    public void setIfSuccess(int ifSuccess) {
        this.ifSuccess = ifSuccess;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DealFriendApplyResp(int ifSuccess, String msg) {
        this.ifSuccess = ifSuccess;
        this.msg = msg;
    }

    public DealFriendApplyResp() {
    }
}
