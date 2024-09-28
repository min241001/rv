package com.android.launcher3.moudle.timer.adpater;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.R;
import java.util.List;


/**
 * * Author : fangzheng
 * Date : 2024/9/24/
 * Details : 所有计时器列表界面适配器
 */



public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder> {

    private final List<String> dataList;

    public TimerAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_list_all, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(dataList.get(position));
    }


    @Override
    public int getItemCount() {
        if(!dataList.isEmpty())
        return dataList.size();
        else
            return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.listall_textview);

        }
    }
}

