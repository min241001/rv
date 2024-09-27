package com.android.launcher3.common.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * @Author: shensl
 * @Description：mvp架构基类
 * @CreateDate：2023/12/10 11:02
 * @UpdateUser: shensl
 */
public abstract class BaseMvpActivity<V,P extends BasePresenter<V>> extends BaseActivity {

    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
        super.onCreate(savedInstanceState);
    }

    protected abstract P createPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}