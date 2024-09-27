package com.android.launcher3.common.network.listener;


/**
 * @Description：基础监听
 */
public interface BaseListener<T> {

    void onError(int code,String msg);
    void onSuccess(T t);

}
