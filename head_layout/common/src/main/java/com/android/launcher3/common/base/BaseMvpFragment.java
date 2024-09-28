package com.android.launcher3.common.base;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * @Author: shensl
 * @Description：mvp架构基类
 * @CreateDate：2023/12/10 11:02
 * @UpdateUser: shensl
 */
public abstract class BaseMvpFragment<V, P extends BasePresenter<V>> extends BaseFragment {

    protected P mPresenter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
    }

    protected abstract P createPresenter();

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

}