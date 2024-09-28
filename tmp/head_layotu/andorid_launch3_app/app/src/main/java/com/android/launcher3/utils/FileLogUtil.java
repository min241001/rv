package com.android.launcher3.utils;

import android.os.Environment;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.common.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: shensl
 * @Description：文件日志工具
 * @CreateDate：2024/2/6 11:04
 * @UpdateUser: shensl
 */
public class FileLogUtil {

    // 用于格式化日期,作为日志文件名的一部分
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    // debug模式下
    public static void debugMode(String msg) {
        if (BuildConfig.DEBUG) {
            saveInfoToFile(msg);
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    public static String saveInfoToFile(String msg) {
        StringBuffer sb = new StringBuffer();
        try {
            String date = formatter.format(new Date());
            sb.append("\r\n" + date + "\n");
            sb.append(msg + "\n");
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.flush();
            printWriter.close();
            String result = writer.toString();
            sb.append(result);
            String fileName = writeFile(sb.toString());
            return fileName;
        } catch (Exception e) {
            LogUtil.e("an error occured while writing file...\r\n", LogUtil.TYPE_RELEASE);
        }
        return null;
    }

    private static String writeFile(String sb) throws Exception {
        String time = formatter.format(new Date());
        String fileName = String.format("%s.txt", time);
        if (hasSdcard()) {
            String path = getGlobalpath();
            LogUtil.d("path = " + path, LogUtil.TYPE_DEBUG);
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            FileOutputStream fos = new FileOutputStream(path + fileName, true);
            fos.write(sb.getBytes());
            fos.flush();
            fos.close();
        }
        return fileName;
    }

    // 是否有sd卡
    private static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static String getGlobalpath() {
        return Environment.getExternalStorageDirectory() + File.separator + "Log" + File.separator;
    }

}
