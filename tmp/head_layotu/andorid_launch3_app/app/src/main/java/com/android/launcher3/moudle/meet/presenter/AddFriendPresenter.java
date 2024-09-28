package com.android.launcher3.moudle.meet.presenter;

import static com.android.launcher3.R.string.make_friends;
import static com.android.launcher3.R.string.new_friends;
import static com.android.launcher3.R.string.search_friends;

import android.content.Context;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.resp.AddFriendCountResp;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.meet.eneity.FunctionEneity;
import com.android.launcher3.moudle.meet.view.AddFriendView;

import java.util.ArrayList;
import java.util.List;

public class AddFriendPresenter extends BasePresenter<AddFriendView> {

    public List<FunctionEneity> getData(Context context) {
        ArrayList<FunctionEneity> functionEneities = new ArrayList<>();
        FunctionEneity one = new FunctionEneity(R.mipmap.make_friends_icon, context.getString(make_friends), 0);
        FunctionEneity two = new FunctionEneity(R.mipmap.search_friends_icon, context.getString(search_friends), 0);
        FunctionEneity three = new FunctionEneity(R.mipmap.new_friends_icon, context.getString(new_friends), 0);
        functionEneities.add(one);
        functionEneities.add(two);
        functionEneities.add(three);
        return functionEneities;
    }

    /**
     * 好友申请数量接口请求
     */
    public void addFriendCount(String watchId) {
        ApiHelper.addFriendCount(new BaseListener<AddFriendCountResp>() {
            @Override
            public void onError(int code, String msg) {
                LogUtil.d("fail === "+code + msg, LogUtil.TYPE_RELEASE);
            }

            @Override
            public void onSuccess(AddFriendCountResp resp) {
                if (isViewAttached()) {
                    mView.addFriendCountSuccess(resp.getCount());
                }
            }
        }, watchId);
    }

}
