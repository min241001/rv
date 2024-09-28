package com.android.launcher3.moudle.notification.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.launcher3.R;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.base.BaseMvpFragment;
import com.android.launcher3.common.utils.AppLauncherUtils;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.PhoneSIMCardUtil;
import com.android.launcher3.common.utils.ScreenUtil;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.common.widget.RecyclerViewExt;
import com.android.launcher3.moudle.notification.adapter.NotificationListAdapter;
import com.android.launcher3.moudle.notification.adapter.SpacingItemDecoration;
import com.android.launcher3.moudle.notification.bean.NotificationBean;
import com.android.launcher3.moudle.notification.notity.NotifyHelper;
import com.android.launcher3.moudle.notification.presenter.NotificationPresenter;
import com.android.launcher3.utils.FileLogUtil;
import com.android.launcher3.widget.RecyclerViewExtC;

import java.util.List;

/**
 * @Description：上拉消息通知栏页面
 */
public class NotificationFragment extends BaseMvpFragment<NotificationView, NotificationPresenter> implements NotificationView {

    private RecyclerViewExtC recyclerView;
    private Button btnClean;
    private TextView tvNotificationNone;

    private NotificationListAdapter adapter;

    public static Fragment newInstance(String[] params) {
        Fragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        if (params != null && params.length == 0) {
            args.putStringArray("PARAM", params);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected NotificationPresenter createPresenter() {
        return new NotificationPresenter();
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_notification;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnClean = view.findViewById(R.id.btn_clean);
        tvNotificationNone = view.findViewById(R.id.tv_notification_none);
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.i("initData: ", LogUtil.TYPE_RELEASE);
        // 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        int margin = ScreenUtil.dpToPx(getResources(), 16);
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(margin, margin / 2, margin, margin / 2);
        recyclerView.addItemDecoration(itemDecoration);

        // 初始化数据
        mPresenter.initData();

        // 设置适配器
        adapter = new NotificationListAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 单个消息类型时间
        recyclerView.setOnItemClickListener(new RecyclerViewExt.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewExt.ViewHolder vh, int position) {
                // 启动对应的app
                NotificationBean bean = adapter.getItem(position);
                //未插SIM卡判断
                if (!PhoneSIMCardUtil.getInstance().isSIMCardInserted()) {
                    ToastUtils.show(R.string.please_insert_sim_card);
                    return;
                }
                if (LauncherAppManager.isForbiddenInClass(getActivity(), bean.getPackageName())) {
                    ToastUtils.show("上课禁用时间,要专心学习哦~");
                    return;
                }
                if (LauncherAppManager.isAppForbidden(getActivity(), bean.getPackageName())) {
                    Toast.makeText(CommonApp.getInstance(), "应用已被家长禁用", Toast.LENGTH_SHORT).show();
                    //ToastUtils.show(getActivity(), "应用已被家长禁用");
                    return;
                }
                AppLauncherUtils.launchApp(getActivity(), bean.getPackageName());
                // 添加日志记录
                String msg = bean.toString();
                if (!TextUtils.isEmpty(msg)) {
                    LogUtil.d(TAG + "点击了：" + msg, LogUtil.TYPE_RELEASE);
                    FileLogUtil.saveInfoToFile(TAG + "点击了：" + msg);
                }
                // 移除相关的通知
                NotifyHelper.getInstance().onRemoved(bean.getPackageName());
            }

            @Override
            public void onItemLongClick(RecyclerViewExt.ViewHolder vh, int position) {

            }
        });

        // 清除事件
        btnClean.setOnClickListener(view -> mPresenter.cleanData());
    }

    @Override
    public void setNotificationNoneVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setVisibility(View.GONE);
                btnClean.setVisibility(View.GONE);
                tvNotificationNone.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void setNotificationNoneGone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null){
                    recyclerView.setVisibility(View.GONE);
                    btnClean.setVisibility(View.GONE);
                    tvNotificationNone.setVisibility(View.VISIBLE);
                    return;
                }
                recyclerView.setVisibility(View.VISIBLE);
                btnClean.setVisibility(View.VISIBLE);
                tvNotificationNone.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void cleanData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void updateData(List<NotificationBean> notificationBeanList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(notificationBeanList);
                if (notificationBeanList.isEmpty()){
                    tvNotificationNone.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    btnClean.setVisibility(View.VISIBLE);
                    tvNotificationNone.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.updateData();
        }
    }
}
