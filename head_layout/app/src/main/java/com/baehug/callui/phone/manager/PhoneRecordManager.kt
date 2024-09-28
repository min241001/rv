package com.baehug.callui.phone.manager

import android.media.MediaRecorder
import android.widget.Toast
import com.android.launcher3.App
import com.android.launcher3.common.utils.ThreadPoolUtils
import com.android.launcher3.common.utils.ToastUtils
import com.baehug.callui.phone.utils.FileUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 电话录音管理类
 */
class PhoneRecordManager(private val number: String) {

    private val RECORD_DIR = "SuperCall"
    private val RECORD_FILE_TYPE = "record"
    private val RECORD_FILE_PREFIX = "通话录音"
    private val RECORD_FILE_SUFFIX = ".amr"

    var isRecording = false
    private var mFile: File? = null
    private var mediaRecorder: MediaRecorder? = null

    fun startRecord(): Boolean {
        return try {
            tryForthWayRecord()
            true
        } catch (e: Exception) {
            mFile?.delete()
            try {
                tryDefaultWayRecord()
                true
            } catch (e: Exception) {
                Toast.makeText(App.getInstance(), "录音开启失败，设备可能被占用", Toast.LENGTH_SHORT).show()
                mFile?.delete()
                isRecording = false
                false
            }
        }
    }

    fun stopRecord() {
        ThreadPoolUtils.getExecutorService().execute(Runnable {
            getMediaRecorder()?.also {
                try {
                    it.stop()
                    it.reset()
                    it.release()
                    destroyMediaRecorder()
                    isRecording = false
                    ToastUtils.show("录音文件保存在${mFile?.parent}目录下")
                } catch (e: Exception) {
                    getMediaRecorder()?.release()
                    destroyMediaRecorder()
                    isRecording = false
                }
            }
        })
    }

    private fun tryDefaultWayRecord() {
        mFile = generateRecordFile(number)
        mFile?.also {
            getMediaRecorder()?.apply {
                reset()
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setAudioChannels(2)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(it.absolutePath)
                prepare()
                start()
                isRecording = true
            }
        }
    }

    private fun tryForthWayRecord() {
        mFile = generateRecordFile(number)
        mFile?.also {
            getMediaRecorder()?.apply {
                reset()
                setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
                setAudioChannels(2)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(it.absolutePath)
                prepare()
                start()
                isRecording = true
            }
        }
    }

    private fun getMediaRecorder(): MediaRecorder? {
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()
        }
        return mediaRecorder
    }

    private fun destroyMediaRecorder() {
        mediaRecorder = null
    }

    private fun generateRecordFile(number: String): File? {
        val recordFilePath = getRecordFilePath() ?: return null
        val date = Date()
        return File(recordFilePath + File.separator +
                RECORD_FILE_PREFIX + number + "_"
                + SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE).format(date) + "_"
                + SimpleDateFormat("HHmmss", Locale.SIMPLIFIED_CHINESE).format(date) + RECORD_FILE_SUFFIX)
    }

    @Synchronized
    private fun getRecordFilePath(): String? {
        var recordDir = FileUtils.getExternalStorageDirFile()
        if (recordDir == null) {
            recordDir = FileUtils.getExternalFilesDirectory(App.getInstance(), RECORD_FILE_TYPE)
        }
        if (recordDir == null) {
            return ""
        }
        if (!recordDir.exists()) {
            recordDir.mkdirs()
        }
        val recordDirPath = recordDir.path + File.separator + RECORD_DIR
        val folder = File(recordDirPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return recordDirPath
    }

}