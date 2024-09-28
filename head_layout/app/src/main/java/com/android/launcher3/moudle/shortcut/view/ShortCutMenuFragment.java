package com.android.launcher3.moudle.shortcut.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpFragment;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.WifiUtil;
import com.android.launcher3.moudle.launcher.view.LauncherActivity;
import com.android.launcher3.moudle.shortcut.presenter.PullDownInterface;
import com.android.launcher3.moudle.shortcut.presenter.ShortCutMenuPresenter;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.moudle.shortcut.util.SignalStrengthUtils;

/**
 * @Description：设置面板
 */
public class ShortCutMenuFragment extends BaseMvpFragment<IShortCutMenuView, ShortCutMenuPresenter>
        implements IShortCutMenuView, View.OnTouchListener, PullDownInterface {

    private static final String TAG = "ShortCutMenuFragment_123123";

    private ImageView ivSignal;
    private ImageView ivWifi;
    private TextView tvNetworkType;
    private TextView tvManufacturerType;
    private TextView tvSimCard;

    private ImageView mIvBle;

    private Handler handler;


    private boolean mWifiOpen; // wifi控件是否被打开
    private long mLastHiddenChangedTime1; // 记录上次onHiddenChanged回调的时间
    private long mLastHiddenChangedTime2; // 记录上次onHiddenChanged回调的时间
    private int mWifiUpdateCount; // 强制刷新wifi次数
    private boolean mForceRefresh; // 是否强制刷新
    private float startX;
    private float startY;
    private DisplayFragment mDisplayFragment;
    private EditFragment mEditFragment;
    private View mIvMute;
    private View mLlStatus;

    public static Fragment newInstance(String[] params) {
        Fragment fragment = new ShortCutMenuFragment();
        Bundle args = new Bundle();
        if (params != null && params.length == 0) {
            args.putStringArray("PARAM", params);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected ShortCutMenuPresenter createPresenter() {
        return new ShortCutMenuPresenter(getActivity());
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_qspanel;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        ivSignal = view.findViewById(R.id.iv_signal);
        ivWifi = view.findViewById(R.id.iv_wifi);
        tvNetworkType = view.findViewById(R.id.tv_networkType);
        tvManufacturerType = view.findViewById(R.id.tv_manufacturerType);
        tvSimCard = view.findViewById(R.id.tv_simcard);
        mIvBle = view.findViewById(R.id.iv_ble);
        mIvMute = view.findViewById(R.id.iv_mute);
        mLlStatus = view.findViewById(R.id.ll_status);

        handler = new Handler(Looper.getMainLooper());

        mDisplayFragment = DisplayFragment.newInstance(null);
        mEditFragment = EditFragment.newInstance(null);
        mDisplayFragment.setPullDownInterface(this);
        mEditFragment.setPullDownInterface(this);

        showDisplayFragment();
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.i("initData: ", LogUtil.TYPE_RELEASE);
        mPresenter.initBaseWidgetData();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("onResume: 注册监听", LogUtil.TYPE_RELEASE);
        // 注册监听
        mPresenter.initData();
        // 更新数据
        mPresenter.updateData();
        mPresenter.wifiStatus();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.i("onHiddenChanged: hidden " + hidden, LogUtil.TYPE_RELEASE);
        mWifiOpen = false;
        if (!hidden) {
            // 屏蔽短时间内两次回调
            if (System.currentTimeMillis() - mLastHiddenChangedTime1 < 100) {
                return;
            }
            mLastHiddenChangedTime1 = System.currentTimeMillis();
            PhoneSIMCardUtil.getInstance().listenSignalStrengths();
            mPresenter.updateData();
            mPresenter.wifiStatus();
        } else {
            // 屏蔽短时间内两次回调
            if (System.currentTimeMillis() - mLastHiddenChangedTime2 < 100) {
                return;
            }
            mLastHiddenChangedTime2 = System.currentTimeMillis();
            PhoneSIMCardUtil.getInstance().stopListeningSignalStrengths();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d("反注册监听", LogUtil.TYPE_RELEASE);
        // 反注册监听
        mPresenter.deinitData();
    }

    @Override
    public void setOperatorName(String operatorName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvManufacturerType.setText(operatorName);
            }
        });
    }

    @Override
    public void setNetworkType(String networkType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                tvNetworkType.setText(networkType);
            }
        });
    }

    @Override
    public void setSignalImageResource(int resourceId) {
        runOnUiThread(() -> {
            ivSignal.setImageResource(resourceId);
        });
    }

    @Override
    public void setWifiImageResource(int resourceId) {
        runOnUiThread(() -> {
            ivWifi.setVisibility(View.VISIBLE);
            ivWifi.setImageResource(resourceId);
            if (resourceId == R.drawable.svg_3_wifi_0 && !mForceRefresh) {
                forceRefreshWifiState();
            }
        });
    }

    /**
     * 强制刷新wifi状态机制，wifi状态切换回调可能会很长时间
     */
    private void forceRefreshWifiState() {
        mForceRefresh = true;
        if (handler == null) {
            return;
        }
        handler.postDelayed(() -> {
            int rssi = WifiUtil.getCurrentNetworkRssi(getContext());
            int resourceId = SignalStrengthUtils.getWifiResourceId(rssi);
            LogUtil.i("forceRefreshWifiState: rssi " + rssi + ", mWifiUpdateCount "
                    + mWifiUpdateCount, LogUtil.TYPE_RELEASE);
            if (mWifiUpdateCount < 5 && resourceId == R.drawable.svg_3_wifi_0) {
                forceRefreshWifiState();
                mWifiUpdateCount++;
            } else {
                setWifiImageResource(resourceId);
                mWifiUpdateCount = 0;
                mForceRefresh = false;
            }
        }, 1000);
    }

    @Override
    public void setSIMCardInserted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvSimCard.setVisibility(View.GONE);
                ivSignal.setVisibility(View.VISIBLE);
//                tvNetworkType.setVisibility(View.VISIBLE);
                tvManufacturerType.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void setNoSIMCardInserted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvSimCard.setVisibility(View.VISIBLE);
                ivSignal.setVisibility(View.GONE);
//                tvNetworkType.setVisibility(View.GONE);
                tvManufacturerType.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setWifiOpen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivWifi.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void setWifiClose() {
        runOnUiThread(() -> {
            LogUtil.i("setWifiClose: mWifiOpen " + mWifiOpen, LogUtil.TYPE_RELEASE);
            // wifi按钮被打开的时候不隐藏状态栏wifi图标
            if (!mWifiOpen) {
                ivWifi.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                float rawY = event.getRawY();
                float rawX = event.getRawX();
                if (Math.abs(rawY - startY) < 5 && Math.abs(rawX - startX) < 5) {
                    LogUtil.i(TAG, "onTouch ACTION_UP: click " + v.getId() + " " + Math.abs(rawY - startY), LogUtil.TYPE_RELEASE);
                    setViewClickListener(v);
                } else if (startY - rawY > 10 && Math.abs(rawY - startY) > Math.abs(rawX - startX)) {
                    LogUtil.i(TAG, "onTouch ACTION_UP: scoll " + Math.abs(rawY - startY), LogUtil.TYPE_RELEASE);
                    if (getActivity() instanceof LauncherActivity) {
                        ((LauncherActivity) getActivity()).onBackPressed();
                    }
                } else {
                    LogUtil.i(TAG, "onTouch ACTION_UP: y " + Math.abs(rawY - startY) + " x " + Math.abs(rawX - startX), LogUtil.TYPE_RELEASE);
                }
                break;
        }
        return true;
    }

    private void setViewClickListener(View v) {
        switch (v.getId()) {
        }
    }

    private void showDisplayFragment() {
        Log.i(TAG, "showDisplayFragment: ");
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.pull_down_display_enter, R.anim.pull_down_display_out);
        transaction.replace(R.id.fragment_container, mDisplayFragment);
        transaction.commit();
    }

    private void showEditFragment() {
        Log.i(TAG, "showEditFragment: ");
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.pull_down_display_enter, R.anim.pull_down_display_out);
        transaction.replace(R.id.fragment_container, mEditFragment);
        transaction.commit();
    }

    @Override
    public void onViewClickListener(int type) {
        Log.i(TAG, "onViewClickListener: type " + type);
        if (type == 1) {
            showEditFragment();
        } else {
            showDisplayFragment();
        }
    }

    @Override
    public void onStatusBarBleListener(int visibility) {
        Log.i(TAG, "onStatusBarBleListener: visibility " + visibility);
        mIvBle.setVisibility(visibility);
    }

    @Override
    public void onStatusBarMuteListener(int visibility) {
        Log.i(TAG, "onStatusBarBleListener: visibility " + visibility);
        mIvMute.setVisibility(visibility);
    }

    @Override
    public void onStatusBarWifiListener(boolean open) {
        Log.i(TAG, "onStatusBarWifiListener: open " + open);
        mWifiOpen = open;
    }
}
