package com.android.launcher3.moudle.record.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.moudle.notification.util.TimeUtils
import com.android.launcher3.moudle.record.utils.FileUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Author : yanyong
 * Date : 2024/6/14
 * Details : 录音管理类
 */
class AudioManagerXM {

    private val TAG: String = "AudioManager"

    // 音频输入-麦克风
    private val AUDIO_INPUT = MediaRecorder.AudioSource.MIC

    /**
     * 采用频率
     * 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
     * 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
     */
    private val AUDIO_SAMPLE_RATE = 16000

    // 声道 单声道  CHANNEL_IN_STEREO  CHANNEL_IN_MONO
    private val AUDIO_CHANNEL: Int = AudioFormat.CHANNEL_IN_MONO

    // 编码 AudioFormat.ENCODING_PCM_16BIT AudioFormat.ENCODING_PCM_8BIT
    private val AUDIO_ENCODING: Int = AudioFormat.ENCODING_PCM_16BIT

    // 缓冲区字节大小
    private var mMinBufferSize = 0

    // 录音对象
    private var mAudioRecord: AudioRecord? = null

    // 录音状态
    private var mStatus: Status = Status.STATUS_NO_READY

    // 文件名
    private var mFileName: String? = null

    // 录音文件 /storage/emulated/0/pauseRecordDemo/wav/xxxxx.wav
    private val mFileNames: MutableList<String> = ArrayList()

    // 录音文件全路径
    var mSaveFile: String = ""

    companion object {
        val Instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioManagerXM()
        }
    }

    /**
     * 使用默认参数录音创建录音对象
     */
    fun createAudio() {
        // 获得缓冲区字节大小
        mMinBufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING)
        LogUtil.i(TAG, "createAudio: mMinBufferSize $mMinBufferSize", LogUtil.TYPE_RELEASE)
        mAudioRecord = AudioRecord(
            AUDIO_INPUT,
            AUDIO_SAMPLE_RATE,
            AUDIO_CHANNEL,
            AUDIO_ENCODING,
            mMinBufferSize)
        this.mFileName = TimeUtils.getCurTime()
    }

    fun createAudio(fileName: String?, audioSource: Int) {
        mMinBufferSize = AudioRecord.getMinBufferSize(
            AUDIO_SAMPLE_RATE,
            AUDIO_CHANNEL, AUDIO_CHANNEL
        )
        mAudioRecord = AudioRecord(audioSource, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, mMinBufferSize)
        this.mFileName = fileName
    }

    fun createAudio(
        fileName: String?,
        audioSource: Int,
        sampleRateInHz: Int,
        channelConfig: Int,
        audioFormat: Int
    ) {
        mMinBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, channelConfig)
        mAudioRecord = AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, mMinBufferSize)
        this.mFileName = fileName
    }

    /**
     * 开始录音
     */
    fun startRecord() {
        LogUtil.i(TAG, "startRecord: status $mStatus fileName $mFileName", LogUtil.TYPE_RELEASE)
        check(mStatus != Status.STATUS_START) { "正在录音" }
        LogUtil.d(TAG, "startRecord: " + mAudioRecord!!.state, LogUtil.TYPE_DEBUG)
        mAudioRecord!!.startRecording()
        Thread { writeDataTOFile() }.start()
    }

    /**
     * 暂停录音
     */
    fun pauseRecord() {
        LogUtil.d(TAG, "pauseRecord: ", LogUtil.TYPE_RELEASE)
        mStatus = if (mStatus != Status.STATUS_START) {
            throw IllegalStateException("没有在录音")
        } else {
            mAudioRecord!!.stop()
            Status.STATUS_PAUSE
        }
        setListener()
    }

    /**
     * 停止录音
     */
    fun stopRecord() {
        LogUtil.d(TAG, "stopRecord: ", LogUtil.TYPE_RELEASE)
        check(!(mStatus == Status.STATUS_NO_READY || mStatus == Status.STATUS_READY)) { "录音尚未开始" }
        mAudioRecord!!.stop()
        mStatus = Status.STATUS_STOP
        setListener()
        mSaveFile = FileUtil.getWavFileAbsolutePath(mFileName)
        if (mRecordStreamListener != null) {
            mFileName?.let { mRecordStreamListener!!.saveRecordFileListener(it) }
        }
        release()
    }

    fun setListener() {
        if (mRecordStreamListener != null) {
            mRecordStreamListener!!.recordStateListener(mStatus)
        }
    }

    /**
     * 释放资源
     */
    private fun release() {
        LogUtil.d(TAG, "release: ", LogUtil.TYPE_RELEASE)
        try {
            if (mFileNames.size > 0) {
                val filePaths: MutableList<String> = ArrayList()
                for (fileName in mFileNames) {
                    filePaths.add(FileUtil.getPcmFileAbsolutePath(fileName))
                }
                mFileNames.clear()
                //将多个pcm文件转化为wav文件
                mergePCMFilesToWAVFile(filePaths)
            }
        } catch (e: IllegalStateException) {
            throw IllegalStateException(e.message)
        }
        if (mAudioRecord != null) {
            mAudioRecord!!.release()
            mAudioRecord = null
        }
        mStatus = Status.STATUS_NO_READY
        setListener()
    }

    /**
     * 取消录音
     */
    fun cancel() {
        mFileNames.clear()
        mFileName = null
        if (mAudioRecord != null) {
            mAudioRecord!!.release()
            mAudioRecord = null
        }
        mStatus = Status.STATUS_NO_READY
        setListener()
    }

    /**
     * 将音频信息写入文件
     */
    private fun writeDataTOFile() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        val audiodata = ByteArray(mMinBufferSize)
        var fos: FileOutputStream? = null
        var readsize = 0
        try {
            var currentFileName = mFileName
            if (mStatus == Status.STATUS_PAUSE) {
                //假如是暂停录音 将文件名后面加个数字,防止重名文件内容被覆盖
                currentFileName += mFileNames.size
            }
            if (currentFileName != null) {
                mFileNames.add(currentFileName)
            }
            val file: File = File(FileUtil.getPcmFileAbsolutePath(currentFileName))
            if (file.exists()) {
                file.delete()
            }
            fos = FileOutputStream(file) // 建立一个可存取字节的文件
        } catch (e: IllegalStateException) {
            LogUtil.e(TAG, e.message, LogUtil.TYPE_RELEASE)
            throw IllegalStateException(e.message)
        } catch (e: FileNotFoundException) {
            LogUtil.e(TAG, e.message, LogUtil.TYPE_RELEASE)
        }
        //将录音状态设置成正在录音状态
        mStatus = Status.STATUS_START
        setListener()
        while (mStatus == Status.STATUS_START) {
            readsize = mAudioRecord!!.read(audiodata, 0, mMinBufferSize)
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                try {
                    fos.write(audiodata)
                } catch (e: IOException) {
                    LogUtil.e(TAG, e.message, LogUtil.TYPE_RELEASE)
                }
            }
        }
        try {
            fos?.close()
        } catch (e: IOException) {
            LogUtil.e(TAG, "writeDataTOFile: IOException " + e.message, LogUtil.TYPE_RELEASE)
        }
    }

    /**
     * 将pcm合并成wav
     *
     * @param filePaths
     */
    private fun mergePCMFilesToWAVFile(filePaths: List<String>) {
        Thread {
            if (!PcmToWav.mergePCMFilesToWAVFile(filePaths, mSaveFile)) {
                //操作失败
                LogUtil.e(TAG, "mergePCMFilesToWAVFile fail", LogUtil.TYPE_RELEASE)
                throw IllegalStateException("mergePCMFilesToWAVFile fail")
            }
            mFileName = null
        }.start()
    }

    /**
     * 将单个pcm文件转化为wav文件
     */
    private fun makePCMFileToWAVFile() {
        Thread {
            if (!PcmToWav.makePCMFileToWAVFile(
                    FileUtil.getPcmFileAbsolutePath(mFileName),
                    FileUtil.getWavFileAbsolutePath(mFileName),
                    true
                )
            ) {
                //操作失败
                LogUtil.e(TAG, "makePCMFileToWAVFile fail", LogUtil.TYPE_RELEASE)
                throw IllegalStateException("makePCMFileToWAVFile fail")
            }
            mFileName = null
        }.start()
    }

    /**
     * 获取录音对象的状态
     *
     * @return
     */
    fun getStatus(): Status {
        return mStatus
    }

    /**
     * 获取本次录音文件的个数
     *
     * @return
     */
    fun getPcmFilesCount(): Int {
        return mFileNames.size
    }

    private var mRecordStreamListener: RecordStreamListener? = null

    fun setRecordStreamListener(listener: RecordStreamListener) {
        mRecordStreamListener = listener
    }

    /**
     * 录音对象的状态
     */
    enum class Status {
        STATUS_NO_READY,    // 未开始
        STATUS_READY,       // 预备
        STATUS_START,       // 录音
        STATUS_PAUSE,       // 暂停
        STATUS_STOP         // 停止
    }
}