package com.android.launcher3.common.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by shensonglong on 2018/1/28.
 */
public class BasePresenter<V> {

    protected final String TAG = BasePresenter.class.getSimpleName() + "--->>>";

    protected Reference<V> mViewRef;
    protected V mView;

    public void attachView(V view) {
        mViewRef = new WeakReference<V>(view);
        mView = mViewRef.get();
    }

    public V getView() {
        if (mViewRef != null) {
            return mViewRef.get();
        }
        return null;
    }

    public boolean isViewAttached() {
        return getView() != null;
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

}
