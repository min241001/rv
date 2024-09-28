package com.android.launcher3.moudle.meet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.App;
import com.android.launcher3.R;
import com.android.launcher3.common.network.api.Api;
import com.android.launcher3.common.network.resp.FriendResp;
import com.android.launcher3.common.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchedFriendsAdapter2 extends RecyclerView.Adapter<SearchedFriendsAdapter2.ItemHolder> {

    private List<FriendResp.FriendBean> list = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public SearchedFriendsAdapter2(List<FriendResp.FriendBean> list) {
        this.list = list;
    }

    public void setFriendBean(List<FriendResp.FriendBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searched_friends_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {
        FriendResp.FriendBean bean = list.get(position);
        holder.friend_name.setText(bean.getName());
        ImageUtil.displayImage(App.getInstance(), Api.getCdnUrl(bean.getAvatar()), holder.friend_avatar, R.drawable.icon_avatar);
        if (bean.getIfFriend() == 1) {
            holder.friend_ifFriend.setVisibility(View.GONE);
        } else {
            holder.friend_ifFriend.setVisibility(View.VISIBLE);
        }
        if (onItemClickListener != null) {
            final int pos = position;
            holder.searched_friend_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemRootClickListener(pos,list.get(pos));
                }
            });

            holder.friend_ifFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemAddFriendClickListener(pos,list.get(pos));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemRootClickListener(int position,FriendResp.FriendBean bean);

        void onItemAddFriendClickListener(int position,FriendResp.FriendBean bean);
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        View searched_friend_item_layout;
        TextView friend_name;
        ImageView friend_ifFriend;
        ImageView friend_avatar;

        public ItemHolder(View itemView) {
            super(itemView);
            friend_name = itemView.findViewById(R.id.friend_name);
            friend_ifFriend = itemView.findViewById(R.id.friend_ifFriend);
            friend_avatar = itemView.findViewById(R.id.friend_avatar);
            searched_friend_item_layout = itemView.findViewById(R.id.searched_friend_item_layout);
        }
    }
}
