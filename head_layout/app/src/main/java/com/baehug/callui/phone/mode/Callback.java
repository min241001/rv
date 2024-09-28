package com.baehug.callui.phone.mode;

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : callback
 */
public interface Callback<R> {

    /**
     * 成功
     * @param response
     */
    void onSuccess(R response);

    /**
     * 失败
     * @param code
     * @param message
     */
    void onFailed(int code, String message);

}
