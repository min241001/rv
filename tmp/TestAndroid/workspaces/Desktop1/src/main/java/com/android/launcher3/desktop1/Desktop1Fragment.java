package com.android.launcher3.desktop1;

import static com.android.launcher3.common.CompileConfig.STYLE_147;
import static com.android.launcher3.common.CompileConfig.STYLE_TYPE;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.PageDesktopFragment;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.page.PagerGridLayoutManager;

import java.util.ArrayList;

/**
 * @Author: zeckchan
 * @Description：桌面风格一
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class Desktop1Fragment extends PageDesktopFragment {
    private ArrayList<AppBean> appBeans = new ArrayList<>();

    @Override
    protected void initView(View view) {
        super.initView(view);
        //tv1.setText("tv1");
        if (recyclerView != null) {
            // 适配不同机型设置9宫格四个角显示不全问题
            switch (STYLE_TYPE) {
                case STYLE_147:
                    int dime = (int) getResources().getDimension(R.dimen.padding);
                    recyclerView.setPadding(dime, dime, dime, dime);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void updateView() {
        super.updateView();
        if (lists != null && !lists.isEmpty()) {
            appBeans.clear();
            appBeans.addAll(lists);
            AppBean bean = new AppBean();
            bean.setId(102);
            bean.setType("1");
            bean.setFlag(7);
            appBeans.add(bean);

        }
        adapter.getList().clear();
        adapter.setList(appBeans);
        adapter.notifyItemRangeChanged(0, appBeans.size());
    }

    @Override
    public void initData() {
        super.initData();

        //recyclerView.setAdapter(adapter);

    }

    @Override
    public void initEvent() {
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                if (adapter.getItem(position).getFlag() == 7) {
                    Toast.makeText(getActivity(), "menu:" + position, Toast.LENGTH_SHORT).show();
                } else {
                    lunchApp(adapter.getItem(position));
                }
            }
        });

    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        try {
            int slide = AppLocalData.getInstance().getSlide();
            if (slide == 1) {
                GridLayoutManager layoutManage = new GridLayoutManager(getActivity(), 3);
                layoutManage.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (position == lists.size() - 1) {
                            return 3;
                        }else {

                            return 1;
                        }

                    }
                });
                return layoutManage;
                //return new GridLayoutManager(getContext(), 3);
            } else {
                return new PagerGridLayoutManager(3, 3, PagerGridLayoutManager.HORIZONTAL);
            }
        } catch (Exception e) {
            Log.e(TAG, "e:" + e);
        }
        return new GridLayoutManager(getContext(), 3);
    }

    @Override
    protected BaseRecyclerAdapter<AppBean> createAdapter() {
        return new Desktop1Adapter(getContext());
    }

}