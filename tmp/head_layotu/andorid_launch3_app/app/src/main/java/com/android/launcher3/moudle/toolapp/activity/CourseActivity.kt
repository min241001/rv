package com.android.launcher3.moudle.toolapp.activity

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.launcher3.App
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseMvpActivity
import com.android.launcher3.common.bean.Course
import com.android.launcher3.common.bean.CourseList
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.dialog.Loading
import com.android.launcher3.common.utils.CommonUtils
import com.android.launcher3.common.utils.CommonUtils.isNetworkConnected
import com.android.launcher3.common.utils.ToastUtils
import com.android.launcher3.moudle.toolapp.fragment.CourseFragment
import com.android.launcher3.moudle.toolapp.presenter.CoursePresenter
import com.android.launcher3.moudle.toolapp.view.CourseView
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_course.*
import kotlinx.android.synthetic.main.no_network_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CourseActivity : BaseMvpActivity<CourseView, CoursePresenter>(), CourseView {

    private val waAcctId by lazy { AppLocalData.getInstance().watchId }

    private val map = HashMap<Int, CourseList>()

    private var week = CommonUtils.getCourseWeek()

    private var isBack = false

    private lateinit var pagerAdapter: PagerAdapter

    private val mLoading: Loading by lazy { Loading.init(this) }

    override fun getResourceId(): Int {
        return R.layout.activity_course
    }

    override fun createPresenter(): CoursePresenter {
        return CoursePresenter()
    }

    override fun initView() {
        val isConnect = isNetworkConnected(App.getInstance())
        if (!isConnect) {
            no_network_layout.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        mLoading.startPregress(com.android.launcher3.common.R.layout.loading_view, 30000L)
        mLoading.setCancelable(false)
        mPresenter.getCourse(waAcctId)

        val header = ClassicsHeader(this)
        header.setEnableLastTime(false)
        header.setFinishDuration(0)
        refreshLayout.setRefreshHeader(header)

        refreshLayout.setEnableAutoLoadMore(false)

        refreshLayout.setOnRefreshListener(OnRefreshListener {
            mPresenter.getCourse(waAcctId)
        })
    }

    override fun onCourseSuccess(resp: Course) {
        mLoading.stopPregress()
        refreshLayout.finishRefresh()
        layout_error.visibility = View.GONE
        val courseList = resp.list
        map.clear()
        courseList.forEachIndexed { _, courseList ->
            map[courseList.week] = courseList
        }
        pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = week - 1

        viewPager.addOnPageChangeListener(object : OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position > 0 ){
                    isBack = false
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == 0 && viewPager.currentItem == 6){
                   ToastUtils.show("已经是最后一页了哦")
                }
            }
        })
    }

    override fun onCourseFail(code: Int, msg: String) {
        mLoading.stopPregress()
        refreshLayout.finishRefresh()
        layout_error.visibility = View.VISIBLE
        ToastUtils.show(msg)
    }


    inner class PagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            val week = position + 1
            return CourseFragment.newInstance(week, map[week])
        }

        override fun getCount(): Int {
            return 7
        }
    }

    override fun onBackPressed() {
        if (no_network_layout.isShown) {
            finish()
            return
        }

        if (viewPager.currentItem == 0 ) {
            if (isBack){
                finish()
            }else{
                lifecycleScope.launch {
                    delay(100)
                    isBack = true
                }
               ToastUtils.show( "已经是第一页了哦")
            }

        } else {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }
}