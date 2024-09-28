package com.android.launcher3.moudle.notification.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.moudle.notification.bean.NotificationBean;
import com.android.launcher3.moudle.notification.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter {

    private List<NotificationBean> data = new ArrayList<>(/**/);
    private int selectedId = -1;

    public void setSelectedId(int selectedId) {
        this.selectedId = selectedId;
    }

    public void setList(List<NotificationBean> list) {
        this.data.clear();
        this.data.addAll(list);
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        private ImageView appIcon;
        private TextView title;
        private TextView content;
        private TextView container;

        public Holder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.iv_icon);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
            container = itemView.findViewById(R.id.tv_time);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Holder holder = new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NotificationBean bean = data.get(position);
        Log.d("TAG", "onBindViewHolder: " + bean.toString());
        ((Holder) holder).appIcon.setImageDrawable(bean.getAppIcon());
        ((Holder) holder).title.setText(bean.getAppName());
        ((Holder) holder).content.setText(bean.getContent());
        ((Holder) holder).container.setText(TimeUtils.getTimeAgo(data.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public NotificationBean getItem(int position) {
        return data.get(position);
    }

}
