package com.android.launcher3.moudle.toolapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.android.launcher3.R
import com.android.launcher3.common.CommonApp
import com.android.launcher3.common.base.BaseMvpActivity
import com.android.launcher3.common.utils.ToastUtils
import com.android.launcher3.moudle.alarm.activity.AlarmActivity
import com.android.launcher3.moudle.calendar.activity.CalendarActivity
import com.android.launcher3.moudle.toolapp.adapter.ToolAppAdapter
import com.android.launcher3.moudle.toolapp.entity.App
import com.android.launcher3.moudle.toolapp.presenter.ToolAppPresenter
import com.android.launcher3.moudle.toolapp.view.ToolAppView
import com.android.launcher3.widget.RecyclerViewExtC

/**
 * Author : thh
 * Date : 2024/2/2
 * Details : 工具
 */
class ToolAppActivity : BaseMvpActivity<ToolAppView, ToolAppPresenter>(),ToolAppView {

    private var recyclerView: RecyclerViewExtC? = null

    private val mData = ArrayList<App>()

    private val adapter by lazy { ToolAppAdapter(mData,R.layout.item_tool_app) }

    override fun getResourceId(): Int {
        return R.layout.activity_tool_app
    }

    override fun createPresenter(): ToolAppPresenter {
        return ToolAppPresenter()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {
        recyclerView = findViewById(R.id.rl_app)
        val layoutManager = GridLayoutManager(this,2)
        recyclerView?.layoutManager = layoutManager
        mData.addAll(mPresenter.initAppData())
        recyclerView?.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun initEvent() {
        super.initEvent()

        adapter.setOnItemClickListener{ _, _, position ->
            when (mData[position].packName) {
                "" -> {
                    val intent = Intent(this,StopWatchActivity::class.java)
                    startActivity(intent)
                }
                "com.android.logger" -> {
                    val intent = Intent(this, LoggerActivity::class.java)
                    startActivity(intent)
                }
                "com.android.calculator2" -> {
                    val intent = Intent(this, CalculatorActivity::class.java)
                    startActivity(intent)
                }

                "com.android.calendar" -> {
                    val intent = Intent(this, CalendarActivity::class.java)
                    startActivity(intent)
                }

                "com.android.deskclock" -> {
                    val intent = Intent(this, AlarmActivity::class.java)
                    startActivity(intent)
                }

                else -> {
                    try {
                        val intent = packageManager.getLaunchIntentForPackage(mData[position].packName)
                        startActivity(intent)
                    }catch (e:Exception){
                        ToastUtils.show("找不到该应用")
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}