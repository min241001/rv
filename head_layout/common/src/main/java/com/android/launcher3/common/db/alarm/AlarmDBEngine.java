package com.android.launcher3.common.db.alarm;

import android.content.Context;

import com.android.launcher3.common.db.database.LauncherDatabase;

import java.util.List;

public class AlarmDBEngine {
    private static final String TAG = "AlarmDBEngine";

    private AlarmDao alarmDao;

    public static AlarmDBEngine alarmDBEngine = null;

    public static AlarmDBEngine getInstance(Context ctx) {
        if (alarmDBEngine == null) {
            alarmDBEngine = new AlarmDBEngine(ctx);
        }
        return alarmDBEngine;
    }

    public AlarmDBEngine(Context context) {

        LauncherDatabase studentDatabase = LauncherDatabase.getInstance(context);
        alarmDao = studentDatabase.get_alarm_dao();

    }

    //插入
    public void insert_alarm(AlarmModel... models) {
        alarmDao.insert_alarm(models);
    }

    //更新
    public void update_alarm(AlarmModel model) {
        alarmDao.update_alarm(model);
    }

    //条件删除
    public void delete_alarm(int id) {
        alarmDao.delete_alarm(id);
    }

    //全部查询
    public List<AlarmModel> quary_all_alarm() {
        return alarmDao.get_all_alarm();
    }

    public AlarmModel quary_alarm_id(int id) {
        return alarmDao.get_alarm_id(id);
    }
}

