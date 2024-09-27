package com.android.launcher3.desktop4.cellular.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyGridView extends View {

    private Paint mPaint;
    private int[][] gridPositions; // 存储九宫格各个格子的位置信息

    public MyGridView(Context context) {
        super(context);
        init();
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        //开启抗锯齿
        mPaint.setAntiAlias(true);
        //图片抗锯齿滤镜
        mPaint.setFilterBitmap(true);
        // 初始化九宫格的位置信息，这里简单示例，你可以根据需要调整
        gridPositions = new int[][]{
                {1, 0}, {2, 0}, {3, 0},
                {0, 1}, {1, 1}, {2, 1}, {3, 1},
                {0, 2}, {1, 2}, {2, 2}, {3, 2},
                {0, 3}, {1, 3}, {2, 3}, {3, 3}
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int cellSize = Math.min(width, height) / 3; // 每个格子的大小

        // 绘制九宫格
        for (int[] position : gridPositions) {
            int x = position[0] * cellSize;
            int y = position[1] * cellSize;
            canvas.drawCircle(x, y, cellSize / 4, mPaint); // 这里用圆形表示每个格子，你可以自定义形状
        }
    }
}

