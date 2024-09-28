package com.android.launcher3.desktop5;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.PageDesktopFragment;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.page.PagerGridLayoutManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;

import java.util.ArrayList;

/**
 * @Author: zeckchan
 * @Description：桌面风格五
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class Desktop5Fragment extends PageDesktopFragment {
    private ArrayList<AppBean> appBeans = new ArrayList<>();
    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        int slide = AppLocalData.getInstance().getSlide();
        LogUtil.i("d5","slide:"+slide);
        if (slide == 1){
            //return new GridLayoutManager(getContext(), 2);
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position>(adapter.getList().size()-2)) {
                        return 2;
                    }
                    return 1;
                }
            });
            return layoutManager;
        }else {
            return new PagerGridLayoutManager(2, 2, PagerGridLayoutManager.HORIZONTAL);
        }
    }
    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    @Override
    protected void initEvent(){
        //super.initEvent();
        adapter.setOnItemClickListener((parent, view, position) -> lunchApp(adapter.getItem(position)));
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                if(adapter.getItem(position).getFlag()==7){
                    setWorkspaceDefaultId(1);
                }else {
                    lunchApp(adapter.getItem(position));
                }

            }
        });
    }
    protected void setWorkspaceDefaultId(int id) {
        SharedPreferencesUtils.setParam(getActivity(), "_DEFAULT_WORKSPACE_ID_", id);
        getActivity().sendBroadcast(new Intent(Constants.SET_WORK_SPACE_ACTION));
    }
    @Override
    public void updateView() {
        //super.updateView();
        if (lists != null && !lists.isEmpty()) {
            appBeans.clear();
            appBeans.addAll(lists);
            LogUtil.i("d4", "before appBeans.size:" + appBeans.size());
            AppBean bean = new AppBean();
            bean.setId(102);
            bean.setType("1");
            bean.setFlag(7);
            appBeans.add(bean);
            adapter.getList().clear();
            adapter.setList(appBeans);;
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    protected BaseRecyclerAdapter<AppBean> createAdapter() {
        return new Desktop5Adapter(getContext());
    }
}