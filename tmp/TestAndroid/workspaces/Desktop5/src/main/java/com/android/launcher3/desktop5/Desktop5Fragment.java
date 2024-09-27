package com.android.launcher3.desktop5;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.PageDesktopFragment;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.page.PagerGridLayoutManager;

/**
 * @Author: zeckchan
 * @Description：桌面风格五
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class Desktop5Fragment extends PageDesktopFragment {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        int slide = AppLocalData.getInstance().getSlide();
        if (slide == 1){
            return new GridLayoutManager(getContext(), 2);
        }else {
            return new PagerGridLayoutManager(2, 2, PagerGridLayoutManager.HORIZONTAL);
        }
    }

    @Override
    protected BaseRecyclerAdapter<AppBean> createAdapter() {
        return new Desktop5Adapter(getContext());
    }
}