package com.android.launcher3.utils;

import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.SCREENSHOT;
import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.ALIPAY;
import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.BLUETOOTH;
import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.MOBILE_NETWORK;
import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.WIFI;
import static com.android.launcher3.moudle.shortcut.cst.ShortCutMenuConst.KEY_SP_WIDGET_NAME;

import android.text.TextUtils;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.shortcut.bean.Widget;
import com.android.launcher3.moudle.shortcut.bean.WidgetEnum;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.moudle.shortcut.util.ShutcutMenuUtil;
import com.android.launcher3.moudle.shortcut.util.WidgetResManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author : yanyong
 * Date : 2024/5/29
 * Details : 下拉控件数据构建管理类
 */
public class WidgetManager {

    private static WidgetManager Instance;
    private final String[] WIDGET_IDS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A"};
    private SPManager mSPManager;

    private WidgetManager() {
        mSPManager = SPManager.getInstance();
    }

    public static WidgetManager getInstance() {
        if (Instance == null) {
            synchronized (SPManager.class) {
                if (Instance == null) {
                    Instance = new WidgetManager();
                }
            }
        }
        return Instance;
    }

    /**
     * 插入初始化数据
     */
    public void initAddedIds() {
        StringBuilder sb = new StringBuilder();
        for (String id : WIDGET_IDS) {
            sb.append(id).append(",");
        }
        String ids = sb.substring(0, sb.length() - 1);
        LogUtil.i("initAddedIds: ids " + ids, LogUtil.TYPE_RELEASE);
        mSPManager.saveString(KEY_SP_WIDGET_NAME, ids);
    }

    /**
     * 保存已添加数据
     *
     * @param list 数据集
     */
    public void saveAddedIds(List<Widget> list) {
        if (list == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Widget widget : list) {
            sb.append(widget.getId()).append(",");
        }
        int index = sb.lastIndexOf(",");
        LogUtil.i("saveAddedIds: sb " + sb + " index " + index, LogUtil.TYPE_RELEASE);
        String str;
        if (index > 0) {
            str = sb.substring(0, sb.length() - 1);
        } else {
            str = sb.toString();
        }
        mSPManager.saveString(KEY_SP_WIDGET_NAME, str);
    }

    /**
     * 获取已添加的控件数据
     *
     * @param isAdded  是否需要添加按钮
     * @param isAlipay 是否安装支付宝应用
     * @return 控件数据集合
     */
    public List<Widget> getAddedWidgetList(boolean isAdded, boolean isAlipay) {
        String str = mSPManager.getString(KEY_SP_WIDGET_NAME);
        List<Widget> widgets = new ArrayList<>();
        if (TextUtils.isEmpty(str)) {
            return widgets;
        }
        LogUtil.i("getAddedWidgetList: str " + str + " isAlipay " + isAlipay, LogUtil.TYPE_RELEASE);
        String[] split = str.split(",");
        for (String id : split) {
            if (TextUtils.isEmpty(id)) {
                continue;
            }

            if (id.equals(WidgetEnum.formEnum(SCREENSHOT))) {
                // 需要添加按钮
                if (isAdded) {
                    Widget build = new Widget(id);
                    if (!widgets.contains(build)) {
                        widgets.add(build);
                    }
                }
            } else {
                Widget build = new Widget(id);
                if (!widgets.contains(build)) {
                    widgets.add(build);
                }
            }
        }
        // 支付宝未安装，删除支付宝控件数据
        if (!isAlipay) {
            widgets.remove(new Widget(WidgetEnum.formEnum(ALIPAY)));
        }
        LogUtil.i("getAddedWidgetList: widgets " + widgets.size(), LogUtil.TYPE_RELEASE);
        return widgets;
    }

    /**
     * 获取已添加的控件数据
     *
     * @param isAlipay 是否安装支付宝应用
     * @return 控件数据集合
     */
    public List<Widget> getDisplayWidgetList(boolean isAlipay, boolean edit) {
        String str = mSPManager.getString(KEY_SP_WIDGET_NAME);
        List<Widget> widgets = new ArrayList<>();
        if (TextUtils.isEmpty(str)) {
            return widgets;
        }
        LogUtil.i("getAddedWidgetList: str " + str + " isAlipay " + isAlipay, LogUtil.TYPE_RELEASE);
        String[] split = str.split(",");
        for (String id : split) {
            if (TextUtils.isEmpty(id)) {
                continue;
            }
            Widget build = new Widget(id);
            if (!widgets.contains(build)) {
                widgets.add(build);
            }
        }
        // 支付宝未安装，删除支付宝控件数据
        if (!isAlipay) {
            widgets.remove(new Widget(WidgetEnum.formEnum(ALIPAY)));
        }
        LogUtil.i("getAddedWidgetList: widgets " + widgets.size(), LogUtil.TYPE_RELEASE);
        return widgets;
    }

    /**
     * 获取待安装控件数据
     *
     * @param isExitAlipay 是否安装支付宝应用
     * @return 控件数据集合
     */
    public List<Widget> getNotAddedWidgetList(boolean isExitAlipay) {
        String str = mSPManager.getString(KEY_SP_WIDGET_NAME);
        List<Widget> widgets = new ArrayList<>();
        if (TextUtils.isEmpty(str)) {
            return widgets;
        }

        LogUtil.i("getNotAddedWidgetList: str " + str + " isExitAlipay " + isExitAlipay, LogUtil.TYPE_DEBUG);
        String typeAlipay = WidgetEnum.formEnum(ALIPAY);
        String[] split = str.split(",");
        List<String> ids = Arrays.asList(split);
        for (String id : WIDGET_IDS) {
            // 过滤待添加的数据
            if (!ids.contains(id) && !TextUtils.isEmpty(id)) {
                Widget build = new Widget(id);
                if (widgets.contains(build)) {
                    continue;
                }
                build.setResId(WidgetResManager.getInstance().getWidgetResId(build.getId(), false));
                build.setName(WidgetResManager.getInstance().getWidgetName(build.getId()));
                if (build.getId().equals(typeAlipay)) {
                    if (isExitAlipay) {
                        widgets.add(build);
                    }
                } else {
                    widgets.add(build);
                }
            }
        }

        Widget build = new Widget(typeAlipay);
        // 双重校验支付宝是否存在，并且判断是否在已添加中
        if (!widgets.contains(build) && isExitAlipay && !str.contains(typeAlipay)) {
            LogUtil.i("getNotAddedWidgetList: add alipay Widget", LogUtil.TYPE_DEBUG);
            widgets.add(build);
        }
        return widgets;
    }

    public boolean isSelected(Widget widget) {
        if (widget == null) {
            return false;
        }
        String id = widget.getId();
        if (WidgetEnum.formEnum(WIFI).equals(id) ||
                WidgetEnum.formEnum(BLUETOOTH).equals(id)) {
            return true;
        } else if (WidgetEnum.formEnum(MOBILE_NETWORK).equals(id)) {
            return PhoneSIMCardUtil.getInstance().isSIMCardInserted()
                    && !ShutcutMenuUtil.getInstance().isAirplaneMode();
        }
        return false;
    }

    public boolean getWidgetDataStatus() {
        String str = mSPManager.getString(KEY_SP_WIDGET_NAME);
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        LogUtil.i("getWidgetDataStatus: str " + str, LogUtil.TYPE_RELEASE);
        return true;
    }
}
