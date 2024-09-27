package com.android.launcher3.common.mode;

import java.util.List;

/**
 * @Author: shensl
 * @Description：app相关的业务接口
 * @CreateDate：2023/12/10 14:24
 * @UpdateUser: shensl
 */
interface IAppMode<T> {

    /**
     * 加载默认配置
     * @return
     */
    List<T> loadDefaultConfig();

    T getAppByPackageName(String packageName);

}