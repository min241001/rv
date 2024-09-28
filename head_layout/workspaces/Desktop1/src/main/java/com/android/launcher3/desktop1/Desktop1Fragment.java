package com.android.launcher3.desktop1;

import static com.android.launcher3.common.CompileConfig.STYLE_147;
import static com.android.launcher3.common.CompileConfig.STYLE_TYPE;

import android.content.Intent;
import android.util.Log;
import android.view.View;

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
 * @Description：桌面九宫格风格
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class Desktop1Fragment extends PageDesktopFragment {
    private ArrayList<AppBean> appBeans = new ArrayList<>();
    @Override
    protected void initView(View view) {
        super.initView(view);
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
    protected void initEvent(){
        //super.initEvent();
        adapter.setOnItemClickListener((parent, view, position) -> lunchApp(adapter.getItem(position)));
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                if(adapter.getItem(position).getFlag()==7){
                    //Toast.makeText(getActivity(), "menu:"+position, Toast.LENGTH_SHORT).show();
                    setWorkspaceDefaultId(2);
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
    public void initData(){
        super.initData();

    }
    @Override
    public void updateView() {
        //super.updateView();
        if (lists != null && !lists.isEmpty()) {
            appBeans.clear();
            appBeans.addAll(lists);
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
    protected RecyclerView.LayoutManager createLayoutManager() {
        int slide = AppLocalData.getInstance().getSlide();
        if (slide == 1){
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),3);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position>(adapter.getList().size()-2)) {
                        return 3;
                    }
                    return 1;
                }
            });
            return layoutManager;
            //return new GridLayoutManager(getContext(), 3);
        }else {
            Log.i("df1", "111");
            return new PagerGridLayoutManager(3, 3, PagerGridLayoutManager.HORIZONTAL);
        }
    }

    @Override
    protected BaseRecyclerAdapter<AppBean> createAdapter() {
        return new Desktop1Adapter(getContext());
    }

}