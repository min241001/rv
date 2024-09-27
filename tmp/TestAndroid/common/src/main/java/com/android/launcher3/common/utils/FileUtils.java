package com.android.launcher3.common.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * @Author: shensl
 * @Description：文件工具
 * @CreateDate：2023/12/9 09:35
 * @UpdateUser: shensl
 */
public class FileUtils {

    /**
     * 创建目录
     *
     * @param directoryPath 文件目录
     * @return
     */
    public static File createDirectory(String directoryPath) {
        File directory = new File(Environment.getExternalStorageDirectory(), directoryPath);

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                return directory;
            } else {
                return null;
            }
        } else {
            return directory;
        }
    }

    /**
     * 创建文件
     *
     * @param directoryPath 文件目录
     * @param fileName      文件名称
     * @return
     */
    public static File createFile(String directoryPath, String fileName) {
        File directory = createDirectory(directoryPath);
        if (directory != null) {
            File file = new File(directory, fileName);

            try {
                if (file != null && file.isFile()) {
                    file.delete();
                }
                if (file.createNewFile()) {
                    return file;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}
