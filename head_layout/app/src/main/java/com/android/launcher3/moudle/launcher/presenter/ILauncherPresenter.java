package com.android.launcher3.moudle.launcher.presenter;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

interface ILauncherPresenter {

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 加载页面
     */
    void loadFragments(@NonNull FragmentManager fragmentManager);

    void hideAllFragment(FragmentManager fragmentManager);

    void showShortcutMenuFragment(FragmentManager fragmentManager);

    void showNotificationFragment(FragmentManager fragmentManager);

    void showTouchUpEventFragment(FragmentManager fragmentManager);

    void setPageTransformer(ViewPager viewPager);
}
