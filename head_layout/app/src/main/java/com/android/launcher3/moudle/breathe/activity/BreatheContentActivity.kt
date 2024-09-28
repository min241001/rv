package com.android.launcher3.moudle.breathe.activity

import android.view.View
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseMvpActivity
import com.android.launcher3.moudle.breathe.presenter.BreathePresenter
import com.android.launcher3.moudle.breathe.view.BreatheView
import kotlinx.android.synthetic.main.activity_breathe_content.*

class BreatheContentActivity : BaseMvpActivity<BreatheView, BreathePresenter>(), BreatheView {

    private val status by lazy { intent.getIntExtra("status", 0) }

    override fun getResourceId(): Int {
        return R.layout.activity_breathe_content
    }

    override fun createPresenter(): BreathePresenter {
        return BreathePresenter()
    }

    override fun initView() {
        if (status == 1) {
            tv_picker.visibility = View.VISIBLE
            tv_breathe.setText(R.string.breathe_duration)
        } else {
            tv_breathe.setText(R.string.breathe_rhythm)
        }
    }

    override fun initData() {
        mPresenter.getData(status)
    }

    override fun onDataSuccess(data: List<String>) {
        picker.displayedValues = data.toTypedArray()
        picker.minValue = 0
        picker.maxValue = data.size - 1
        picker.value = mPresenter.getKeepValue(status)
    }

    fun click(view: View) {
        mPresenter.setKeepValue(status, picker.value)
        finish()
    }
}