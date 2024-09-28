package com.android.launcher3.common.db.alarm;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
    //增
    @Insert
    void insert_alarm(AlarmModel... models);

    //改
    @Update
    void update_alarm(AlarmModel model);

    //删
    @Query("DELETE FROM AlarmModel WHERE id = :id")
    void delete_alarm(int id);

    //查
    @Query("SELECT * FROM AlarmModel ORDER BY time ASC")
    List<AlarmModel> get_all_alarm();

    //查
    @Query("SELECT * FROM AlarmModel WHERE id = :id ORDER BY time ASC")
    AlarmModel get_alarm_id(int id);

}
