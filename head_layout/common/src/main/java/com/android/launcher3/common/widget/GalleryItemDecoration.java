package com.android.launcher3.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.common.utils.DensityUtils;

public class GalleryItemDecoration extends RecyclerView.ItemDecoration {
    /*** 自定义默认的Item的边距*/
    private int mPageMargin = 10;
    /*** 第一张图片的左边距*/
    private int mLeftPageVisibleWidth;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//计算一下第一中图片距离屏幕左边的距离：(屏幕的宽度-item的宽度)/2。其中item的宽度=实际ImagView的宽度+margin。//我这里设置的ImageView的宽度为100+margin=110
        if (mLeftPageVisibleWidth ==0) {
//计算一次就好了
            mLeftPageVisibleWidth = (int) (DensityUtils.px2dip(
                                getScreenWidth(view.getContext()) -
                                        DensityUtils.dip2px(110, view.getContext()),
                                view.getContext()) / 2);
        }
//获取当前Item的position
        int position = parent.getChildAdapterPosition(view);//获得Item的数量
        int itemCount = parent.getAdapter().getItemCount();
        int leftMagin;
        if (position == 0){
            leftMagin= dpToPx(mLeftPageVisibleWidth);
        }else{
            leftMagin=dpToPx(mPageMargin);
        }

        int rightMagin;
        if (position == itemCount-1) {
            rightMagin=dpToPx(mLeftPageVisibleWidth);
        }else{
            rightMagin=dpToPx(mPageMargin);
        }
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();//10,10分别是item到上下的margin
        layoutParams.setMargins(leftMagin,10,rightMagin,10);
        view.setLayoutParams(layoutParams);
        super.getItemOffsets(outRect, view, parent, state);
    }
    /*** d p转换成px* @param dp：*/
    private int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    /*** 获取屏幕的宽度* @param context:* @return :*/

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
}
