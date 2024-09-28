package com.android.launcher3.common.db.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.launcher3.common.db.alarm.AlarmDao;
import com.android.launcher3.common.db.alarm.AlarmModel;
import com.android.launcher3.common.db.pullwidget.WidgetBean;
import com.android.launcher3.common.db.pullwidget.WidgetDao;


@Database(entities = {AlarmModel.class, WidgetBean.class}, version = 1, exportSchema = false)//关联数据库
public abstract class LauncherDatabase extends RoomDatabase {
    //用户只需要操作DAO
    public abstract AlarmDao get_alarm_dao();

    public abstract WidgetDao getWidgetDao();

    //单例模式
    public static LauncherDatabase launcherDatabase;

    public static LauncherDatabase getInstance(Context context) {
        if (launcherDatabase == null) {
            launcherDatabase = Room.databaseBuilder(context.getApplicationContext(), LauncherDatabase.class, "launcher_db")
//                    .allowMainThreadQueries() //主线程也能操作数据库 只能测试用
                    .build();
        }
        return launcherDatabase;
    }

}
