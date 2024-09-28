package com.renny.contractgridview.event;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * Create by pengmin on 2024/9/19 .
 */
public interface ILayoutInflater {

        /**
         * 异步加载View
         *
         * @param parent   父布局
         * @param layoutId 布局资源id
         * @param callback 加载回调
         */
        void asyncInflateView(@NonNull ViewGroup parent, int layoutId, InflateCallback callback);

        /**
         * 同步加载View
         *
         * @param parent   父布局
         * @param layoutId 布局资源id
         * @return 加载的View
         */
        View inflateView(@NonNull ViewGroup parent, int layoutId);

    }

