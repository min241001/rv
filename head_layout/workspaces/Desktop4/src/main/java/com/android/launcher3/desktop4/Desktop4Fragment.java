package com.android.launcher3.desktop4;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.android.launcher3.common.base.BaseDesktopFragment;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.desktop4.adapter.CheckerBoardAdapter;
import com.android.launcher3.desktop4.view.CheckerBoardView;

import java.util.ArrayList;

/**
 * Author : zhangjiankang
 * Date : 2024/5/22
 * Details : 棋盘风格
 */
public class Desktop4Fragment extends BaseDesktopFragment {

    // 定义控件
    private FrameLayout relativeLayout;
    private CheckerBoardView checkerBoardStyleView;
    private CheckerBoardAdapter adapter;
    private ArrayList<AppBean> appBeans = new ArrayList<>();

    @Override
    protected int getResourceId() {
        return R.layout.desktop4_fragment_main;
    }

    @Override
    protected void initView(View view) {
        relativeLayout = findViewById(R.id.four_container);
        checkerBoardStyleView = findViewById(R.id.checker_board_view);
    }

    @Override
    public void updateView() {
        initAdapter();
    }

    private void initAdapter() {
        if (lists != null && !lists.isEmpty()) {
            appBeans.clear();
            appBeans.addAll(lists);
            LogUtil.i("d4","before appBeans.size:"+appBeans.size());
            AppBean bean = new AppBean();
            bean.setId(102);
            bean.setType("1");
            bean.setFlag(7);
            appBeans.add(bean);
            LogUtil.i("d4","after appBeans.size:"+appBeans.size());
            adapter = new CheckerBoardAdapter(getActivity(), appBeans);
            checkerBoardStyleView.setAdapter(adapter);
            checkerBoardStyleView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    public void onKeyDown(int keyCode) {
        // 19 - 放大
        if (keyCode == 19) {
            checkerBoardStyleView.makeBigger();
        } else {
            checkerBoardStyleView.makeSmaller();
        }
    }

}
