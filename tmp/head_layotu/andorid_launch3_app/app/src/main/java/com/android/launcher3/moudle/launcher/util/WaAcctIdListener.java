package com.android.launcher3.moudle.launcher.util;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/4 18:42
 * @UpdateUser: jamesfeng
 */
public interface WaAcctIdListener {
    /**
     * 账号变更
     *
     * @param newAcctId 新账号id
     * @param oldAcctId 旧账号id
     */
    void waAcctIdChange(String newAcctId, String oldAcctId);
}
