package com.android.launcher3.moudle.meet.view;

import com.android.launcher3.common.network.resp.FriendResp;

import java.util.List;

public interface SearchedFriendsView {

    //碰碰交友接口请求
    void searchSuccess(List<FriendResp.FriendBean> list);
    void searchFail(int code, String msg);

    //请求添加好友
    void addFriendSuccess();
    void addFriendFail(int code, String msg);

    //搜索好友
    void searchFriendSuccess(List<FriendResp.FriendBean> list);
    void searchFriendFail(int code, String msg);

}
