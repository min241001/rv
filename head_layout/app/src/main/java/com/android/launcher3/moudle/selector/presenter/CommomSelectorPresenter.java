package com.android.launcher3.moudle.selector.presenter;

import android.util.Log;

import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.common.bean.SelectorBean;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.moudle.selector.view.CommomSelectorView;

import java.util.List;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2023/12/15 18:30
 * @UpdateUser: shensl
 */
public abstract class CommomSelectorPresenter extends BasePresenter<CommomSelectorView> implements ICommomSelectorPresenter {

    private boolean isSwipeToExitEnabled;

    @Override
    public void initData() {
        setSwipeToExitEnabled(getDefaultId() == 0);
        // 开启子线程加载数据
        ThreadPoolUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                List<SelectorBean> selectorBeans = getSelectorBeans();
                int defaultId = getDefaultId();
                Log.e("TAG", "defaultId" + defaultId);
                if (isViewAttached()) {
                    getView().setData(selectorBeans, defaultId);
                }
                for (int i = 0; i < selectorBeans.size(); i++) {
                    SelectorBean bean = selectorBeans.get(i);
                    if (bean.getId() == defaultId) {
                        if (isViewAttached()) {
                            getView().scrollToPosition(i);
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void setSwipeToExitEnabled(boolean swipeToExitEnabled) {
        this.isSwipeToExitEnabled = swipeToExitEnabled;
    }

    @Override
    public boolean isSwipeToExitEnabled() {
        return isSwipeToExitEnabled;
    }
}