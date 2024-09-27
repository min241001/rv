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

import androidx.recyclerview.widget.RecyclerView;

import com.renny.contractgridview.R;
import com.renny.contractgridview.bean.AppInfoBean;
import com.renny.contractgridview.opreator.AppListOpreator;
import com.renny.contractgridview.utils.AppUtils;
import com.renny.contractgridview.utils.LogUtil;

import java.util.List;

/**
 * Created by pengmin on 2024/8/30.
 */

public class VerticalOverlayAdapter extends RecyclerView.Adapter<VerticalOverlayAdapter.viewHolder> {
    private static final String TAG = "itemc";
    private static final int ITEM_TYPE_NORMAL = 0;//普通类型
    private static final int ITEM_TYPE_HEAD = 1;//尾部局类型
    private View mHeadView;//尾部局
    private List<AppInfoBean> beans;
    private List<AppInfoBean> beans2;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private Handler mHandler;
    private RecyclerView rv;


    public VerticalOverlayAdapter(List<AppInfoBean> beans, List<AppInfoBean> beans2, Handler mHandler, RecyclerView rv,Context context) {
        this.beans = beans;
        this.beans2 = beans2;
        this.context = context;
        this.mHandler = mHandler;
        this.rv = rv;
    }
    public void setHeadView() {
        if (this.beans == null) {
            return;
        }
        AppInfoBean bean = new AppInfoBean();
        bean.setType(-1);
        this.beans.add(0, bean);
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == ITEM_TYPE_HEAD) {//头布局
            mHeadView = inflater.inflate(R.layout.head_banner, rv, false);
            //return new RecyclerView.ViewHolder(mHeadView){};
            //viewHolder(view);
            return new viewHolder(mHeadView);
        }else {
            //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vertical_overlay_fragment_item,
             //       viewGroup, false);
            View view =inflater.inflate(R.layout.vertical_overlay_fragment_item,viewGroup, false);
            return new viewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mHeadView != null && position == 0) {//头布局
            return ITEM_TYPE_HEAD;
        }
        return ITEM_TYPE_NORMAL;//普通类型
        //return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(viewHolder viewHolder, int position) {
        if(viewHolder.getItemViewType()==ITEM_TYPE_HEAD){

        }else {
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
                    if (onItemClickListener != null) {
                        int pos = viewHolder.getLayoutPosition();
                        onItemClickListener.onItemClik(viewHolder.itemView, pos);
                    }

                }

            });
            viewHolder.v_root.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (onItemClickListener != null) {
                        int pos = viewHolder.getLayoutPosition();
                        onItemClickListener.onItemLongClik(viewHolder.itemView, pos);
                    }
                    return false;
                }
            });
            viewHolder.rl1.setOnLongClickListener(new View.OnLongClickListener() {
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
        }
        onViewRecycled(viewHolder);

    }

    private void InitGridView(GridView gv) {
        if (beans2.size() > 3) {
            beans2 = beans2.subList(0, 3);
        }
        GridViewAdapter adapter = new GridViewAdapter(beans2, context);
        adapter.setListView(gv);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppUtils.LauncherActivity(context, beans2.get(position).getPackage_name());
            }
        });
        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                          @Override
                                          public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                              //ImageView iv = view.findViewById(R.id.item_card_gv_item_del);
                                              //iv.setVisibility(View.VISIBLE);
                                              // adapter.notifyDataSetChanged();
                                              Toast.makeText(context, "del visible", Toast.LENGTH_SHORT).show();

                                              return false;
                                          }
                                      }

        );
    }

    @Override
    public int getItemCount() {
        return beans.size();
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

    public interface OnItemClickListener {
        void onItemClik(View view, int position);

        void onItemLongClik(View view, int position);
    }

    public void setItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }

}
