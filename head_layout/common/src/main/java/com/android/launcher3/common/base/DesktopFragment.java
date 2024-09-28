package com.android.launcher3.common.base;

import android.view.View;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.AppBean;

/**
 * @Author: shensl
 * @Description：桌面风格类
 * @CreateDate：2023/12/16 10:12
 * @UpdateUser: shensl
 */
public abstract class DesktopFragment extends WallpaperFragment {

    private RecyclerView recyclerView;

    private BaseRecyclerAdapter<AppBean> adapter;

    @Override
    protected int getResourceId() {
        return R.layout.fragment_desktop;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    protected void initData() {
        super.initData();
        // 设置布局管理器
        RecyclerView.LayoutManager layoutManager = createLayoutManager();
        recyclerView.setLayoutManager(layoutManager);

        // 设置适配器
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 设置点击事件
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                lunchApp(adapter.getItem(position));
            }
        });

        // 设置滑动事件
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void updateView() {
        adapter.setList(lists);
        adapter.notifyDataSetChanged();
    }

    protected abstract RecyclerView.LayoutManager createLayoutManager();

    protected abstract BaseRecyclerAdapter<AppBean> createAdapter();

}
