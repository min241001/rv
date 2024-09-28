package com.android.launcher3.moudle.shortcut.widgets;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class MyItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpacing;
    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    public MyItemDecoration(int spacing) {
        this.mLeft = spacing;
        this.mTop = spacing;
        this.mRight = spacing;
        this.mBottom = spacing;
    }

    public MyItemDecoration(float spacing) {
        this.mLeft = (int) spacing;
        this.mTop = (int) spacing;
        this.mRight = (int) spacing;
        this.mBottom = (int) spacing;
    }

    public MyItemDecoration(float vertical, float horizontal) {
        this.mLeft = (int) horizontal;
        this.mRight = (int) horizontal;
        this.mTop = (int) vertical;
        this.mBottom = (int) vertical;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mLeft, mTop, mRight, mBottom);
    }
}
