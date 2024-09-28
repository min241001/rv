package com.android.launcher3.moudle.selector.view;


import com.android.launcher3.common.bean.SelectorBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.moudle.selector.presenter.CommomSelectorPresenter;
import com.android.launcher3.moudle.selector.presenter.DialSelectorPresenter;

/**
 * @Author: zeckchan
 * @Description：表盘选择页面
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class DialSelectorActivity extends CommomSelectorActivity<DialSelectorPresenter> {

    @Override
    protected CommomSelectorPresenter createPresenter() {
        return new DialSelectorPresenter();
    }

    @Override
    protected void onItemClickToSetting(SelectorBean bean, int position) {
        super.onItemClickToSetting(bean, position);
        if (bean.getId() >= 1000){
            AppLocalData.getInstance().setFaceFilePath(bean.getFilepath());
            AppLocalData.getInstance().setFaceClassName(bean.getClassName());
        }
        backward();
    }

}