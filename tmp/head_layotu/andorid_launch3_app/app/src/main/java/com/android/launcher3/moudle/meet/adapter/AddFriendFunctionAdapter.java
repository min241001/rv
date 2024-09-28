package com.android.launcher3.moudle.meet.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.launcher3.R;
import com.android.launcher3.moudle.meet.eneity.FunctionEneity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class AddFriendFunctionAdapter extends BaseQuickAdapter<FunctionEneity, BaseViewHolder> {


    public AddFriendFunctionAdapter(int layoutResId, @Nullable List<FunctionEneity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FunctionEneity item) {
        helper.setImageResource(R.id.iv_funcIcon, item.getFuncIcon());
        helper.setText(R.id.iv_funcName, item.getFuncName());
        if (item.getFlag() > 0 && item.getFuncName().equals("新的好友")) {
            helper.getView(R.id.iv_function_viability).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_function_viability).setVisibility(View.INVISIBLE);
        }
    }


}
