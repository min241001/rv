package com.android.launcher3.common.mode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import com.android.launcher3.common.BuildConfig;
import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.WorkspaceBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeckchan
 * @Description：桌面风格业务实现
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class WorkspaceMode extends BaseMode<WorkspaceBean> implements IWorkspaceMode {

    public WorkspaceMode(Context context) {
        super(context);
    }

    @Override
    public List<WorkspaceBean> loadDefaultData() {
        List<WorkspaceBean> lists = new ArrayList<>();

        //读取表盘的配置文件
        TypedArray workspaceDefaultArray = getResources().obtainTypedArray(R.array._default_style);
        //读取表盘的ID
        int[] workspaceIdArray = getResources().getIntArray(workspaceDefaultArray.getResourceId(0, -1));
        //读取表盘配置的名称
        String[] workspaceNameArray = getResources().getStringArray(workspaceDefaultArray.getResourceId(1, -1));
        //读取表盘配置的缩略图
        TypedArray workspaceThumbArray = getResources().obtainTypedArray(workspaceDefaultArray.getResourceId(2, -1));
        //读取表盘的类型
        int[] workspaceTypeArray = getResources().getIntArray(workspaceDefaultArray.getResourceId(4, -1));
        for (int i = 0; i < workspaceTypeArray.length; i++) {
            if (!isSupportType(workspaceTypeArray[i]))
                continue;

            lists.add(new WorkspaceBean(workspaceIdArray[i], workspaceNameArray[i], getResources().getDrawable(workspaceThumbArray.getResourceId(i, -1), getResources().newTheme())));
        }
        workspaceDefaultArray.recycle();

        return lists;
    }

    @SuppressLint("ResourceType")
    @Override
    public Class<?> getDefaultClassById() throws ClassNotFoundException {
        return getDefaultClassById(R.array._default_style, AppLocalData.getInstance().getWorkspaceDefaultId());
    }

}