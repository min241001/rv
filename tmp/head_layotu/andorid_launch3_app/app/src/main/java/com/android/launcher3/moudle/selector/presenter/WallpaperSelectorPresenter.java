package com.android.launcher3.moudle.selector.presenter;

import com.android.launcher3.App;
import com.android.launcher3.common.bean.WorkspaceBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.mode.WallpaperMode;

import java.util.List;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2023/12/15 19:40
 * @UpdateUser: shensl
 */
public class WallpaperSelectorPresenter extends CommomSelectorPresenter {

    private WallpaperMode wallpaperMode;

    public WallpaperSelectorPresenter() {
        this.wallpaperMode = new WallpaperMode(App.getInstance());
    }

    @Override
    public void setDefaultId(int selectedId) {
        AppLocalData.getInstance().setWallpaperDefaultId(selectedId);
    }

    @Override
    public int getDefaultId() {
        return AppLocalData.getInstance().getWallpaperDefaultId();
    }

    @Override
    public List getSelectorBeans() {
        List<WorkspaceBean> workspaceBeans = wallpaperMode.loadDefaultData();
        workspaceBeans.addAll(wallpaperMode.loadThemeData());
        workspaceBeans.add(wallpaperMode.loadMoreData());
        return workspaceBeans;
    }
}
