package com.android.launcher3.moudle.breathe.presenter

import com.android.launcher3.App
import com.android.launcher3.common.base.BasePresenter
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.moudle.breathe.view.BreatheView

class BreathePresenter : BasePresenter<BreatheView>(){

    fun getData(status : Int){

        if (isViewAttached){
            view.onDataSuccess(
                 when (status) {
                    0 -> {
                        App.getInstance().resources.getStringArray(com.android.launcher3.common.R.array.rhythm)
                            .toList()
                    }

                    else -> {
                        App.getInstance().resources.getStringArray(com.android.launcher3.common.R.array.duration)
                            .toList()
                    }
                },
            )
        }
    }


    fun setKeepValue(status : Int , value :Int){
        when (status) {
            0 -> {
               AppLocalData.getInstance().rhythm = value
            }

            else -> {
                AppLocalData.getInstance().duration = value
            }
        }
    }

    fun getKeepValue(status : Int) : Int{
        return when (status) {
            0 -> {
                AppLocalData.getInstance().rhythm
            }

            else -> {
                AppLocalData.getInstance().duration
            }
        }
    }
}