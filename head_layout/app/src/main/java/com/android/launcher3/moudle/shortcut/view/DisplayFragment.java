package com.android.launcher3.moudle.shortcut.view;

import static com.android.launcher3.common.CompileConfig.SYSTEM_WIFI;
import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.fromStr;
import static com.android.launcher3.moudle.shortcut.cst.ShortCutMenuConst.PKG_SETTING;
import static com.android.launcher3.moudle.shortcut.util.BaseUtil.DURATION_400;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.App;
import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpFragment;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.moudle.launcher.view.LauncherActivity;
import com.android.launcher3.moudle.shortcut.adapter.RvDisplayAdapter;
import com.android.launcher3.moudle.shortcut.bean.Widget;
import com.android.launcher3.moudle.shortcut.bean.WidgetEnum;
import com.android.launcher3.moudle.shortcut.cst.ShortCutMenuConst;
import com.android.launcher3.moudle.shortcut.presenter.IPullDownView;
import com.android.launcher3.moudle.shortcut.presenter.PullDownInterface;
import com.android.launcher3.moudle.shortcut.presenter.PullDownPresenter;
import com.android.launcher3.moudle.shortcut.util.BaseUtil;
import com.android.launcher3.moudle.shortcut.util.IntentUtil;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.moudle.shortcut.util.ShutcutMenuUtil;
import com.android.launcher3.moudle.shortcut.widgets.MyItemDecoration;
import com.android.launcher3.netty.NettyManager;
import com.android.launcher3.utils.KeyCodeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : yanyong
 * Date : 2024/7/5
 * Details : 下拉控件显示页
 */
public class DisplayFragment extends BaseMvpFragment<IPullDownView, PullDownPresenter> implements IPullDownView, View.OnClickListener {

    private static final String TAG = "DisplayFragment";
    private static final long UPDATE_DELAY = 100;
    private RvDisplayAdapter mRvDisplayAdapter;
    private List<Widget> mWidgetBeans = new ArrayList<>();
    private RecyclerView mRv;
    private long mLastUpdateTime = 0;
    private int mBattery;
    // 是否滑动到底部
    private boolean mScrollBottom;
    // 向上滑动次数
    private int mCount;
    // 向下滑动距离
    private int mScrollY;
    private Handler mHandler = new Handler();

    public static DisplayFragment newInstance(String[] params) {
        DisplayFragment fragment = new DisplayFragment();
        Bundle args = new Bundle();
        if (params != null && params.length == 0) {
            args.putStringArray("PARAM", params);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected PullDownPresenter createPresenter() {
        return new PullDownPresenter(getActivity());
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_display;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        LogUtil.i(TAG, "initView: ", LogUtil.TYPE_RELEASE);
        mRv = findViewById(R.id.rv);
    }

    @Override
    protected void initData() {
        super.initData();
        // 注册监听
        mPresenter.initData();
        mBattery = BaseUtil.getBattery();
        LogUtil.i("initData: mBattery " + mBattery, LogUtil.TYPE_RELEASE);
        // 注册监听
        mPresenter.initData();
        mRvDisplayAdapter = new RvDisplayAdapter(mWidgetBeans, mBattery);
        mRvDisplayAdapter.setOnItemClickListener(mOnItemClickListener);
        mRv.setAdapter(mRvDisplayAdapter);
        GridLayoutManager manager = getGridLayoutManager();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position < mWidgetBeans.size()) {
                    return 1;
                }
                return 2;
            }
        });
        mRv.setLayoutManager(manager);
        mRv.addItemDecoration(new MyItemDecoration(getResources().getDimension(R.dimen._8dp), getResources().getDimension(R.dimen._7dp)));
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 检查是否滑动到了最底部
                if (dy > 0 && !recyclerView.canScrollVertically(1)) {
                    // 滚动到底部时的逻辑
                    mScrollBottom = true;
                }
                if (dy < 0) {
                    if (mScrollY < -20) {
                        mScrollY = 0;
                        mScrollBottom = false;
                        mCount = 0;
                    }
                    mScrollY += dy;
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                    // 停止滚动并且滑动到底部时的逻辑
                    Log.i(TAG, "onScrollStateChanged: mCount " + mCount + " " + mScrollBottom);
                    if (mCount > 0 && mScrollBottom) {
                        if (getActivity() instanceof LauncherActivity) {
                            ((LauncherActivity) getActivity()).onBackPressed();
                        }
                    } else {
                        mCount++;
                    }
                }
            }
        });
    }

    @Override
    protected void initEvent() {
        super.initEvent();

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("onResume: 注册监听", LogUtil.TYPE_RELEASE);
        mPresenter.getAllWidgetData(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.i("onHiddenChanged: hidden " + hidden, LogUtil.TYPE_RELEASE);

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d("onPause: 移除监听", LogUtil.TYPE_RELEASE);
        // 反注册监听
        mPresenter.deinitData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onUpdateWidgetData(List<Widget> list) {
        if (list == null || list.size() == 0) {
            LogUtil.i("not update list", LogUtil.TYPE_RELEASE);
            return;
        }
        mWidgetBeans = list;
        LogUtil.i("list.size " + list.size(), LogUtil.TYPE_RELEASE);
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastUpdateTime < UPDATE_DELAY) {
            LogUtil.i("updateWidget: not update", LogUtil.TYPE_RELEASE);
            return;
        }
        mLastUpdateTime = currentTime;
        mRvDisplayAdapter.setWidgetBeans(list);
    }

    @Override
    public void setSignalImageResource(int resourceId) {
    }

    @Override
    public void setBatteryProgress(int battery, int resourceId) {
        if (mBattery != battery) {
            Log.i(TAG, "setBatteryProgress: mBattery " + mBattery);
            mBattery = battery;
            if (mRvDisplayAdapter != null) {
                mRvDisplayAdapter.setBattery(battery);
            }
        }
    }

    @Override
    public void onClearComplete(long duration, String availMem, String totalMem) {
        LogUtil.i("onClearComplete: duration " + duration + " availMem "
                + availMem + " totalMem " + totalMem, LogUtil.TYPE_RELEASE);
        mHandler.postDelayed(() -> {
            runOnUiThread(() -> {
                if (TextUtils.isEmpty(availMem)) {
                    ToastUtils.show(R.string.clear_cache_hint2);
                } else {
                    ToastUtils.show(App.getInstance().getString(R.string.clear_cache_hint1, availMem, totalMem));
                }
            });
        }, duration > 2000 ? 0 : 2000 - duration);
    }

    private RvDisplayAdapter.OnItemClickListener mOnItemClickListener = new RvDisplayAdapter.OnItemClickListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onItemClickListener(View view, Widget bean) {
            if (!BaseUtil.repeatedClicks(view, DURATION_400)) {
                return;
            }
            Log.i(TAG, "initData: setOnItemClickListener " + bean.toString());
            onItemListener(bean, view.isSelected());
            switch (WidgetEnum.fromStr(bean.getId())) {
                case MOBILE_NETWORK:
                    if (PhoneSIMCardUtil.getInstance().isSIMCardInserted()
                            && !ShutcutMenuUtil.getInstance().isAirplaneMode()) {
                        setWidgetData(bean, R.drawable.svg_3_network_wihte, R.drawable.svg_3_network_gay);
                    }
                    break;
                case WIFI:
                    setWidgetData(bean, R.drawable.svg_3_wifi_white, R.drawable.svg_3_wifi_gay);
                    break;
                case BLUETOOTH:
                    setWidgetData(bean, R.drawable.svg_3_bluetooth_white, R.drawable.svg_3_bluetooth_gay);
                    break;
                case MUTE:
                    setWidgetData(bean, R.drawable.svg_3_mute_white_9, R.drawable.svg_3_mute_gay);
                    break;
            }
        }

        @Override
        public void onItemLongClickListener(View view, Widget bean) {
            if (!BaseUtil.repeatedClicks(view, DURATION_400)) {
                return;
            }
            Log.i(TAG, "initData: setOnItemClickListener " + bean.toString());
            switch (fromStr(bean.getId())) {
                case WIFI: // WIFI
                    startWifiApp();
                    break;
                case BLUETOOTH: // 蓝牙
                    startBluetoothApp();
                    break;
            }
        }

        @Override
        public void onEditClickListener() {
            Log.i(TAG, "onEditClickListener: ");
            mPullDownInterface.onViewClickListener(1);
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    private void setWidgetData(Widget bean, int resId1, int resId2) {
        bean.setSelected(!bean.getSelected());
        bean.setResId(bean.getSelected() ? resId1 : resId2);
        mRvDisplayAdapter.notifyDataSetChanged();
    }

    /**
     * 跳转到自定义wifi ShortCutMenuConst.ACTION_SETTING_WIFI
     * 系统wifi Settings.ACTION_WIFI_SETTINGS
     */
    private void startWifiApp() {
        LogUtil.i("startWifiApp:  SYSTEM_WIFI " + SYSTEM_WIFI + " Build.DISPLAY " + Build.DISPLAY, LogUtil.TYPE_RELEASE);
        if (SYSTEM_WIFI) {
            IntentUtil.startActivity(getContext(), Settings.ACTION_WIFI_SETTINGS);
            return;
        }
        IntentUtil.startActivity(getContext(), ShortCutMenuConst.ACTION_SETTING_WIFI);
    }

    private void startBluetoothApp() {
        if (LauncherAppManager.isForbiddenInClass(getActivity(), "com.baehug.settings")) {
            ToastUtils.show(R.string.app_forbidden_in_class);
        } else if (LauncherAppManager.isAppForbidden(getActivity(), "com.baehug.settings")) {
            ToastUtils.show(com.android.launcher3.common.R.string.app_forbidden);
        } else {
            IntentUtil.startActivity(getContext(), Settings.ACTION_BLUETOOTH_SETTINGS);
        }
    }

    private void startSettingApp() {
        if (LauncherAppManager.isForbiddenInClass(getActivity(), PKG_SETTING)) {
            ToastUtils.show(getString(R.string.app_forbidden_in_class));
            return;
        }
        if (LauncherAppManager.isAppForbidden(getActivity(), PKG_SETTING)) {
            ToastUtils.show(com.android.launcher3.common.R.string.app_forbidden);
            return;
        }
        IntentUtil.startActivity(getContext(), ShortCutMenuConst.ACTION_SETTING);
    }

    private void onItemListener(Widget bean, boolean isSelect) {
        switch (fromStr(bean.getId())) {
            case WIFI: // WIFI
                LogUtil.i("onItemListener: " + isSelect + " " + bean.getSelected(), LogUtil.TYPE_RELEASE);
                if (mPullDownInterface != null) {
                    mPullDownInterface.onStatusBarWifiListener(!bean.getSelected());
                }
                if (!bean.getSelected()) {
                    ShutcutMenuUtil.getInstance().enableWifi();
                    NettyManager.forceResetAndConnect();
                } else {
                    ShutcutMenuUtil.getInstance().disableWifi();
                }
                break;
            case MOBILE_NETWORK: // 蜂窝网络
                if (!PhoneSIMCardUtil.getInstance().isSIMCardInserted() || ShutcutMenuUtil.getInstance().isAirplaneMode()) {
                    LogUtil.i("数据不可用", LogUtil.TYPE_RELEASE);
                    ToastUtils.show(R.string.please_insert_sim_card);
                    return;
                }
                if (!bean.getSelected()) {
                    ShutcutMenuUtil.getInstance().enableDataConnection();
                    NettyManager.forceResetAndConnect();
                } else {
                    ShutcutMenuUtil.getInstance().disableDataConnection();
                }
                mPresenter.updateStatusMobileData();
                break;
            case BLUETOOTH: // 蓝牙
                Log.i(TAG, "onItemListener: BLUETOOTH");
                if (mPullDownInterface != null) {
                    mPullDownInterface.onStatusBarBleListener(!bean.getSelected() ? View.VISIBLE : View.GONE);
                }
                if (!bean.getSelected()) {
                    ShutcutMenuUtil.getInstance().enableBluetooth();
                } else {
                    ShutcutMenuUtil.getInstance().disableBluetooth();
                }
                break;
            case SCREENSHOT: // 截图
                Log.i(TAG, "onItemListener: SCREENSHOT 截图");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        KeyCodeUtil.sendKeyCode(KeyEvent.KEYCODE_SYSRQ);
                    }
                }, 500);
                if (getActivity() instanceof LauncherActivity) {
                    ((LauncherActivity) getActivity()).onBackPressed();
                }
                break;
            case CLEAR: // 清理
                mPresenter.clearCache();
                break;
            case TASK: // 最近任务
                if (LauncherAppManager.isForbiddenInClass(getActivity(), "com.android.systemui")) {
                    ToastUtils.show(R.string.app_forbidden_in_class);
                    return;
                }
                try {
                    String pkg = "com.android.systemui";
                    String cls = "com.android.systemui.recents.RecentsActivity";
                    jumpToTargetActivity(pkg, cls);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ALIPAY: // 支付宝
                lunchApp(mPresenter.getAppBean());
                break;
            case SETTINGS: // 设置
                startSettingApp();
                break;
            case BRIGHTNESS: // 亮度
                IntentUtil.startActivity(getContext(), "com.baehug.settings.adjustment");
                break;
            case MUTE: // 响铃、静音
                if (mPullDownInterface != null) {
                    mPullDownInterface.onStatusBarMuteListener(!bean.getSelected() ? View.VISIBLE : View.GONE);
                }
                BaseUtil.setRingerMode(getContext(), bean.getSelected());
                break;
        }
    }

    private PullDownInterface mPullDownInterface;

    public void setPullDownInterface(PullDownInterface pullDownInterface) {
        this.mPullDownInterface = pullDownInterface;
    }

    private GridLayoutManager getGridLayoutManager() {
        GridLayoutManager manager = new GridLayoutManager(getContext(),
                2, GridLayoutManager.VERTICAL, false);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        return manager;
    }
}
