package com.android.launcher3.moudle.selector.view;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.launcher3.App;
import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpActivity;
import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.bean.SelectorBean;
import com.android.launcher3.common.constant.SettingsConstant;
import com.android.launcher3.common.utils.BaseActivityManager;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.PhoneSIMCardUtil;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.moudle.selector.adapter.CommomSelectorAdapter;
import com.android.launcher3.moudle.selector.presenter.CommomSelectorPresenter;

import java.util.List;

public abstract class CommomSelectorActivity<P extends CommomSelectorPresenter> extends BaseMvpActivity<CommomSelectorView, CommomSelectorPresenter> implements CommomSelectorView {

    private RecyclerView recyclerView;

    private CommomSelectorAdapter adapter;


    @Override
    protected int getResourceId() {
        return R.layout.activity_dial_selector;
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView = findViewById(R.id.recycler_View);
    }

    @Override
    protected void initData() {
        super.initData();
        // 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        // 设置适配器
        adapter = new CommomSelectorAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);

        // 初始化数据
        mPresenter.initData();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 设置点击事件
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                SelectorBean bean = adapter.getItem(position);
                int selectedId = bean.getId();

                if (selectedId == -1){
                    //未插SIM卡判断
                    if (!PhoneSIMCardUtil.getInstance().isSIMCardInserted()) {
                        ToastUtils.show(R.string.please_insert_sim_card);
                        return;
                    }
                    //上课禁用判断
                    if (LauncherAppManager.isForbiddenInClass(getApplicationContext(),SettingsConstant.THEME_ACTION_MAIN)){
                        ToastUtils.show(R.string.app_forbidden_in_class);
                        return;
                    }
                    //应用监督判断
                    if (LauncherAppManager.isAppForbidden(App.getInstance(), SettingsConstant.THEME_ACTION_MAIN)){
                        ToastUtils.show(R.string.app_forbidden);
                        return;
                    }
                    startActivityAction(SettingsConstant.THEME_ACTION);
                    finish();
                }else {
                    adapter.setSelectedId(selectedId);
                    // 设置id
                    mPresenter.setDefaultId(selectedId);
                    // 保存壁紙
                    onItemClickToSetting(bean, position);
                }
            }
        });

        // 左右滑动事件
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // 判断是否滑动到第一个了
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();

                    // 判断是否滑动到了第一个Item
                    if (firstVisibleItemPosition == 0 && firstCompletelyVisibleItemPosition == 0) {
                        // 滑动到了第一个Item
                        mPresenter.setSwipeToExitEnabled(true);
                    } else {
                        mPresenter.setSwipeToExitEnabled(false);
                    }
                }
            }
        });
    }

    protected void onItemClickToSetting(SelectorBean bean, int position) {
        // 子类可重新该类，实现自己的业务功能
    }

    @Override
    public void backward() {
        // 返回事件，子类可重新该方法，实现自己的功能
        int position = 0;
        boolean reload = false;
        jumpToTargetActivity(position, reload);
    }

    @Override
    public void forward() {
        super.forward();
        int position = 1;
        boolean reload = false;
        jumpToTargetActivity(position, reload);
    }

    @Override
    public void spacesWard() {
        super.spacesWard();
        int position = 1;
        boolean reload = true;
        jumpToTargetActivity(position, reload);
    }

    private void jumpToTargetActivity(int position, boolean reload) {
        // 清除所有页面
        if (position == 0 || reload){
            BaseActivityManager.getInstance().finishActivityList();
        }
        // 返回事件，子类可重新该方法，实现自己的功能
        Intent intent = new Intent();
        intent.setAction("com.baehug.launcher.main");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("NAV", position);
        LogUtil.d(TAG + "NAV = " + position, LogUtil.TYPE_RELEASE);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected boolean getSwipeToExitEnabled() {
        return mPresenter.isSwipeToExitEnabled();
    }

    @Override
    public void scrollToPosition(int position) {
        runOnUiThread(() -> recyclerView.scrollToPosition(position));
    }

    @Override
    public void setData(List<SelectorBean> selectorBeans, int defaultId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(selectorBeans);
                adapter.setSelectedId(defaultId);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItemPosition == 0) {
               finish();
            } else {
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }
}
