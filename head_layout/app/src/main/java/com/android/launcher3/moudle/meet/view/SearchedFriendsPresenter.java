package com.android.launcher3.moudle.meet.view;

import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.resp.FriendResp;
import com.android.launcher3.common.network.resp.SearchFriendResp;

public class SearchedFriendsPresenter extends BasePresenter<SearchedFriendsView> {

    //碰碰交友接口请求
    public void findFriends(long watchId, String response) {
        ApiHelper.nearbyUsers(new BaseListener<FriendResp>() {
            @Override
            public void onError(int code, String msg) {
                if (isViewAttached()) {
                    mView.searchFail(code, msg);
                }
            }

            @Override
            public void onSuccess(FriendResp friendResp) {
                if (isViewAttached()) {
                    mView.searchSuccess(friendResp.getList());
                }
            }
        }, watchId, response);
    }

    //请求添加好友接口请求
    public void addFriend(String watchId, String toWatchId, String tel, boolean isFromSearch) {
        ApiHelper.addFriend(new BaseListener<Void>() {
            @Override
            public void onError(int code, String msg) {
                if (isViewAttached()) {
                    mView.addFriendFail(code, msg);
                }
            }

            @Override
            public void onSuccess(Void result) {
                if (isViewAttached()) {
                    mView.addFriendSuccess();
                }
            }
        }, watchId, toWatchId, tel, isFromSearch);
    }

    //搜索好友接口请求
    public void searchFriend(String tel, String waAcctId, int pageNum, int pageSize){
        ApiHelper.getUser(new BaseListener<SearchFriendResp>() {
            @Override
            public void onError(int code, String msg) {
                if (isViewAttached()) {
                    mView.searchFriendFail(code, msg);
                }
            }

            @Override
            public void onSuccess(SearchFriendResp searchFriendResp) {
                if (isViewAttached()) {
                    mView.searchFriendSuccess(searchFriendResp.getList());
                }
            }
        },tel,waAcctId,String.valueOf(pageNum),String.valueOf(pageSize));
    }

}
