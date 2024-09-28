package com.android.launcher3.moudle.record.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.launcher3.R
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.moudle.notification.util.TimeUtils
import com.android.launcher3.moudle.record.modle.RecordBean

class RecordAdapter(var mData: ArrayList<RecordBean>) :
    RecyclerView.Adapter<RecordAdapter.Holder>() {

    private var onItemClickListener: OnRecordItemClickListener? = null

    private var flag: Boolean = false

    fun setData(data: ArrayList<RecordBean>) {
        mData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_record_cover, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bean = mData.get(position)
        holder.tvName.text = bean.name
        holder.tvTime.text = TimeUtils.long2str(bean.time, TimeUtils.PATTERN_YMD)
        holder.tvDuration.text = TimeUtils.getRecordTime(bean.duration)
        holder.cb.isChecked = bean.checked
        if (flag) {
            holder.cb.visibility = View.VISIBLE
        } else {
            holder.cb.visibility = View.GONE
        }

        holder.root.setOnClickListener(View.OnClickListener {
            LogUtil.i("onBindViewHolder: holder.root position $position", LogUtil.TYPE_RELEASE)
            onItemClickListener?.onItemClickListener(position)
        })

        holder.cb.setOnClickListener(View.OnClickListener {
            LogUtil.i("onBindViewHolder: holder.cb isChecked " + holder.cb.isChecked, LogUtil.TYPE_RELEASE)
            bean.checked = holder.cb.isChecked
            onItemClickListener?.onItemCheckedListener(position)
        })
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData.size
    }

    fun getItem(position: Int): RecordBean {
        return mData[position]
    }

    fun setFlag(flag: Boolean) {
        LogUtil.i("setFlag: flag $flag", LogUtil.TYPE_RELEASE)
        this.flag = flag
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnRecordItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnRecordItemClickListener {
        fun onItemClickListener(position: Int)

        fun onItemCheckedListener(position: Int)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView
        val tvTime: TextView
        val tvDuration: TextView
        val cb: CheckBox
        val root: View

        init {
            tvName = itemView.findViewById(R.id.tv_name)
            tvTime = itemView.findViewById(R.id.tv_time)
            tvDuration = itemView.findViewById(R.id.tv_duration)
            cb = itemView.findViewById(R.id.cb)
            root = itemView.findViewById(R.id.root)
        }
    }
}