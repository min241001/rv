package com.android.launcher3.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected static final String TAG = BaseFragment.class.getSimpleName();

    private DateBroadcastReceiver broadcastReceiver;
    private IntentFilter filter;
    protected Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getResourceId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
        initEvent();
        broadcastReceiver = new DateBroadcastReceiver();
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(broadcastReceiver, filter);
    }

    protected abstract int getResourceId();

    protected <T extends View> T findViewById(@IdRes int id) {
        if (id == View.NO_ID) {
            return null;
        }
        return getView().findViewById(id);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    protected void initView() {
        // 由子类覆盖重写
    }

    protected void initData() {
        // 由子类覆盖重写
    }

    protected void initEvent() {
        // 由子类覆盖重写
    }

    protected void handleDateChange() {

    }

    protected void handleScreenOn() {

    }

    protected void handleScreenOff() {

    }

    class DateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case Intent.ACTION_SCREEN_ON :
                    handleScreenOn();
                    break;

                case Intent.ACTION_SCREEN_OFF :
                    handleScreenOff();
                    break;

                default:
                    handleDateChange();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(broadcastReceiver);
    }
}
