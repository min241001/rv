package com.android.launcher3.common.utils;

import com.android.launcher3.common.CommonApp;

import java.util.Random;

public class StringUtils {
    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断字符串是否不为空
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }


    /**
     * 将字符串首字符变小写
     * @param str
     * @return
     */
    public static String toFirstLowerCase(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 将字符串首字符变大写
     * @param str
     * @return
     */
    public static String toFirstUpperCase(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 随机生成字符字符串
     * @param num
     * @return
     */
    public static String randomString(int num) {
        // String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for(int i = 0 ; i < num; ++i){
            int number = random.nextInt(52);//[0,62)

            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 去除最后第一个字符
     * @param str
     * @return
     */
    public static String abandonFirstChar(String str) {
        if (StringUtils.isBlank(str))
            return null;

        return str.substring(1, str.length());
    }

    /**
     * 随机生成数字字符串
     * @param num
     * @return
     */
    public static String randomNum(int num) {
        String str = "1234567890";
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0 ; i < num; i++){
            int number = random.nextInt(10);//[0,62)
            stringBuffer.append(str.charAt(number));
        }
        return stringBuffer.toString();
    }

    /**
     * 判断是否为整数
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        try {
            // 判断传进来的字符串是否为空
            if (isNotBlank(str)) {
                Integer.parseInt(str);
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为doublel类型
     * @param str
     * @return
     */
    public static boolean isDouble(String str) {
        try {
            // 判断传进来的字符串是否为空
            if (isNotBlank(str)) {
                Double.parseDouble(str);
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * 去除最后一个字符
     * @param str
     * @return
     */
    public static String abandonLastChar(String str) {
        if (StringUtils.isBlank(str))
            return null;

        return str.substring(0, str.length() - 1);
    }

    /**
     * 判断字符串是否是人名币金额
     * @param str
     * @return
     */
    public static boolean isCNYNumber(String str){
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false){
            return false;
        } else{
            return true;
        }
    }

    /**
     * 生成
     * @param startTime
     * @return
     */
    public static String useTime(Long startTime) {
        return msecToTime((int)(System.currentTimeMillis() - startTime));
    }

    public static String msecToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        int millisecond = 0;
        if (time <= 0)
            return "00:00:00.000";
        else {
            second = time / 1000;
            minute = second / 60;
            millisecond = time % 1000;
            if (second < 60) {
                timeStr = "00:00:" + unitFormat(second) + "." + unitFormat2(millisecond);
            } else if (minute < 60) {
                second = second % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second) + "." + unitFormat2(millisecond);
            } else {// 数字>=3600 000的时候
                hour = minute / 60;
                minute = minute % 60;
                second = second - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second) + "."
                        + unitFormat2(millisecond);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {// 时分秒的格式转换
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String unitFormat2(int i) {// 毫秒的格式转换
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "00" + Integer.toString(i);
        else if (i >= 10 && i < 100) {
            retStr = "0" + Integer.toString(i);
        } else
            retStr = "" + i;
        return retStr;
    }

    public static String getStrById(int id) {
        try {
            return CommonApp.getInstance().getString(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取字符串
     *
     * @param strId 字符串id
     * @return
     */
    public static String getStr(int strId) {
        if (strId == -1 || strId == 0) {
            return "";
        }
        try {
            return CommonApp.getInstance().getString(strId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
