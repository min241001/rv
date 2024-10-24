package com.antiphon.recyclerviewdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antiphon.recyclerviewdemo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @创建者 mingyan.su
 * @创建时间 2019/12/23 16:11
 * @类描述 {TODO}头尾布局适配器
 */
public class HeadFootViewAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<String> mData;

    private  View mHeadView;//头布局
    private  View mFootView;//尾部局

    private static final int ITEM_TYPE_NORMAL = 0;//普通类型
    private static final int ITEM_TYPE_HEAD = 1;//头布局类型
    private static final int ITEM_TYPE_FOOT = 2;//尾部局类型
    private final RecyclerView rv;

    public HeadFootViewAdapter(Context context, List<String> stringList,RecyclerView rv) {
        this.mContext = context;
        this.mData = stringList;
        this.rv = rv;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeadView != null && position == 0) {//头布局
            return ITEM_TYPE_HEAD;
        } else if (mFootView != null && position == mData.size() - 1) {//尾部局
            return ITEM_TYPE_FOOT;
        }
        return ITEM_TYPE_NORMAL;//普通类型
    }
    class TestViewHolder extends RecyclerView.ViewHolder {

        public TestViewHolder(View itemView2) {
            super(itemView2);
        }

        public void bind(int pos) {
            //TextView tvTiltle = itemView.findViewById(R.id.tvTitle);

            //tvTiltle.setText("第" + pos + "个item");

                ///StaggeredGridLayoutManager.LayoutParams clp = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
                StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                //clp.setFullSpan(true);
                //tvTiltle.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                itemView.setLayoutParams(layoutParams);
                itemView.setBackgroundColor(Color.YELLOW);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //根据不同类型来获取不同的ViewHolder，里面装载不同的布局
        if (viewType == ITEM_TYPE_HEAD) {//头布局
            //头布局
            mHeadView = LayoutInflater.from(mContext).inflate(R.layout.head_banner, parent, false);

            return new NormalHolder(mHeadView) {
            };
        } else if (viewType == ITEM_TYPE_FOOT) {//尾部局
            //尾部局
            mFootView = LayoutInflater.from(mContext).inflate(R.layout.foot_loadmore, parent, false);
            /*StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180);
            params.setFullSpan(true);
            mFootView.setLayoutParams(params);*/
            //mFootView.setTextColor(Color.BLACK);
            //mFootView.setGravity(Gravity.CENTER);
            /*StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);*/
            TestViewHolder itemv = new TestViewHolder(mFootView) {};
            //itemv.itemView.setLayoutParams(layoutParams);
            return itemv;
            //return new RecyclerView.ViewHolder(mFootView) {
           // };
        } else {
            return new NormalHolder(inflater.inflate(R.layout.item_linear, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NormalHolder) {//普通类型ViewHolder
            NormalHolder viewHolder = (NormalHolder) holder;
            viewHolder.mTv_name.setText(mData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //普通类型ViewHolder
    public class NormalHolder extends RecyclerView.ViewHolder {
        TextView mTv_name;

        NormalHolder(@NonNull View itemView) {
            super(itemView);
            mTv_name = itemView.findViewById(R.id.tv_name);
        }
    }

    public void setHeadView() {
        if (mData == null) return;
        mData.add(0, "头布局");
    }

    public void setFootView() {
        if (mData == null) return;
        mData.add(mData.size(), "尾布局");
    }
}
