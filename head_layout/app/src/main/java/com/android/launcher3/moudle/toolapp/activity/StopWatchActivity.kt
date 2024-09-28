package com.android.launcher3.moudle.toolapp.activity

import android.annotation.SuppressLint
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseMvpActivity
import com.android.launcher3.moudle.toolapp.adapter.StopWatchAdapter
import com.android.launcher3.moudle.toolapp.presenter.StopWatchPresenter
import com.android.launcher3.moudle.toolapp.view.StopWatchView
import com.android.launcher3.widget.RecyclerViewExtC

class StopWatchActivity : BaseMvpActivity<StopWatchView, StopWatchPresenter>(), StopWatchView {

    private lateinit var btnStart: ImageButton
    private lateinit var btnLap: ImageButton
    private lateinit var btnReset: ImageButton
    private lateinit var tvTimer:TextView

    private var recyclerView: RecyclerViewExtC? = null

    private val mData = ArrayList<String>()

    private val adapter by lazy { StopWatchAdapter(mData) }

    override fun getResourceId(): Int {
        return R.layout.activity_stop_watch
    }

    override fun createPresenter(): StopWatchPresenter {
        return StopWatchPresenter()
    }

    override fun initView() {
        recyclerView = findViewById(R.id.rl_stopWatch)
        btnStart = findViewById(R.id.ib_start)
        btnLap = findViewById(R.id.ib_count)
        btnReset = findViewById(R.id.ib_reset)
        tvTimer = findViewById(R.id.tv_Timer)

        val layoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
    }

    override fun initEvent() {

        btnStart.setOnClickListener {
            mPresenter.startPauseTimer()
        }

        btnLap.setOnClickListener {
            mPresenter.lapTimer()
        }

        btnReset.setOnClickListener {
            mPresenter.resetTimer()
        }
    }

    override fun updateTimerText(data: String) {
        runOnUiThread {
            tvTimer.text = data
        }
    }

    override fun updateLapText(data: List<String>) {
        mData.clear()
        mData.addAll(data)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun resetTimer() {
        tvTimer.text = "00:00:00.00"
        btnLap.isEnabled = false
        btnReset.isEnabled = false
        mData.clear()
        adapter.notifyDataSetChanged()
    }

    override fun pauseTimer() {
        btnLap.isEnabled = false
        btnReset.isEnabled = true
        btnStart.setBackgroundResource(R.mipmap.icon_start)
    }

    override fun startTimer() {
        btnLap.isEnabled = true
        btnReset.isEnabled = false
        btnStart.setBackgroundResource(R.mipmap.icon_pause)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.removeTimer()
    }
}