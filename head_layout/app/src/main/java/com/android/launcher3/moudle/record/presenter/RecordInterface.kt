package com.android.launcher3.moudle.record.presenter

interface RecordInterface {

    /**
     * 开始计时回调
     */
    fun startTimingListener()

    /**
     * 保存文件回调
     * @param save 是否保存成功
     */
    fun saveFileListener(save: Boolean)

}