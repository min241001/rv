package com.android.launcher3.moudle.toolapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.launcher3.R
import com.android.launcher3.common.bean.CourseList
import com.android.launcher3.common.utils.CommonUtils
import com.android.launcher3.moudle.toolapp.adapter.CourseAdapter

class CourseFragment : Fragment() {

    private var week: Int = 1
    private var courseList: CourseList? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_course, container, false)
        val rl_morning = view.findViewById<RecyclerView>(R.id.rl_morning)
        val rl_afternoon = view.findViewById<RecyclerView>(R.id.rl_afternoon)
        val tv_morning = view.findViewById<TextView>(R.id.tv_morning)
        val tv_afternoon = view.findViewById<TextView>(R.id.tv_afternoon)
        val tv_course_week = view.findViewById<TextView>(R.id.tv_course_week)
        val layout_empty = view.findViewById<LinearLayout>(R.id.layout_empty)

        tv_course_week.text = CommonUtils.transformWeek(week)
        if (courseList != null) {
            val morningData = courseList!!.morningCourse
            val afternoonData = courseList!!.afternoonCourse

            if (morningData.isEmpty()) {
                tv_morning.visibility = View.GONE
            } else {
                tv_morning.visibility = View.VISIBLE
                rl_morning.layoutManager = LinearLayoutManager(requireContext())
                rl_morning.adapter = CourseAdapter(morningData, R.layout.item_course)
            }

            if (afternoonData.isEmpty()) {
                tv_afternoon.visibility = View.GONE
            } else {
                tv_afternoon.visibility = View.VISIBLE
                rl_afternoon.layoutManager = LinearLayoutManager(requireContext())
                rl_afternoon.adapter = CourseAdapter(afternoonData, R.layout.item_course)
            }

            if (morningData.isEmpty() && afternoonData.isEmpty()){
                layout_empty.visibility = View.VISIBLE
            }

        }else{
            layout_empty.visibility = View.VISIBLE
            tv_morning.visibility = View.GONE
            tv_afternoon.visibility = View.GONE
        }

        return view
    }

    companion object {

        fun newInstance(week: Int, courseList: CourseList?): CourseFragment {
            val fragment = CourseFragment()
            fragment.week = week
            fragment.courseList = courseList
            return fragment
        }
    }
}