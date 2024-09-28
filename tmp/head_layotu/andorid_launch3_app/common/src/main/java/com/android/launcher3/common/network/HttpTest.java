package com.android.launcher3.common.network;

import android.content.Context;

import com.android.launcher3.common.network.api.ApiHelper;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.resp.HotTopicsResp;
import com.android.launcher3.common.network.resp.PageResponse;
import com.android.launcher3.common.network.resp.ThemeDetailsResp;
import com.android.launcher3.common.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shensl
 * @Description：{DATA}
 * @CreateDate：2023/12/6 16:36
 * @UpdateUser: shensl
 */
public class HttpTest {

    private static String TAG = HttpTest.class.getSimpleName() + ">>>>>";

    public static void testInterface(Context context) {
        // 热门主题
//        getHotTopics();
        // 主题详情
//        getThemeDetails();
        // 获取文件夹Workspaces下面有几个liarbry
        queryWorkspacesLiarbry(context);
    }

    // 获取文件夹Workspaces下面有几个liarbry
    private static void queryWorkspacesLiarbry(Context context) {
        int libraryCount = 0;

        List<File> libraries = getLibraries(context);
        if(libraries!=null&&libraries.size()>0) {
            for (File library : libraries) {
                LogUtil.d("Library = " + library.getName(), LogUtil.TYPE_RELEASE);
                libraryCount++;
            }
        }

        LogUtil.d("Workspaces目录下的库数量为：" + libraryCount, LogUtil.TYPE_RELEASE);
    }

    private static List<File> getLibraries(Context context) {
        final String WORKSPACES_FOLDER = "Workspaces";
        List<File> libraries = new ArrayList<>();
        // 获取应用程序根目录的File对象
        File rootDirectory = context.getExternalFilesDir(null);
        if (rootDirectory != null) {
            LogUtil.d("rootDirectory.getAbsolutePath() = " + rootDirectory.getAbsolutePath(), LogUtil.TYPE_RELEASE);
            // 构建工作空间文件夹的完整路径
            File workspaceFolder = new File(rootDirectory, WORKSPACES_FOLDER);
            if (workspaceFolder.exists() && workspaceFolder.isDirectory()) {
                // 获取工作空间文件夹中的所有文件和目录
                File[] files = workspaceFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            // 如果是目录，则将目录添加到列表中
                            libraries.add(file);
                        }
                    }
                }
            }
        }
        return libraries;
    }

    // 热门主题
    private static void getHotTopics() {
        int pageNum = 1;
        int pageSize = 2;
        ApiHelper.getHotTopics(new BaseListener<PageResponse<HotTopicsResp>>() {
            @Override
            public void onError(int code, String msg) {
                LogUtil.d(TAG + "code = " + code, LogUtil.TYPE_RELEASE);
            }

            @Override
            public void onSuccess(PageResponse<HotTopicsResp> response) {
                if (response != null && response.getList() != null && response.getList().size() > 0) {
                    for (HotTopicsResp hotTopicsResp : response.getList()) {
                        LogUtil.d(TAG + hotTopicsResp.toString(), LogUtil.TYPE_RELEASE);
                    }
                }
            }
        }, pageNum, pageSize);
    }

    // 主题详情
    private static void getThemeDetails() {
        int themeId = 1;
        int waAcctId = 2;
        ApiHelper.getThemeDetails(new BaseListener<ThemeDetailsResp>() {
            @Override
            public void onError(int code, String msg) {
                LogUtil.d(TAG + "msg = " + msg, LogUtil.TYPE_RELEASE);
            }

            @Override
            public void onSuccess(ThemeDetailsResp themeDetailsResp) {
                LogUtil.d(TAG + "object = " + themeDetailsResp.toString(), LogUtil.TYPE_RELEASE);
            }
        }, themeId, waAcctId);
    }

}
