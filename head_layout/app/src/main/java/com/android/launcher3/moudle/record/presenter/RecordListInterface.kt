package com.android.launcher3.moudle.record.presenter

import com.android.launcher3.moudle.record.modle.RecordBean

interface RecordListInterface {

    /**
     * 刷新录音文件列表
     * @param list 录音数据集
     */
    fun onUpdateRecordList(list: List<RecordBean>)
}