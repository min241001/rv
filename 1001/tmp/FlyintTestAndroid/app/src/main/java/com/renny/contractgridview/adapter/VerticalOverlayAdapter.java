package com.renny.contractgridview.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.renny.contractgridview.R;
import com.renny.contractgridview.base.Constants;
import com.renny.contractgridview.bean.AppInfoBean;
import com.renny.contractgridview.event.ItemEventListener;
import com.renny.contractgridview.opreator.AppListOpreator;
import com.renny.contractgridview.utils.AppUtils;
import com.renny.contractgridview.utils.LogUtil;

import java.util.List;

/**
 * Created by pengmin on 2024/8/30.
 */

public class VerticalOverlayAdapter extends RecyclerView.Adapter<VerticalOverlayAdapter.viewHolder> implements ItemEventListener {
    private static final String TAG = "itemc";
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;
    private static final int ITEM_TYPE_NORMAL = 0;//普通类型
    private static final int ITEM_TYPE_HEAD = 1;//尾部局类型
    private View mHeadView;//尾部局
    private List<AppInfoBean> beans;
    private List<AppInfoBean> beans2;
    private Context context;
    private Handler mHandler;
    private RecyclerView rv;


    public VerticalOverlayAdapter(List<AppInfoBean> beans, List<AppInfoBean> beans2, Handler mHandler, RecyclerView rv, Context context) {
        this.beans = beans;
        this.beans2 = beans2;
        this.context = context;
        this.mHandler = mHandler;
        this.rv = rv;
        LogUtil.i(Constants.ft, "init costruct");
    }

    public void setHeadView() {
        if (this.beans == null) {
            return;
        }
        AppInfoBean bean = new AppInfoBean();
        bean.setType(-1);
        this.beans.add(0, bean);
        LogUtil.i(Constants.ft, "set head data");
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.i(Constants.ft, "onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == ITEM_TYPE_HEAD) {//头布局
            mHeadView = inflater.inflate(R.layout.layout_head_view, parent, false);
            return new viewHolder(mHeadView);
        } else {
            //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_overlay_fragment_item,
            //      parent, false);
            View view = inflater.inflate(R.layout.vertical_overlay_fragment_item, parent, false);
            return new viewHolder(view);
        }

    }


    @Override
    public int getItemViewType(int position) {
        LogUtil.i(Constants.ft, "getItemViewType");
        if (position == 0) {//头布局
            return ITEM_TYPE_HEAD;
        }
        return ITEM_TYPE_NORMAL;//普通类型
        //return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        LogUtil.i(Constants.fe, "onBindViewHolder");
        LogUtil.i(Constants.fe, "holder.getItemViewType():"+holder.getItemViewType());
        if (holder.getItemViewType() == ITEM_TYPE_HEAD) {

        } else {
            viewHolder viewHolder = (viewHolder) holder;
            viewHolder.app_name.setText(beans.get(position).getApp_name());
            viewHolder.textView2.setText(context.getString(R.string.all_apps));
            viewHolder.textView2.setTextColor(context.getResources().getColor(R.color.text_color_default));
            viewHolder.app_icon.setImageDrawable(beans.get(position).getApp_icon());
            //rl3gridview  //rl2,more item //rl1,常规
            LogUtil.i(TAG, "----->type;" + beans.get(position).getType(), 1);
            switch (beans.get(position).getType()) {
                case 0:
                    viewHolder.rl_plus_o.setVisibility(View.GONE);
                    viewHolder.rl1_o.setVisibility(View.VISIBLE);
                    viewHolder.rl2_o.setVisibility(View.GONE);
                    viewHolder.rl3_o.setVisibility(View.GONE);
                    break;
                case 1://more menu
                    // Log.i(TAG,"positon2:"+position);
                    viewHolder.rl_plus_o.setVisibility(View.GONE);
                    viewHolder.rl1_o.setVisibility(View.GONE);
                    viewHolder.rl2_o.setVisibility(View.VISIBLE);
                    viewHolder.rl3_o.setVisibility(View.GONE);
                    break;
                case 2://gridview
                    // Log.i(TAG,"positon2:"+position);
                    viewHolder.rl_plus_o.setVisibility(View.GONE);
                    viewHolder.rl1_o.setVisibility(View.GONE);
                    viewHolder.rl2_o.setVisibility(View.GONE);
                    viewHolder.rl3_o.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    viewHolder.rl_plus_o.setVisibility(View.VISIBLE);
                    viewHolder.rl1_o.setVisibility(View.GONE);
                    viewHolder.rl2_o.setVisibility(View.GONE);
                    viewHolder.rl3_o.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
       /* if(position==0&&beans.get(position).getType()==3){
            if(beans.get(position).getHide()) {
                viewHolder.rl_plus_o.setVisibility(View.GONE);
            }else {
                viewHolder.rl_plus_o.setVisibility(View.VISIBLE);
            }
        }*/

            if (beans.get(position).getEditMode()) {//编辑模式
                viewHolder.iv_del1.setVisibility(View.VISIBLE);
                viewHolder.iv_del2.setVisibility(View.VISIBLE);
                viewHolder.iv_up1.setVisibility(View.VISIBLE);
                viewHolder.iv_up2.setVisibility(View.VISIBLE);
            } else {
                viewHolder.iv_del1.setVisibility(View.GONE);
                viewHolder.iv_del2.setVisibility(View.GONE);
                viewHolder.iv_up1.setVisibility(View.GONE);
                viewHolder.iv_up2.setVisibility(View.GONE);
            }
            //InitGridView(viewHolder.item_gv);
            viewHolder.v_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.i(Constants.fe, "click1" );
                    if (onItemClickListener != null) {
                        LogUtil.i(Constants.fe, "click" );
                        onItemClickListener.onItemClick(viewHolder.itemView, position);
                    }

                }
            });
            viewHolder.v_root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    LogUtil.i(Constants.fe, "long click1" );
                    if (onItemLongClickListener != null) {
                        LogUtil.i(Constants.fe, "long click" );
                        onItemLongClickListener.onItemLongClick(viewHolder.itemView, position);
                    }
                    return false;
                }
            });
            /*viewHolder.rl1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AppListOpreator.InsertPlusItem(AppUtils.defaultApp, position, VerticalOverlayAdapter.this, mHandler);
                    return false;
                }
            });
            viewHolder.rl3.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AppListOpreator.InsertPlusItem(AppUtils.defaultApp, position, VerticalOverlayAdapter.this, mHandler);
                    return false;
                }
            });
        }*/
        }
        onViewRecycled(holder);

    }


    @Override
    public int getItemCount() {
        LogUtil.i(Constants.ft, "getItemCount");
        return beans.size();
    }
    //普通类型ViewHolder
    public class HeadHolder extends RecyclerView.ViewHolder {
        //TextView mTv_name;

        HeadHolder(@NonNull View itemView) {
            super(itemView);
            //mTv_name = itemView.findViewById(R.id.tv_name);
        }
    }

    class viewHolder extends RecyclerView.ViewHolder {
        View v_root;
        RelativeLayout rl1_o, rl2_o, rl3_o;
        RelativeLayout rl1, rl3;
        RelativeLayout rl_plus, rl_plus_o;
        GridView item_gv;
        TextView app_name, textView2;
        ImageView app_icon;
        ImageView iv_del1, iv_del2, iv_up1, iv_up2;


        public viewHolder(View v) {
            super(v);
            v_root = v;

            app_name = v.findViewById(R.id.app_name);
            textView2 = v.findViewById(R.id.info_text2);
            app_icon = v.findViewById(R.id.app_icon);
            rl1 = v.findViewById(R.id.item_card_rl);
            rl3 = v.findViewById(R.id.item_card_rl3);
            rl1_o = v.findViewById(R.id.item_card_rl_o);
            rl2_o = v.findViewById(R.id.item_card_rl2_o);
            rl3_o = v.findViewById(R.id.item_card_rl3_o);
            item_gv = v.findViewById(R.id.item_gv);
            iv_del1 = v.findViewById(R.id.item_card_rv_del);
            iv_del2 = v.findViewById(R.id.item_card_rv_del2);
            iv_up1 = v.findViewById(R.id.item_card_rv_up);
            iv_up2 = v.findViewById(R.id.item_card_rv_up2);
            rl_plus_o = v.findViewById(R.id.vertical_overlay_item_plus_o);
            rl_plus = v.findViewById(R.id.vertical_overlay_item_plus);
        }
    }

    public void setItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }

    public void setItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.onItemLongClickListener = mOnItemLongClickListener;
    }
}
