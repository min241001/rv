package com.android.launcher3.moudle.notification.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Author: shensl
 * @Description：时间工具类
 * @CreateDate：2024/1/9 14:02
 * @UpdateUser: shensl
 */
public class TimeUtils {

    private static final long TIME_SECOND = 60L;// 刚刚
    private static final long TIME_MINUTE = 60 * 60L;// 分钟前
    private static final long TIME_HOUR = 24 * 60 * 60L;// 小时前
    private static final long TIME_DAY = 365 * 24 * 60 * 60L;// 天前
    public static final String PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_YMDHM = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_YMDHMS_ = "yyyyMMddHHmmss";
    public static final String PATTERN_YMD = "yyyy-MM-dd";
    public static final String PATTERN_HMS = "HH:mm:ss";

    /**
     * 获取时间
     *
     * @param timeInMillis 历史时间，单位：毫秒
     * @return
     */
    public static String getTimeAgo(long timeInMillis) {
        // 当前时间
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        long diffInMillis = (currentTimeInMillis - timeInMillis) / 1000L;

        if (diffInMillis < TIME_SECOND) {
            return "刚刚";
        } else if (diffInMillis < TIME_MINUTE) {
            long minutes = diffInMillis / TIME_SECOND;
            return minutes + "分钟前";
        } else if (diffInMillis < TIME_HOUR) {
            long hours = diffInMillis / TIME_MINUTE;
            return hours + "小时前";
        } else if (diffInMillis < TIME_DAY) {
            long days = diffInMillis / TIME_HOUR;
            return days + "天前";
        } else {
            long years = diffInMillis / TIME_DAY;
            return years + "年前";
        }
    }

    public static String long2str(long time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date = new Date(time);
        return sdf.format(date);
    }

    public static String getRecordTime(long time) {
        StringBuilder sb = new StringBuilder();
        if (time < 10) {
            sb.append("00:0").append(time);
        } else if (time < 60) {
            sb.append("00:").append(time);
        } else {
            long minutes = time / 60;
            long seconds = time % 60;
            if (minutes < 10) {
                sb.append("0").append(minutes).append(":");
            } else {
                sb.append(minutes).append(":");
            }
            if (seconds < 10) {
                sb.append("0").append(seconds);
            } else {
                sb.append(seconds);
            }
        }
        return sb.toString();
    }

    public static String getCurTime() {
        return long2str(System.currentTimeMillis(), PATTERN_YMDHMS_);
    }

    @SuppressLint("SimpleDateFormat")
    public static long str2long(String pattern, String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(timeStr);
            if (date == null) {
                return -1;
            }
            long time = date.getTime();
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取当天上课禁用时间戳
     *
     * @param timeStr 上课禁用时间
     * @return long
     */
    public static long getClassBanTime(String timeStr) {
        String ymd = long2str(System.currentTimeMillis(), TimeUtils.PATTERN_YMD);
        return str2long(PATTERN_YMDHM, ymd + " " + timeStr);
    }
}
