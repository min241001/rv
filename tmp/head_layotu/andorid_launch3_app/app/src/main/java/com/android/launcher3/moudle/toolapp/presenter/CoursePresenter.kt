package com.android.launcher3.moudle.toolapp.presenter

import com.android.launcher3.common.base.BasePresenter
import com.android.launcher3.common.bean.Course
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.network.api.ApiHelper
import com.android.launcher3.common.network.listener.BaseListener
import com.android.launcher3.moudle.toolapp.view.CourseView

class CoursePresenter : BasePresenter<CourseView>() {

    private val waAcctId by lazy { AppLocalData.getInstance().watchId }

    fun getCourse(waAccId:String){

        if (waAcctId.isNullOrEmpty()){
            return
        }

        ApiHelper.getCourse(object : BaseListener<Course> {
            override fun onError(code: Int, msg: String) {
                if (isViewAttached){
                    view.onCourseFail(code,msg)
                }
            }

            override fun onSuccess(response: Course) {
                if (isViewAttached) {
                    view.onCourseSuccess(response)
                }
            }
        }, waAccId)
    }
}