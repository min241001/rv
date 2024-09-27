package com.android.launcher3.desktop3;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.DesktopFragment;
import com.android.launcher3.common.bean.AppBean;

/**
 * @Author: zeckchan
 * @Description：桌面风格三
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class Desktop3Fragment extends DesktopFragment {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    @Override
    protected BaseRecyclerAdapter<AppBean> createAdapter() {
        return new Desktop3Adapter(getContext());
    }

}