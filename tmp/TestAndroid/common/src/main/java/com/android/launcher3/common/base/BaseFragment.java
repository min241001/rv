package com.android.launcher3.common.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;

/**
 * @Description：基类
 */
public abstract class BaseFragment extends Fragment {

    protected static final String TAG = BaseFragment.class.getSimpleName();

    // 蜂窝的层数
    public static int mAppLayer = 6;
    protected Context mContext;
    protected View mRootView;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext = null;
        mRootView = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mAppLayer = AppUtils.getAppLayer(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getResourceId(), container, false);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        initEvent();
    }

    protected void runOnUiThread(Runnable runnable) {
        if (getActivity() != null && !getActivity().isDestroyed()) {
            getActivity().runOnUiThread(runnable);
        }
    }


    protected void jumpToTargetActivity(String pkg, String cls) {
        if (TextUtils.isEmpty(pkg) || TextUtils.isEmpty(cls)) {
            LogUtil.e("pkg或者cls跳转界面的");
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(pkg, cls));
        startActivity(intent);
    }

    protected void jumpToTargetActivity(Class<?> clazz) {
        if (clazz == null) {
            LogUtil.e("clazz == null");
            return;
        }
        Intent intent = new Intent(getContext(), clazz);
        startActivity(intent);
    }

    protected final <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    protected abstract int getResourceId();

    protected void initView(View view) {
        // 由子类覆盖重写
    }

    protected void initData() {
        // 由子类覆盖重写
    }

    protected void initEvent() {
        // 由子类覆盖重写
    }


    /**
     * 启动应用
     *
     * @param app
     */
    protected void lunchApp(AppBean app) {
        LauncherAppManager.launcherApp(getActivity(), app);
    }

}
