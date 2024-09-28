package com.android.launcher3.moudle.shortcut.adapter;

import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.BATTERY;
import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.MUTE;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.moudle.shortcut.bean.Widget;
import com.android.launcher3.moudle.shortcut.bean.WidgetEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : yanyong
 * Date : 2024/7/1
 * Details : 下拉显示的控件适配器
 */
public class RvDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 下拉控件数据
    public static final int TYPE_WIDGET = 1;
    // 底部切换按钮
    private static final int TYPE_BOTTOM = 2;
    private List<Widget> mWidgets = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    // 电量
    private int mBattery;

    public RvDisplayAdapter(List<Widget> list, int battery) {
        this.mWidgets = list;
        this.mBattery = battery;
    }

    public void setWidgetBeans(List<Widget> list) {
        this.mWidgets = list;
        notifyDataSetChanged();
    }

    public void setBattery(int battery) {
        this.mBattery = battery;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_WIDGET) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_widget_item2, parent, false);
            return new ItemHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_widget_bottom, parent, false);
            return new BottomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_WIDGET) {
            ItemHolder itemHolder = (ItemHolder) holder;
            setWidgetData(itemHolder, position);
        } else {
            if (onItemClickListener != null) {
                ((BottomViewHolder) holder).tv.setOnClickListener(v -> onItemClickListener.onEditClickListener());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setWidgetData(ItemHolder holder, int position) {
        Widget bean = mWidgets.get(position);
        try {
            holder.iv.setImageResource(bean.getResId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bean.getId().equals(WidgetEnum.formEnum(BATTERY))) {
            holder.iv.setVisibility(View.GONE);
            holder.tv.setVisibility(View.VISIBLE);
            holder.tv.setText(mBattery + "%");
        } else {
            holder.iv.setVisibility(View.VISIBLE);
            holder.tv.setVisibility(View.GONE);
        }
        if (bean.getSelected()) {
            if (bean.getId().equals(WidgetEnum.formEnum(MUTE))) {
                holder.root.setBackgroundResource(R.drawable.bg_pull_down_red);
            } else {
                holder.root.setBackgroundResource(R.drawable.bg_pull_down_blue);
            }
        } else {
            holder.root.setBackgroundResource(R.drawable.bg_pull_down_default);
        }
        if (onItemClickListener != null) {
            final int pos = position;
            holder.root.setOnClickListener(v -> onItemClickListener.onItemClickListener(holder.itemView, mWidgets.get(pos)));
            holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClickListener(holder.itemView, mWidgets.get(pos));
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mWidgets.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mWidgets.size()) {
            return TYPE_WIDGET;
        } else {
            return TYPE_BOTTOM;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, Widget bean);

        void onItemLongClickListener(View view, Widget bean);

        void onEditClickListener();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        View root;
        ImageView iv;
        TextView tv;

        public ItemHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            iv = itemView.findViewById(R.id.iv);
            tv = itemView.findViewById(R.id.tv);
        }
    }

    static class BottomViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public BottomViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}
