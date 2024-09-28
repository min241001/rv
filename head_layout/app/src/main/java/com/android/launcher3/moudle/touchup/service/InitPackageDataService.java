package com.android.launcher3.moudle.touchup.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class InitPackageDataService extends Service {
    private static final String TAG = "InventorySyncService";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            InitData();
        } catch (Exception e) {
            stopSelf();

        }
        return super.onStartCommand(intent, flags, startId);

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                ///case Constants.msgWhat.UPDATE_APPS_DATA:
                    //StringUtil.SendEventMsg(Constants.eventWhat.SET_PACKAGE_DATA, "set_data");
                 //   stopSelf();
                  //  break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void InitData() {
        new Thread(new Runnable() {
            public void run() {
                // AppUtil.getAllAppInfo(InitPackageDataService.this, handler,true);
               // AppUtil.GetAppsA(InitPackageDataService.this, handler);
            }
        }).start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}