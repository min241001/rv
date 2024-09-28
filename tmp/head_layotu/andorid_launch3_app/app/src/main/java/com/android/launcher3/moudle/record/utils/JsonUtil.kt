package com.android.launcher3.moudle.record.utils

import android.text.TextUtils
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.moudle.record.modle.RecordBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonUtil {

    companion object {
        /**
         * 集合转json字符串
         */
        fun list2json(list: List<RecordBean>): String {
            if (list == null) {
                return ""
            }
            val gson = Gson()
            return gson.toJson(list)
        }

        /**
         * json字符串转集合
         */
        fun json2list(json: String): List<RecordBean> {
            if (TextUtils.isEmpty(json)) {
                return ArrayList()
            }

            try {
                val listType = object : TypeToken<List<RecordBean?>?>() {}.type
                return Gson().fromJson(json, listType)
            } catch (e: Exception) {
                LogUtil.e(e.message, LogUtil.TYPE_RELEASE)
            }
            return ArrayList()
        }
    }

}