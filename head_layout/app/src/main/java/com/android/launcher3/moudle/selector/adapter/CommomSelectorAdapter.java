package com.android.launcher3.moudle.selector.adapter;

import android.content.Context;
import android.view.View;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.BaseRecyclerHolder;
import com.android.launcher3.common.bean.SelectorBean;

import java.util.ArrayList;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2023/12/15 18:41
 * @UpdateUser: shensl
 */
public class CommomSelectorAdapter extends BaseRecyclerAdapter<SelectorBean> {

    private int selectId;

    public CommomSelectorAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.list_item_common_selector);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, SelectorBean item, int position, boolean isScrolling) {
        holder.setText(R.id.face_name, item.getName());
        holder.setImageDrawable(R.id.face_thumb, item.getThumb());
        holder.setImageResource(R.id.face_radio, getVisible(item.getId()) ? R.mipmap.icon_checked : R.mipmap.icon_unchecked);
        if (item.getId() == -1){
            holder.setVisibility(R.id.face_radio, View.INVISIBLE);
        }else {
            holder.setVisibility(R.id.face_radio, View.VISIBLE);
        }
    }

    public void setSelectedId(int selectedId) {
        this.selectId = selectedId;
    }

    private boolean getVisible(int itemId) {
        return this.selectId == itemId ? true : false;
    }

}
