package com.android.launcher3.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.launcher3.App;

public class SPManager {

    private static SPManager Instance;

    private SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mEditor;

    private Context mContext;

    private SPManager() {
        this.mContext = App.getInstance();
        mSharedPreferences = mContext.getSharedPreferences("launcher", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static SPManager getInstance() {
        if (Instance == null) {
            synchronized (SPManager.class) {
                if (Instance == null) {
                    Instance = new SPManager();
                }
            }
        }
        return Instance;
    }

    public void saveString(String key, String value) {
        mEditor.putString(key, value).apply();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public void saveBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }
}
