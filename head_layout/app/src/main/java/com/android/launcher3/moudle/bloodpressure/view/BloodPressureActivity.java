package com.android.launcher3.moudle.bloodpressure.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpActivity;
import com.android.launcher3.widget.CustomBarChart;
import com.android.launcher3.moudle.bloodpressure.model.DayAxisValueFormatter;
import com.android.launcher3.moudle.bloodpressure.model.MyAxisValueFormatter;
import com.android.launcher3.moudle.bloodpressure.presenter.BloodPressurePresenter;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class BloodPressureActivity extends BaseMvpActivity<BloodPressureView, BloodPressurePresenter> implements BloodPressureView, OnChartValueSelectedListener {

    private ImageView imageView;
    private AnimationDrawable animationDrawable;
    private Boolean isWear = false;
    private CustomBarChart barChart;
    private int recentBlood;

    private TextView tv_test;
    private TextView tv_recent;
    private TextView tv_low;
    private TextView tv_high;

    @Override
    public void onGetDataSuccess(@NonNull List<String> data) {

    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_blood_pressure;
    }

    @Override
    protected BloodPressurePresenter createPresenter() {
        return new BloodPressurePresenter();
    }


    @Override
    protected void initView() {
        imageView = findViewById(R.id.iv_img);
        tv_test = findViewById(R.id.tv_test);
        barChart = findViewById(R.id.chat);
        tv_low = findViewById(R.id.tv_low);
        tv_high = findViewById(R.id.tv_high);
    }

    @Override
    protected void initData() {

        //配置图表
        initChat();

        //判断是否佩戴好手表
        if (isWear) {
            //佩戴手表 开始测量
        } else {
            //TODO:再次测量逻辑待处理
            tv_test.setText("再次测量");
            //手表脱落弹窗
            //实例化Dialog对象，并应用NormalDialogStyle去掉系统样式
            Dialog dialog = new Dialog(this, com.android.launcher3.common.R.style.FallHintDialog);
            View view = View.inflate(this, com.android.launcher3.common.R.layout.dialog_fall_pressure_hint, null);
            dialog.setContentView(view);//给dialog设置一个视图
            dialog.setCanceledOnTouchOutside(true);//在点击窗口外的地方可以退出
            Window dialogWindow = dialog.getWindow();//获取Dialog的窗口
            //实例化窗口布局对象
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度铺满
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;//高度自适应
            lp.gravity = Gravity.CENTER;//窗口停靠在顶部居中
            dialogWindow.setAttributes(lp);
            dialog.show();
        }

        //动画
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        if (imageView != null) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    animationDrawable.start();
                }
            });
        }
    }

    private void initChat() {
        barChart.setOnChartValueSelectedListener(this);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setRoundedBarRadius(45f);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setBackgroundResource(com.android.launcher3.common.R.drawable.bg_blood_xygen_style);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setEnabled(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawLabels(true);
//        xAxis.setAxisLineColor(Color.parseColor("#686868"));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(false);

        ValueFormatter custom = new MyAxisValueFormatter();
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setDrawLabels(true);

        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(10f); // this replaces setStartAtZero(true)
        rightAxis.setAxisLineColor(Color.parseColor("#ff190404"));
        rightAxis.setTextColor(Color.parseColor("#523636"));
        rightAxis.setGridColor(Color.parseColor("#523636"));
        rightAxis.setDrawGridLines(true);
        rightAxis.setTextSize(20f);
        //设置y轴坐标数的个数，包含0点，后面设为true，才可固定右边y轴有几个坐标值
        //设置y轴的标签数。这个数字不是固定的(如果force==false)，并且只能近似。如果启用了强制(True)，则会绘制精确的标签计数–这会导致轴上的不均匀数字。
        rightAxis.setLabelCount(3, false);
//        rightAxis.setAxisMinimum(60f);
//        rightAxis.setAxisMaximum(180f);


        Legend l = barChart.getLegend();
        l.setEnabled(false);

        setData(7, 100);
    }


    @Override
    public void onDestroy() {
        if (imageView != null) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    animationDrawable.stop();
                }
            });
        }
        super.onDestroy();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @SuppressLint("SetTextI18n")
    private void setData(int count, float range) {

        float start = 1f;


        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));


//            if (Math.random() * 100 < 25) {
//                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.ic_launcher)));
//            } else {
//                values.add(new BarEntry(i, val));
//            }

            values.add(new BarEntry(i, new float[]{val, (float) (Math.abs((Math.random() * (range + 1)) - val))}));

        }


        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "2017");

            set1.setDrawIcons(false);
            set1.getColors().clear();
            float max = values.get(0).getY();
            float min = values.get(0).getY();
            for (int i = 0; i <= values.size() - 1; i++) {
                if (values.get(i).getY() > max) {
                    max = values.get(i).getY();
                }
                if (values.get(i).getY() < min) {
                    min = values.get(i).getY();
                }


//                if (values.get(i).getY() < 70f) {
//                    set1.getColors().add(getResources().getColor(com.android.launcher3.common.R.color.blood_oxygen_red, null));
//                } else if (values.get(i).getY() >= 70f && (int) values.get(i).getY() < 90f) {
//                    set1.getColors().add(getResources().getColor(com.android.launcher3.common.R.color.blood_oxygen_yellow, null));
//                } else if (values.get(i).getY() >= 90f) {
//                    set1.getColors().add(getResources().getColor(com.android.launcher3.common.R.color.blood_oxygen_blue, null));
//                }
            }

            set1.setColors(new int[]{com.android.launcher3.common.R.color.blood_oxygen_red, com.android.launcher3.common.R.color.blood_oxygen_yellow}, this);

            recentBlood = (int) values.get(values.size() - 1).getY();
            float[] yVals = values.get(values.size() - 1).getYVals();
            float yVal0 = yVals[0];
            float yVal1 = yVals[1];

            tv_low.setText((int) yVal0 + "");
            tv_high.setText((int) yVal1 + (int) yVal0 + "");

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
//            data.setValueTypeface(tfLight);
            data.setBarWidth(0.2f);
            data.setDrawValues(false);

//            barChart.setExtraBottomOffset(10F);
            barChart.setData(data);
        }
    }
}