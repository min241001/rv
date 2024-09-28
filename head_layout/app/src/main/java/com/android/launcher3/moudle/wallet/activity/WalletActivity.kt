package com.android.launcher3.moudle.wallet.activity

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import com.android.launcher3.App
import com.android.launcher3.R
import com.android.launcher3.common.base.BaseMvpActivity
import com.android.launcher3.common.constant.SettingsConstant
import com.android.launcher3.common.data.AppLocalData
import com.android.launcher3.common.network.resp.ResidueResp
import com.android.launcher3.common.utils.CommonUtils.isNetworkConnected
import com.android.launcher3.common.utils.ToastUtils
import com.android.launcher3.moudle.wallet.presenter.WalletPresenter
import com.android.launcher3.moudle.wallet.view.WalletView
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.no_network_layout.*

class WalletActivity : BaseMvpActivity<WalletView, WalletPresenter>(), WalletView {

    private val waAcctId by lazy { AppLocalData.getInstance().watchId }

    override fun getResourceId(): Int {
        return R.layout.activity_wallet
    }

    override fun createPresenter(): WalletPresenter {
        return WalletPresenter()
    }

    override fun initView() {
        super.initView()
        val isConnect =isNetworkConnected(App.getInstance());
        if (!isConnect){
            no_network_layout.visibility = View.VISIBLE
            wallet_layout.visibility = View.GONE
        } else {
            no_network_layout.visibility = View.GONE
            wallet_layout.visibility = View.VISIBLE
        }
        tv_waAcctId.text = waAcctId

        btn_buy.setOnClickListener {
            val watchId = AppLocalData.getInstance().watchId
            if (watchId.isNullOrEmpty()){
                ToastUtils.show("账号为空")
                return@setOnClickListener
            }
            startActivityAction(SettingsConstant.BUY_ACTION)
        }
    }


    override fun setDiamond(resp: ResidueResp) {
        runOnUiThread {
            tv_diamondNum.text = resp.diamondNum
            tv_integralNum.text = resp.integralNum
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getResidue(waAcctId)
    }
}