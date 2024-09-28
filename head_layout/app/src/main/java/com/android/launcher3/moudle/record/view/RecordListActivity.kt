package com.android.launcher3.moudle.record.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseMvpActivity
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.moudle.record.adapter.RecordAdapter
import com.android.launcher3.moudle.record.base.RecordConstant.KEY_BUNDLE
import com.android.launcher3.moudle.record.modle.RecordBean
import com.android.launcher3.moudle.record.presenter.RecordListInterface
import com.android.launcher3.moudle.record.presenter.RecordListPresenter
import com.android.launcher3.moudle.shortcut.util.BaseUtil
import com.android.launcher3.moudle.shortcut.util.IntentUtil
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.activity_record_list.*
import java.util.*

/**
 * Author : yanyong
 * Date : 2024/6/12
 * Details : 录音列表
 */
@Suppress("UNREACHABLE_CODE")
class RecordListActivity : BaseMvpActivity<RecordListInterface, RecordListPresenter>(),
    View.OnClickListener, RecordListInterface, RecordAdapter.OnRecordItemClickListener {

    // 编辑状态
    private var mEditFlag: Boolean = false

    private var mCount: Int = 0

    private var mData = ArrayList<RecordBean>()

    private var mAlertDialog: AlertDialog? = null

    private val mAdapter by lazy { RecordAdapter(mData) }

    override fun getResourceId(): Int {
        return R.layout.activity_record_list
    }

    override fun createPresenter(): RecordListPresenter {
        return RecordListPresenter()
    }

    override fun initView() {
        super.initView()
        LogUtil.i("initView: ", LogUtil.TYPE_RELEASE)
        tv_all.setOnClickListener(this)
        tv_edit.setOnClickListener(this)
        btn_delete.setOnClickListener(this)
        tv_edit.visibility = View.GONE
        tv_all.visibility = View.GONE
    }

    override fun initData() {
        super.initData()
        LogUtil.i("initData: ", LogUtil.TYPE_RELEASE)
        val layoutManager = LinearLayoutManager(this)
        //设置动画为空
        rv_record.itemAnimator = null
        rv_record.layoutManager = layoutManager
        rv_record.adapter = mAdapter
        mAdapter.setOnItemClickListener(this)
        mPresenter.getRecordFiles(this)
    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("onResume: ", LogUtil.TYPE_RELEASE)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(v: View?) {
        if (!BaseUtil.repeatedClicks(v, BaseUtil.DURATION_300)) {
            LogUtil.i("onClick: repeated click", LogUtil.TYPE_RELEASE)
            return
        }
        when (v!!.id) {
            // 全选/取消全选
            R.id.tv_all -> {
                if (mData.isEmpty()) {
                    return
                }
                val isCheckedAll = isCheckedAll()
                LogUtil.i("onClick: tv_all isCheckedAll $isCheckedAll", LogUtil.TYPE_RELEASE)
                /*if (isCheckedAll) {
                    tv_all.setText(R.string.select_all)
                } else {
                    tv_all.setText(R.string.select_cancel_all)
                }*/
                for (bean in mData) {
                    bean.checked = !isCheckedAll
                }
                mAdapter.notifyDataSetChanged()
                isCheckedAll()
                LogUtil.i("onClick: tv_all mCount $mCount", LogUtil.TYPE_RELEASE)
                if (mCount > 0) {
                    btn_delete.visibility = View.VISIBLE
                } else {
                    btn_delete.visibility = View.GONE
                }

            }
            // 编辑录音
            R.id.tv_edit -> {
                val isCheckedAll = isCheckedAll()
                LogUtil.i("onClick: tv_all mEditFlag $mEditFlag isCheckedAll: $isCheckedAll", LogUtil.TYPE_RELEASE)
                /*if (isCheckedAll) {
                    tv_all.setText(R.string.select_cancel_all)
                } else {
                    tv_all.setText(R.string.select_all)
                }*/
                mEditFlag = !mEditFlag
                if (mEditFlag) {
                    tv_edit.setText(R.string.cancel)
                    tv_all.visibility = View.VISIBLE
                } else {
                    tv_edit.setText(R.string.edit)
                    tv_all.visibility = View.GONE
                }
                mAdapter.setFlag(mEditFlag)
                for (bean in mData) {
                    bean.checked = false
                }
                btn_delete.visibility = View.GONE
            }
            // 删除录音
            R.id.btn_delete -> {
                LogUtil.i("onClick: btn_delete mEditFlag $mEditFlag", LogUtil.TYPE_RELEASE)
                showDialog()
            }
        }
    }

    /**
     * 判断是否所有item被选中
     */
    private fun isCheckedAll() :Boolean {
        mCount = 0
        for (bean in mData) {
            if (bean.checked) {
                mCount++
            }
        }
        LogUtil.i("isCheckedAll: mCount $mCount, size $mData.size", LogUtil.TYPE_RELEASE)
        return mCount == mData.size
    }

    /**
     * 刷新录音列表数据
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onUpdateRecordList(list: List<RecordBean>) {
        if (list == null) {
            return
        }
        LogUtil.i("onUpdateRecordList: " + list.size, LogUtil.TYPE_RELEASE)
        if (list.isNotEmpty()) {
            tv_not_record.visibility = View.GONE
            tv_edit.visibility = View.VISIBLE
        } else {
            tv_not_record.visibility = View.VISIBLE
            tv_edit.visibility = View.GONE
            tv_all.visibility = View.GONE
        }

        mData = list as ArrayList<RecordBean>
        Collections.sort(list) { o1, o2 ->
            (o2.time - o1.time).toInt()
        }
        mAdapter.setData(mData)
    }

    /**
     * 单个item点击监听
     */
    override fun onItemClickListener(position: Int) {
        val bean = mData[position]
        LogUtil.i("onItemClickListener: position $position mEditFlag $mEditFlag $bean", LogUtil.TYPE_RELEASE)
        // 编辑状态
        if (mEditFlag) {
            bean.checked = !bean.checked
            /*if (isCheckedAll()) {
                tv_all.setText(R.string.select_cancel_all)
            } else {
                tv_all.setText(R.string.select_all)
            }*/
            isCheckedAll()
            if (mCount > 0) {
                btn_delete.visibility = View.VISIBLE
            } else {
                btn_delete.visibility = View.GONE
            }
            mAdapter.notifyItemChanged(position)
            return
        }

        // 跳转到播放页
        val bundle = Bundle()
        bundle.putSerializable(KEY_BUNDLE, bean)
        IntentUtil.startActivity(this, RecordPlayActivity::class.java, bundle)
    }

    /**
     * CheckBox点击监听
     */
    override fun onItemCheckedListener(position: Int) {
        /*if (isCheckedAll()) {
            tv_all.setText(R.string.select_cancel_all)
        } else {
            tv_all.setText(R.string.select_all)
        }*/
        isCheckedAll()
        if (mCount > 0) {
            btn_delete.visibility = View.VISIBLE
        } else {
            btn_delete.visibility = View.GONE
        }
    }

    /**
     * 删除录音dialog
     */
    private fun showDialog() {
        val view: View = layoutInflater.inflate(R.layout.layout_dialog_view_record_delete, null, false)
        mAlertDialog = AlertDialog.Builder(this).setView(view).create()
        @SuppressLint("NonConstantResourceId")
        val listener = View.OnClickListener { v: View ->
            when (v.id) {
                R.id.btn_cancel -> {
                    mAlertDialog!!.dismiss()
                }
                R.id.btn_connect -> {
                    tv_edit.setText(R.string.edit)
                    tv_all.visibility = View.GONE
                    btn_delete.visibility = View.GONE
                    mEditFlag = false;
                    mAlertDialog!!.dismiss()
                    mAdapter.setFlag(false)
                    mPresenter.deleteCheckedRecordData(this, mData)
                }
            }
        }
        view.findViewById<View>(R.id.btn_cancel).setOnClickListener(listener)
        view.findViewById<View>(R.id.btn_connect).setOnClickListener(listener)
        if (!mAlertDialog!!.isShowing) {
            mAlertDialog!!.show()
        }
    }
}