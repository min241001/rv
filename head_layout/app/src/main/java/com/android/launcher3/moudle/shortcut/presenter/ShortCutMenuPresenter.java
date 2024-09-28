package com.android.launcher3.moudle.shortcut.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.android.launcher3.App;
import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.mode.AppMode;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.shortcut.callback.CallBackManager;
import com.android.launcher3.moudle.shortcut.callback.SignalStrengthCallBack;
import com.android.launcher3.moudle.shortcut.callback.SwitchChangeCallBack;
import com.android.launcher3.moudle.shortcut.callback.WifiSignalCallBack;
import com.android.launcher3.moudle.shortcut.cst.ShortCutMenuConst;
import com.android.launcher3.moudle.shortcut.util.BaseUtil;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.moudle.shortcut.util.ShutcutMenuUtil;
import com.android.launcher3.moudle.shortcut.util.SignalStrengthUtils;
import com.android.launcher3.common.utils.WifiUtil;
import com.android.launcher3.moudle.shortcut.view.IShortCutMenuView;
import com.android.launcher3.utils.WidgetManager;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2024/1/4 15:36
 * @UpdateUser: shensl
 */
public class ShortCutMenuPresenter extends BasePresenter<IShortCutMenuView> implements IShortCutMenuPresenter {

    private Activity mActivity;
    private AppMode appMode;
    private long mLastMobileTime;
    private long mLastUpdateStatusTime;
    private int mLastWifiLevel;

    public ShortCutMenuPresenter(Activity act) {
        appMode = new AppMode(act);
        mActivity = act;
    }

    @Override
    public void initData() {
        // 注册监听
        CallBackManager.getInstance().registerCallBack(mSwitchChangeCallBack);
        CallBackManager.getInstance().registerCallBack(mWifiSignalCallBack);
        CallBackManager.getInstance().registerCallBack(simCallBack);
    }

    @Override
    public void deinitData() {
        // 移除监听
        CallBackManager.getInstance().unregisterCallBack(mSwitchChangeCallBack);
        CallBackManager.getInstance().unregisterCallBack(mWifiSignalCallBack);
        CallBackManager.getInstance().unregisterCallBack(simCallBack);
    }

    @Override
    public void updateData() {
        // 信号、插卡、网络等判断
        if (isViewAttached()) {
            updateStatusMobileData();
            boolean wifiConnected = WifiUtil.isWifiConnected(App.getInstance());
            LogUtil.i("updateData: isWifiConnected " + wifiConnected, LogUtil.TYPE_RELEASE);
            if (wifiConnected) {
                getView().setWifiImageResource(BaseUtil.getWifiResId());
            }
        }
        if (PhoneSIMCardUtil.getInstance().isSIMCardInserted()) {// 有SIM卡
            String networkType = BaseUtil.getNetworkType();
            String operatorName = PhoneSIMCardUtil.getInstance().getOperatorName();
            if (isViewAttached()) {
                getView().setSIMCardInserted();
                getView().setOperatorName(networkType + operatorName);
//                getView().setNetworkType(networkType);
            }
            LogUtil.d(String.format("有SIM卡，厂商名称：%s ,网络类型：%s", operatorName, networkType), LogUtil.TYPE_RELEASE);
        } else {// 无SIM卡
            if (isViewAttached()) {
                getView().setNoSIMCardInserted();
            }
            LogUtil.d("无SIM卡", LogUtil.TYPE_RELEASE);
        }
    }

    /**
     * 刷新状态栏有无SIM卡状态图
     *
     * @param state 状态
     */
    private void updateSimCardStatus(boolean state) {
        LogUtil.i("updateSimCardStatus state " + state, LogUtil.TYPE_RELEASE);
        if (System.currentTimeMillis() - mLastMobileTime < 200) {
            return;
        }
        mLastMobileTime = System.currentTimeMillis();
    }

    /**
     * 刷新状态栏网络状态数据
     */
    public void updateStatusMobileData() {
        updateStatusMobileData(PhoneSIMCardUtil.getInstance().getCellularSignalStrength());
    }

    /**
     * 刷新状态栏网络状态数据
     *
     * @param level 网络信号等级
     */
    public void updateStatusMobileData(int level) {
        LogUtil.d("数据信号强度: " + level + " isMobileDataEnabled "
                + ShutcutMenuUtil.getInstance().isMobileDataEnabled(), LogUtil.TYPE_RELEASE);
        int signalResourceId;
        if (ShutcutMenuUtil.getInstance().isMobileDataEnabled()) {
            signalResourceId = SignalStrengthUtils.getMobileSignalResourceId(level);
        } else {
            signalResourceId = SignalStrengthUtils.getSignalResourceId(level);
        }
        if (isViewAttached()) {
            getView().setSignalImageResource(signalResourceId);
        }
    }

    public void wifiStatus() {
        if (WifiUtil.isWifiConnected(App.getInstance())) {
            if (isViewAttached()) {
                getView().setWifiOpen();
            }
        } else {
            if (isViewAttached()) {
                getView().setWifiClose();
            }
        }
    }

    // wifi强度监听
    final WifiSignalCallBack mWifiSignalCallBack = new WifiSignalCallBack() {
        @Override
        public void onSignalLevel(int level) {
            LogUtil.i("onSignalLevel: 强(3) <--> 弱(0) level " + level, LogUtil.TYPE_RELEASE);
            if (isViewAttached()) {
                getView().setWifiImageResource(SignalStrengthUtils.getWifiLevelResourceId(level));
            }
        }
    };

    final SwitchChangeCallBack mSwitchChangeCallBack = new SwitchChangeCallBack() {
        @Override
        public void onSwitchStateTypeChange(SwitchStateType type, boolean state) {
            switch (type) {
                case SIM:// SIM卡
                    updateData();
                    updateSimCardStatus(state);
                    break;
                case WIFI:// WIFI
                    if (state) {
                        if (isViewAttached()) {
                            getView().setWifiOpen();
                        }
                    } else {
                        if (isViewAttached()) {
                            getView().setWifiClose();
                        }
                    }
                    break;
            }
        }
    };

    final SignalStrengthCallBack simCallBack = level -> {
        if (isViewAttached() && (mLastWifiLevel != level
                || System.currentTimeMillis() - mLastUpdateStatusTime > 1000)) {
            LogUtil.i("onSignalStrength: level " + level, LogUtil.TYPE_RELEASE);
            mLastWifiLevel = level;
            mLastUpdateStatusTime = System.currentTimeMillis();
            updateStatusMobileData(level);
        }
    };

    @Override
    public AppBean getAppBean() {
        return appMode.getAppByPackageName(ShortCutMenuConst.ACTION_ALIPAY);
    }

    @SuppressLint({"ResourceType", "Recycle"})
    public void initBaseWidgetData() {
        boolean status = WidgetManager.getInstance().getWidgetDataStatus();
        LogUtil.i("initBaseWidgetData: status " + status, LogUtil.TYPE_DEBUG);
        if (!status) {
            WidgetManager.getInstance().initAddedIds();
        }
    }
}
