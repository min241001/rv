package com.android.launcher3.moudle.timer.adpater;

import static com.android.launcher3.moudle.launcher.view.LauncherActivity.context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.moudle.timer.util.ItemUtil;
import com.android.launcher3.moudle.timer.view.TimerPauseActivity;

import java.util.List;


/**
 * * Author : fangzheng
 * Date : 2024/9/24
 * Details : 计时器主界面的列表适配器
 */


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ItemUtil> itemList;


    public MyAdapter(List<ItemUtil> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemUtil item = itemList.get(position);
        holder.textView1.setText(item.getSum_time());
        holder.textView2.setText(item.getCurrent_time());
        holder.imageButton.setBackgroundResource(item.getDrawable());

    }

    @Override
    public int getItemCount() {
        if(!itemList.isEmpty()) {
            return itemList.size();
        }else
            return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView1,textView2;
        ImageButton imageButton;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.sum_time);
            textView2 =itemView.findViewById(R.id.current_time);
            imageButton = itemView.findViewById(R.id.time_status);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Intent intent=new Intent(context, TimerPauseActivity.class);
            context.startActivity(intent);


        }
    }
}
