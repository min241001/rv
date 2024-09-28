package com.android.launcher3.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.launcher3.common.constant.SettingsConstant.DIAL_RECEIVER
import com.android.launcher3.common.constant.SettingsConstant.WALLPAPER_RECEIVER
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.utils.BaseActivityManager

class ThemeBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            WALLPAPER_RECEIVER ->{
                val themeId = intent.getStringExtra("themeId")
                if (themeId != null){
                    AppLocalData.getInstance().wallpaperDefaultId = themeId.toInt()
                }
            }

            DIAL_RECEIVER ->{
                try {
                    val themeId = intent.getStringExtra("themeId")
                    val filePath = intent.getStringExtra("filePath")
                    val className = intent.getStringExtra("className")
                    AppLocalData.getInstance().faceDefaultId = themeId.toInt()
                    AppLocalData.getInstance().faceFilePath = filePath
                    AppLocalData.getInstance().faceClassName = className
                    BaseActivityManager.getInstance().finishActivityList()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }
}