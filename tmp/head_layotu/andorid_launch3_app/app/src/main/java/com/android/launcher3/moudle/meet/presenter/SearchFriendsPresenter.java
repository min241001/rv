package com.android.launcher3.moudle.meet.presenter;

import android.content.Context;

import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.resp.SearchFriendResp;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.meet.view.SearchFriendsView;

public class SearchFriendsPresenter extends BasePresenter<SearchFriendsView> {

    public void searchFri(Context context,String tel, String waAcctId){
        ApiHelper.getUser(new BaseListener<SearchFriendResp>() {
            @Override
            public void onError(int code, String msg) {
                LogUtil.d("搜索失败 === " + msg, LogUtil.TYPE_RELEASE);
                if (isViewAttached()) {
                    mView.searchFail(code, msg);
                }
            }

            @Override
            public void onSuccess(SearchFriendResp searchFriendResp) {
                LogUtil.d("请求成功 === " + searchFriendResp.toString(), LogUtil.TYPE_RELEASE);
                if (isViewAttached()) {
                    mView.searchSuccess(searchFriendResp.getList(), searchFriendResp.getTotal());
                }
            }
        },tel,waAcctId,"","");
    }

}
