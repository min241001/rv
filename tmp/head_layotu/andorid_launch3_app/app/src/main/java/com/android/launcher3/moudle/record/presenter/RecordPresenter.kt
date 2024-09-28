package com.android.launcher3.moudle.record.presenter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.android.launcher3.common.base.BasePresenter
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.common.utils.SharedPreferencesUtils
import com.android.launcher3.common.utils.ThreadPoolUtils
import com.android.launcher3.moudle.record.audio.AudioManagerXM
import com.android.launcher3.moudle.record.audio.RecordStreamListener
import com.android.launcher3.moudle.record.base.RecordConstant.*
import com.android.launcher3.moudle.record.modle.RecordBean
import com.android.launcher3.moudle.record.utils.FileUtil
import com.android.launcher3.moudle.record.utils.JsonUtil

/**
 * Author : yanyong
 * Date : 2024/6/14
 * Details : 录音功能presenter AudioRecord方式
 */
class RecordPresenter : BasePresenter<RecordInterface>(), RecordStreamListener {

    private var voicePath: String = "" // 设置媒体文件的保存路径
    private var mFileName: String = ""
    private val mArr = arrayOf(":", "*", "_", "/", "\\", "%", "#", "~", "^", "|", "&", "：", "——", "～")
    
    //录音状态
    private var status: AudioManagerXM.Status = AudioManagerXM.Status.STATUS_NO_READY

    override fun attachView(view: RecordInterface?) {
        super.attachView(view)

    }

    override fun detachView() {
        super.detachView()
    }

    /**
     * 开始录音
     */
    fun startRecord() {
        try {
            val status = AudioManagerXM.Instance.getStatus()
            if (status == AudioManagerXM.Status.STATUS_NO_READY || status == AudioManagerXM.Status.STATUS_PAUSE) {
                AudioManagerXM.Instance.createAudio()
                AudioManagerXM.Instance.startRecord()
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, "startRecord: Exception " + e.message, LogUtil.TYPE_RELEASE)
        }
    }

    /**
     * 停止录音
     */
    fun stopRecord() {
        try {
            AudioManagerXM.Instance.stopRecord()
        } catch (e: Exception) {
            LogUtil.e(TAG, "stopRecord: Exception " + e.message, LogUtil.TYPE_RELEASE)
        }
    }

    /**
     * 取消录音
     */
    fun cancelRecord() {
        try {
            AudioManagerXM.Instance.cancel()
        } catch (e: Exception) {
            LogUtil.e(TAG, "cancelRecord: Exception " + e.message, LogUtil.TYPE_RELEASE)
        }
    }

    /**
     * 暂停或开始录音
     */
    fun recodeStartOrPause() {
        try {
            val status = AudioManagerXM.Instance.getStatus()
            LogUtil.i(TAG, "recodeStartOrPause: status $status", LogUtil.TYPE_RELEASE)
            if (status == AudioManagerXM.Status.STATUS_PAUSE) {
                AudioManagerXM.Instance.startRecord()
            } else if (status == AudioManagerXM.Status.STATUS_START) {
                AudioManagerXM.Instance.pauseRecord()
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, "recodeStartOrPause: Exception " + e.message, LogUtil.TYPE_RELEASE)
        }
    }

    /**
     * 获取文件名称
     */
    fun getFileName(): String {
        return mFileName
    }

    /**
     * 删除不需要保存的录音文件
     */
    fun deleteRecordFile() {
        ThreadPoolUtils.getExecutorService().execute {
            val delete = FileUtil.deleteFile(voicePath)
            LogUtil.i("saveRecordFile: delete $delete", LogUtil.TYPE_RELEASE)
        }
    }

    /**
     * 保存录音文件
     */
    fun saveRecordFile(context: Context, name: String, duration: Long) {
        val bean = RecordBean()
        bean.time = System.currentTimeMillis()
        bean.duration = duration

        LogUtil.i("saveRecordFile: name $name, mFileName $mFileName voicePath $voicePath", LogUtil.TYPE_RELEASE)
        if (TextUtils.isEmpty(name)) {
            if (TextUtils.isEmpty(mFileName)) {
                bean.name = voicePath
            } else {
                bean.name = mFileName
            }
            bean.file = voicePath
            voicePath = "";
            saveRecordData(context, bean)
        } else {
            if (mFileName == name) {
                bean.name = mFileName
                bean.file = voicePath
                voicePath = "";
                saveRecordData(context, bean)
            } else {
                bean.name = name
                bean.file = FileUtil.getWavFileAbsolutePath(name)
                // 修改录音文件名称
                ThreadPoolUtils.getExecutorService().execute {
                    val rename = FileUtil.fileRename(voicePath, bean.file)
                    LogUtil.i("saveRecordFile: rename $rename", LogUtil.TYPE_RELEASE)
                    if (!rename) {
                        bean.file = voicePath
                    }
                    voicePath = "";
                    saveRecordData(context, bean)
                }
            }
        }
        if (isViewAttached) {
            view.saveFileListener(true)
        }
    }

    /**
     * 搜索是否存在相同文件名称
     */
    fun searchFileName(context: Context, name: String): Boolean {
        val param = SharedPreferencesUtils.getParam(context, KEY_RECORD_DATA, "")
        LogUtil.i("covertName: param $param name $name", LogUtil.TYPE_RELEASE)
        val recordBeans = JsonUtil.json2list(param.toString())
        for (bean in recordBeans) {
            if (bean.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询名字中是否存在特殊字符
     */
    fun matchesSpecialCharacters(text: String): Boolean {
        for (s in mArr) {
            if (text.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保存录音数据到sp
     */
    private fun saveRecordData(context: Context, bean: RecordBean) {
        if (null == context || bean == null) {
            return
        }
        LogUtil.i("setRecordData: $bean", LogUtil.TYPE_RELEASE)
        val param = SharedPreferencesUtils.getParam(context, KEY_RECORD_DATA, "")
        val json2list = JsonUtil.json2list(param.toString()) as MutableList<RecordBean>
        json2list.add(bean)
        val list2json = JsonUtil.list2json(json2list)
        SharedPreferencesUtils.setParam(context, KEY_RECORD_DATA, list2json)
    }

    override fun saveRecordFileListener(fileName: String) {
        mFileName = fileName
        voicePath = FileUtil.getWavFileAbsolutePath(fileName)
        LogUtil.i(TAG, "saveRecordFileListener: fileName $fileName voicePath $voicePath", LogUtil.TYPE_RELEASE)
    }

    override fun recordStateListener(status: AudioManagerXM.Status) {
        LogUtil.i(TAG, "recordStateListener: status $status", LogUtil.TYPE_RELEASE)
        this.status = status
        if (status == AudioManagerXM.Status.STATUS_START) {
            if (isViewAttached) {
                view.startTimingListener()
            }
        }
    }

    fun getStatus() : AudioManagerXM.Status {
        return status
    }

    fun setRecordStreamListener() {
        AudioManagerXM.Instance.setRecordStreamListener(this)
    }
}