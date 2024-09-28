package com.android.launcher3.moudle.meet.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.launcher3.R;
import com.android.launcher3.common.network.api.Api;
import com.android.launcher3.common.network.resp.FriendApplyResp;
import com.android.launcher3.common.utils.ImageUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;

public class FriendApplyAdapter extends BaseQuickAdapter<FriendApplyResp.FriendApplyBean, BaseViewHolder> {

    public FriendApplyAdapter(int layoutResId, @Nullable List<FriendApplyResp.FriendApplyBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FriendApplyResp.FriendApplyBean item) {
        ImageUtil.displayImage(mContext, Api.getCdnUrl(item.getAvatar()),helper.getView(R.id.friend_apply_icon),R.drawable.icon_avatar);
        helper.setText(R.id.friend_apply_name,item.getName());
        helper.addOnClickListener(R.id.friend_apply_accept);
        helper.addOnClickListener(R.id.friend_apply_refuse);
    }
}
