package com.android.launcher3.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: shensl
 * @Description：上拉消息通知栏
 * @CreateDate：2024/1/24 17:57
 * @UpdateUser: shensl
 */
public class NotificationRecyclerView extends RecyclerView {

    public NotificationRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NotificationRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}