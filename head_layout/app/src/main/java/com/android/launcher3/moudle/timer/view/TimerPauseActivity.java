package com.android.launcher3.moudle.timer.view;

import android.widget.ImageButton;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.moudle.timer.custom.CustomView;


/**
 * * Author : fangzheng
 * Date : 2024/9/24
 * Details : 计时器暂停界面
 */


public class TimerPauseActivity extends BaseActivity {

    private ImageButton btn_1,btn_2,btn_3,btn_4;


    @Override
    protected int getResourceId() {
        return R.layout.activity_timer_pause;
    }

    @Override
    protected void initView() {
        super.initView();
        CustomView customView = findViewById(R.id.custom_view);
        customView.setPercentage(50); // 设置进度为75%


        btn_1=findViewById(R.id.timer_back);
        btn_2=findViewById(R.id.timer_add);
        btn_3=findViewById(R.id.timer_cancle);
        btn_4=findViewById(R.id.timer_pause);

        btn_1.setOnClickListener(v -> {
             onBackPressed();
        });

        btn_2.setOnClickListener(v -> {

        });

        btn_3.setOnClickListener(v -> {

        });

        btn_4.setOnClickListener(v -> {

        });

    }

    @Override
    protected void initData() {
        super.initData();

    }


}
