package com.android.launcher3.common.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: shensl
 * @Description：线程池工具类
 * @CreateDate：2023/12/9 10:05
 * @UpdateUser: shensl
 */
public class ThreadPoolUtils {
    private volatile static ExecutorService executorService;

    private ThreadPoolUtils() {
    }
    public static ExecutorService getExecutorService() {
        if (null == executorService) {
            synchronized (ThreadPoolUtils.class) {
                if (null == executorService) {
                    ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue<>(20);
                    executorService = new ThreadPoolExecutor(3,7,10, TimeUnit.MINUTES,arrayBlockingQueue);
                }
            }
        }
        return executorService;
    }

    public static synchronized void shutdown() {
        if (executorService != null) {
            try {
                executorService.shutdownNow();
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                executorService = null;
            }
        }
    }
}
