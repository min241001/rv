package com.baehug.callui.phone.utils

import android.content.Context
import android.provider.ContactsContract
import com.android.launcher3.common.utils.LogUtil

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 联系人信息获取工具类
 */
object ContactUtil {

    /**¬
     * 根据电话号码获取联系人
     */
    @JvmStatic
    fun getContentCallLog(mContext: Context?, number: String?): ContactInfo? {
        try {
            mContext?.contentResolver?.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ), null, null, null
            )?.use { phoneCursor ->
                while (phoneCursor.moveToNext()) {
                    val columnIndex =
                        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    if (columnIndex < 0) {
                        continue
                    }
                    var phoneNumber = phoneCursor.getString(columnIndex)
                    phoneNumber = phoneNumber?.replace("-", "")?.replace(" ", "")
                    if (number == phoneNumber) {
                        var displayName =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        if (displayName == null) {
                            displayName = phoneNumber
                        }
                        return ContactInfo(
                            displayName,
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                        )
                    }
                }
            }
            return null
        } catch (e: Exception) {
            LogUtil.e("getContentCallLog$e", LogUtil.TYPE_RELEASE)
            return null
        }
    }

    data class ContactInfo(val displayName: String, val photoUri: String?)
}