package com.android.launcher3.moudle.shortcut.presenter;

import com.android.launcher3.common.bean.AppBean;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2024/1/4 16:06
 * @UpdateUser: shensl
 */
public interface IShortCutMenuPresenter {
    void initData();

    void deinitData();

    void updateData();

    AppBean getAppBean();
}
