package com.android.launcher3.moudle.bloodpressure.model;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class MyAxisValueFormatter extends ValueFormatter {

    private final DecimalFormat mFormat;

    public MyAxisValueFormatter() {
//        mFormat = new DecimalFormat("###,###,###,##0.0");
        mFormat = new DecimalFormat("#");
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + " ";
    }
}
