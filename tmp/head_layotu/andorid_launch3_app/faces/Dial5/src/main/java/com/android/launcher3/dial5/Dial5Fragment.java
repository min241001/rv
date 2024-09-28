package com.android.launcher3.dial5;


import android.icu.util.Calendar;
import android.widget.ImageView;

import com.android.launcher3.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Dial5Fragment extends BaseFragment {

    private ImageView ivHour1, ivHour2, ivMinute1, ivMinute2;

    @Override
    protected int getResourceId() {
        return R.layout.fragment_watch5;
    }

    @Override
    protected void initView() {
        ivHour1 = findViewById(R.id.iv_1);
        ivHour2 = findViewById(R.id.iv_2);
        ivMinute1 = findViewById(R.id.iv_3);
        ivMinute2 = findViewById(R.id.iv_4);

    }

    @Override
    protected void initData() {
        getDateTime();
    }

    @Override
    protected void handleDateChange() {
        getDateTime();
    }


    private void getDateTime(){
        Calendar calendar = Calendar.getInstance();
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());
        displayTimeAsImages(time);
    }

    private void displayTimeAsImages(String formattedTime) {
        String[] timeParts = formattedTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        displayHourAsImage(ivHour1,hour / 10);
        displayHourAsImage(ivHour2,hour % 10);

        displayMinuteAsImage(ivMinute1,minute / 10);
        displayMinuteAsImage(ivMinute2,minute % 10);
    }

    private void displayHourAsImage(ImageView imageView, int digit) {
        int resourceId = getResources().getIdentifier("hour_" + digit, "drawable",mContext.getPackageName());
        imageView.setImageResource(resourceId);
    }

    private void displayMinuteAsImage(ImageView imageView, int digit) {
        int resourceId = getResources().getIdentifier("minute_" + digit, "drawable",mContext.getPackageName());
        imageView.setImageResource(resourceId);
    }

}