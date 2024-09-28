package com.android.launcher3.moudle.meet.view;

import com.android.launcher3.common.network.resp.DealFriendApplyResp;
import com.android.launcher3.common.network.resp.FriendApplyResp;

public interface FriendsRequestView {

    //好友申请列表成功
    void friendApplySuccess(FriendApplyResp friendApplyResp);

    //好友申请列表失败
    void friendApplyFail(int code,String msg);

    //处理添加好友申请成功
    void dealFriendApplySuccess(DealFriendApplyResp friendApplyResp);

    //处理添加好友申请失败
    void dealFriendApplyFail(int code,String msg);

}
