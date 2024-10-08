package com.renny.contractgridview.event;
import android.view.View;

/**
 * Create by pengmin on 2024/9/14 .
 */
public interface ItemEventListener {
    interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
