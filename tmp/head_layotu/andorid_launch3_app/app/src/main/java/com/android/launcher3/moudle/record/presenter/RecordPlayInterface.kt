package com.android.launcher3.moudle.record.presenter

interface RecordPlayInterface {

    /**
     * 播放进度回调
     * @param position 进度百分比
     * @param time     播放时间
     */
    fun onProgressListener(position: Int, time: Int)

    /**
     * 录音文件出错
     */
    fun onRecordError()
}