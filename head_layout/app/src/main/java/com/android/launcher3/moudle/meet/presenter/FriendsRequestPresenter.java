package com.android.launcher3.moudle.meet.presenter;

import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.resp.DealFriendApplyResp;
import com.android.launcher3.common.network.resp.FriendApplyResp;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.meet.view.FriendsRequestView;

public class FriendsRequestPresenter extends BasePresenter<FriendsRequestView> {

    /**
     * 好友申请列表接口
     */
    public void friendApply(String waAcctId, int pageNum, int pageSize) {
        ApiHelper.friendApply(new BaseListener<FriendApplyResp>() {
            @Override
            public void onError(int code, String msg) {
                LogUtil.d("好友申请列表接口fail == " + msg, LogUtil.TYPE_RELEASE);
                if (isViewAttached()) {
                    mView.friendApplyFail(code, msg);
                }
            }

            @Override
            public void onSuccess(FriendApplyResp friendApplyResp) {
                LogUtil.d("接口请求成功 数量 === " + friendApplyResp.getTotal(), LogUtil.TYPE_RELEASE);
                if (isViewAttached()) {
                    mView.friendApplySuccess(friendApplyResp);
                }
            }
        }, waAcctId, pageNum, pageSize);
    }

    /**
     * 处理添加好友申请接口
     */
    public void dealFriendApply(int fromWaAcctId, int applyId, int ifAgree) {
        ApiHelper.dealFriendApply(new BaseListener<DealFriendApplyResp>() {
            @Override
            public void onError(int code, String msg) {
                LogUtil.d("好友申请列表接口fail == " + msg, LogUtil.TYPE_RELEASE);
                if (isViewAttached()) {
                    mView.dealFriendApplyFail(code, msg);
                }
            }

            @Override
            public void onSuccess(DealFriendApplyResp dealFriendApplyResp) {
                LogUtil.d("接口请求成功 === " + dealFriendApplyResp.toString(), LogUtil.TYPE_RELEASE);
                if (isViewAttached()) {
                    mView.dealFriendApplySuccess(dealFriendApplyResp);
                }
            }
        }, fromWaAcctId, applyId, ifAgree);
    }


}
