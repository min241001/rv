package com.android.launcher3.common.network.resp;

/**
 * @Author: shensl
 * @Description：通用返回
 * @CreateDate：2023/12/6 15:35
 * @UpdateUser: shensl
 */
public class BaseReponse<T> {
    private int code;
    private String msg;
    private T data;
    private String err;
    private long timestamp;

    public boolean isSuccess(){
        return this.code == 0;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public String getErr() {
        return err;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "BaseReponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", err='" + err + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
