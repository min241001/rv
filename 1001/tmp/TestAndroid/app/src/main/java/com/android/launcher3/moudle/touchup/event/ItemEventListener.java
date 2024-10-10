package com.android.launcher3.moudle.touchup.event;

import android.view.View;

/**
 * Create by pengmin on 2024/9/14 .
 */
public interface ItemEventListener {
    interface OnItemClickListener {
        void onItemClik(View view, int position);
    }
    interface OnItemLongClickListener {
        void onItemLongClik(View view, int position);
    }
}
