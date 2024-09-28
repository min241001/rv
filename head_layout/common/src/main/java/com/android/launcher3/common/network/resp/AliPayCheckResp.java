package com.android.launcher3.common.network.resp;

public class AliPayCheckResp {

    //激活状态 1处理中 2已激活 3激活失败 4未申请
    private int activationStatus;

    @Override
    public String toString() {
        return "AliPayCheckResp{" +
                "activationStatus=" + activationStatus +
                '}';
    }

    public int getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(int activationStatus) {
        this.activationStatus = activationStatus;
    }

    public AliPayCheckResp(int activationStatus) {
        this.activationStatus = activationStatus;
    }

    public AliPayCheckResp() {
    }
}
