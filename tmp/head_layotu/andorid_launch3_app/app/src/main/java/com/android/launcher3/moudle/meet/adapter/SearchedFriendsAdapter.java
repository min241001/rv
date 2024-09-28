package com.android.launcher3.moudle.meet.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.launcher3.R;
import com.android.launcher3.common.network.api.Api;
import com.android.launcher3.common.network.resp.FriendResp;
import com.android.launcher3.common.utils.ImageUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class SearchedFriendsAdapter extends BaseQuickAdapter<FriendResp.FriendBean, BaseViewHolder> {

    public SearchedFriendsAdapter(int layoutResId, @Nullable List<FriendResp.FriendBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FriendResp.FriendBean item) {
        helper.setText(R.id.friend_name,item.getName());
//        是否是好友，1是 2否
        if (item.getIfFriend() == 1){
            helper.setVisible(R.id.friend_ifFriend,false);
        } else {
            helper.setVisible(R.id.friend_ifFriend,true);
        }
        LogUtil.d(item.getAvatar(), LogUtil.TYPE_RELEASE);
        ImageUtil.displayImage(mContext, Api.getCdnUrl(item.getAvatar()),helper.getView(R.id.friend_avatar),R.drawable.icon_avatar);
        helper.addOnClickListener(R.id.friend_ifFriend);
        helper.addOnClickListener(R.id.searched_friend_item_layout);
    }
}
