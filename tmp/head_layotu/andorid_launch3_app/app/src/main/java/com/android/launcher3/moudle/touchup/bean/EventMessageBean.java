package com.android.launcher3.moudle.touchup.bean;

/**
 * Created by pengmin on 2024/8/30.
 */
public class EventMessageBean {
    private int what = 0;
    private int type = 0;
    private String msg = "";

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
