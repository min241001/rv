package com.android.launcher3.moudle.meet.view;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpActivity;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.dialog.Loading;
import com.android.launcher3.common.network.resp.FriendResp;
import com.android.launcher3.common.utils.AppLauncherUtils;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.GsonUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.moudle.meet.adapter.SearchedFriendsAdapter2;
import com.android.launcher3.widget.RecyclerViewExtC;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchedFriendsActivity extends BaseMvpActivity<SearchedFriendsView, SearchedFriendsPresenter> implements SearchedFriendsView {

    private Loading mLoading = Loading.Companion.init(this);
    private LinearLayout reset;
    private TextView title;
    private SmartRefreshLayout refreshLayout;
    private RecyclerViewExtC friendsRecyclerView;
    private SearchedFriendsAdapter2 adapter;
    private List<FriendResp.FriendBean> friendsList = new ArrayList<>();
    private String type;
    private String response;
    private String tel = "";
    private int pageNum = 1;
    private int totalNum = 0;
    private int pageSize = 10;

    @Override
    protected int getResourceId() {
        return R.layout.searched_friends;
    }

    @Override
    protected SearchedFriendsPresenter createPresenter() {
        return new SearchedFriendsPresenter();
    }

    @Override
    protected void initView() {
        title = findViewById(R.id.title_txt);
        reset = findViewById(R.id.reset_search);
        friendsRecyclerView = findViewById(R.id.friends_recycler);
        refreshLayout = findViewById(R.id.searched_refresh);
        // 设置自定义的Header和Footer
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(true);
        title.setText(getString(R.string.search_friends));
        updateFailView();
        // 0 - 碰碰交友  1 - 搜索好友
        type = getIntent().getExtras().getString("type");
        response = getIntent().getExtras().getString("response");
        tel = getIntent().getExtras().getString("tel");
        totalNum = getIntent().getExtras().getInt("totalNum");
        LogUtil.d("类型 ==== " + type, LogUtil.TYPE_RELEASE);
        if ("0".equals(type)) {
            mLoading.startPregress(com.android.launcher3.common.R.layout.black_loading, 30000L);
            mLoading.setCancelable(false);
            checkWatchId();
        } else {
            String friendList = getIntent().getExtras().getString("friendList");
            if (friendList != null && !friendList.isEmpty()) {
                friendsList.clear();
                Type tokenType = new TypeToken<List<FriendResp.FriendBean>>() {
                }.getType();
                friendsList.addAll(GsonUtil.INSTANCE.fromJson(friendList, tokenType));
                if (friendList != null && !friendsList.isEmpty()) {
                    //是否满足下拉加载更多的条件
                    if (totalNum <= pageSize) {
                        refreshLayout.setEnableLoadMore(false);
                    } else {
                        refreshLayout.setEnableLoadMore(true);
                    }
                    updateSuccessView(friendsList);
                }
            } else {
                updateFailView();
            }
        }
    }

    private void checkWatchId() {
        if (!AppUtils.isNetworkAvailable(getActivity()) && !AppUtils.isWifiConnected(getActivity())) {
            mLoading.stopPregress();
            ToastUtils.show(com.android.launcher3.common.R.string.no_network);
            updateFailView();
            return;
        }
        if (AppLocalData.getInstance().getWatchId().isEmpty()) {
            mLoading.stopPregress();
            ToastUtils.show(R.string.no_waAcctId);
            updateFailView();
        } else {
            mPresenter.findFriends(Long.valueOf(AppLocalData.getInstance().getWatchId()), response);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLoading.stopPregress();
    }

    @Override
    protected void initEvent() {
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFailView();
                mLoading.startPregress(com.android.launcher3.common.R.layout.black_loading, 30000L);
                // 区分 0 - 碰碰交友  1 - 搜索好友
                if ("0".equals(type)) {
                    checkWatchId();
                } else if ("1".equals(type)) {
                    pageNum = 1;
                    mPresenter.searchFriend(tel, AppLocalData.getInstance().getWatchId(), pageNum, pageSize);
                }
            }
        });

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNum += 1;
                // 0 - 碰碰交友  1 - 搜索好友
                if ("0".equals(type)) {
                    checkWatchId();
                } else {
                    mPresenter.searchFriend(tel, AppLocalData.getInstance().getWatchId(), pageNum, pageSize);
                }
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                friendsList.clear();
                pageNum = 1;
                // 0 - 碰碰交友  1 - 搜索好友
                if ("0".equals(type)) {
                    checkWatchId();
                } else {
                    mPresenter.searchFriend(tel, AppLocalData.getInstance().getWatchId(), pageNum, pageSize);
                }
                refreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void searchSuccess(List<FriendResp.FriendBean> list) {
        mLoading.stopPregress();
        if (list == null || list.isEmpty()) {
            updateFailView();
            ToastUtils.show(R.string.no_find_friends);
            return;
        }
        friendsList.addAll(list);
        //是否满足下拉加载更多的条件
        if (list.size() <= pageSize) {
            refreshLayout.setEnableLoadMore(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
        }
        updateSuccessView(friendsList);
    }

    private AlertDialog.Builder builder = null;
    private View customView;
    private AlertDialog dialog = null;

    private void updateSuccessView(List<FriendResp.FriendBean> list) {
        reset.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        friendsRecyclerView.setVisibility(View.VISIBLE);
        title.setText(getString(R.string.searched_friends));
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new SearchedFriendsAdapter(R.layout.searched_friends_item, list);
        adapter = new SearchedFriendsAdapter2(list);
        adapter.setOnItemClickListener(new SearchedFriendsAdapter2.OnItemClickListener() {
            @Override
            public void onItemRootClickListener(int position,FriendResp.FriendBean bean) {
                //是否是好友，1是 2否
                if (bean != null && bean.getIfFriend() == 1) {
                    AppLauncherUtils.launchApp(getActivity(), "com.baehug.watch.wechat");
                }
            }

            @Override
            public void onItemAddFriendClickListener(int position,FriendResp.FriendBean bean) {
                if (bean == null){
                    return;
                }
                if (builder == null) {
                    builder = new AlertDialog.Builder(SearchedFriendsActivity.this, com.android.launcher3.common.R.style.TransparentDialogStyle);
                    LayoutInflater inflater = LayoutInflater.from(SearchedFriendsActivity.this);
                    customView = inflater.inflate(com.android.launcher3.common.R.layout.add_friend_dialog, null);
                }
                customView.findViewById(com.android.launcher3.common.R.id.cancel_add_friend).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                customView.findViewById(com.android.launcher3.common.R.id.confirm_add_friend).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tel != null && !tel.isEmpty()) {
                            mPresenter.addFriend(AppLocalData.getInstance().getWatchId(), String.valueOf(bean.getWaAcctId()), tel, true);
                        } else {
                            mPresenter.addFriend(AppLocalData.getInstance().getWatchId(), String.valueOf(bean.getWaAcctId()), tel, false);
                        }
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.setView(customView);
                if (dialog == null) {
                    dialog = builder.create();
                }
                dialog.show();
            }
        });
        friendsRecyclerView.setAdapter(adapter);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        /*adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.friend_ifFriend:

                        break;
                    case R.id.searched_friend_item_layout:

                        break;
                }
            }
        });*/
    }

    private void updateFailView() {
        refreshLayout.setVisibility(View.GONE);
        reset.setVisibility(View.VISIBLE);
        friendsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void searchFail(int code, String msg) {
        LogUtil.d(msg, LogUtil.TYPE_DEBUG);
        mLoading.stopPregress();
        ToastUtils.show(R.string.search_fail);
        updateFailView();
    }

    @Override
    public void addFriendSuccess() {
        ToastUtils.show(R.string.wait_agree);
    }

    @Override
    public void addFriendFail(int code, String msg) {
        ToastUtils.show(msg);
    }

    @Override
    public void searchFriendSuccess(List<FriendResp.FriendBean> list) {
        mLoading.stopPregress();
        if (list == null || list.isEmpty()) {
            updateFailView();
            ToastUtils.show(R.string.search_fail);
            return;
        }
        //是否满足下拉加载更多的条件
        if (list.size() < pageSize) {
            refreshLayout.setEnableLoadMore(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
        }
        friendsList.addAll(list);
        adapter.setFriendBean(friendsList);
//        adapter.setNewData(friendsList);
//        adapter.notifyDataSetChanged();

    }

    @Override
    public void searchFriendFail(int code, String msg) {
        mLoading.stopPregress();
        ToastUtils.show(getString(R.string.search_fail) + msg);
        updateFailView();
    }

}
