package com.android.launcher3.common.mode;

import static com.android.launcher3.common.CompileConfig.SYSTEM_DEFAULT_THEME_JSON;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.launcher3.common.CompileConfig;
import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.WorkspaceBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.ImageUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

        LogUtil.i(TAG, "loadDefaultConfig: " + SYSTEM_DEFAULT_THEME_JSON, LogUtil.TYPE_RELEASE);
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<WorkspaceBean>>(){}.getType();
            List<WorkspaceBean> list = gson.fromJson(SYSTEM_DEFAULT_THEME_JSON, type);
            for (WorkspaceBean bean : list) {
                if (bean.getType() == 2 || bean.getType() == CompileConfig.WATCH_INDEX_DIAL) {
                    bean.setThumb(ImageUtil.getDrawable(bean.getIconId()));
                    bean.setName(StringUtils.getStr(bean.getNameId()));
                    lists.add(bean);
                }
            }
        } catch (JsonSyntaxException e) {
            LogUtil.e(TAG, "loadDefaultConfig: ", e, LogUtil.TYPE_RELEASE);
        }

        return lists;
    }

    @SuppressLint("ResourceType")
    @Override
    public Class<?> getDefaultClassById() throws ClassNotFoundException {
        return getDefaultClassById(R.array._default_style, AppLocalData.getInstance().getWorkspaceDefaultId());
    }

}