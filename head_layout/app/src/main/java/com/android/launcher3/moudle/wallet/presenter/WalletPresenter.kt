package com.android.launcher3.moudle.wallet.presenter

import com.android.launcher3.common.base.BasePresenter
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.network.api.ApiHelper
import com.android.launcher3.common.network.listener.BaseListener
import com.android.launcher3.common.network.resp.ResidueResp
import com.android.launcher3.common.utils.LogUtil
import com.android.launcher3.moudle.wallet.view.WalletView

class WalletPresenter : BasePresenter<WalletView>() {

    private val waAcctId by lazy { AppLocalData.getInstance().watchId }

    fun getResidue(waAccId:String){

        if (waAcctId.isNullOrEmpty()){
            return
        }

        ApiHelper.getResidue(object : BaseListener<ResidueResp> {
            override fun onError(code: Int, msg: String) {
                LogUtil.d(
                    TAG + "获取数据失败 $code --- $msg",
                    LogUtil.TYPE_RELEASE
                )
            }
            override fun onSuccess(response: ResidueResp) {
                LogUtil.d(
                    TAG + "获取数据成功，数据为：" + response,
                    LogUtil.TYPE_RELEASE
                )
                if (isViewAttached) {
                    view.setDiamond(response)
                }
            }
        }, waAccId)
    }
}