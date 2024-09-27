package com.android.launcher3.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    // 格式(HH:mm)
    public static Date getDate(String currentTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date nowDate = null;
        try {
            nowDate = simpleDateFormat.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return nowDate;
    }

    //获取当前时间 格式(HH:mm)
    public static String getCurrentTime(){
        Calendar calendar1 = Calendar.getInstance();
        int hourOfDay = calendar1.get(Calendar.HOUR_OF_DAY);
        int minute = calendar1.get(Calendar.MINUTE);
        return hourOfDay + ":" + minute;
    }

    /**
     * 时间转换
     * @param time 当前时间
     * @param startTime 当前时间格式
     * @param endTime 需要转换的时间格式
     * @return 返回指定格式时间
     */
    private static String TimeCycle(String time, String startTime, String endTime) {
        DateFormat formatS = new SimpleDateFormat(startTime, Locale.getDefault());
        DateFormat formatE = new SimpleDateFormat(endTime, Locale.getDefault());
        try {
            Date date24 = formatS.parse(time);
            assert date24 != null;
            return formatE.format(date24);
        } catch (Exception e) {
            e.printStackTrace();
            return time;
        }
    }

    /**
     * 24小时制转12小时
     * @param time24
     * @return
     */
    public static String convert24To12(String time24) {
        return TimeCycle(time24, "HH:mm", "hh:mm");
    }

    /**
     * 12小时制转24小时
     * @param time12
     * @return
     */
    public static String convert12To24(String time12) {
        return TimeCycle(time12, "ahh:mm", "HH:mm");
    }

    /**
     * 24小时制获取上午还是下午
     * @param time24
     * @return
     */
    public static String getTodayFlag(String time24) {
        return TimeCycle(time24, "HH:mm", "a");
    }

    /**
     * 24小时制转12小时
     * @param time24
     * @return
     */
    public static String get12HourTime(String time24) {
        return TimeCycle(time24, "HH:mm", "ahh:mm");
    }

    public static String GetWeek() {
        String week = android.text.format.DateFormat.format("EEEE", System.currentTimeMillis()).toString();
        return week;
    }

    public static String GetMonthAndDay() {
        String date = android.text.format.DateFormat.format("MM月dd日 ", System.currentTimeMillis()).toString();
        return date;
    }

    public static String GetTime() {
        //String str = DateFormat.format("yyyy-MM-dd EEEE HH:mm:ss", System.currentTimeMillis());
        String time = android.text.format.DateFormat.format("HH:mm", System.currentTimeMillis()).toString();
        return time;
    }
    public static String GetDateAndWeek(){
        String date = android.text.format.DateFormat.format("MM月dd日 EEEE", System.currentTimeMillis()).toString();
        return date;
    }

}
