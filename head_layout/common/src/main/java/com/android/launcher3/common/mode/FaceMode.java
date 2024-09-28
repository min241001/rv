package com.android.launcher3.common.mode;

import static com.android.launcher3.common.constant.SettingsConstant.SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.R;
import com.android.launcher3.common.bean.FaceBean;
import com.android.launcher3.common.bean.WorkspaceBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shensl
 * @Description：主题业务功能实现
 * @CreateDate：2023/12/10 14:12
 * @UpdateUser: shensl
 */
public class FaceMode extends BaseMode<FaceBean> implements IWorkspaceMode {

    public FaceMode(Context context) {
        super(context);
    }

    @SuppressLint("ResourceType")
    @Override
    public List<FaceBean> loadDefaultData() {
        List<FaceBean> lists = new ArrayList<>();

        //读取配置文件
        TypedArray typedArray = getResources().obtainTypedArray(R.array._default_face);
        //读取ID
        int[] idArray = getResources().getIntArray(typedArray.getResourceId(0, -1));
        //读取名称
        String[] nameArray = getResources().getStringArray(typedArray.getResourceId(1, -1));
        //读取缩略图
        TypedArray thumbArray = getResources().obtainTypedArray(typedArray.getResourceId(2, -1));
        //读取类型
//        int[] typeArray = getResources().getIntArray(faceDefaultArray.getResourceId(4, -1));
        for (int i = 0; i < idArray.length; i++) {
//            if(!isSupportType(faceTypeArray[i]))
//                continue;

            lists.add(new FaceBean(idArray[i], nameArray[i], getResources().getDrawable(thumbArray.getResourceId(i, -1),getResources().newTheme())));
        }
        typedArray.recycle();

        return lists;
    }

    @SuppressLint("ResourceType")
    @Override
    public Class<?> getDefaultClassById() throws ClassNotFoundException {
        return getDefaultClassById(R.array._default_face, AppLocalData.getInstance().getFaceDefaultId());
    }


    public Class<?> getClassById() throws ClassNotFoundException {
        return getDefaultClassById(R.array._default_face, 1);
    }

    public FaceBean loadMoreData() {
        return new FaceBean(-1,"更多表盘",getResources().getDrawable(R.drawable.ic_more));
    }

    public List<FaceBean> loadDialData() {
        List<FaceBean> lists = new ArrayList<>();
        try {
            ContentResolver contentResolver = CommonApp.getInstance().getContentResolver();
            Uri contentUri = Uri.parse("content://com.baehug.theme.provider/dial");
            Cursor cursor = contentResolver.query(contentUri, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int themeId = cursor.getInt(cursor.getColumnIndex("themeId"));
                    String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                    String filepath = cursor.getString(cursor.getColumnIndex("filepath"));
                    String className = cursor.getString(cursor.getColumnIndex("className"));
                    String imagePath = cursor.getString(cursor.getColumnIndex("imagePath"));
                    FaceBean faceBean = new FaceBean(themeId, fileName, Converter.fileToDrawable(new File(imagePath)));
                    faceBean.setClassName(className);
                    faceBean.setFilepath(filepath);
                    lists.add(faceBean);
                }
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lists;
    }

}