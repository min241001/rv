package com.android.launcher3.utils;

import com.android.launcher3.common.bean.WorkspaceBean;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.selector.presenter.WorkspaceSelectorPresenter;

import java.util.List;

/**
 * Author : yanyong
 * Date : 2024/9/9
 * Details : 开关机悬浮框管理器
 */
public class OnOffFloatingManager {

    private static final String TAG = "OnOffFloatingManager";
    private static OnOffFloatingManager Instance;

    private OnOffWindow mOnOffWindow;

    private boolean mShow;

    private WorkspaceSelectorPresenter mPresenter;

    private OnOffFloatingManager() {
        mOnOffWindow = new OnOffWindow();
        mPresenter = new WorkspaceSelectorPresenter();
    }

    public static OnOffFloatingManager getInstance() {
        if (Instance == null) {
            synchronized (OnOffFloatingManager.class) {
                if (Instance == null) {
                    Instance = new OnOffFloatingManager();
                }
            }
        }
        return Instance;
    }

    public void showFloating() {
        LogUtil.i(TAG, "showFloating: mShow " + mShow, LogUtil.TYPE_RELEASE);
        if (!mShow) {
            if (mOnOffWindow != null) {
                mShow = true;
                mOnOffWindow.show();
            }
        }
    }

    public boolean isShowing() {
        return mShow;
    }

    public void dismissFloating() {
        if (mOnOffWindow != null) {
            mOnOffWindow.dismiss();
            setShowing();
        }
    }

    public void setShowing() {
        mShow = false;
    }

    /**
     * 设置下一个风格
     */
    public void setNextWorkspace() {
        if (mPresenter == null) {
            return;
        }
        int defaultId = mPresenter.getDefaultId();
        LogUtil.i(Constants.ws,"get defaultId:"+defaultId);
        List<WorkspaceBean> beans = mPresenter.getSelectorBeans();
        int nextId = 0;
        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getId() == defaultId) {
                if (i == beans.size() - 1) {
                    nextId = beans.get(0).getId();
                } else {
                    nextId = beans.get(i + 1).getId();
                }
            }
        }
        LogUtil.i(TAG, "setNextWorkspace: nextId " + nextId + " defaultId " + defaultId, LogUtil.TYPE_RELEASE);
        mPresenter.setDefaultId(nextId);
    }
}
