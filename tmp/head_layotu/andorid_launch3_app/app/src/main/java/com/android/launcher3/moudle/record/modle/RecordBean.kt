package com.android.launcher3.moudle.record.modle

import java.io.Serializable

class RecordBean : Serializable {

    // 录音名称
    lateinit var name: String

    // 录音的时间
    var time: Long = 0

    // 录音的时长
    var duration: Long = 0

    // 录音文件路径
    lateinit var file: String

    // 是否被选中
    var checked: Boolean = false

    override fun toString(): String {
        return "RecordBean(name='$name', checked=$checked, time=$time, duration=$duration, file='$file')"
    }

}