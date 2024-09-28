package com.android.launcher3.common.base;

import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.constant.SettingsConstant;

/**
 * @Author: shensl
 * @Description：桌面单列风格类
 * @CreateDate：2023/12/16 10:12
 * @UpdateUser: shensl
 */
public abstract class PageDesktopFragment extends BaseDesktopFragment {

    protected RecyclerView recyclerView;

    protected BaseRecyclerAdapter<AppBean> adapter;

    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected int getResourceId() {
        return R.layout.fragment_desktop;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        recyclerView = view.findViewById(R.id.recycler_view);
        getActivity().getContentResolver().registerContentObserver(
                Settings.System.getUriFor(SettingsConstant.SLIDE),
                true,
                settingsContentObserver
        );
    }

    private ContentObserver settingsContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            layoutManager = createLayoutManager();
            recyclerView.setLayoutManager(layoutManager);
        }
    };

    @Override
    protected void initData() {
        super.initData();
        // 设置布局管理器
        layoutManager = createLayoutManager();
        recyclerView.setLayoutManager(layoutManager);

        // 设置适配器
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 设置点击事件
        adapter.setOnItemClickListener((parent, view, position) -> lunchApp(adapter.getItem(position)));

    }

    @Override
    public void updateView() {
        adapter.setList(lists);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(settingsContentObserver);
    }

    public void setPageSize(int size) {
        Log.i(TAG, "setPageSize: size " + size);
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    protected abstract RecyclerView.LayoutManager createLayoutManager();

    protected abstract BaseRecyclerAdapter<AppBean> createAdapter();

}
