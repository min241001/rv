package com.android.launcher3.moudle.selector.view;

import com.android.launcher3.common.bean.SelectorBean;
import com.android.launcher3.moudle.selector.presenter.CommomSelectorPresenter;
import com.android.launcher3.moudle.selector.presenter.WorkspaceSelectorPresenter;

/**
 * @Author: zeckchan
 * @Description：桌面风格页面
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class WorkspaceSelectorActivity extends CommomSelectorActivity<WorkspaceSelectorPresenter> {

    @Override
    protected CommomSelectorPresenter createPresenter() {
        return new WorkspaceSelectorPresenter();
    }

    @Override
    protected void onItemClickToSetting(SelectorBean bean, int position) {
        super.onItemClickToSetting(bean, position);
        spacesWard();
    }

}