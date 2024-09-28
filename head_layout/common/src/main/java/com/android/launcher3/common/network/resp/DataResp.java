package com.android.launcher3.common.network.resp;

public class DataResp {

    private Object data;

    @Override
    public String toString() {
        return "DataResp{" +
                "data=" + data +
                '}';
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public DataResp(Object data) {
        this.data = data;
    }

    public DataResp() {
    }
}
