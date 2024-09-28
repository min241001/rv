package com.android.launcher3.common.network.resp;

public class ResidueResp {

    private String waAcctId;
    private String diamondNum;
    private String integralNum;

    public String getWaAcctId() {
        return waAcctId;
    }

    public void setWaAcctId(String waAcctId) {
        this.waAcctId = waAcctId;
    }

    public String getDiamondNum() {
        return diamondNum;
    }

    public void setDiamondNum(String diamondNum) {
        this.diamondNum = diamondNum;
    }

    public String getIntegralNum() {
        return integralNum;
    }

    public void setIntegralNum(String integralNum) {
        this.integralNum = integralNum;
    }

    @Override
    public String toString() {
        return "ResidueResp{" +
                "waAcctId='" + waAcctId + '\'' +
                ", diamondNum='" + diamondNum + '\'' +
                ", integralNum='" + integralNum + '\'' +
                '}';
    }
}
