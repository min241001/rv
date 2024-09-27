package com.android.launcher3.common.mode;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import androidx.annotation.ArrayRes;

import com.android.launcher3.common.BuildConfig;
import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.SettingBean;

import java.util.List;

/**
 * @Author: shensl
 * @Description：基础业务mode
 * @CreateDate：2023/12/10 14:09
 * @UpdateUser: shensl
 */
abstract class BaseMode<T> {

    protected static final String TAG = BaseMode.class.getSimpleName();

    protected Context context;

    public BaseMode(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    /**
     * 加载默认数据
     *
     * @return
     */
    public abstract List<T> loadDefaultData();

    protected Resources getResources() {
        return context.getResources();
    }

    /**
     * 是否支持的类型
     *
     * @param screenType
     * @return
     */
    protected boolean isSupportType(int screenType) {
        ////if (BuildConfig.SCREEN_TYPE) {
            // 圆款
            return screenType == 0 || screenType == 2;
       //// }
        // 方款
        ////return screenType == 1 || screenType == 2;
    }

    protected Class<?> getDefaultClassById(@ArrayRes int id, int defaultId) throws ClassNotFoundException {
        String workspaceClass = "";

        TypedArray workspaceDefaultArray = getResources().obtainTypedArray(id);
        int[] workspaceIdArray = getResources().getIntArray(workspaceDefaultArray.getResourceId(0, -1));
        String[] workspaceClassArray = getResources().getStringArray(workspaceDefaultArray.getResourceId(3, -1));

        for (int i = 0; i < workspaceIdArray.length; i++) {
            if (workspaceIdArray[i] == defaultId) {
                workspaceClass = workspaceClassArray[i];
                break;
            }
        }

        Log.d(TAG, "--------- WorkspaceClass: " + workspaceClass);

        return Class.forName(workspaceClass);
    }

}