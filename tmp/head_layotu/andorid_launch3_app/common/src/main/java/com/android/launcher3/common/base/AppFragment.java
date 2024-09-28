package com.android.launcher3.common.base;

import static com.android.launcher3.common.constant.SettingsConstant.APP_RECEIVER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.ThreadPoolUtils;

import java.lang.ref.WeakReference;

public abstract class AppFragment extends BaseFragment{

    private AppBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    private static final int UI_SYNC_DATA = 1;
    protected Handler uiHandler = null;

    private static class UIHandler extends Handler {

        private WeakReference<Fragment> weakReference;

        public UIHandler(Fragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UI_SYNC_DATA:
                    Fragment fragment = weakReference.get();
                    if (fragment != null && fragment instanceof AppFragment) {
                        ((AppFragment) fragment).mAppLayer = AppUtils.getAppLayer(fragment.getContext());
                        ((AppFragment) fragment).updateView();
                    }
                    break;
            }
        }
    }

    protected void syncData() {

    }

    protected void updateView() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        broadcastReceiver = new AppBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(APP_RECEIVER);
        mContext.registerReceiver(broadcastReceiver, intentFilter);
        uiHandler = new UIHandler(this);
        getData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
        if (uiHandler != null) {
            uiHandler.removeCallbacksAndMessages(null);
            uiHandler = null;
        }
    }

    class AppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
        }
    }

    private void getData(){
        ThreadPoolUtils.getExecutorService().execute(() -> {
            syncData();
            if (uiHandler != null){
                uiHandler.sendEmptyMessage(UI_SYNC_DATA);
            }
        });
    }
}
