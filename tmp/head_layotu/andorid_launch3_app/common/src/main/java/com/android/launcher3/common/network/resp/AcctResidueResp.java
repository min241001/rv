package com.android.launcher3.common.network.resp;

/**
 * @Author: shensl
 * @Description：账户余额
 * @CreateDate：2023/12/6 15:53
 * @UpdateUser: shensl
 */
public class AcctResidueResp {

    private int waAcctId;// 账户id
    private int diamondNum;// 钻石数量

    public int getWaAcctId() {
        return waAcctId;
    }

    public int getDiamondNum() {
        return diamondNum;
    }

    @Override
    public String toString() {
        return "AcctResidueResp{" +
                "waAcctId=" + waAcctId +
                ", diamondNum=" + diamondNum +
                '}';
    }
}
