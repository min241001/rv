package com.android.launcher3.moudle.shortcut.presenter;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.android.launcher3.App;
import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.mode.AppMode;
import com.android.launcher3.common.utils.BluetoothUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.MobileDataUtil;
import com.android.launcher3.common.utils.ShortCutUtils;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.common.utils.WifiUtil;
import com.android.launcher3.moudle.shortcut.bean.Widget;
import com.android.launcher3.moudle.shortcut.bean.WidgetEnum;
import com.android.launcher3.moudle.shortcut.callback.BatteryCallBack;
import com.android.launcher3.moudle.shortcut.callback.CallBackManager;
import com.android.launcher3.moudle.shortcut.cst.ShortCutMenuConst;
import com.android.launcher3.moudle.shortcut.util.AirplaneModeUtil;
import com.android.launcher3.moudle.shortcut.util.BaseUtil;
import com.android.launcher3.moudle.shortcut.util.MemoryManager;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.moudle.shortcut.util.ShutcutMenuUtil;
import com.android.launcher3.moudle.shortcut.util.SignalStrengthUtils;
import com.android.launcher3.moudle.shortcut.util.WidgetResManager;
import com.android.launcher3.utils.WidgetManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2024/1/4 15:36
 * @UpdateUser: shensl
 */
public class PullDownPresenter extends BasePresenter<IPullDownView> implements IShortCutMenuPresenter {

    private List<Widget> mWidgetList = new ArrayList<>();
    private Activity mActivity;
    private AppMode appMode;
    // 上次请求的时间
    private long mLastRequestTime;
    // 上次移动网络图标刷新时间
    private long mLastMobileTime;
    private long mLastMem;
    private long mStartClearTime;
    private final BatteryCallBack mBatteryCallBack = new BatteryCallBack() {
        @Override
        public void onBatteryChange(int battery) {
            if (isViewAttached()) {
                getView().setBatteryProgress(battery, BaseUtil.getBatteryResId(battery));
            }
        }
    };

    public PullDownPresenter(Activity act) {
        appMode = new AppMode(act);
        mActivity = act;
    }

    @Override
    public void initData() {
        CallBackManager.getInstance().registerCallBack(mBatteryCallBack);
    }

    @Override
    public void deinitData() {
        CallBackManager.getInstance().unregisterCallBack(mBatteryCallBack);
    }

    @Override
    public void updateData() {

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
        for (Widget widget : mWidgetList) {
            if (WidgetEnum.formEnum(WidgetEnum.MOBILE_NETWORK).equals(widget.getId())) {
                widget.setSelected(state && ShutcutMenuUtil.getInstance().isMobileDataEnabled());
                LogUtil.i("updateSimCardStatus " + widget.toString(), LogUtil.TYPE_RELEASE);
                break;
            }
        }

        if (isViewAttached()) {
            getView().onUpdateWidgetData(mWidgetList);
        }
    }

    /**
     * 获取所有下拉已添加控件数据
     */
    public void getAllWidgetData(boolean edit) {
        if (System.currentTimeMillis() - mLastRequestTime < 100) {
            LogUtil.i("getWidgetData: Block requests for the same time!", LogUtil.TYPE_RELEASE);
            return;
        }
        mLastRequestTime = System.currentTimeMillis();
        int brightness = ShortCutUtils.INSTANCE.getSystemBrightness();
        int volume = ShortCutUtils.INSTANCE.getSound();
        getWidgetData(edit);
    }

    /**
     * 设置控件的状态
     */
    public void getWidgetData(boolean edit) {
        mWidgetList = WidgetManager.getInstance().getDisplayWidgetList(
                BaseUtil.isAppExit(mActivity, ShortCutMenuConst.ACTION_ALIPAY), edit);
        try {
            for (Widget bean : mWidgetList) {
                switch (WidgetEnum.fromStr(bean.getId())) {
                    case WIFI:
                        bean.setSelected(WifiUtil.isWifiEnabled(mActivity));
                        LogUtil.i("WIFI: " + WifiUtil.isWifiEnabled(mActivity), LogUtil.TYPE_RELEASE);
                        break;
                    case MOBILE_NETWORK:
                        bean.setSelected(mobileSelect());
                        LogUtil.i("NETWORK mobileSelect " + mobileSelect(), LogUtil.TYPE_RELEASE);
                        break;
                    case BLUETOOTH:
                        LogUtil.i("BLUETOOTH " + BluetoothUtil.isBluetoothEnabled(), LogUtil.TYPE_RELEASE);
                        bean.setSelected(BluetoothUtil.isBluetoothEnabled());
                        break;
                    case MUTE:
                        bean.setSelected(BaseUtil.isVibrate());
                        break;
                }
                bean.setResId(WidgetResManager.getInstance().getWidgetResId(bean.getId(), bean.getSelected()));
                bean.setName(WidgetResManager.getInstance().getWidgetName(bean.getId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isViewAttached()) {
            getView().onUpdateWidgetData(mWidgetList);
        }
    }

    @Override
    public AppBean getAppBean() {
        return appMode.getAppByPackageName(ShortCutMenuConst.ACTION_ALIPAY);
    }

    /**
     * 判断移动网络是否选中状态
     *
     * @return 是否被打开
     */
    private boolean mobileSelect() {
        if (mActivity == null) {
            return false;
        }
        if (!PhoneSIMCardUtil.getInstance().isSIMCardInserted() || AirplaneModeUtil.isAirplaneMode(mActivity)) {
            LogUtil.i("mobileSelect: sim " + PhoneSIMCardUtil.getInstance().isSIMCardInserted()
                    + " AirplaneMode " + AirplaneModeUtil.isAirplaneMode(mActivity), LogUtil.TYPE_RELEASE);
            return false;
        } else {
            LogUtil.i("mobileSelect: " + MobileDataUtil.INSTANCE.getDataEnabled(mActivity), LogUtil.TYPE_RELEASE);
            return MobileDataUtil.INSTANCE.getDataEnabled(mActivity);
        }
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

    public void clearCache() {
        mLastMem = MemoryManager.getInstance().getAvailMemory();
        mStartClearTime = System.currentTimeMillis();
        PackageManager packageManager = App.getInstance().getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(0);
        ThreadPoolUtils.getExecutorService().execute(() -> {
            try {
                for (ApplicationInfo appInfo : appList) {
                    // 获取缓存目录
                    String packageName = appInfo.packageName;
                    File dataDir = new File(packageManager.getApplicationInfo(packageName, 0).dataDir);
                    File cacheDir = new File(dataDir.getParentFile(), "cache");
                    boolean dir = deleteDir(cacheDir);
                }
                long duration = System.currentTimeMillis() - mStartClearTime;
                if (isViewAttached()) {
                    String availMem = MemoryManager.getInstance().getCleanSpace(mLastMem);
                    String totalMemory = MemoryManager.getInstance().getTotalMemory();
                    getView().onClearComplete(duration, availMem, totalMemory);
                    LogUtil.i(TAG, "clearCache: " + duration + " lastMem " + mLastMem
                            + " availMem " + availMem + " totalMem " + totalMemory, LogUtil.TYPE_RELEASE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children == null) {
                return false;
            }
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        return false;
    }
}
