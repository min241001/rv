package com.android.launcher3.moudle.shortcut.adapter;

import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.BATTERY;
import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.MUTE;
import static com.android.launcher3.moudle.shortcut.bean.WidgetEnum.WIFI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.launcher3.R;
import com.android.launcher3.moudle.shortcut.bean.Widget;
import com.android.launcher3.moudle.shortcut.bean.WidgetEnum;
import com.android.launcher3.moudle.shortcut.widgets.ItemTouchInterface;
import com.android.launcher3.moudle.shortcut.widgets.ItemViewHolderInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : yanyong
 * Date : 2024/5/9
 * Details : 可拖动的控件适配器
 */
public class RvDragAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchInterface {
    private List<Widget> mWidgets = new ArrayList<>();
    private List<Widget> mAddedWidgets = new ArrayList<>();
    private int mBattery;
    // 下拉控件数据
    public static final int TYPE_WIDGET = 1;
    // 更多
    private static final int TYPE_MORE = 2;
    // 待添加
    private static final int TYPE_ADDED = 3;
    // 底部切换按钮
    private static final int TYPE_BOTTOM = 4;

    public RvDragAdapter(List<Widget> list, List<Widget> list2, int battery) {
        this.mBattery = battery;
        this.mWidgets = list;
        this.mAddedWidgets = list2;
    }

    public void setWidgetBeans(List<Widget> list) {
        this.mWidgets = list;
        notifyDataSetChanged();
    }

    public void setWidgetBeans(List<Widget> list, List<Widget> list2) {
        this.mWidgets = list;
        this.mAddedWidgets = list2;
        notifyDataSetChanged();
    }

    public int getWidgetBeans() {
        return mWidgets == null ? 0 : mWidgets.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_WIDGET) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_widget_item, parent, false);
//            startShakeByViewAnim(view, 1,  10000);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_MORE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_more_item, parent, false);
            return new BottomViewHolder(view);
        } else if (viewType == TYPE_ADDED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_widget_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_widget_bottom, parent, false);
            return new BottomViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        int addedCount = 0;
        if (mAddedWidgets != null && mAddedWidgets.size() > 0) {
            addedCount = mAddedWidgets.size() + 1;
        }
        return mWidgets.size() + addedCount + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mWidgets.size()) {
            return TYPE_WIDGET;
        } else if ((position < mWidgets.size() + 1) && mAddedWidgets.size() > 0) {
            return TYPE_MORE;
        } else if (mAddedWidgets.size() > 0 && (position < mWidgets.size() + mAddedWidgets.size() + 1)) {
            return TYPE_ADDED;
        } else {
            return TYPE_BOTTOM;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_WIDGET) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            setWidgetData(itemHolder, position);
        } else if (getItemViewType(position) == TYPE_MORE) {

        } else if (getItemViewType(position) == TYPE_ADDED) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            setAddedWidgetData(itemHolder, position);
        } else {
            BottomViewHolder bvHolder = (BottomViewHolder) holder;
            bvHolder.tv.setText(R.string.complete);
            if (onItemClickListener != null) {
                bvHolder.tv.setOnClickListener(v -> onItemClickListener.onEditClickListener());
            }
        }
    }

    private void setWidgetData(ItemViewHolder holder, int position) {
        Widget bean = mWidgets.get(position);
        setData(bean, holder);
        if (bean.getId().equals(WidgetEnum.formEnum(WIFI))
                || bean.getId().equals(WidgetEnum.formEnum(BATTERY))) {
            holder.ivType.setVisibility(View.GONE);
        } else {
            holder.ivType.setVisibility(View.VISIBLE);
            holder.ivType.setImageResource(R.drawable.svg_3_remove_widget);
        }
        if (bean.getId().equals(WidgetEnum.formEnum(BATTERY))) {
            holder.iv.setVisibility(View.GONE);
            holder.tv.setVisibility(View.VISIBLE);
            holder.tv.setText(mBattery + "%");
        } else {
            holder.iv.setVisibility(View.VISIBLE);
            holder.tv.setVisibility(View.GONE);
        }
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                Widget widget = mWidgets.get(position);
                if (!widget.getId().equals(WidgetEnum.formEnum(WIFI))
                        && !widget.getId().equals(WidgetEnum.formEnum(BATTERY))) {
                    onItemClickListener.onItemClickListener(holder.itemView, bean, true);
                }
            });
        }
    }

    private void setAddedWidgetData(ItemViewHolder holder, int position) {
        int pos = position - mWidgets.size() - 1;
        Widget bean = mAddedWidgets.get(pos);
        holder.ivType.setImageResource(R.drawable.svg_add_green);
        setData(bean, holder);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                        onItemClickListener.onItemClickListener(holder.itemView, bean, false);
                    }
            );
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }
    }

    private void setData(Widget bean, ItemViewHolder holder) {
        try {
            holder.iv.setImageResource(bean.getResId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bean.getSelected()) {
            if (bean.getId().equals(WidgetEnum.formEnum(MUTE))) {
                holder.iv.setBackgroundResource(R.drawable.bg_pull_down_red);
            } else {
                holder.iv.setBackgroundResource(R.drawable.bg_pull_down_blue);
            }
        } else {
            holder.iv.setBackgroundResource(R.drawable.bg_pull_down_default);
        }
        holder.tvName.setText(bean.getName());
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < mWidgets.size()) {
            Widget bean = mWidgets.remove(fromPosition);
            if ((toPosition > fromPosition) && (mWidgets.size() <= toPosition)) {
                //将当前item移至最后一位
                mWidgets.add(bean);
            } else {
                mWidgets.add(toPosition, bean);
            }
            if (mOnDragInterface != null) {
                mOnDragInterface.onDragListener(mWidgets);
            }
            notifyItemMoved(fromPosition, toPosition);
        } else {
            notifyDataSetChanged();
        }
    }

    private void startShakeByViewAnim(View view, float shakeDegrees, long duration) {
        if (view == null) {
            return;
        }
        //从左向右
        Animation rotateAnim = new RotateAnimation(-shakeDegrees, shakeDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(duration);
        rotateAnim.setRepeatMode(Animation.REVERSE);
        rotateAnim.setRepeatCount(10);

        AnimationSet smallAnimationSet = new AnimationSet(false);
        smallAnimationSet.addAnimation(rotateAnim);
        view.startAnimation(smallAnimationSet);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, Widget bean, boolean idAdded);

        void onEditClickListener();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemViewHolderInterface {

        TextView tv;
        TextView tvName;
        ImageView iv;
        ImageView ivType;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            tvName = itemView.findViewById(R.id.tv_name);
            iv = itemView.findViewById(R.id.iv);
            ivType = itemView.findViewById(R.id.iv_type);
        }

        @Override
        public void onItemSelected() {
            // 被选中item拖动开始回调
        }

        @Override
        public void onItemCleared() {
            // 被选中item拖动结束回调
        }
    }

    static class BottomViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public BottomViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }
    }

    private OnDragInterface mOnDragInterface;

    public void setOnDragInterface(OnDragInterface onDragInterface) {
        mOnDragInterface = onDragInterface;
    }

    public interface OnDragInterface {

        void onDragListener(List<Widget> list);
    }
}
