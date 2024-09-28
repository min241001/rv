package com.android.launcher3.moudle.timer.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import java.util.Calendar;


/**
 * * Author : fangzheng
 * Date : 2024/9/24
 * Details : 计时器时间选择器界面
 */




public class TimerAddActivity extends BaseActivity implements NumberPicker.OnScrollListener, NumberPicker.Formatter, NumberPicker.OnValueChangeListener {

    private String TAG="Fangzheng";
    private NumberPicker hourPicker,minutePicker,secondPicker;
    private TextView tx_hour,tx_minute,tx_second,tx_time;
    private ImageButton btn_back;
    private Button btn_ok;

    @Override
    protected int getResourceId() {
        return R.layout.activity_timer_add;
    }

    @Override
    protected void initView() {
        super.initView();
        hourPicker=findViewById(R.id.hourpicker);//数字选择器
        minutePicker=findViewById(R.id.minutepicker);
        secondPicker=findViewById(R.id.secondpicker);



        tx_hour=findViewById(R.id.tx_hour);//文本显示
        tx_minute=findViewById(R.id.tx_minute);
        tx_second=findViewById(R.id.tx_second);

        btn_back=findViewById(R.id.Image_Back);
        btn_back.setOnClickListener(v -> {
               onBackPressed();
        });

        tx_time=findViewById(R.id.tx_time);

        btn_ok=findViewById(R.id.btn_ok); //获取当前的选择的时间数据并返回给上一个页面
        btn_ok.setOnClickListener(v -> {

               Intent resultIntent =new Intent();
               resultIntent.putExtra("hour",hourPicker.getValue());
               resultIntent.putExtra("minute",minutePicker.getValue());
               resultIntent.putExtra("second",secondPicker.getValue());

               setResult(RESULT_OK,resultIntent);
               finish();

        });
            init();

    }

    @Override
    protected void initData() {
        super.initData();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 获取24小时制的小时
        int minute = calendar.get(Calendar.MINUTE); // 获取分钟

        // 格式化输出
        String currentTime = String.format("%02d:%02d", hour, minute);
        tx_time.setText(currentTime);
    }


    @SuppressLint("NewApi")
    private void init() {
        hourPicker.setFormatter(this);
        hourPicker.setOnValueChangedListener(this);
        hourPicker.setOnScrollListener(this);
        hourPicker.setMaxValue(24);
        hourPicker.setMinValue(0);
        hourPicker.setValue(0);

        minutePicker.setFormatter(this);
        minutePicker.setOnValueChangedListener(this);
        minutePicker.setOnScrollListener(this);
        minutePicker.setMaxValue(60);
        minutePicker.setMinValue(0);
        minutePicker.setValue(30);

        secondPicker.setFormatter(this);
        secondPicker.setOnValueChangedListener(this);
        secondPicker.setOnScrollListener(this);
        secondPicker.setMaxValue(60);
        secondPicker.setMinValue(0);
        secondPicker.setValue(0);


    }


    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {

    }

    @Override
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }


    @SuppressLint({"NonConstantResourceId", "NewApi"})
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()){
            case R.id.hourpicker:
                  hourPicker.setBackgroundResource(R.mipmap.ic_timer_select);
                  minutePicker.setBackgroundResource(R.mipmap.ic_timer_unselect);
                  secondPicker.setBackgroundResource(R.mipmap.ic_timer_unselect);



                  tx_hour.setVisibility(View.VISIBLE);
                  tx_minute.setVisibility(View.INVISIBLE);
                  tx_second.setVisibility(View.INVISIBLE);
                break;

            case R.id.minutepicker:
                  hourPicker.setBackgroundResource(R.mipmap.ic_timer_unselect);
                  minutePicker.setBackgroundResource(R.mipmap.ic_timer_select);
                  secondPicker.setBackgroundResource(R.mipmap.ic_timer_unselect);

                  tx_hour.setVisibility(View.INVISIBLE);
                  tx_minute.setVisibility(View.VISIBLE);
                  tx_second.setVisibility(View.INVISIBLE);
                break;

            case R.id.secondpicker:
                  hourPicker.setBackgroundResource(R.mipmap.ic_timer_unselect);
                  minutePicker.setBackgroundResource(R.mipmap.ic_timer_unselect);
                  secondPicker.setBackgroundResource(R.mipmap.ic_timer_select);

                  tx_hour.setVisibility(View.INVISIBLE);
                  tx_minute.setVisibility(View.INVISIBLE);
                  tx_second.setVisibility(View.VISIBLE);
                break;
        }
    }

}
