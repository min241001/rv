package com.android.launcher3.moudle.meet.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpActivity;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.network.resp.DealFriendApplyResp;
import com.android.launcher3.common.network.resp.FriendApplyResp;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.moudle.meet.adapter.FriendApplyAdapter;
import com.android.launcher3.moudle.meet.presenter.FriendsRequestPresenter;
import com.android.launcher3.widget.RecyclerViewExtC;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsRequestActivity extends BaseMvpActivity<FriendsRequestView, FriendsRequestPresenter> implements FriendsRequestView {

    private TextView noFriendsView;
    private TextView titleTxt;
    private RecyclerViewExtC friendRecycler;
    private SmartRefreshLayout refreshLayout;
    private int pageNum = 0;
    private int pageSize = 10;
    private FriendApplyAdapter friendApplyAdapter;
    private List<FriendApplyResp.FriendApplyBean> list = new ArrayList<>();
    private boolean isRequestFlag = false;

    @Override
    protected int getResourceId() {
        return R.layout.friends_request;
    }

    @Override
    protected FriendsRequestPresenter createPresenter() {
        return new FriendsRequestPresenter();
    }

    @Override
    protected void initView() {
        noFriendsView = findViewById(R.id.no_friends_request_view);
        titleTxt = findViewById(R.id.title_txt);
        refreshLayout = findViewById(R.id.refresh);
        titleTxt.setText(getString(R.string.friends_request));
        friendRecycler = findViewById(R.id.friend_request_recyclerView);
        friendRecycler.setLayoutManager(new LinearLayoutManager(this));
        // 设置自定义的Header和Footer
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
    }

    @Override
    protected void initEvent() {
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!isRequestFlag) {
                    isRequestFlag = true;
                    pageNum += 1;
                    mPresenter.friendApply(AppLocalData.getInstance().getWatchId(), pageNum, pageSize);
                }
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!isRequestFlag) {
                    isRequestFlag = true;
                    list.clear();
                    pageNum = 1;
                    mPresenter.friendApply(AppLocalData.getInstance().getWatchId(), pageNum, pageSize);
                }
                refreshLayout.finishRefresh();
            }
        });
    }

    @Override
    protected void initData() {
        if (!isRequestFlag) {
            isRequestFlag = true;
            list.clear();
            pageNum = 1;
            mPresenter.friendApply(AppLocalData.getInstance().getWatchId(), pageNum, pageSize);
        }
    }

    @Override
    public void friendApplySuccess(FriendApplyResp friendApplyResp) {
        isRequestFlag = false;
        if (friendApplyResp.getList() == null || friendApplyResp.getTotal() == 0) {
            updateFailView();
            return;
        }
        list.addAll(friendApplyResp.getList());
        initAdapter(list);
    }

    @Override
    public void friendApplyFail(int code, String msg) {
        isRequestFlag = false;
        ToastUtils.show(msg);
        updateFailView();
    }

    @Override
    public void dealFriendApplySuccess(DealFriendApplyResp dealFriendApplyResp) {
        isRequestFlag = false;
        //1成功 2失败
        if (dealFriendApplyResp.getIfSuccess() == 2) {
            ToastUtils.show(dealFriendApplyResp.getMsg());
            return;
        }
        list.remove(delIndex);
        if (list.size() == 0) {
            updateFailView();
            return;
        }
        friendApplyAdapter.notifyItemRemoved(delIndex);
    }

    //删除数据的下标
    private int delIndex = 0;

    @Override
    public void dealFriendApplyFail(int code, String msg) {
        isRequestFlag = false;
        ToastUtils.show(msg);
    }

    private void updateFailView() {
        refreshLayout.setVisibility(View.GONE);
        friendRecycler.setVisibility(View.GONE);
        noFriendsView.setVisibility(View.VISIBLE);
    }

    private void initAdapter(List<FriendApplyResp.FriendApplyBean> data) {
        refreshLayout.setVisibility(View.VISIBLE);
        friendRecycler.setVisibility(View.VISIBLE);
        noFriendsView.setVisibility(View.GONE);
        //是否满足下拉加载更多的条件
        if (data.size() < 10) {
            refreshLayout.setEnableLoadMore(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
        }
        friendApplyAdapter = new FriendApplyAdapter(R.layout.friend_apply_item, data);
        friendRecycler.setAdapter(friendApplyAdapter);
        friendApplyAdapter.notifyDataSetChanged();
        friendApplyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    //1-同意 2-拒绝
                    case R.id.friend_apply_accept:
                        if (!isRequestFlag) {
                            isRequestFlag = true;
                            delIndex = position;
                            mPresenter.dealFriendApply(data.get(position).getFromWaAcctId(), data.get(position).getApplyId(), 1);
                        }
                        break;
                    case R.id.friend_apply_refuse:
                        if (!isRequestFlag) {
                            isRequestFlag = true;
                            delIndex = position;
                            mPresenter.dealFriendApply(data.get(position).getFromWaAcctId(), data.get(position).getApplyId(), 2);
                        }
                        break;
                }
            }
        });
    }

}
