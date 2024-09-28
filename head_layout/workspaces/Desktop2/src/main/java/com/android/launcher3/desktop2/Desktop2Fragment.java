package com.android.launcher3.desktop2;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.android.launcher3.common.base.BaseDesktopFragment;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.desktop2.adapter.CellularAdapter;
import com.android.launcher3.desktop2.view.HoneycombView;

import java.util.ArrayList;

/**
 * Author : zhangjiankang
 * Date : 2024/6/12
 * Details : 蜂窝风格
 */
public class Desktop2Fragment extends BaseDesktopFragment {

    private HoneycombView cellular_view;
    private CellularAdapter adapter;
    private ArrayList<AppBean> appBeans = new ArrayList<>();

    @Override
    protected int getResourceId() {
        return R.layout.desktop2_fragment_main;
    }

    @Override
    protected void initView(View view) {
        cellular_view = findViewById(R.id.cellular_list);
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
            adapter = new CellularAdapter(getActivity(), appBeans);
            cellular_view.setAdapter(adapter);
            cellular_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(final AdapterView<?> parent, final View view,
                                        final int position, final long id) {
                    if(adapter.getItem(position).getFlag()==7){
                        setWorkspaceDefaultId(1);
                        //Toast.makeText(getActivity(), "menu:"+position, Toast.LENGTH_SHORT).show();
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

    @Override
    public void updateView() {
        initAdapter();
    }

    public void onKeyDown(int keyCode) {
        // 19 - 放大
//        if (keyCode == 19) {
//            cellular_view.makeBigger();
//        } else {
//            cellular_view.makeSmaller();
//        }
    }

}
