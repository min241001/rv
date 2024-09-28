package com.android.launcher3.moudle.selector.presenter;

import com.android.launcher3.App;
import com.android.launcher3.common.bean.FaceBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.mode.FaceMode;

import java.util.List;

/**
 * @Author: shensl
 * @Description：表盘选择页面presenter业务实现
 * @CreateDate：2023/12/12 15:06
 * @UpdateUser: shensl
 */
public class DialSelectorPresenter extends CommomSelectorPresenter {

    private FaceMode faceMode;

    public DialSelectorPresenter() {
        this.faceMode = new FaceMode(App.getInstance());
    }

    @Override
    public void setDefaultId(int selectedId) {
        AppLocalData.getInstance().setFaceDefaultId(selectedId);
    }

    @Override
    public int getDefaultId() {
        return AppLocalData.getInstance().getFaceDefaultId();
    }

    @Override
    public List<FaceBean> getSelectorBeans() {
        List<FaceBean> faceBeans = faceMode.loadDefaultData();
        faceBeans.addAll(faceMode.loadDialData());
        faceBeans.add(faceMode.loadMoreData());
        return faceBeans;
    }
}
