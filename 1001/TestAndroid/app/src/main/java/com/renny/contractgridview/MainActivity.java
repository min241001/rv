package com.renny.contractgridview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.renny.contractgridview.activity.AppListActivity;
import com.renny.contractgridview.adapter.FavoriteAdapter;
import com.renny.contractgridview.adapter.VerticalOverlayAdapter;
import com.renny.contractgridview.base.Constants;
import com.renny.contractgridview.base.LogUtil;
import com.renny.contractgridview.bean.AppInfoBean;

import com.renny.contractgridview.opreator.DefaultAppsOpreator;
import com.renny.contractgridview.opreator.FavoriteAppsOpreator;
import com.renny.contractgridview.utils.AppUtil;
import com.renny.contractgridview.utils.CommonUtil;
import com.renny.contractgridview.view.OverlayLayoutManager;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_overlay_fragment);
        //EventBus.getDefault().register(this);
        gson = new Gson();
        initView();
        InitBlurView();
        initEvent();
        initAdapter();
        initData();


    }
    private void initView(){
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
    private void InitBlurView() {
        blurView = findViewById(R.id.vof_blur_view);
        CommonUtil.SetBlur(MainActivity.this, blurView);
    }

    private void initApps() {
        CommonUtil.InitAppsData(MainActivity.this, gson);
        if (AppUtil.defaultApp.size() == 0 || AppUtil.apps.size() == 0) {
            if (thread == null) {
                thread = new Thread(new Runnable() {
                    public void run() {
                        AppUtil.GetAppsA(MainActivity.this, gson);
                    }
                });
                thread.start();
            }
        }
    }
    private void initEvent() {
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
                mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
                            Toast.makeText(MainActivity.this, "1rv_item click", Toast.LENGTH_SHORT).show();
                        }
                        if (position < beans.size()) {
                            if (position != beans.size() - 1) {
                                if (beans.get(position).getType() == 3) {
                                    Intent intent = new Intent(MainActivity.this, AppListActivity.class);
                                    intent.putExtra("type", 1);
                                    intent.putExtra("source_position", position);
                                    launcher.launch(intent);
                                } else {
                                    AppUtil.LauncherActivity(MainActivity.this, beans.get(position).getPackage_name());
                                }
                            } else {
                                //Toast.makeText(MainActivity.this, "menu", Toast.LENGTH_SHORT).show();
                                MainActivity.this.sendBroadcast(new Intent(Constants.SET_VIEW_PAGER_ACTION));
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

    private void initData() {

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


    private void initAdapter() {
        try {
            LogUtil.i(Constants.temp_tag1, "---- initAdapter  ");
            // if (beans != null && fav_beans != null) {
            OverlayLayoutManager layoutManager = new OverlayLayoutManager(MainActivity.this, OrientationHelper.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            ////layoutManager.setSmoothScrollbarEnabled(true);
            mAdapter = new VerticalOverlayAdapter(beans, fav_beans, mHandle, MainActivity.this, mRecyclerView, launcher);
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
                Intent intent = new Intent(MainActivity.this, AppListActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case R.id.vertical_overlay_complete:
                editFlag = false;
                mAdapter.DelPlusButton();
                mHandle.sendEmptyMessageDelayed(Constants.msgWhat.CLOSE_EDITMODE_STATUS, 100);
                //DefaultAppsOpreator.RemovePulsItem(AppUtil.defaultApp,mAdapter);
                //Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();
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
                                fadapter.AddFavItem(source_position, target_position, MainActivity.this);
                                mAdapter.notifyDataSetChanged();
                                fadapter.notifyDataSetChanged();
                            }
                        } else if (type == 1) {
                            if (mAdapter != null) {
                                DefaultAppsOpreator.AddItem_CheckDuplication(source_position, beans, AppUtil.defaultApp.get(target_position), mAdapter, MainActivity.this);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
        //CommonUtil.SaveState(gson,beans,fav_beans);
        launcher.unregister();
        if (thread != null) {
            thread = null;
        }
    }
}
