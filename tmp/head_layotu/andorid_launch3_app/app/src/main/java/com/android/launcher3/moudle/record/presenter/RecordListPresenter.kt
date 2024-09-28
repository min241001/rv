package com.android.launcher3.moudle.record.presenter

import android.content.Context
import com.android.launcher3.common.base.BasePresenter
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.common.utils.SharedPreferencesUtils
import com.android.launcher3.common.utils.ThreadPoolUtils
import com.android.launcher3.moudle.record.base.RecordConstant.*
import com.android.launcher3.moudle.record.modle.RecordBean
import com.android.launcher3.moudle.record.utils.FileUtil
import com.android.launcher3.moudle.record.utils.JsonUtil
import java.util.*

/**
 * Author : yanyong
 * Date : 2024/6/14
 * Details : 录音列表展示功能presenter
 */
class RecordListPresenter : BasePresenter<RecordListInterface>() {

    /**
     * 获取目录下的所有音频文件
     */
    fun getRecordFiles(context: Context) {
        val param = SharedPreferencesUtils.getParam(context, KEY_RECORD_DATA, "")
        LogUtil.i("getRecordFiles: param $param", LogUtil.TYPE_RELEASE)
        val recordBeans = JsonUtil.json2list(param.toString())
        if (isViewAttached) {
            view.onUpdateRecordList(recordBeans)
        }
    }

    /**
     * 删除被选中的录音文件
     */
    fun deleteCheckedRecordData(context: Context, list: List<RecordBean>) {
        if (context == null || list == null) {
            return
        }

        // 删除被选中的录音文件
        ThreadPoolUtils.getExecutorService().execute {
            for (bean in list) {
                if (bean.checked) {
                    val delete = FileUtil.deleteFile(bean.file)
                    LogUtil.i("deleteCheckedRecordData: delete $delete", LogUtil.TYPE_RELEASE)
                }
            }
        }

        val recordBeans = ArrayList<RecordBean>()
        for (bean in list) {
            if (!bean.checked) {
                recordBeans.add(bean)
            }
        }

        val json = JsonUtil.list2json(recordBeans)
        // 保存未选中的文件
        SharedPreferencesUtils.setParam(context, KEY_RECORD_DATA, json)
        LogUtil.i("deleteCheckedRecordData: json $json", LogUtil.TYPE_RELEASE)

        getRecordFiles(context)
    }
}