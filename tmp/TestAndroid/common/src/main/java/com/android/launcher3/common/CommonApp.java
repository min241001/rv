package com.android.launcher3.common;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.PhoneSIMCardUtil;
import com.android.launcher3.common.utils.ScreenAdapter;

public class CommonApp extends Application {

    private static final String TAG = CommonApp.class.getSimpleName() + "--->>>";

    private static CommonApp instance;

    public static CommonApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG + "应用程序入口", LogUtil.TYPE_RELEASE);
        instance = this;
        AppLocalData.getInstance().init(this);
        PhoneSIMCardUtil.getInstance().init(this);
        ScreenAdapter.setup(this);
        ScreenAdapter.registerDefault(this, 320);

    }

    @Override
    protected void attachBaseContext(Context base) {
        Configuration configuration = base.getResources().getConfiguration();
        float fontScale = configuration.fontScale;
        if (fontScale == 1.0f ){
            super.attachBaseContext(wrap(base, 1.3f));
        }else {
            super.attachBaseContext(base);
        }
    }

    private static Context wrap(Context context, float fontScale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.fontScale = fontScale;
        return context.createConfigurationContext(configuration);
    }
}
