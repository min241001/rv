package com.android.launcher3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.moudle.launcher.util.WatchAccountUtils;

/**
 *  获取账号
 */
public class UpdateAccIdReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtil.d("走获取账号广播：接收", LogUtil.TYPE_RELEASE);
        WatchAccountUtils.getInstance().getWaAcctIdByNet(3);

    }
}
