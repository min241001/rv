package com.android.launcher3.moudle.toolapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.launcher3.R

class StopWatchAdapter(private var mData: ArrayList<String>) : RecyclerView.Adapter<StopWatchAdapter.Holder>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCountText: TextView
        val tvCount: TextView

        init {
            tvCountText = itemView.findViewById(R.id.tv_count_text)
            tvCount = itemView.findViewById(R.id.tv_count)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stop_watch, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tvCountText.text = "计次${mData.size - position}"
        holder.tvCount.text = mData[position]

    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun getItem(position: Int): String {
        return mData[position]
    }
}