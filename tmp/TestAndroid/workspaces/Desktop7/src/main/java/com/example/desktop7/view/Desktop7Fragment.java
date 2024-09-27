package com.example.desktop7.view;

import android.view.View;
import android.widget.AdapterView;

import com.android.launcher3.common.base.BaseDesktopFragment;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.desktop7.R;
import com.example.desktop7.adapter.ThreeWithFourAdapter;

import java.util.ArrayList;

public class Desktop7Fragment extends BaseDesktopFragment {

    private ThreeWithFourView threeWithFourView;
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
            adapter = new ThreeWithFourAdapter(getActivity(), appBeans);
            threeWithFourView.setAdapter(adapter);
            threeWithFourView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(final AdapterView<?> parent, final View view,
                                        final int position, final long id) {
                    lunchApp(adapter.getItem(position));
                }
            });
        }
    }

}
