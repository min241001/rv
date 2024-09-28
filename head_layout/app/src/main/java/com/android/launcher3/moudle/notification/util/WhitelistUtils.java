package com.android.launcher3.moudle.notification.util;

import java.util.Arrays;
import java.util.List;

public class WhitelistUtils {
    private static List<String> whitelist = Arrays.asList(
            "org.codeaurora.ims",
            "android",
            "com.android.settings",
            "com.android.systemui",
            "com.android.calendar"
    );


    // 判断包名是否在白名单中
    public static boolean isPackageAllowed(String packageName) {
        return whitelist.contains(packageName);
    }
}
