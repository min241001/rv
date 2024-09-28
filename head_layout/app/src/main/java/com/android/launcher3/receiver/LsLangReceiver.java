package com.android.launcher3.receiver;

import static com.android.launcher3.common.constant.SettingsConstant.LS_LAND_MODE_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.launcher3.moudle.island.IslandFloat;


public class LsLangReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(LS_LAND_MODE_ACTION)) {
            int ls_land_mode = intent.getIntExtra("mode", 0);
            if (ls_land_mode == 1) {
                Log.e("TAG", "----1----");
                //设置中灵动岛开关打开
                //读取通知
                //遍历通知是否有音频播放
                //有音频播放 显示音频播放悬浮窗
                //无音频播放 显示短信，电话，微聊是否有未读

            } else {
                //灵动岛功能关闭
                Log.e("TAG", "----0----");
                new IslandFloat().disMissAllFloat();
            }

        }
    }
}
