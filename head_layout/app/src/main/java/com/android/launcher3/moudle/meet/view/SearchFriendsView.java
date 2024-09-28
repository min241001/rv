package com.android.launcher3.moudle.meet.view;

import com.android.launcher3.common.network.resp.FriendResp;

import java.util.List;

public interface SearchFriendsView {

    void searchSuccess(List<FriendResp.FriendBean> list,int totalNum);

    void searchFail(int code, String msg);

}
