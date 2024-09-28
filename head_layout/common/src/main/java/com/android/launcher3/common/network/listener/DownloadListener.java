package com.android.launcher3.common.network.listener;


public interface DownloadListener extends BaseListener<String> {

    void onProgress(int progress);

}
