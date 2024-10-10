package com.renny.contractgridview.activity;

import static com.android.launcher3.common.utils.ScreenUtil.getScreenHeight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.DateUtils;
import com.android.launcher3.moudle.touchup.adapter.AppListAdapter;
import com.android.launcher3.moudle.touchup.bean.AppInfoBean;
import com.android.launcher3.moudle.touchup.utils.AppUtil;
import com.android.launcher3.moudle.touchup.utils.CommonUtil;
import com.android.launcher3.moudle.touchup.view.BottomScaleLayoutManager;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;

/**
 * Create by pengmin on 2024/9/3 .
 */
public class AppListActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rv;
    private ImageView ib_finish;
    private AppListAdapter adapter;
    private int type = 0;
    private ArrayList<AppInfoBean> beans = new ArrayList<AppInfoBean>();
    private int source_position = -1;
    //title
    private ImageView back_icon, fav_iv1, fav_iv2, fav_iv3;
    private TextView tv_time, head_title, tv_title, fav_title;
    private BlurView blurView;
    private LinearLayout fav_ll;
    private NestedScrollView apl_nsv;
    private ActivityResultLauncher launcher = null;


    @Override
    protected int getResourceId() {
        return R.layout.activity_app_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", 0);
        source_position = getIntent().getIntExtra("source_position", -1);
        InitTitle();
        InitView();
        InitBlurView();
        RegistorActivityForResult();
        InitData();
        InitAdapter();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void InitTitle() {
        back_icon = findViewById(R.id.head_back_iv);
        tv_time = findViewById(R.id.head_time_tv);
        head_title = findViewById(R.id.head_title_tv);
        //back_icon.setBackground(getDrawable(R.mipmap.ic_finish));
        back_icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_finish));
        tv_time.setText(DateUtils.GetTime());
        back_icon.setOnClickListener(this);
    }

    private void InitView() {
        tv_title = findViewById(R.id.applist_title2);
        rv = findViewById(R.id.apps_rv);
        apl_nsv = findViewById(R.id.apl_nsv);
        fav_ll = findViewById(R.id.activity_all_list_ll);
        fav_title = findViewById(R.id.fav_title);
        fav_iv1 = findViewById(R.id.activity_app_list_img1);
        fav_iv2 = findViewById(R.id.activity_app_list_img2);
        fav_iv3 = findViewById(R.id.activity_app_list_img3);
        ib_finish = findViewById(R.id.apps_finish);

        ib_finish.setOnClickListener(this);
        fav_iv1.setOnClickListener(this);
        fav_iv2.setOnClickListener(this);
        fav_iv3.setOnClickListener(this);
        if (apl_nsv != null) {
            apl_nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (scrollY < 200) {
                            rv.setNestedScrollingEnabled(false);
                        } else {
                            rv.setNestedScrollingEnabled(true);
                        }
                }
            });
        }
    }

    private void InitBlurView() {
        blurView = findViewById(R.id.blur_view);
        CommonUtil.SetBlur(this, blurView);

    }

    private void InitData() {
        beans.addAll(AppUtil.apps);
        if (type == 1) {//加载默认applist
            beans.clear();
            beans.addAll(AppUtil.defaultApp);
           // beans = AppUtil.defaultApp;
        }
        if (type == 0) {
            tv_title.setText(getString(R.string.all_apps));
            //tv_title.setTextSize(getResources().getDimension(R.dimen.sp14));//
            fav_title.setVisibility(View.VISIBLE);
            fav_ll.setVisibility(View.VISIBLE);
            if (beans.size() >= 3) {
                fav_iv1.setImageDrawable(AppUtils.getAppIcon(this,beans.get(beans.size()-1).getPackage_name()));
                fav_iv2.setImageDrawable(AppUtils.getAppIcon(this,beans.get(beans.size()-2).getPackage_name()));
                fav_iv3.setImageDrawable(AppUtils.getAppIcon(this,beans.get(beans.size()-3).getPackage_name()));
            }
        }


    }

    private void InitAdapter() {
        if (beans != null) {
            adapter = new AppListAdapter(this, beans,rv);
            BottomScaleLayoutManager layoutManager = new BottomScaleLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
            layoutManager.setSmoothScrollbarEnabled(true);
            rv.setLayoutManager(layoutManager);
            rv.setAdapter(adapter);
            rv.setMinimumHeight(getScreenHeight());
            CommonUtil.SetRVProperties(rv,30);
            adapter.setItemClickListener(new AppListAdapter.OnItemClickListener() {
                @Override
                public void onItemClik(View view, int position) {
                    //  if(type==0) {
                    if(Constants.logFlag) {
                        Toast.makeText(AppListActivity.this, "package_name" + beans.get(position).getPackage_name(), Toast.LENGTH_SHORT).show();
                    }
                    //AppUtil.LauncherActivity(AppListActivity.this, AppUtil.defaultApp.get(position).getPackage_name());
                    if (launcher != null) {
                        Intent intent = new Intent(AppListActivity.this,AppDetailsActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("bean",beans.get(position));
                        b.putInt("target_position", position);
                        b.putInt("type",type);
                        //b.putParcelableArrayList("beans", (ArrayList<? extends Parcelable>) beans);
                        intent.putExtra("data",b);
                        launcher.launch(intent);
                        //Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void RegistorActivityForResult() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    int target_position = result.getData().getIntExtra("target_position", -1);
                    //int source_position = result.getData().getIntExtra("source_position", -1);
                    if (target_position != -1) {
                        Intent intent = new Intent();//源，目标Source, target
                        intent.putExtra("type", type);
                        intent.putExtra("target_position", target_position);
                        intent.putExtra("source_position", source_position);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        launcher.unregister();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apps_finish:
                finish();
                break;
            case R.id.head_back_iv:
                if(Constants.logFlag) {
                    Toast.makeText(AppListActivity.this, "back" , Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            case R.id.activity_app_list_img1:
                    CommonUtil.SetBackResult(this, beans,type,source_position,1);
                break;
            case R.id.activity_app_list_img2:
                    CommonUtil.SetBackResult(this,  beans,type,source_position,2 );
                break;
            case R.id.activity_app_list_img3:
                    CommonUtil.SetBackResult(this, beans, type,source_position,3 );
                break;
            default:
                break;
        }

    }
}
