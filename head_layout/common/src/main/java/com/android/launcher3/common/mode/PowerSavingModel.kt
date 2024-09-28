package com.android.launcher3.common.mode

import android.annotation.SuppressLint
import android.content.Context
import com.android.launcher3.common.CommonApp
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.utils.BluetoothUtil
import com.android.launcher3.common.utils.MobileDataUtil
import com.android.launcher3.common.utils.WifiUtil
import com.baehug.lib.nettyclient.NettyTcpClient

object PowerSavingModel {

    @SuppressLint("StaticFieldLeak")
    private val processMode = ProcessMode(CommonApp.getInstance())

    private val context: Context
        get() = CommonApp.getInstance()

    fun openPowerSaving(){
        AppLocalData.getInstance().wifi = WifiUtil.isWifiEnabled(context)
        AppLocalData.getInstance().mobileData = MobileDataUtil.getDataEnabled(context)
        AppLocalData.getInstance().bluetooth = BluetoothUtil.isBluetoothEnabled()

        WifiUtil.disableWifi(context)
        MobileDataUtil.setDataEnabled(context,false)
        BluetoothUtil.disableBluetooth()

        NettyTcpClient.getInstance().close()
        processMode.processTasks()
    }

    fun closePowerSaving(){
        try {
            if (AppLocalData.getInstance().wifi){
                WifiUtil.enableWifi(context)
            }

            if (AppLocalData.getInstance().mobileData){
                MobileDataUtil.setDataEnabled(context,true)
            }

            if (AppLocalData.getInstance().bluetooth){
                BluetoothUtil.enableBluetooth()
            }
            NettyTcpClient.getInstance().resetAndConnect()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}