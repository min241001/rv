package com.android.launcher3.moudle.meet.view;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpActivity;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.meet.adapter.AddFriendFunctionAdapter;
import com.android.launcher3.moudle.meet.eneity.FunctionEneity;
import com.android.launcher3.moudle.meet.presenter.AddFriendPresenter;
import com.android.launcher3.widget.RecyclerViewExtC;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

public class AddFriendActivity extends BaseMvpActivity<AddFriendView, AddFriendPresenter> implements AddFriendView {

    private RecyclerViewExtC functionRecyclerView;
    private TextView titleTxtView;
    private AddFriendFunctionAdapter functionAdapter;
    private List<FunctionEneity> data;

    @Override
    protected int getResourceId() {
        return R.layout.activity_add_friend;
    }

    @Override
    protected AddFriendPresenter createPresenter() {
        return new AddFriendPresenter();
    }

    @Override
    protected void initView() {
        functionRecyclerView = findViewById(R.id.function_recyclerView);
        titleTxtView = findViewById(R.id.title_txt);
        titleTxtView.setText(getString(com.android.launcher3.common.R.string.add_friend));
        functionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        data = mPresenter.getData(this);
        initAdapter();
    }

    private void initAdapter(){
        functionAdapter = new AddFriendFunctionAdapter(R.layout.item_add_friend_function,data);
        functionRecyclerView.setAdapter(functionAdapter);
        functionAdapter.notifyDataSetChanged();
        functionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (getString(R.string.make_friends).equals(data.get(position).getFuncName())){
                    startActivity(new Intent(AddFriendActivity.this,MakeFriendsActivity.class));
                }
                if (getString(R.string.search_friends).equals(data.get(position).getFuncName())){
                    startActivity(new Intent(AddFriendActivity.this,SearchFriendsActivity.class));
                }
                if (getString(R.string.new_friends).equals(data.get(position).getFuncName())){
                    startActivity(new Intent(AddFriendActivity.this,FriendsRequestActivity.class));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.addFriendCount(AppLocalData.getInstance().getWatchId());
    }

    @Override
    public void addFriendCountSuccess(int count) {
        LogUtil.d("数量 ==== "+count, LogUtil.TYPE_RELEASE);
        if (count > 0){
            data.get(2).setFlag(1);
        } else {
            data.get(2).setFlag(0);
        }
        initAdapter();
    }
}
