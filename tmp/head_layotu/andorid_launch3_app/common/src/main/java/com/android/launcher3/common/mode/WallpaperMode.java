package com.android.launcher3.common.mode;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.WorkspaceBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeckchan
 * @Description：壁纸业务实现
 * @CreateDate：2023/11/6 11:02
 * @UpdateUser: shensl 2023/12/10 10:12
 */
public class WallpaperMode extends BaseMode<WorkspaceBean> implements IWorkspaceMode {

    public WallpaperMode(Context context) {
        super(context);
    }

    @Override
    public List<WorkspaceBean> loadDefaultData() {
        List<WorkspaceBean> lists = new ArrayList<>();

        //读取表盘的配置文件
        TypedArray typedArray = getResources().obtainTypedArray(R.array._default_wallpaper);
        //读取表盘的ID
        int[] idArray = getResources().getIntArray(typedArray.getResourceId(0, -1));
        //读取表盘配置的名称
        String[] workspaceNameArray = getResources().getStringArray(typedArray.getResourceId(1, -1));
        //读取表盘配置的缩略图
        TypedArray thumbArray = getResources().obtainTypedArray(typedArray.getResourceId(2, -1));
        //读取表盘的类型
        int[] workspaceTypeArray = getResources().getIntArray(typedArray.getResourceId(4, -1));
        for (int i = 0; i < workspaceTypeArray.length; i++) {
            if (!isSupportType(workspaceTypeArray[i]))
                continue;

            lists.add(new WorkspaceBean(idArray[i], workspaceNameArray[i], getResources().getDrawable(thumbArray.getResourceId(i, -1),getResources().newTheme())));
        }
        typedArray.recycle();

        return lists;
    }

    public WorkspaceBean loadMoreData() {
        return new WorkspaceBean(-1,"更多壁纸",getResources().getDrawable(R.drawable.ic_more));
    }

    public List<WorkspaceBean> loadThemeData() {
        List<WorkspaceBean> lists = new ArrayList<>();
        try {
            ContentResolver contentResolver = CommonApp.getInstance().getContentResolver();
            Uri contentUri = Uri.parse("content://com.baehug.theme.provider/wallpaper");
            Cursor cursor = contentResolver.query(contentUri, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int themeId = cursor.getInt(cursor.getColumnIndex("themeId"));
                    String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                    String filepath = cursor.getString(cursor.getColumnIndex("filepath"));
                    lists.add(new WorkspaceBean(themeId,fileName, Converter.fileToDrawable(
                            new File(Environment.getExternalStorageDirectory() + File.separator + filepath))));
                }
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lists;
    }

    @SuppressLint("ResourceType")
    @Override
    public Class<?> getDefaultClassById() throws ClassNotFoundException {
        return getDefaultClassById(R.array._default_wallpaper, AppLocalData.getInstance().getWallpaperDefaultId());
    }

}