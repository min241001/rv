package com.android.launcher3.utils;

import android.content.res.AssetManager;
import android.content.res.Resources;


public class PluginEnv {

    public ClassLoader pluginClassLoader;
    public Resources pluginRes;
    public AssetManager pluginAsset;
    public Resources.Theme pluginTheme;
    public String localPath;

    public PluginEnv(String localPath, ClassLoader pluginClassLoader, Resources pluginRes, AssetManager pluginAsset, Resources.Theme pluginTheme) {
        this.pluginClassLoader = pluginClassLoader;
        this.pluginRes = pluginRes;
        this.pluginAsset = pluginAsset;
        this.pluginTheme = pluginTheme;
        this.localPath = localPath;
    }
}
