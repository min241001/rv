/*
 * Copyright (C) 2023 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * Create by pengmin on 2024/9/2 .
 */
package com.renny.contractgridview.view.rv;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;

import com.renny.contractgridview.event.ILayoutInflater;
import com.renny.contractgridview.event.InflateCallback;
import com.renny.contractgridview.utils.InflateUtils;

public class LayoutInflater implements ILayoutInflater {

    private AsyncLayoutInflater mInflater;

    private LayoutInflater() {

    }

    private static final class InstanceHolder {
        static final LayoutInflater sInstance = new LayoutInflater();
    }

    public static LayoutInflater get() {
        return InstanceHolder.sInstance;
    }

    @Override
    public void asyncInflateView(@NonNull ViewGroup parent, int layoutId, InflateCallback callback) {
        if (mInflater == null) {
            Context context = parent.getContext();
            mInflater = new AsyncLayoutInflater(new ContextThemeWrapper(context.getApplicationContext(), context.getTheme()));
        }
        mInflater.inflate(layoutId, parent, (view, resId, parent1) -> {
            if (callback != null) {
                callback.onInflateFinished(resId, view);
            }
        });
    }

    @Override
    public View inflateView(@NonNull ViewGroup parent, int layoutId) {
        return InflateUtils.getInflateView(parent, layoutId);
    }
}
