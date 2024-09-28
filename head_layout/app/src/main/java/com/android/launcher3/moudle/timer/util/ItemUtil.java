package com.android.launcher3.moudle.timer.util;



/**
 * * Author : fangzheng
 * Date : 2024/9/24
 * Details : 总时间，当前时间，状态
 */

public class ItemUtil {

    private String sum_time;
    private String current_time;
    private int drawable;

    public ItemUtil(String sum_time,String current_time,int drawable){
        this.sum_time=sum_time;
        this.current_time=current_time;
        this.drawable=drawable;
    }

    public String getSum_time(){
        return  sum_time;
    }

    public  String getCurrent_time(){
        return  current_time;
    }

    public int getDrawable(){
        return drawable;
    }

}
