package com.android.launcher3.common.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.launcher3.common.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * @Description：{图片工具：把第三方工具进行二次封装，方便后期维护}
 */
public class ImageUtil {

    /**
     * 显示图片
     *
     * @param context
     * @param drawable       图片地址
     * @param imageView      图片控件
     * @param error          错误图片
     * @param width          宽度
     * @param height         高度
     */
    public static void displayImage(Context context, @Nullable Drawable drawable, ImageView imageView, @DrawableRes int error, int width, int height) {
        RequestOptions options = new RequestOptions().error(error);
        Glide.with(context)
                .load(drawable)
                .override(width, height)
                .apply(options)
                .into(imageView);
    }

    /**
     * 显示图片
     *
     * @param context
     * @param url       图片地址
     * @param imageView 图片控件
     * @param error     错误图片
     */
    public static void displayImage(Context context, String url, ImageView imageView, @DrawableRes int error) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.default_avatar_icon)
                .error(error)
                .into(imageView);
    }

    /**
     * 显示图片
     *
     * @param fragment
     * @param url       图片地址
     * @param imageView 图片控件
     * @param error     错误图片
     */
    public static void displayImage(Fragment fragment, String url, ImageView imageView, @DrawableRes int error) {
        Glide.with(fragment)
                .load(url)
                .error(error)
                .into(imageView);
    }

}
