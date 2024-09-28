package com.android.launcher3.moudle.shortcut.util;

import com.android.launcher3.App;
import com.android.launcher3.R;
import com.android.launcher3.moudle.shortcut.bean.WidgetEnum;

/**
 * Author : yanyong
 * Date : 2024/5/20
 * Details : 下拉控件图片资源管理文件
 */
public class WidgetResManager {

    private static WidgetResManager Instance;

    public WidgetResManager() {
    }

    public static WidgetResManager getInstance() {
        if (Instance == null) {
            synchronized (WidgetResManager.class) {
                if (Instance == null) {
                    Instance = new WidgetResManager();
                }
            }
        }
        return Instance;
    }

    /**
     * 通过控件类型获取下拉对应的图片资源
     *
     * @param id 控件类型
     * @return 图片资源
     */
    public int getWidgetResId(String id, boolean checked) {
        if (checked) {
            switch (WidgetEnum.fromStr(id)) {
                case WIFI:
                    return R.drawable.svg_3_wifi_white;
                case MOBILE_NETWORK:
                    return R.drawable.svg_3_network_wihte;
                case BLUETOOTH:
                    return R.drawable.svg_3_bluetooth_white;
                case MUTE:
                    return R.drawable.svg_3_mute_white_9;
            }
            return R.drawable.svg_3_wifi_white;
        }
        switch (WidgetEnum.fromStr(id)) {
            case WIFI:
                return R.drawable.svg_3_wifi_gay;
            case MOBILE_NETWORK:
                return R.drawable.svg_3_network_gay;
            case BLUETOOTH:
                return R.drawable.svg_3_bluetooth_gay;
            case TASK:
                return R.drawable.svg_3_task_gay;
            case SCREENSHOT:
                return R.drawable.svg_3_screenshot_gay;
            case ALIPAY:
                return R.drawable.svg_3_alipay_gay;
            case SETTINGS:
                return R.drawable.svg_3_setting_gay;
            case CLEAR:
                return R.drawable.svg_3_clear_gay;
            case MUTE:
                return R.drawable.svg_3_mute_gay;
            case BRIGHTNESS:
                return R.drawable.svg_3_sun_gay;
        }
        return 0;
    }

    public String getWidgetName(String id) {
        switch (WidgetEnum.fromStr(id)) {
            case BATTERY:
                return getStrById(R.string.battery_name);
            case WIFI:
                return getStrById(R.string.wifi);
            case MOBILE_NETWORK:
                return getStrById(R.string.mobile_network);
            case BLUETOOTH:
                return getStrById(R.string.widget_bluetooth);
            case TASK:
                return getStrById(R.string.recent_tasks);
            case SCREENSHOT:
                return getStrById(R.string.screenshot);
            case ALIPAY:
                return getStrById(R.string.widget_alipay);
            case SETTINGS:
                return getStrById(R.string.widget_setting);
            case CLEAR:
                return getStrById(R.string.tasks_clear);
            case MUTE:
                return getStrById(R.string.mute);
            case BRIGHTNESS:
                return getStrById(R.string.brightness_setting);
        }
        return "";
    }

    private String getStrById(int id) {
        return App.getInstance().getResources().getString(id);
    }
}
