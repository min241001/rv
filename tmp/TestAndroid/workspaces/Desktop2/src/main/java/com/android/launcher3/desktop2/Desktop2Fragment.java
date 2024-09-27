package com.android.launcher3.desktop2;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.launcher3.common.base.BaseDesktopFragment;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.desktop2.cellular.adapter.CellularAdapter;
import com.android.launcher3.desktop2.cellular.view.DemoView;

import java.util.ArrayList;

/**
 * @Description：桌面风格二
 */
public class Desktop2Fragment extends BaseDesktopFragment {

    private DemoView cellular_view;
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
                        Toast.makeText(getActivity(), "menu:"+position, Toast.LENGTH_SHORT).show();
                    }else {
                        lunchApp(adapter.getItem(position));
                    }
                }
            });
        }
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
