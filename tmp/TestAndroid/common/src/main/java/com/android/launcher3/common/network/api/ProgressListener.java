package com.android.launcher3.common.network.api;

public interface ProgressListener {
    void onProgress(long bytesRead, long contentLength, boolean done);
}
