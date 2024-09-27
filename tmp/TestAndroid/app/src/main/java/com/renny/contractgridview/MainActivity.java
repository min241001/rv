package com.renny.contractgridview;

import static com.renny.contractgridview.utils.ScreenUtil.getScreenHeight;
import static com.renny.contractgridview.utils.ScreenUtil.getScreenWidth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.renny.contractgridview.adapter.OverlayLayoutManager;
import com.renny.contractgridview.adapter.VerticalOverlayAdapter;
import com.renny.contractgridview.base.Constants;
import com.renny.contractgridview.bean.AppInfoBean;
import com.renny.contractgridview.opreator.AppListOpreator;
import com.renny.contractgridview.utils.AppUtils;
import com.renny.contractgridview.utils.CommonUtil;
import com.renny.contractgridview.utils.LogUtil;

import java.util.List;

import eightbitlab.com.blurview.BlurView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "main";
    private RecyclerView mRecyclerView;
    private NestedScrollView nsv;
    private Button btn_complete;
    private VerticalOverlayAdapter mAdapter;
    private BlurView blurView;
    private ImageButton btn_plus;
    private RelativeLayout button_plus_rl;
    private boolean editFlag = false;
    private RelativeLayout tuef_head_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_overlay_fragment);
        //EventBus.getDefault().register(this);
        initView();
        InitBlurView();
        initEvent();
        initAdapter();


    }

    private void InitBlurView() {
        blurView = findViewById(R.id.vof_blur_view);
        CommonUtil.SetBlur(this, blurView);
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.vertical_overlay_rv);
        //nsv = findViewById(R.id.tuef_nsv);
        //nsv.setSmoothScrollingEnabled(true);
        btn_complete = findViewById(R.id.vertical_overlay_complete);
        btn_plus = findViewById(R.id.vertical_overlay_plus);
        btn_plus = findViewById(R.id.vertical_overlay_plus);
        button_plus_rl = findViewById(R.id.vertical_overlay_plus_rl);
        tuef_head_layout = findViewById(R.id.tuef_head_layout);
        ///****islandFloat = new IslandFloat(this);
    }

    private void initEvent() {
        /*if (nsv != null) {
            nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Log.i(TAG, "------------nsv  scrollOldY:" + oldScrollY);
                    Log.i(TAG, "nsv   scrollY:" + scrollY);
                    Log.i(TAG, "nsv  v.getChildAt(0).getMeasuredHeight():" + v.getChildAt(0).getMeasuredHeight());
                    Log.i(TAG, "nsv   v.getMeasuredHeight():" + v.getMeasuredHeight());
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
                        if (scrollY <=tuef_head_layout.getMeasuredHeight()&&(oldScrollY-scrollY)>0) {
                            mRecyclerView.setNestedScrollingEnabled(false);
                        }else{
                            mRecyclerView.setNestedScrollingEnabled(true);
                        }
                        if(scrollY==0) {
                            mRecyclerView.setMinimumHeight(getScreenHeight(MainActivity.this) - tuef_head_layout.getMeasuredHeight());
                            nsv.setMinimumHeight(getScreenHeight(MainActivity.this) - tuef_head_layout.getMeasuredHeight());
                        }else if(scrollY==tuef_head_layout.getMeasuredHeight()){
                            mRecyclerView.setMinimumHeight(getScreenHeight(MainActivity.this));
                            nsv.setMinimumHeight(getScreenHeight(MainActivity.this) );
                        }

                    }
                }
            });
        }*/

        if (mRecyclerView != null) {
            mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //Log.i(TAG, "------------rv  scrollOldY:" + oldScrollY);
                    // Log.i(TAG, "rv   scrollY:" + scrollY);
                }
            });

            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //Log.i(TAG, "rv  getChildCount:" + recyclerView.getChildCount());
                    //Log.i(TAG, "rv  newState:" + newState);
                    //Log.i(TAG, "rv  MeasuredWidthAndState:" + recyclerView.getMeasuredWidthAndState());
                    //Log.i(TAG, "rv  getAdapter().getItemCount:" + recyclerView.getAdapter().getItemCount());
                    
                        /*if (editFlag) {
                            if (recyclerView.getChildCount() == 5 || newState == 0) {
                                button_plus_rl.setVisibility(View.GONE);
                            } else {
                                button_plus_rl.setVisibility(View.VISIBLE);
                            }
                        }*/
                    if (newState == 0) {
                        //mRecyclerView.setNestedScrollingEnabled(false);
                    } else {
                        //mRecyclerView.setNestedScrollingEnabled(true);
                    }
                }
            });
        }
        if (mAdapter != null) {
            mAdapter.setItemClickListener(new VerticalOverlayAdapter.OnItemClickListener() {
                @Override
                public void onItemClik(View view, int position) {
                    if (position != AppUtils.defaultApp.size() - 1) {
                        AppUtils.LauncherActivity(MainActivity.this, AppUtils.defaultApp.get(position).getPackage_name());
                    } else {
                        //Toast.makeText(this, "menu", Toast.LENGTH_SHORT).show();
                        sendBroadcast(new Intent(Constants.SET_VIEW_PAGER_ACTION));
                    }
                }

                @Override
                public void onItemLongClik(View view, int position) {
                /*for (int i = 0; i < AppUtils.defaultApp.size()-1; i++) {
                    AppUtils.defaultApp.get(i).setEditMode(true);
                    //mAdapter.notifyItemChanged(i);
                }*/
                    //LogUtil.i("vv", "after:" + AppUtils.defaultApp.size(), 1);
                }
            });
        }
        ///***
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFlag = false;
                //mAdapter.DelPlusButton();
                AppListOpreator.RemovePulsItem(AppUtils.defaultApp, mAdapter);
                Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();
                btn_complete.setVisibility(View.GONE);
                btn_plus.setVisibility(View.GONE);

            }
        });
    }

    private void initAdapter() {
        try {
            AppUtils.InitData(this, mHandle);
            //AppUtils.getAllAppInfo(this, mHandle,false);
            //LogUtil.i("itemc ", "data size:" + data.size(), 0);
            //LogUtil.i("itemc ", "default data size:" + AppUtils.defaultApp.size(), 0);
            OverlayLayoutManager layoutManager = new OverlayLayoutManager(OrientationHelper.VERTICAL, false);
            //layoutManager.setMeasuredDimension(getScreenWidth(this),getScreenHeight(this));
            mRecyclerView.setLayoutManager(layoutManager);
            //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            //mRecyclerView.setLayoutManager(new StackLayoutManager());
            mAdapter = new VerticalOverlayAdapter(AppUtils.defaultApp, AppUtils.defaultApp, mHandle, mRecyclerView,this);
            mAdapter.setHeadView();
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setMinimumHeight(getScreenHeight(this));


        } catch (Exception e) {
            LogUtil.i(TAG, "e:" + e, 0);
        }
    }

    private final Handler mHandle = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == Constants.msgWhat.OPEN_EDIT_MODE) {
                editFlag = true;
                btn_complete.setVisibility(View.VISIBLE);
                btn_plus.setVisibility(View.VISIBLE);
                // uiHandle.sendEmptyMessageDelayed(1, 1000);
                //int i = msg.arg1;
            }
            if (msg.what == Constants.msgWhat.UPDATE_APPS_DATA) {
                if (mAdapter != null) {
                    Toast.makeText(MainActivity.this, "size:" + AppUtils.defaultApp.size(), Toast.LENGTH_SHORT).show();
                    mAdapter.notifyItemRangeChanged(0, AppUtils.defaultApp.size());
                }

            }
        }
    };
}
