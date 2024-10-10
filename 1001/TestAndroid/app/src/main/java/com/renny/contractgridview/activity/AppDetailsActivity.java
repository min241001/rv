package com.renny.contractgridview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.DateUtils;
import com.android.launcher3.moudle.touchup.bean.AppInfoBean;
import com.android.launcher3.moudle.touchup.utils.CommonUtil;
import com.android.launcher3.moudle.touchup.view.CircleProgress;
import com.google.android.material.imageview.ShapeableImageView;

import eightbitlab.com.blurview.BlurView;

/**
 * Create by pengmin on 2024/9/3 .
 */
public class AppDetailsActivity extends BaseActivity {
    //title
    private ImageView back_icon;
    private TextView tv_time, head_title, tv_title,tada_name;
    private BlurView blurView;
    private ShapeableImageView tada_icon;
    private RelativeLayout tada_rl_o;
    private CircleProgress circle_progress;
    private int type = -1;
    private int target_position = -1;
    @Override
    protected int getResourceId() {
        return R.layout.activity_app_details;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitViews();
        InitTitle();
        InitBlurView();
        InitEvent();
        InitProgressData();
        InitData();
    }
    private void InitData(){
        Bundle  b = getIntent().getBundleExtra("data");
        AppInfoBean bean = (AppInfoBean)b.getSerializable("bean");
        target_position = b.getInt("target_position");
        head_title.setText(bean.getApp_name());
        tada_name.setText(bean.getApp_name());
        tada_icon.setImageDrawable(AppUtils.getAppIcon(this,bean.getPackage_name()));
    }
    private void InitEvent(){
        tada_rl_o.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.putExtra("type", type);
                intent.putExtra("target_position", target_position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    private void InitViews(){
        tada_rl_o = findViewById(R.id.tada_rl_o);
        tada_name = findViewById(R.id.tada_tv);
        tada_icon = findViewById(R.id.tada_iv);
        circle_progress = findViewById(R.id.apd_circle_progress);

    }
    private void InitTitle() {
        back_icon = findViewById(R.id.head_back_iv);
        tv_time = findViewById(R.id.head_time_tv);
        head_title = findViewById(R.id.head_title_tv);
        tv_time.setText(DateUtils.GetTime());
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
   private void  InitProgressData(){
      circle_progress.setProgress((int)(Math.random() * 100));
    }
    private void  InitBlurView(){
        blurView = findViewById(R.id.blur_view);
        CommonUtil.SetBlur(this,blurView);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
