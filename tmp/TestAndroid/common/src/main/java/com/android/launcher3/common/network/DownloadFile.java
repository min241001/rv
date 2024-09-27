package com.android.launcher3.common.network;

import com.android.launcher3.common.network.listener.DownloadListener;
import com.android.launcher3.common.utils.FileUtils;
import com.android.launcher3.common.utils.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;


public class DownloadFile {

    private static final String TAG = DownloadFile.class.getSimpleName() + "--->>>";

    /**
     * 下载文件到本地磁盘
     *
     * @param body
     * @param directoryPath 文件目录
     * @param fileName      文件名称
     * @param listener
     * @return
     */
    public static boolean writeResponseBodyToDisk(ResponseBody body, String directoryPath, String fileName, DownloadListener listener) {
        // 如果目录不存在，则创建目录
        File dirFile = FileUtils.createDirectory(directoryPath);
        if (dirFile == null || !dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }

        // 创建文件对象
        File file = FileUtils.createFile(directoryPath, fileName);
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }

        try {
            LogUtil.d(TAG + "文件路径 = " + file.getAbsolutePath(), LogUtil.TYPE_RELEASE);
            // 创建文件输出流
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            // 创建缓冲输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            //响应体的字节流为空判断处理
            if (body.byteStream() == null){
                return false;
            }
            // 读取响应体的字节流
            InputStream inputStream = body.byteStream();
            // 缓冲区大小
            byte[] buffer = new byte[4096];
            // 数据总大小
            long fileSize = body.contentLength();
            // 已下载数据大小
            long totalBytesRead = 0;
            // 读取长度
            int bytesRead = 0;
            // 下载进度
            int progress = 0;
            // 循环从输入流中读取数据，并写入输出流中
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                progress = (int) ((totalBytesRead * 100) / fileSize);
                if(listener!=null){
                    listener.onProgress(progress);
                }
                LogUtil.d(TAG + ",数据总大小 = " + fileSize + ",已下载数据大小 = " + totalBytesRead + ",下载进度 = " + progress, LogUtil.TYPE_RELEASE);
            }
            // 刷新缓冲输出流
            bufferedOutputStream.flush();
            // 关闭输出流和输入流
            fileOutputStream.close();
            bufferedOutputStream.close();
            inputStream.close();
            return totalBytesRead == fileSize;
        } catch (IOException e) {
            e.printStackTrace();
            if (file.isFile()) {
                file.delete();
            }
            return false;
        }
    }
}
