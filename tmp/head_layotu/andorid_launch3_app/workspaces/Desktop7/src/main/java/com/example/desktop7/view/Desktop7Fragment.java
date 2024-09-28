package com.example.desktop7.view;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.android.launcher3.common.base.BaseDesktopFragment;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.desktop7.R;
import com.example.desktop7.adapter.ThreeWithFourAdapter;

import java.util.ArrayList;

/**
 * Author : zhangjiankang
 * Date : 2024/7/4
 * Details : 3列 - 4列 - 3列 排序，简称343风格（蜂窝风格2）
 */
public class Desktop7Fragment extends BaseDesktopFragment {

    private HoneycombView2 threeWithFourView;
    private ThreeWithFourAdapter adapter;
    private ArrayList<AppBean> appBeans = new ArrayList<>();

    @Override
    protected int getResourceId() {
        return R.layout.fragment_desktop7;
    }

    @Override
    protected void initView(View view) {
        threeWithFourView = findViewById(R.id.three_with_four_view);
    }

    @Override
    protected void updateView() {
        initAdapter();
    }

    private void initAdapter() {
        if (lists != null && !lists.isEmpty()) {
            appBeans.clear();
            appBeans.addAll(lists);
            AppBean bean = new AppBean();
            bean.setId(102);
            bean.setType("1");
            bean.setFlag(7);
            appBeans.add(bean);
            adapter = new ThreeWithFourAdapter(getActivity(), appBeans);
            threeWithFourView.setAdapter(adapter);
            threeWithFourView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(final AdapterView<?> parent, final View view,
                                        final int position, final long id) {
                    if(adapter.getItem(position).getFlag()==7){
                        setWorkspaceDefaultId(1);
                    }else {
                        lunchApp(adapter.getItem(position));
                    }
                }
            });
        }
    }
    protected void setWorkspaceDefaultId(int id) {
        SharedPreferencesUtils.setParam(getActivity(), "_DEFAULT_WORKSPACE_ID_", id);
        getActivity().sendBroadcast(new Intent(Constants.SET_WORK_SPACE_ACTION));
    }
}
