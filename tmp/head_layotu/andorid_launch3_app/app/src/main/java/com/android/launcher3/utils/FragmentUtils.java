package com.android.launcher3.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2024/1/30 11:00
 * @UpdateUser: shensl
 */
public class FragmentUtils {

    // 添加Fragment
    public static void addFragment(FragmentManager fragmentManager, Fragment fragment, int containerId) {
        showFragment(fragmentManager, fragment, containerId, true);
    }

    // 显示Fragment
    public static void showFragment(FragmentManager fragmentManager, Fragment fragment, int containerId) {
        showFragment(fragmentManager, fragment, containerId, true);
    }

    private static void showFragment(FragmentManager fragmentManager, Fragment fragment, int containerId, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentById(containerId);

        if (existingFragment != null) {
            fragmentTransaction.show(existingFragment);
        } else {
            if (fragment != null) {
                if (fragment.isAdded()) {
                    fragmentTransaction.show(fragment);
                } else {
                    fragmentTransaction.add(containerId, fragment);
                }
            }
        }

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commitAllowingStateLoss();
    }

    // 隐藏Fragment
    public static void hideFragment(FragmentManager fragmentManager, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

}