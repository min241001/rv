package com.renny.contractgridview;

import static com.renny.contractgridview.utils.ScreenUtil.getScreenHeight;
import static com.renny.contractgridview.utils.ScreenUtil.getScreenWidth;

import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
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
    private Button btn_complete;
    private VerticalOverlayAdapter mAdapter;
    private BlurView blurView;
    private ImageButton btn_plus;
    private RelativeLayout button_plus_rl;

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
        btn_complete = findViewById(R.id.vertical_overlay_complete);
        btn_plus = findViewById(R.id.vertical_overlay_plus);
        btn_plus = findViewById(R.id.vertical_overlay_plus);
        button_plus_rl = findViewById(R.id.vertical_overlay_plus_rl);
        ///****islandFloat = new IslandFloat(this);
    }

    private void initEvent() {
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
            mAdapter = new VerticalOverlayAdapter(AppUtils.defaultApp, AppUtils.defaultApp, mHandle, this);
            mRecyclerView.setAdapter(mAdapter);
            //mRecyclerView.setMinimumHeight(getScreenHeight(this));

        } catch (Exception e) {
            LogUtil.i(TAG, "e:" + e, 0);
        }
    }

    private final Handler mHandle = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == Constants.msgWhat.CLOSE_COMPLETE_BUTTON) {
                btn_complete.setVisibility(View.VISIBLE);
                btn_plus.setVisibility(View.VISIBLE);
                // uiHandle.sendEmptyMessageDelayed(1, 1000);
                //int i = msg.arg1;
            }
            if (msg.what == Constants.msgWhat.UPDATE_APPS_DATA) {
                if(mAdapter!=null) {
                    Toast.makeText(MainActivity.this, "size:"+AppUtils.defaultApp.size(), Toast.LENGTH_SHORT).show();
                    mAdapter.notifyItemRangeChanged(0,AppUtils.defaultApp.size());
                }

            }
        }
    };
}
