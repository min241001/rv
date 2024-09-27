package com.android.launcher3.desktop4;

import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.android.launcher3.common.base.BaseDesktopFragment;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.desktop4.cellular.adapter.CheckerBoardAdapter;
import com.android.launcher3.desktop4.cellular.view.CheckerBoardStyleView;

import java.util.ArrayList;

/**
 * @Description：桌面风格四
 */
public class Desktop4Fragment extends BaseDesktopFragment {

    // 定义控件
    private FrameLayout relativeLayout;
    private CheckerBoardStyleView checkerBoardStyleView;
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
            adapter = new CheckerBoardAdapter(getActivity(), appBeans);
            checkerBoardStyleView.setAdapter(adapter);
            checkerBoardStyleView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(final AdapterView<?> parent, final View view,
                                        final int position, final long id) {
                    lunchApp(adapter.getItem(position));
                }
            });
        }
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
