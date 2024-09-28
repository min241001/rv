package com.android.launcher3.moudle.record.audio

import com.android.launcher3.common.utils.LogUtil
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author : yanyong
 * Date : 2024/6/14
 * Details : 录音文件转换pcm文件不可播放，需要转换成wav文件
 */
class PcmToWav {

    /**
     * 合并多个pcm文件为一个wav文件
     *
     * @param filePathList    pcm文件路径集合
     * @param destinationPath 目标wav文件路径
     * @return true|false
     */
    companion object {
        private val TAG: String = "PcmToWav"

        @JvmStatic
        fun mergePCMFilesToWAVFile(
            filePathList: List<String?>,
            destinationPath: String?
        ): Boolean {
            val file = arrayOfNulls<File>(filePathList.size)
            var buffer: ByteArray? = null
            var TOTAL_SIZE = 0
            val fileNum = filePathList.size
            for (i in 0 until fileNum) {
                file[i] = File(filePathList[i])
                TOTAL_SIZE += file[i]!!.length().toInt()
            }

            // 填入参数，比特率等等。这里用的是16位单声道 8000 hz
            val header = WaveHeader()
            // 长度字段 = 内容的大小（TOTAL_SIZE) +
            // 头部字段的大小(不包括前面4字节的标识符RIFF以及fileLength本身的4字节)
            header.fileLength = TOTAL_SIZE + (44 - 8)
            header.FmtHdrLeth = 16
            header.BitsPerSample = 16
            header.Channels = 2
            header.FormatTag = 0x0001
            header.SamplesPerSec = 8000
            header.BlockAlign = (header.Channels * header.BitsPerSample / 8).toShort()
            header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec
            header.DataHdrLeth = TOTAL_SIZE
            var h: ByteArray? = null
            h = try {
                header.getHeader()
            } catch (e1: IOException) {
                LogUtil.e(TAG, e1.message, LogUtil.TYPE_RELEASE)
                return false
            }
            if (h!!.size != 44) // WAV标准，头部应该是44字节,如果不是44个字节则不进行转换文件
                return false

            //先删除目标文件
            val destfile = File(destinationPath)
            if (destfile.exists()) destfile.delete()

            //合成所有的pcm文件的数据，写到目标文件
            try {
                buffer = ByteArray(1024 * 4) // Length of All Files, Total Size
                var inStream: InputStream? = null
                var ouStream: OutputStream? = null
                ouStream = BufferedOutputStream(
                    FileOutputStream(
                        destinationPath
                    )
                )
                ouStream.write(h, 0, h.size)
                for (j in 0 until fileNum) {
                    inStream = BufferedInputStream(FileInputStream(file[j]))
                    var size = inStream.read(buffer)
                    while (size != -1) {
                        ouStream.write(buffer)
                        size = inStream.read(buffer)
                    }
                    inStream.close()
                }
                ouStream.close()
            } catch (e: FileNotFoundException) {
                LogUtil.e(TAG, e.message, LogUtil.TYPE_RELEASE)
                return false
            } catch (ioe: IOException) {
                LogUtil.e(TAG, ioe.message, LogUtil.TYPE_RELEASE)
                return false
            }
            clearFiles(filePathList)
            LogUtil.i(
                TAG,
                "mergePCMFilesToWAVFile  success!" + SimpleDateFormat("yyyy-MM-dd hh:mm").format(
                    Date()
                ), LogUtil.TYPE_DEBUG)
            return true
        }


        /**
         * 将一个pcm文件转化为wav文件
         *
         * @param pcmPath         pcm文件路径
         * @param destinationPath 目标文件路径(wav)
         * @param deletePcmFile   是否删除源文件
         * @return
         */
        fun makePCMFileToWAVFile(
            pcmPath: String?,
            destinationPath: String?,
            deletePcmFile: Boolean
        ): Boolean {
            var buffer: ByteArray? = null
            var TOTAL_SIZE = 0
            val file = File(pcmPath)
            if (!file.exists()) {
                return false
            }
            TOTAL_SIZE = file.length().toInt()
            // 填入参数，比特率等等。这里用的是16位单声道 8000 hz
            val header = WaveHeader()
            // 长度字段 = 内容的大小（TOTAL_SIZE) +
            // 头部字段的大小(不包括前面4字节的标识符RIFF以及fileLength本身的4字节)
            header.fileLength = TOTAL_SIZE + (44 - 8)
            header.FmtHdrLeth = 16
            header.BitsPerSample = 16
            header.Channels = 2
            header.FormatTag = 0x0001
            header.SamplesPerSec = 8000
            header.BlockAlign = (header.Channels * header.BitsPerSample / 8).toShort()
            header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec
            header.DataHdrLeth = TOTAL_SIZE
            var h: ByteArray? = null
            h = try {
                header.getHeader()
            } catch (e1: IOException) {
                LogUtil.e(TAG, e1.message, LogUtil.TYPE_RELEASE)
                return false
            }
            if (h!!.size != 44) // WAV标准，头部应该是44字节,如果不是44个字节则不进行转换文件
                return false

            //先删除目标文件
            val destfile = File(destinationPath)
            if (destfile.exists()) destfile.delete()

            //合成所有的pcm文件的数据，写到目标文件
            try {
                buffer = ByteArray(1024 * 4) // Length of All Files, Total Size
                var inStream: InputStream? = null
                var ouStream: OutputStream? = null
                ouStream = BufferedOutputStream(
                    FileOutputStream(
                        destinationPath
                    )
                )
                ouStream.write(h, 0, h.size)
                inStream = BufferedInputStream(FileInputStream(file))
                var size = inStream.read(buffer)
                while (size != -1) {
                    ouStream.write(buffer)
                    size = inStream.read(buffer)
                }
                inStream.close()
                ouStream.close()
            } catch (e: FileNotFoundException) {
                LogUtil.e(TAG, e.message, LogUtil.TYPE_RELEASE)
                return false
            } catch (ioe: IOException) {
                LogUtil.e(TAG, ioe.message, LogUtil.TYPE_RELEASE)
                return false
            }
            if (deletePcmFile) {
                file.delete()
            }
            LogUtil.i(
                TAG,
                "makePCMFileToWAVFile  success!" + SimpleDateFormat("yyyy-MM-dd hh:mm").format(
                    Date()
                ), LogUtil.TYPE_RELEASE
            )
            return true
        }

        /**
         * 清除文件
         *
         * @param filePathList
         */
        private fun clearFiles(filePathList: List<String?>) {
            for (i in filePathList.indices) {
                val file = File(filePathList[i])
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }
}