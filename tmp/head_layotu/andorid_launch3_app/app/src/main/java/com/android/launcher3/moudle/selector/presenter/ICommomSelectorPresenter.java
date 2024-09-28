package com.android.launcher3.moudle.selector.presenter;

import com.android.launcher3.common.bean.SelectorBean;

import java.util.List;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2023/12/15 18:30
 * @UpdateUser: shensl
 */
public interface ICommomSelectorPresenter<T extends SelectorBean> {
    void initData();

    void setDefaultId(int selectedId);

    int getDefaultId();

    List<T> getSelectorBeans();

    void setSwipeToExitEnabled(boolean b);

    boolean isSwipeToExitEnabled();
}
