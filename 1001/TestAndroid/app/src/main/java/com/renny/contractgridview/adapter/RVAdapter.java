package com.renny.contractgridview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.renny.contractgridview.bean.AppInfoBean;
import com.renny.contractgridview.event.ItemEventListener;
import com.renny.contractgridview.view.rv.LayoutInflater;
import com.renny.contractgridview.view.rv.PreInflateHelper;
import com.renny.contractgridview.view.rv.RVHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by pengmin on 2024/9/18 .
 */
public abstract class RVAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> implements ItemEventListener {

    protected List<AppInfoBean> beans = new ArrayList<>();
    protected ItemEventListener.OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;
    protected Context context;
    protected PreInflateHelper sInstance;
    protected RVHolder holder;
    public int layout_id;

   // public RVAdapter(Context context, List<T> source, RecyclerView rv, int layout_id) ;

    public RVAdapter(Context context, List<AppInfoBean> bs, RecyclerView rv, int layout_id) {
        this.layout_id = layout_id;
        this.context = context;
        this.beans =bs;
        sInstance = new PreInflateHelper();
        sInstance.setAsyncInflater(LayoutInflater.get());
        sInstance.preload(rv,getLayout_id());
    }
    public int getLayout_id(){
        return layout_id;
    }
    @Override
    public void onBindViewHolder(@NonNull V viewHolder, int position) {
        bindData(viewHolder, position);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            int pos = viewHolder.getLayoutPosition();
                            onItemClickListener.onItemClik(viewHolder.itemView, pos);
                        }
                    }
                });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    int pos = viewHolder.getLayoutPosition();
                    onItemLongClickListener.onItemLongClik(viewHolder.itemView, pos);
                }
                return false;
            }
        });
    }
    protected abstract void bindData(@NonNull V holder, int position);


    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        final V viewHolder = getViewHolder(parent, viewType);
        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
   // @NonNull
    //protected abstract V getViewHolder(ViewGroup parent, int viewType);
    protected V getViewHolder(ViewGroup parent, int viewType) {
        View view = sInstance.getView(parent,getLayout_id());
        /*if(holder==null){
            holder = new RVHolder(view);
        }*/
        return (V) new RVHolder(view);
    }
    @Override
    public int getItemCount() {
        return beans.size();
    }

    public AppInfoBean getItem(int position) {
        return CheckPosition(position) ? beans.get(position) : null;
    }

    private boolean CheckPosition(int position) {
        return position >= 0 && position < beans.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    /*public List<T> getData() {
        return (List<T>) beans;
    }*/
    public List<AppInfoBean> getData() {
        return beans;
    }

    public RVAdapter add(AppInfoBean item) {
        beans.add(item);
        notifyItemInserted(beans.size() - 1);
        return this;
    }

    /**
     * 删除列表中指定索引的数据
     *
     * @param pos 位置
     */
    public RVAdapter delete(int pos) {
        if (CheckPosition(pos)) {
            beans.remove(pos);
            notifyItemRemoved(pos);
        }
        return this;
    }
    public void clear(){
        holder.clearViews();
    }


    public void setItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }

    public void setItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.onItemLongClickListener = mOnItemLongClickListener;
    }
}
