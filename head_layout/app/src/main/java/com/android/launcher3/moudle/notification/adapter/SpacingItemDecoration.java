package com.android.launcher3.moudle.notification.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 设置间距
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int leftMargin;
    private int topMargin;
    private int rightMargin;
    private int bottomMargin;

    public SpacingItemDecoration(int leftMargin,int topMargin,int rightMargin,int bottomMargin) {
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;
        this.rightMargin = rightMargin;
        this.bottomMargin = bottomMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = leftMargin;
        outRect.right = rightMargin;
        outRect.bottom = bottomMargin;
        // 首行顶部不添加间距
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = 0;
        } else {
            outRect.top = topMargin;
        }
    }

}