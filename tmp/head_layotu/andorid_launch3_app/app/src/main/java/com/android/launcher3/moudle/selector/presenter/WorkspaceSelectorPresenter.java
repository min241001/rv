package com.android.launcher3.moudle.selector.presenter;

import com.android.launcher3.App;
import com.android.launcher3.common.bean.WorkspaceBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.mode.WorkspaceMode;

import java.util.List;

/**
 * @Author: shensl
 * @Description：桌面风格页面presenter层业务实现
 * @CreateDate：2023/12/14 9:43
 * @UpdateUser: shensl
 */
public class WorkspaceSelectorPresenter extends CommomSelectorPresenter {

    // 定义数据
    private WorkspaceMode workspaceMode;

    public WorkspaceSelectorPresenter() {
        // 获取数据
        this.workspaceMode = new WorkspaceMode(App.getInstance());
    }

    @Override
    public void setDefaultId(int selectedId) {
        AppLocalData.getInstance().setWorkspaceDefaultId(selectedId);
    }

    @Override
    public int getDefaultId() {
        return AppLocalData.getInstance().getWorkspaceDefaultId();
    }

    @Override
    public List<WorkspaceBean> getSelectorBeans() {
        return workspaceMode.loadDefaultData();
    }

}
