package com.android.launcher3.dial3;

import android.icu.util.Calendar;
import android.widget.ImageView;
import com.android.launcher3.base.BaseFragment;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Dial3Fragment extends BaseFragment {

    private ImageView iv_hour1;
    private ImageView iv_hour2;

    private ImageView iv_colon;

    private ImageView iv_minute1;
    private ImageView iv_minute2;


    @Override
    protected int getResourceId() {
        return R.layout.dial3_fragment_main;
    }

    @Override
    protected void initView() {
        iv_hour1 = findViewById(R.id.iv_hour1);
        iv_hour2 = findViewById(R.id.iv_hour2);
        iv_minute1 = findViewById(R.id.iv_minute1);
        iv_minute2 = findViewById(R.id.iv_minute2);
        iv_colon = findViewById(R.id.iv_colon);
        getDateTime();
    }

    @Override
    protected void initData() {
        super.initData();
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

        displayDigitAsImage(iv_hour1,hour / 10);
        displayDigitAsImage(iv_hour2,hour % 10);

        displayColonAsImage();

        displayDigitAsImage(iv_minute1,minute / 10);
        displayDigitAsImage(iv_minute2,minute % 10);
    }

    private void displayDigitAsImage(ImageView imageView, int digit) {
        int resourceId = getResources().getIdentifier("digit_" + digit, "drawable",mContext.getPackageName());
        imageView.setImageResource(resourceId);
    }

    private void displayColonAsImage() {
        iv_colon.setImageResource(R.drawable.colon);
    }
}