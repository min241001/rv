package com.android.launcher3.moudle.record.audio

interface RecordStreamListener {

    fun saveRecordFileListener(fileName: String)

    fun recordStateListener(status: AudioManagerXM.Status)
}