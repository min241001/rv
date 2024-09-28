package com.android.launcher3.moudle.touchup.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.DateUtils;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.touchup.view.rv.LayoutInflater;
import com.android.launcher3.moudle.touchup.view.rv.PreInflateHelper;
import com.android.launcher3.moudle.touchup.bean.AppInfoBean;
import com.android.launcher3.moudle.touchup.event.ItemEventListener;
import com.android.launcher3.moudle.touchup.opreator.DefaultAppsOpreator;
import com.android.launcher3.moudle.touchup.opreator.FavoriteAppsOpreator;
import com.android.launcher3.moudle.touchup.utils.AppUtil;
import com.android.launcher3.moudle.touchup.utils.CommonUtil;

import java.util.List;

/**
 * Created by pengmin on 2024/8/30.
 */

public class VerticalOverlayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemEventListener {
    private static final String TAG = "VerticalOverlayAdapter";
    private List<AppInfoBean> beans = null;
    private List<AppInfoBean> fav_beans;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Context context;
    private Handler mHandler;
    private FavoriteAdapter favoriteAdapter;
    private ActivityResultLauncher launcher;
    private static final int ITEM_TYPE_HEAD = 1;//头布局类型
    private static final int ITEM_TYPE_NORMAL = 0;//普通类型
    private View mHeadView;//头布局
    private RecyclerView rv;
    private  PreInflateHelper sInstance;
    private mViewHolder holder;


    public VerticalOverlayAdapter(List<AppInfoBean> beans, List<AppInfoBean> fav_beans, Handler mHandler, Context context, RecyclerView rv, ActivityResultLauncher launcher) {
        this.beans = beans;
        this.fav_beans = fav_beans;
        this.context = context;
        this.mHandler = mHandler;
        this.launcher = launcher;
        this.rv = rv;
        sInstance = new PreInflateHelper();
        sInstance.setAsyncInflater(LayoutInflater.get());
        sInstance.preload(rv, getItemLayoutId(0));
    }
    public int getItemLayoutId(int viewType) {
        return R.layout.vertical_overlay_fragment_item;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //return new mViewHolder(view);
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(context);
        if (viewType == ITEM_TYPE_HEAD) {//头布局
            mHeadView = inflater.inflate(R.layout.layout_head_view, viewGroup, false);
            return new mViewHolder(mHeadView);
        } else {
            //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_overlay_fragment_item,
            //      parent, false);
            View view = sInstance.getView(viewGroup,getItemLayoutId(0));
            return new mViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {//头布局
            return ITEM_TYPE_HEAD;
        }
        return ITEM_TYPE_NORMAL;//普通类型
       // return super.getItemViewType(position);
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date;

        public HeadViewHolder(View v) {
            super(v);
           // tv_date = findViewById(R.id.tf_tv_date);
        }
    }


    class mViewHolder extends RecyclerView.ViewHolder {
        View v_root;
        private SparseArray<View> mViews;
        private SparseArray<View> mViews2;

        LinearLayout menu_item;
        RelativeLayout rv_item_o;
        RelativeLayout common_item_bg, common_item, rv_item;
        RelativeLayout rl_plus, rl_plus_o;
        RecyclerView fav_item_rv;
        TextView app_name, textView2;
        ImageView app_icon;
        ImageView iv_del1, iv_up1, iv_up2;
        //calendaar
        RelativeLayout calendar_rl;
        TextView touchup_rv_item_child_calendar_tv1, touchup_rv_item_child_calendar_tv2;
        //weather
        RelativeLayout weather_rl;
        ImageView tu_rv_weather_row_iv1,
                tu_rv_weather_column_1iv1,
                tu_rv_weather_column_2iv1,
                tu_rv_weather_column_3iv1,
                tu_rv_weather_column_4iv1,
                tu_rv_weather_column_5iv1;
        TextView tu_rv_weather_row_tv1,
                tu_rv_weather_column_1tv1,
                tu_rv_weather_column_2tv1,
                tu_rv_weather_column_3tv1,
                tu_rv_weather_column_4tv1,
                tu_rv_weather_column_5tv1,
                tu_rv_weather_column_1tv2,
                tu_rv_weather_column_2tv2,
                tu_rv_weather_column_3tv2,
                tu_rv_weather_column_4tv2,
                tu_rv_weather_column_5tv2;
        //sport
        RelativeLayout sport_rl;
        TextView tu_rv_sport_tv1, tu_rv_sport_tv2, tu_rv_sport_tv_date;
        //music
        RelativeLayout music_rl;
        ImageView tu_rv_music_icon;
        TextView tu_rv_music_name_tv;
        //timer
        RelativeLayout timer_rl;
        ImageView tu_rv_timer_icon;
        TextView tu_rv_timer_tv1,tu_rv_timer_tv2;
        //alarm_clock
        RelativeLayout alarm_clock_rl;
        ImageView tu_rv_alarm_clock_icon;
        TextView tu_rv_alarm_clock_tv1,tu_rv_alarm_clock_tv2,tu_rv_alarm_clock_tv3;

        public mViewHolder(View v) {
            super(v);
            mViews = new SparseArray<>();
            mViews2 = new SparseArray<>();
            v_root = GetVRoot(getItemLayoutId(0));
            //menu and plus
            iv_del1 = findViewById(R.id.item_card_rv_del);
            iv_up1 = findViewById(R.id.item_card_rv_up);
            //plus&menu
            menu_item = findViewById(R.id.vertical_overlay_fragmeng_menu_item);
            textView2 = findViewById(R.id.info_text2);
            fav_item_rv = findViewById(R.id.fav_item_rv);
            rl_plus_o = findViewById(R.id.vertical_overlay_item_plus_o);
            rl_plus = findViewById(R.id.vertical_overlay_item_plus);
            //rv_item
            //View rv_v = findViewById(R.id.touchup_rv_rv_item);
            rv_item_o = findViewById(R.id.vertical_overlay_fragmeng_rv_item_o);
            rv_item = findViewById(R.id.vertical_overlay_fragmeng_rv_item);
            iv_up2 = findViewById(R.id.item_card_rv_up2);

            //default_item
            //View defaultv =findViewById(R.id.touchup_rv_default_item);
            app_name = findViewById(R.id.touchup_default_app_name);
            app_icon = findViewById(R.id.touchup_default_app_icon);
            //default item
            common_item_bg = findViewById(R.id.vertical_overlay_fragmeng_common_item_bg);
            common_item = findViewById(R.id.vertical_overlay_fragmeng_common_item);
            //calendar
            calendar_rl = findViewById(R.id.touchup_rv_item_child_calendar);
            touchup_rv_item_child_calendar_tv1 = findViewById(R.id.touchup_rv_item_child_calendar_tv1);
            touchup_rv_item_child_calendar_tv2 = findViewById(R.id.touchup_rv_item_child_calendar_tv2);
            //weather
            weather_rl = findViewById(R.id.touchup_rv_item_child_weather);
            tu_rv_weather_row_tv1 = findViewById(R.id.tu_rv_weather_row_tv1);////row
            tu_rv_weather_row_iv1 = findViewById(R.id.tu_rv_weather_row_iv1);
            tu_rv_weather_column_1iv1 = findViewById(R.id.tu_rv_weather_column_1iv1); //column
            tu_rv_weather_column_2iv1 = findViewById(R.id.tu_rv_weather_column_2iv1);
            tu_rv_weather_column_3iv1 = findViewById(R.id.tu_rv_weather_column_3iv1);
            tu_rv_weather_column_4iv1 = findViewById(R.id.tu_rv_weather_column_4iv1);
            tu_rv_weather_column_5iv1 = findViewById(R.id.tu_rv_weather_column_5iv1);
            tu_rv_weather_column_1tv1 = findViewById(R.id.tu_rv_weather_column_1tv1);
            tu_rv_weather_column_2tv1 = findViewById(R.id.tu_rv_weather_column_2tv1);
            tu_rv_weather_column_3tv1 = findViewById(R.id.tu_rv_weather_column_3tv1);
            tu_rv_weather_column_4tv1 = findViewById(R.id.tu_rv_weather_column_4tv1);
            tu_rv_weather_column_5tv1 = findViewById(R.id.tu_rv_weather_column_5tv1);
            tu_rv_weather_column_1tv2 = findViewById(R.id.tu_rv_weather_column_1tv2);
            tu_rv_weather_column_2tv2 = findViewById(R.id.tu_rv_weather_column_2tv2);
            tu_rv_weather_column_3tv2 = findViewById(R.id.tu_rv_weather_column_3tv2);
            tu_rv_weather_column_4tv2 = findViewById(R.id.tu_rv_weather_column_4tv2);
            tu_rv_weather_column_5tv2 = findViewById(R.id.tu_rv_weather_column_5tv2);
            //sport
            sport_rl = findViewById(R.id.touchup_rv_item_child_sport);
            tu_rv_sport_tv1 = findViewById(R.id.tu_rv_sport_tv1);
            tu_rv_sport_tv2 = findViewById(R.id.tu_rv_sport_tv2);
            tu_rv_sport_tv_date = findViewById(R.id.tu_rv_sport_date_tv);
            //music
            music_rl = findViewById(R.id.touchup_rv_item_child_music);
            tu_rv_music_icon = findViewById(R.id.tu_rv_music_iv1);
            tu_rv_music_name_tv = findViewById(R.id.tu_rv_music_name_tv1);
            //timer
            timer_rl= findViewById(R.id.touchup_rv_item_child_timer);
            tu_rv_timer_icon  =findViewById(R.id.touchup_rv_item_child_timer_iv1);
            tu_rv_timer_tv1 = findViewById(R.id.touchup_rv_item_child_timer_tv1);
            tu_rv_timer_tv2 = findViewById(R.id.touchup_rv_item_child_timer_tv2);
            //alarm_clock
            alarm_clock_rl= findViewById(R.id.touchup_rv_item_child_alarm_clock);
            tu_rv_alarm_clock_icon  =findViewById(R.id.touchup_rv_item_child_alarm_clock_iv1);
            tu_rv_alarm_clock_tv1 = findViewById(R.id.touchup_rv_item_child_alarm_clock_tv1);
            tu_rv_alarm_clock_tv2 = findViewById(R.id.touchup_rv_item_child_alarm_clock_tv2);
            tu_rv_alarm_clock_tv3 = findViewById(R.id.touchup_rv_item_child_alarm_clock_tv3);

        }
        public <T extends View> T findViewById(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }
        public <T extends View> T GetVRoot(int id) {
            View view = mViews2.get(id);
            if (view == null) {
                view = itemView;
                mViews2.put(id, view);
            }
            return (T) view;
        }
        public void clearViews(){
            mViews.clear();
            mViews2.clear();
        }
    }
    public void clean(){
        if(holder!=null){
            holder.clearViews();
            holder = null;
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_TYPE_HEAD) {
            mViewHolder viewHolder = (mViewHolder) holder;
            String date = DateFormat.format("EEEE \nMM月 \ndd ", System.currentTimeMillis()).toString();
            //viewHolder.tv_date.setText(date);
        }
        {//普通类型ViewHolder
            mViewHolder viewHolder = (mViewHolder) holder;
            if (beans.get(position).getType() != -1) {
                try {
                    viewHolder.app_name.setText(beans.get(position).getApp_name());
                    viewHolder.textView2.setText(context.getString(R.string.all_apps));
                    viewHolder.textView2.setTextColor(context.getResources().getColor(R.color.text_color_default));
                    viewHolder.app_icon.setImageDrawable(AppUtils.getAppIcon(context, beans.get(position).getPackage_name()));
                    //rv_itemgridview  //rl2,more item //common_item,常规
                    LogUtil.i(TAG, "----->type;" + beans.get(position).getType(), 1);
                    viewHolder.rl_plus_o.setVisibility(View.GONE);
                    viewHolder.menu_item.setVisibility(View.GONE);
                    viewHolder.rv_item_o.setVisibility(View.GONE);
                    viewHolder.iv_del1.setVisibility(View.GONE);
                    //mViewHolder.iv_del2.setVisibility(View.GONE);
                    viewHolder.iv_up1.setVisibility(View.GONE);
                    viewHolder.iv_up2.setVisibility(View.GONE);

                    if (beans.get(position).getType() == 0) {
                        GradientDrawable shapeDrawable = (GradientDrawable) viewHolder.common_item_bg.getBackground();
                        if (beans.get(position).getId() < AppUtil.app_colors.length) {
                            shapeDrawable.setColor(context.getResources().getColor(AppUtil.app_colors[beans.get(position).getId()]));
                        }
                        viewHolder.common_item_bg.setBackground(shapeDrawable);
                    }
                    viewHolder.common_item_bg.setVisibility(View.GONE);
                    viewHolder.common_item.setVisibility(View.GONE);
                    viewHolder.calendar_rl.setVisibility(View.GONE);
                    viewHolder.weather_rl.setVisibility(View.GONE);
                    viewHolder.sport_rl.setVisibility(View.GONE);
                    viewHolder.music_rl.setVisibility(View.GONE);
                    viewHolder.timer_rl.setVisibility(View.GONE);
                    viewHolder.alarm_clock_rl.setVisibility(View.GONE);
                    switch (beans.get(position).getType()) {
                        case 0:
                            viewHolder.common_item_bg.setVisibility(View.VISIBLE);
                            viewHolder.common_item.setVisibility(View.VISIBLE);
                            switch (beans.get(position).getId()) {
                                case 0:
                                    viewHolder.common_item.setVisibility(View.GONE);
                                    viewHolder.touchup_rv_item_child_calendar_tv1.setText(DateUtils.GetWeek());
                                    viewHolder.touchup_rv_item_child_calendar_tv2.setText(DateUtils.GetMonthAndDay());
                                    viewHolder.calendar_rl.setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    viewHolder.common_item.setVisibility(View.GONE);
                                    viewHolder.weather_rl.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    viewHolder.common_item.setVisibility(View.GONE);
                                    viewHolder.sport_rl.setVisibility(View.VISIBLE);
                                    viewHolder.tu_rv_sport_tv1.setText("户外跑步");
                                    viewHolder.tu_rv_sport_tv2.setText("0千卡");
                                    viewHolder.tu_rv_sport_tv_date.setText(DateUtils.GetDateAndWeek());
                                    break;
                                case 3:
                                    viewHolder.common_item.setVisibility(View.GONE);
                                    viewHolder.music_rl.setVisibility(View.VISIBLE);
                                    viewHolder.tu_rv_music_name_tv.setText("一曲相思");
                                    viewHolder.tu_rv_music_icon.setImageDrawable(context.getResources().getDrawable(R.mipmap.icon_music_pause));
                                    break;
                                case 6:
                                    viewHolder.common_item.setVisibility(View.GONE);
                                    viewHolder.timer_rl.setVisibility(View.VISIBLE);
                                    viewHolder.tu_rv_timer_tv1.setText(beans.get(position).getApp_name());
                                    viewHolder.tu_rv_timer_tv2.setText("00:00.00");
                                    break;
                                case 7:
                                    viewHolder.common_item.setVisibility(View.GONE);
                                    viewHolder.alarm_clock_rl.setVisibility(View.VISIBLE);
                                    viewHolder.tu_rv_alarm_clock_tv1.setText(beans.get(position).getApp_name());
                                    viewHolder.tu_rv_alarm_clock_tv2.setText("03:23");
                                    viewHolder.tu_rv_alarm_clock_tv3.setText("12小时02分钟");
                                    break;
                                default:
                                    viewHolder.common_item.setVisibility(View.VISIBLE);
                                    break;
                            }
                            break;
                        case 1://more menu
                            viewHolder.menu_item.setVisibility(View.VISIBLE);
                            break;
                        case 2://rv firstlayer
                            viewHolder.rv_item_o.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            viewHolder.rl_plus_o.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    if (beans.get(position).getEditMode()) {//编辑模式
                        if (beans.get(position).getType() == 0) {
                            viewHolder.iv_del1.setVisibility(View.VISIBLE);
                            //mViewHolder.iv_del2.setVisibility(View.VISIBLE);
                            viewHolder.iv_up1.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.iv_up2.setVisibility(View.VISIBLE);
                        }
                    }/* else {
            if(beans.get(position).getType()==0) {
                mViewHolder.iv_del1.setVisibility(View.GONE);
                //mViewHolder.iv_del2.setVisibility(View.GONE);
                mViewHolder.iv_up1.setVisibility(View.GONE);
            }else {
                mViewHolder.iv_up2.setVisibility(View.GONE);
            }
        }*/
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
                            if (onItemLongClickListener != null) {
                                int pos = viewHolder.getLayoutPosition();
                                onItemLongClickListener.onItemLongClik(viewHolder.itemView, pos);
                            }
                            return false;
                        }
                    });

        /* mViewHolder.common_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DefaultAppsOpreator.InsertPlusItem(beans, position, VerticalOverlayAdapter.this, mHandler);
                DefaultAppsOpreator.SetFavoriteStatus(fav_beans, position, favoriteAdapter, mHandler);
                return false;
            }
        });
       mViewHolder.rv_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DefaultAppsOpreator.InsertPlusItem(beans, position, VerticalOverlayAdapter.this, mHandler);
                DefaultAppsOpreator.SetFavoriteStatus(fav_beans, position, favoriteAdapter, mHandler);
                return false;
            }
        });*/
                    viewHolder.iv_del1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            DefaultAppsOpreator.DelItem(position, beans, VerticalOverlayAdapter.this);
                        }
                    });
                    // DefaultAppsOpreator.AddItem((position, beans, AppInfoBean bean, VerticalOverlayAdapter a);
                    viewHolder.iv_up1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            DefaultAppsOpreator.SetTopItem(position, beans, VerticalOverlayAdapter.this, mHandler);
                            notifyDataSetChanged();
                            //mHandler.sendEmptyMessageDelayed
                        }
                    });
                    viewHolder.iv_up2.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            DefaultAppsOpreator.SetTopItem(position, beans, VerticalOverlayAdapter.this, mHandler);
                        }
                    });
                    favoriteAdapter = CommonUtil.InitFavRecyclerView(viewHolder.fav_item_rv, fav_beans, launcher, context, mHandler);
                }catch(Exception e){
                    LogUtil.e(TAG,"e:"+e);
                }
            }
        }

        onViewRecycled(holder);
    }
    //
    public FavoriteAdapter GetFavoriteAdapter() {
        if (favoriteAdapter != null) {
            return favoriteAdapter;
        } else {
            return null;
        }
    }

    public void DelPlusButton() {
        DefaultAppsOpreator.RemovePulsItem(beans, VerticalOverlayAdapter.this);
        FavoriteAppsOpreator.Favorite_UnEdit(fav_beans, favoriteAdapter);
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }
    public void setItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }
    public void setItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.onItemLongClickListener = mOnItemLongClickListener;
    }


}
