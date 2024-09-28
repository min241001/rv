package com.android.launcher3.common.db.pullwidget;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WidgetDao {

    /**
     * 获取所有下拉控件数据
     *
     * @return 数据集合
     */
    @Query("SELECT * FROM widget")
    List<WidgetBean> getAllWidgetBean();

    /**
     * 插入一条数据
     *
     * @param bean bean
     */
    @Insert
    void insertWidgetBean(WidgetBean bean);

    /**
     * 插入多条数据
     *
     * @param list list
     */
    @Insert
    void insertWidgetBean(List<WidgetBean> list);

    /**
     * 删除单挑数据根据控件对象
     *
     * @param bean bean
     */
    @Delete
    void deleteWidgetBean(WidgetBean bean);

    /**
     * 删除所有数据
     */
    @Query("DELETE FROM widget")
    void deleteAllWidgetBean();

    /**
     * 跟新一条数据根据控件数据
     *
     * @param bean bean
     */
    @Update
    void updateWidgetBean(WidgetBean bean);

    /**
     * 删除一条数据根据控件id
     *
     * @param id 控件id
     */
    @Query("DELETE FROM widget WHERE id = :id")
    void deleteWidgetBeanById(int id);

    /**
     * 删除一条数据，根据控件类型
     *
     * @param type 控件类型
     */
    @Query("DELETE FROM widget WHERE type = :type")
    int deleteWidgetBeanByType(int type);

    /**
     * 根据添加状态获取所有该状态的数据
     *
     * @param added 天剑状态
     * @return 集合
     */
    @Query("SELECT * FROM widget WHERE added = :added")
    List<WidgetBean> getWidgetBeanByAdded(boolean added);

    /**
     * 根据控件类型获取对应数据
     *
     * @param type 控件类型
     * @return 控件对象
     */
    @Query("SELECT * FROM widget WHERE type = :type")
    WidgetBean getWidgetBeanByType(int type);

    /**
     * 根据控件类型更新数据根据的选中状态，
     *
     * @param type     控件类型
     * @param isSelect 选中状态
     * @return 修改状态
     */
    @Query("UPDATE widget SET `select` = :isSelect, added = :isAdded where type = :type")
    int updateWidgetBeanByType(int type, boolean isSelect, boolean isAdded);

    /**
     * 根据控件类型更新数据根据的选中状态，
     *
     * @param type     控件类型
     * @param isSelect 选中状态
     * @return 修改状态
     */
    @Query("UPDATE widget SET `select` = :isSelect where type = :type")
    int updateWidgetBeanByType(int type, boolean isSelect);
}
