package com.android.launcher3.moudle.touchup.fragment;

import static android.app.Activity.RESULT_OK;
import static androidx.databinding.DataBindingUtil.setContentView;
import static androidx.recyclerview.widget.RecyclerView.*;
import static com.android.launcher3.common.utils.ScreenUtil.getScreenHeight;
import static com.android.launcher3.common.utils.ScreenUtil.getScreenWidth;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseFragment;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.touchup.activity.AppListActivity;
import com.android.launcher3.moudle.touchup.adapter.FavoriteAdapter;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.moudle.touchup.adapter.VerticalOverlayAdapter;
//import com.android.launcher3.moudle.touchup.bean.EventMessageBean;
import com.android.launcher3.moudle.touchup.bean.AppInfoBean;
import com.android.launcher3.moudle.touchup.opreator.DefaultAppsOpreator;
import com.android.launcher3.moudle.touchup.opreator.FavoriteAppsOpreator;
import com.android.launcher3.moudle.touchup.utils.AppUtil;
import com.android.launcher3.moudle.touchup.utils.CommonUtil;
import com.android.launcher3.moudle.touchup.view.OverlayLayoutManager;
import com.google.gson.Gson;
/*import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;*/

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;

public class TouchUpEventFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "tef";
    private boolean editFlag = false;
    private RecyclerView mRecyclerView;
    private Button btn_complete;
    private ImageButton btn_plus;
   // private TextView tv_date;
    private RelativeLayout button_plus_rl;
    //private NestedScrollView nsv;
    private ActivityResultLauncher launcher = null;

    private VerticalOverlayAdapter mAdapter;
    private List<AppInfoBean> beans = new ArrayList<AppInfoBean>();
    private List<AppInfoBean> fav_beans = new ArrayList<AppInfoBean>();
    private BlurView blurView;
    private Gson gson;
    private Thread thread = null;
    private boolean resumeFlag = false;
    private boolean scrollFlag = true;

    public static Fragment newInstance(String[] params) {
        Fragment fragment = new TouchUpEventFragment();
        return fragment;
    }

    @Override
    protected int getResourceId() {
        return R.layout.vertical_overlay_fragment;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
//      EventBus.getDefault().register(this);
        initView();
        InitBlurView();
        RegistorActivityForResult();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeFlag = true;
        // mHandle.sendEmptyMessageDelayed(Constants.msgWhat.UPDATE_PACKAGE_DELAYED, 5 * 1000);
    }

    private void InitBlurView() {
        blurView = findViewById(R.id.vof_blur_view);
        CommonUtil.SetBlur(getActivity(), blurView);
    }

    private void initApps() {
        CommonUtil.InitAppsData(getActivity(), gson);
        if (AppUtil.defaultApp.size() == 0 || AppUtil.apps.size() == 0) {
            if (thread == null) {
                thread = new Thread(new Runnable() {
                    public void run() {
                        AppUtil.GetAppsA(getActivity(), gson);
                    }
                });
                thread.start();
            }
        }
    }

    private void initView() {
       //nsv = findViewById(R.id.tuef_nsv);
        //nsv.setSmoothScrollingEnabled(true);
        mRecyclerView = findViewById(R.id.vertical_overlay_rv);
        //head_layout = findViewById(R.id.tuef_head_layout);
        btn_complete = findViewById(R.id.vertical_overlay_complete);
        btn_plus = findViewById(R.id.vertical_overlay_plus);
        button_plus_rl = findViewById(R.id.vertical_overlay_plus_rl);
        //tv_date = findViewById(R.id.tf_tv_date);
       // String date = DateFormat.format("EEEE \nMM月 \ndd ", System.currentTimeMillis()).toString();
       // tv_date.setText(date);
    }

    @Override
    protected void initData() {
        super.initData();
        gson = new Gson();
        //initApps();
        InitData();
        LogUtil.i(Constants.temp_tag1, "---- initData  ");
        if (mAdapter == null) {
            initAdapter();
        }
    }

    private void InitData() {

        beans.clear();
        fav_beans.clear();
        //添加一个闹钟
        AppInfoBean bean3 = new AppInfoBean();
        bean3.setId(7);
        bean3.setApp_name("闹钟");
        beans.add(bean3);
        beans.addAll(AppUtil.defaultApp);
        //
        AppInfoBean bean1 = new AppInfoBean();
        bean1.setType(1);//倒数第一个item
        AppInfoBean bean2 = new AppInfoBean();
        bean2.setType(2);//倒数第二个item
        beans.add(bean2);
        beans.add(bean1);
        //倒数第一个item

        //favirite
        if (AppUtil.apps.size() > 2) {
            fav_beans.addAll(AppUtil.apps.subList(0, 2));
        }
        FavoriteAppsOpreator.CheckFavoriteBeans(fav_beans);//检查数量
        //// CommonUtil.RestoreState( gson,beans, fav_beans);
        if (mAdapter != null) {
            //mAdapter.notifyDataSetChanged();
            mAdapter.notifyItemRangeChanged(0, beans.size());
            //mAdapter.GetFavoriteAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        btn_complete.setOnClickListener(this);
        btn_plus.setOnClickListener(this);
        try {
            /*if (nsv != null) {
                nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        // LogUtil.i(TAG, "------------nsv  scrollOldY:" + oldScrollY);
                        // LogUtil.i(TAG, "nsv   scrollY:" + scrollY);
                        // LogUtil.i(TAG, "nsv  v.getChildAt(0).getMeasuredHeight():" + v.getChildAt(0).getMeasuredHeight());
                        // LogUtil.i(TAG, "nsv   v.getMeasuredHeight():" + v.getMeasuredHeight());
                        //判断是否滑到的顶部
                        //scrollY == 0
                        if (editFlag) {
                            mRecyclerView.setNestedScrollingEnabled(true);
                            if (scrollY == 0) {
                                button_plus_rl.setVisibility(View.GONE);
                            } else {
                                button_plus_rl.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (scrollY < 20) {
                                mRecyclerView.setNestedScrollingEnabled(false);
                            } else {
                                mRecyclerView.setNestedScrollingEnabled(true);
                            }
                        }
                    }
                });
            }*/
            if (mRecyclerView != null) {
                mRecyclerView.setOnScrollChangeListener(new OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        LogUtil.i(TAG, "------------rv  scrollOldY:" + oldScrollY);
                        LogUtil.i(TAG, "rv   scrollY:" + scrollY);
                    }
                });

                mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }
                });
            }
            if (mAdapter != null) {
                mAdapter.setItemClickListener(new VerticalOverlayAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClik(View view, int position) {
                        if (Constants.logFlag) {
                            Toast.makeText(getActivity(), "1rv_item click", Toast.LENGTH_SHORT).show();
                        }
                        if (position < beans.size()) {
                            if (position != beans.size() - 1) {
                                if (beans.get(position).getType() == 3) {
                                    Intent intent = new Intent(getActivity(), AppListActivity.class);
                                    intent.putExtra("type", 1);
                                    intent.putExtra("source_position", position);
                                    launcher.launch(intent);
                                } else {
                                    AppUtil.LauncherActivity(getActivity(), beans.get(position).getPackage_name());
                                }
                            } else {
                                //Toast.makeText(getActivity(), "menu", Toast.LENGTH_SHORT).show();
                                getActivity().sendBroadcast(new Intent(Constants.SET_VIEW_PAGER_ACTION));
                            }
                        }
                    }
                });
                mAdapter.setItemLongClickListener(new VerticalOverlayAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClik(View view, int position) {
                        DefaultAppsOpreator.InsertPlusItem(beans, position, mAdapter, mHandle);
                        DefaultAppsOpreator.SetFavoriteStatus(fav_beans, position, mAdapter.GetFavoriteAdapter(), mHandle);
                        LogUtil.i("vv", "after:" + AppUtil.defaultApp.size(), 1);
                    }
                });
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "TouchUpEventFragment -> initEvent e:" + e, 1);
        }

    }

    private void initAdapter() {
        try {
            LogUtil.i(Constants.temp_tag1, "---- initAdapter  ");
            // if (beans != null && fav_beans != null) {
            OverlayLayoutManager layoutManager = new OverlayLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ////layoutManager.setSmoothScrollbarEnabled(true);
            mAdapter = new VerticalOverlayAdapter(beans, fav_beans, mHandle, getActivity(), mRecyclerView, launcher);
            //mAdapter.setHasStableIds(true);
            mAdapter.setHeadView();
            mRecyclerView.setAdapter(mAdapter);
            //mRecyclerView.setMinimumHeight((getScreenHeight()));
            //mRecyclerView.setMinimumWidth(getScreenWidth());
            CommonUtil.SetRVProperties(mRecyclerView,30);
            //}
        } catch (Exception e) {
            LogUtil.e(TAG, "e:" + e, 1);
        }
    }

   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(EventMessageBean msg) {
        if (msg.getWhat() == Constants.eventWhat.SET_PACKAGE_DATA) {
            if (Constants.logFlag) {
                if (msg.getMsg().equals("set_data")) {
                    Toast.makeText(getActivity(), "Set data", Toast.LENGTH_SHORT).show();
                    mHandle.sendEmptyMessage(Constants.msgWhat.SAVE_APPS_DATA);
                } else {
                    Toast.makeText(getActivity(), "Set data2", Toast.LENGTH_SHORT).show();
                }
            }
            InitData(false);
        }
    }*/

    private Handler mHandle = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.msgWhat.OPEN_EDITMODE_STATUS:
                    editFlag = true;
                    if (beans.get(0).getType() == 3) {
                        beans.get(0).setHide(false);
                    }
                    btn_complete.setVisibility(View.VISIBLE);
                    //button_plus_rl.setVisibility(View.GONE);
                    ////head_layout.setVisibility(View.GONE);
                    // uiHandle.sendEmptyMessageDelayed(1, 1000);
                    int i = msg.arg1;
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case Constants.msgWhat.CLOSE_EDITMODE_STATUS:
                    if (mAdapter != null) {
                        mAdapter.DelPlusButton();
                    }
                    break;
                case Constants.msgWhat.UPDATE_RV_ITEM:
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case Constants.msgWhat.OPEN_EDITMODE:
                    int position = 0;
                    DefaultAppsOpreator.InsertPlusItem(beans, position, mAdapter, mHandle);
                    DefaultAppsOpreator.SetFavoriteStatus(fav_beans, position, mAdapter.GetFavoriteAdapter(), mHandle);
                    //LogUtil.i("vv", "after:" + AppUtil.defaultApp.size(), 1);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vertical_overlay_plus:
                Intent intent = new Intent(getActivity(), AppListActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case R.id.vertical_overlay_complete:
                editFlag = false;
                mAdapter.DelPlusButton();
                mHandle.sendEmptyMessageDelayed(Constants.msgWhat.CLOSE_EDITMODE_STATUS, 100);
                //DefaultAppsOpreator.RemovePulsItem(AppUtil.defaultApp,mAdapter);
                //Toast.makeText(getActivity(), "complete", Toast.LENGTH_SHORT).show();
                btn_complete.setVisibility(View.GONE);
                button_plus_rl.setVisibility(View.GONE);
               //// head_layout.setVisibility(View.VISIBLE);
                break;
            default:
                break;

        }
    }

    private void RegistorActivityForResult() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    int target_position = result.getData().getIntExtra("target_position", -1);
                    int source_position = result.getData().getIntExtra("source_position", -1);
                    int type = result.getData().getIntExtra("type", -1);
                    if (source_position != -1) {
                        if (type == 0) {
                            LogUtil.i("fav", "source_position:" + source_position, 0);
                            //mAdapter.AddFavItem(source_position,target_position);
                            FavoriteAdapter fadapter = mAdapter.GetFavoriteAdapter();
                            if (fadapter != null) {
                                fadapter.AddFavItem(source_position, target_position, getActivity());
                                mAdapter.notifyDataSetChanged();
                                fadapter.notifyDataSetChanged();
                            }
                        } else if (type == 1) {
                            if (mAdapter != null) {
                                DefaultAppsOpreator.AddItem_CheckDuplication(source_position, beans, AppUtil.defaultApp.get(target_position), mAdapter, getActivity());
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        resumeFlag = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //EventBus.getDefault().unregister(this);
        //CommonUtil.SaveState(gson,beans,fav_beans);
        launcher.unregister();
        if (thread != null) {
            thread = null;
        }
    }

}
