package com.android.launcher3.common.base;


import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.mode.AppMode;

import java.util.List;

/**
 * @Author: shensl
 * @Description：桌面风格基类
 * @CreateDate：2023/11/29 9:31
 * @UpdateUser: shensl
 */
public abstract class BaseDesktopFragment extends AppFragment {

    protected List<AppBean> lists;

    @Override
    protected void syncData() {
        AppMode appMode = new AppMode(getContext());
        lists = appMode.loadDefaultData();
    }

    public List<AppBean> getLists() {
        return lists;
    }

}