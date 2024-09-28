package com.android.launcher3.moudle.shortcut.widgets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.moudle.shortcut.adapter.RvDragAdapter;

/**
 * Author : yanyong
 * Date : 2024/7/5
 * Details : 控件拖动自定义监听
 */
public class ItemTouchCallback extends ItemTouchHelper.Callback {

    private ItemTouchInterface mInterface;
    private RvDragAdapter mRvDragAdapter;

    public ItemTouchCallback(ItemTouchInterface itInterface) {
        this.mInterface = itInterface;
    }

    public ItemTouchCallback(ItemTouchInterface itInterface, RvDragAdapter adapter) {
        this.mInterface = itInterface;
        this.mRvDragAdapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getLayoutPosition();
        int size = mRvDragAdapter.getWidgetBeans();
        // 已添加的数据才可以拖动，其他数据不拖动
        if (position >= size) {
            return makeMovementFlags(0, 0);
        }
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        mInterface.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }


    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            ItemViewHolderInterface itemTouchHelperViewHolder = (ItemViewHolderInterface) viewHolder;
            itemTouchHelperViewHolder.onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        RvDragAdapter.ItemViewHolder itemViewHolder = (RvDragAdapter.ItemViewHolder) viewHolder;
        itemViewHolder.onItemCleared();
    }
}
