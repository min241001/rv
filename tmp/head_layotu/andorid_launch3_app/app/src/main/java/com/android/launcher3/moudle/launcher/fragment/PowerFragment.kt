package com.android.launcher3.moudle.launcher.fragment

import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.View.OnClickListener
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseFragment
import com.android.launcher3.common.bean.AppBean
import com.android.launcher3.common.constant.SettingsConstant.WATCH_BATTERY_CHANGE
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.dialog.ComDialog
import com.android.launcher3.common.mode.PowerSavingModel
import com.android.launcher3.common.utils.AppLauncherUtils
import com.android.launcher3.common.utils.LauncherAppManager
import com.android.launcher3.netty.constant.ServiceConstant.*
import kotlinx.android.synthetic.main.fragment_power.*

class PowerFragment : BaseFragment(),OnClickListener {

    private val dialog by lazy {
        ComDialog.Builder(context
        ) {
            AppLocalData.getInstance().power = false
            PowerSavingModel.closePowerSaving()
            AppLauncherUtils.jumpActivity(context , 1)
        }
    }

    override fun getResourceId(): Int {
        return R.layout.fragment_power
    }

    override fun initView(view: View) {
        dialog.create()
        dialog.setTitle(getString(com.android.launcher3.common.R.string.super_power_mode))
        dialog.setContent(getString(com.android.launcher3.common.R.string.close_power))

        activity?.contentResolver?.registerContentObserver(
            Settings.System.getUriFor(WATCH_BATTERY_CHANGE),
            true,
            contentObserver
        )
    }

    override fun initData() {
        getBattery()
    }

    override fun onResume() {
        super.onResume()
        getBattery()
    }

    override fun initEvent() {
        tv_power_exit.setOnClickListener(this)
        iv_dialer.setOnClickListener(this)
        iv_contact.setOnClickListener(this)
        iv_message.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.contentResolver?.unregisterContentObserver(contentObserver)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.tv_power_exit ->{
                dialog.show()
            }

            R.id.iv_dialer ->{
                startApp(DIALER_PACKAGE)
            }

            R.id.iv_contact ->{
                startApp(CONTACT_PACKAGE)
            }

            R.id.iv_message ->{
                startApp(SMS_PACKAGE)
            }
        }
    }

    private val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            getBattery()
        }
    }

    private fun startApp(packageName : String){
        val appBean = AppBean()
        appBean.packageName = packageName
        LauncherAppManager.launcherApp(activity, appBean)
    }

    private fun getBattery(){
        try {
            val battery = AppLocalData.getInstance().battery
            tv_power_value.text ="超级省电中$battery%"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}