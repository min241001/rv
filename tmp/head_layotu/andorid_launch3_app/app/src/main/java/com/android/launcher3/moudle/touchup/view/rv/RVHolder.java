/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package com.android.launcher3.moudle.touchup.view.rv;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;
/**
 * Create by pengmin on 2024/9/18 .
 */
public class RVHolder extends RecyclerView.ViewHolder {
    private View v;
    private SparseArray<View> mViews;
    private SparseArray<View> mViews2;
    public RVHolder(View itemView) {
        super(itemView);
        v = itemView;
        mViews = new SparseArray<>();
        mViews2 = new SparseArray<>();
    }
    public <T extends View> T findViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
    public <T extends View> T GetVRoot(int id) {
        View view = mViews2.get(id);
        if (view == null) {
            view = itemView;
            mViews2.put(id, view);
        }
        return (T) view;
    }
    public View getView(int viewId) {
        return findViewById(viewId);
    }
    public final Context getContext() {
        return itemView.getContext();
    }
    /**
     * 寻找控件
     *
     * @param id
     * @return
     */
    public View findView(@IdRes int id) {
        return id == 0 ? itemView : findViewById(id);
    }

    /**
     * 清除控件缓存
     */
    public void clearViews() {
        if (mViews != null) {
            mViews.clear();
        }
        if (mViews2 != null) {
            mViews2.clear();
        }
    }
}
