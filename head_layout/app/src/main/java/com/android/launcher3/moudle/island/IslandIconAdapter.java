package com.android.launcher3.moudle.island;

import android.content.Context;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.BaseRecyclerHolder;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class IslandIconAdapter extends BaseRecyclerAdapter<IslandMsgBean> {
    private Context context;

    public IslandIconAdapter(Context context, List<IslandMsgBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.context = context;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, IslandMsgBean item, int position, boolean isScrolling) {
        RoundedImageView iv_app_icon = holder.getView(R.id.iv_app_icon);
        iv_app_icon.setImageDrawable(item.getAppIcon());
    }
}
