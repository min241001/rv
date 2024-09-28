package com.android.launcher3.moudle.shortcut.util;

import android.app.ActivityManager;
import android.content.Context;

import com.android.launcher3.App;

/**
 * Author : yanyong
 * Date : 2024/7/6
 * Details : 内存管理
 */
public class MemoryManager {

    private static MemoryManager Instance;

    private ActivityManager.MemoryInfo mMemoryInfo;

    public MemoryManager() {
        ActivityManager activityManager = (ActivityManager) App.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        //获得MemoryInfo对象
        mMemoryInfo = new ActivityManager.MemoryInfo();
        //获得系统可用内存，保存在MemoryInfo对象上
        activityManager.getMemoryInfo(mMemoryInfo);
    }

    public static MemoryManager getInstance() {
        if (Instance == null) {
            synchronized (MemoryManager.class) {
                if (Instance == null) {
                    Instance = new MemoryManager();
                }
            }
        }
        return Instance;
    }

    /**
     * 获取可用内存
     *
     * @return 字节数
     */
    public long getAvailMemory() {
        if (mMemoryInfo == null) {
            return 0;
        }
        return mMemoryInfo.availMem;
    }

    /**
     * 获取总内存
     *
     * @return 转成GB的内存
     */
    public String getTotalMemory() {
        if (mMemoryInfo == null) {
            return "8 GB";
        }
        return BaseUtil.memoryConvert(mMemoryInfo.totalMem);
    }

    /**
     * 计算清理内存后的空间
     *
     * @param size 清理前内存
     * @return 清理后的空间
     */
    public String getCleanSpace(long size) {
        if (size - getAvailMemory() < 1024 * 1024 * 2) {
            return "";
        }
        return BaseUtil.memoryConvert(size - getAvailMemory());
    }

}
