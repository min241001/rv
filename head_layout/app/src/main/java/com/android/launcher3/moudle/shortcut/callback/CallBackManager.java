package com.android.launcher3.moudle.shortcut.callback;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shensl
 * @Description：回调管理类
 * @CreateDate：2024/1/11 11:51
 * @UpdateUser: shensl
 */
public class CallBackManager {

    private List<BaseCallBack> callBacks = new ArrayList<>();

    private static CallBackManager instance;

    private CallBackManager() {
    }

    public static CallBackManager getInstance() {
        if (instance == null) {
            instance = new CallBackManager();
        }
        return instance;
    }

    // 注册回调
    public void registerCallBack(BaseCallBack callBack) {
        if (!callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
    }

    // 反注册回调
    public void unregisterCallBack(BaseCallBack callBack) {
        if (callBacks.contains(callBack)) {
            callBacks.remove(callBack);
        }
    }

    // 清空回调
    public void clear() {
        callBacks.clear();
    }

    public <T extends BaseCallBack> T getCallBack(Class<T> clazz) {
        if (callBacks != null && callBacks.size() > 0) {
            for (BaseCallBack callback : callBacks) {
                if (clazz.isInstance(callback)) {
                    return clazz.cast(callback);
                }
            }
        }
        return null;
    }

}
