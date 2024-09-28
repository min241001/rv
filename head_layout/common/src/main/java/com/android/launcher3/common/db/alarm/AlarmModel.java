package com.android.launcher3.common.db.alarm;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class AlarmModel implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String time;   // 时间  (时:分)
    private String weekDay; //重复日期(1,2,3,4,5,6,7)
    private String tag; //标签
    private boolean laterAlert = true; //稍后提醒
    private int state; //是否开启(1为开启，0为关闭，2为默认闹钟)

    public AlarmModel(String time, String weekDay, String tag, boolean laterAlert, int state) {
        this.time = time;
        this.weekDay = weekDay;
        this.tag = tag;
        this.laterAlert = laterAlert;
        this.state = state;
    }

//    public AlarmModel(String time, String weekDay, int state) {
//        this.time = time;
//        this.weekDay = weekDay;
//        this.state = state;
//    }

//    public AlarmModel(String time, String weekDay, int state, String tag, boolean laterAlert) {
//        this.time = time;
//        this.weekDay = weekDay;
//        this.state = state;
//        this.tag = tag;
//        this.laterAlert = laterAlert;
//    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setLaterAlert(boolean laterAlert) {
        this.laterAlert = laterAlert;
    }

    public String getTag() {
        return tag;
    }

    public boolean isLaterAlert() {
        return laterAlert;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public int getState() {
        return state;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "AlarmModel{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", weekDay='" + weekDay + '\'' +
                ", tag='" + tag + '\'' +
                ", laterAlert=" + laterAlert +
                ", state=" + state +
                '}';
    }
}
