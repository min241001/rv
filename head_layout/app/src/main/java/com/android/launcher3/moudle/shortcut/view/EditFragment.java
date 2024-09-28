package com.android.launcher3.moudle.shortcut.view;

import static com.android.launcher3.moudle.shortcut.util.BaseUtil.DURATION_400;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpFragment;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.shortcut.adapter.RvDragAdapter;
import com.android.launcher3.moudle.shortcut.bean.Widget;
import com.android.launcher3.moudle.shortcut.bean.WidgetEnum;
import com.android.launcher3.moudle.shortcut.cst.ShortCutMenuConst;
import com.android.launcher3.moudle.shortcut.presenter.IPullDownView;
import com.android.launcher3.moudle.shortcut.presenter.PullDownInterface;
import com.android.launcher3.moudle.shortcut.presenter.PullDownPresenter;
import com.android.launcher3.moudle.shortcut.util.BaseUtil;
import com.android.launcher3.moudle.shortcut.widgets.ItemTouchCallback;
import com.android.launcher3.moudle.shortcut.widgets.MyItemDecoration;
import com.android.launcher3.utils.WidgetManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : yanyong
 * Date : 2024/7/5
 * Details : 下拉控件编辑页
 */
public class EditFragment extends BaseMvpFragment<IPullDownView, PullDownPresenter>
        implements View.OnClickListener, RvDragAdapter.OnDragInterface, IPullDownView {

    private static final String TAG = "EditFragment_123123";
    private static final long UPDATE_DELAY = 100;
    private int mBattery;
    private RecyclerView mRvAdded;
    private RvDragAdapter mRvDragAdapter;
    private List<Widget> mWidgetBeanAddedList = new ArrayList<>();
    private List<Widget> mWidgetBeanNotAddedList = new ArrayList<>();
    private boolean mIsExitAlipay;
    private long mLastUpdateTime;

    public static EditFragment newInstance(String[] params) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        if (params != null && params.length == 0) {
            args.putStringArray("PARAM", params);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected PullDownPresenter createPresenter() {
        return new PullDownPresenter(getActivity());
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_edit;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        LogUtil.i("initView: ", LogUtil.TYPE_RELEASE);
        mRvAdded = findViewById(R.id.rv_top);
    }

    @Override
    protected void initData() {
        super.initData();
        mBattery = BaseUtil.getBattery();
        LogUtil.i("initData: mBattery " + mBattery, LogUtil.TYPE_RELEASE);

        mIsExitAlipay = BaseUtil.isAppExit(getContext(), ShortCutMenuConst.ACTION_ALIPAY);
        mWidgetBeanAddedList = WidgetManager.getInstance().getDisplayWidgetList(mIsExitAlipay, true);
        mWidgetBeanNotAddedList = WidgetManager.getInstance().getNotAddedWidgetList(mIsExitAlipay);

        // 已添加数据集
        mRvDragAdapter = new RvDragAdapter(mWidgetBeanAddedList, mWidgetBeanNotAddedList, mBattery);
        mRvDragAdapter.setOnDragInterface(this);
        mRvDragAdapter.setOnItemClickListener(new RvDragAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, Widget bean, boolean idAdded) {
                if (!BaseUtil.repeatedClicks(view, DURATION_400)) {
                    return;
                }
                setWidgetBeanSelect(bean, idAdded);
            }

            @Override
            public void onEditClickListener() {
                if (mPullDownInterface != null) {
                    mPullDownInterface.onViewClickListener(0);
                    Log.i(TAG, "onClick: tv size " + mWidgetBeanAddedList.size());
                    List<Widget> list = new ArrayList<>();
                    list.add(new Widget(WidgetEnum.formEnum(WidgetEnum.BATTERY)));
                    list.add(new Widget(WidgetEnum.formEnum(WidgetEnum.WIFI)));
                    list.addAll(mWidgetBeanAddedList);
                    WidgetManager.getInstance().saveAddedIds(list);

                    List<Widget> widgets = WidgetManager.getInstance().getAddedWidgetList(false, mIsExitAlipay);
                    for (Widget widget : widgets) {
                        LogUtil.i("onClick: " + widget.toString(),LogUtil.TYPE_RELEASE);
                    }
                }
            }
        });

        mRvAdded.setAdapter(mRvDragAdapter);
        GridLayoutManager manager = getGridLayoutManager();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int topWidgetSize = mWidgetBeanAddedList.size();
                int bottomWidgetSize = mWidgetBeanNotAddedList.size();
                if (position < topWidgetSize) {
                    return 1;
                } else if (bottomWidgetSize > 0 && position < topWidgetSize + 1) {
                    return 2;
                } else if (bottomWidgetSize > 0 && position < topWidgetSize
                        + bottomWidgetSize + 1) {
                    return 1;
                }
                return 2;
            }
        });
        mRvAdded.setLayoutManager(manager);
        mRvAdded.addItemDecoration(new MyItemDecoration(getResources().getDimension(R.dimen._10dp), getResources().getDimension(R.dimen._7dp)));

        // 已添加控件拖动回调
        ItemTouchCallback callback = new ItemTouchCallback(mRvDragAdapter, mRvDragAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRvAdded);
    }

    @Override
    protected void initEvent() {
        super.initEvent();

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("onResume: 注册监听", LogUtil.TYPE_RELEASE);
        mPresenter.getAllWidgetData(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.i("onHiddenChanged: hidden " + hidden, LogUtil.TYPE_RELEASE);

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d("反注册监听", LogUtil.TYPE_RELEASE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv) {

        }
    }

    @Override
    public void onUpdateWidgetData(List<Widget> list) {
        if (list == null || list.size() == 0) {
            LogUtil.i("not update list", LogUtil.TYPE_RELEASE);
            return;
        }
        LogUtil.i("list.size " + list.size(), LogUtil.TYPE_RELEASE);
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastUpdateTime < UPDATE_DELAY) {
            LogUtil.i("updateWidget: not update", LogUtil.TYPE_RELEASE);
            return;
        }
        mLastUpdateTime = currentTime;
        mWidgetBeanAddedList = list;
        mRvDragAdapter.setWidgetBeans(mWidgetBeanAddedList);
    }

    @Override
    public void setSignalImageResource(int resourceId) {

    }

    @Override
    public void setBatteryProgress(int battery, int resourceId) {

    }

    @Override
    public void onClearComplete(long duration, String availMem, String totalMem) {

    }

    /**
     * 根据状态添加/删除控件数据，并刷新列表
     *
     * @param bean   被点击控件
     * @param idAdded 是否已添加
     */
    private void setWidgetBeanSelect(Widget bean, boolean idAdded) {
        Log.i(TAG, "setWidgetBeanSelect: idAdded " + idAdded + bean.toString());
        if (idAdded) {
            mWidgetBeanAddedList.remove(bean);
            mWidgetBeanNotAddedList.add(bean);
        } else {
            mWidgetBeanNotAddedList.remove(bean);
            mWidgetBeanAddedList.add(bean);
        }
        mRvDragAdapter.setWidgetBeans(mWidgetBeanAddedList, mWidgetBeanNotAddedList);
    }

    private PullDownInterface mPullDownInterface;

    public void setPullDownInterface(PullDownInterface pullDownInterface) {
        this.mPullDownInterface = pullDownInterface;
    }

    private GridLayoutManager getGridLayoutManager() {
        GridLayoutManager manager = new GridLayoutManager(getContext(),
                2, GridLayoutManager.VERTICAL, false);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        return manager;
    }

    @Override
    public void onDragListener(List<Widget> list) {

    }
}
