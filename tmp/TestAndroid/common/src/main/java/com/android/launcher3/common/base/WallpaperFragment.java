package com.android.launcher3.common.base;

import static com.android.launcher3.common.constant.SettingsConstant.WALLPAPER_CHANGES_RECEIVER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.launcher3.common.R;

/**
 * @Author: shensl
 * @Description：壁纸
 * @CreateDate：2023/12/16 11:21
 * @UpdateUser: shensl
 */
public abstract class WallpaperFragment extends BaseDesktopFragment {

    private ImageView wallpaperImage;

    private WallpaperBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void initView(View view) {
        super.initView(view);
        wallpaperImage = view.findViewById(R.id.wallpaper_image);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        broadcastReceiver = new WallpaperBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(WALLPAPER_CHANGES_RECEIVER);
        mContext.registerReceiver(broadcastReceiver, intentFilter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initData() {
        super.initData();
        // 获取壁纸并设置给ImageView
        Log.d("WallpaperFragment","initData");
        setImage();
    }

    public static WallpaperFragment newInstance(String[] params) {
        WallpaperFragment fragment = (WallpaperFragment) new Fragment();
        Bundle args = new Bundle();
        if(params != null && params.length > 0) {
            args.putStringArray("PARAM", params);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    class WallpaperBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setImage();
        }
    }

    private void setImage(){
        Bitmap bitmap = WallpaperUtil.getWallpaper();
        wallpaperImage.setImageBitmap(bitmap);
    }

}
