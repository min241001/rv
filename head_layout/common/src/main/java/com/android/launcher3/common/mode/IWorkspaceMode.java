package com.android.launcher3.common.mode;

/**
 * @Author: shensl
 * @Description：桌面风格业务接口
 * @CreateDate：2023/12/10 14:27
 * @UpdateUser: shensl
 */
interface IWorkspaceMode {

    /**
     * 通过ID虎丘默认的类
     * @return
     */
    Class<?> getDefaultClassById() throws ClassNotFoundException;

}
