package com.android.launcher3.utils;

public class ClassNameUtils {

    public static boolean isHostFragmentResourceRequest() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (className.contains("com.android.launcher3.common.base.BaseFragment")
                    ||className.contains("com.android.launcher3.moudle.shortcut.view")
                    ||className.contains("com.android.launcher3.moudle.shortcut.adapter")
                    ||className.contains("com.android.launcher3.common.base.AppFragment")
                    ||className.contains("com.android.launcher3.common.base.BaseRecyclerAdapter")
                    ||className.contains("com.android.launcher3.moudle.notification.adapter")
            ) {
                return true;
            }
        }
        return false;
    }
}
