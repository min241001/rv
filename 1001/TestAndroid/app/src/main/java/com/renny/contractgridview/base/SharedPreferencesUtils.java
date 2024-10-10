package com.renny.contractgridview.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedPreferencesUtils {

    private static final String TAG = "SharedPreferencesUtils";

    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_data";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param context
     * @param key
     * @param object
     */
    public static void setParam(Context context , String key, Object object){

        String type = object.getClass().getSimpleName();
        LogUtil.i(TAG,"存储值的类型 " + type, LogUtil.TYPE_RELEASE);
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }
        else if ("UpgradeDetailResp".equals(type)){
            String json = GsonUtil.INSTANCE.toJson(object);
            editor.putString(key, json);
        }

        editor.commit();
    }

    /**
     * 存储map集合
     * @param context 上下文
     * @param key 键
     * @param map 存储的集合
     * @param <K> 指定map的键
     * @param <T> 指定map的值
     */
    public static <K,T> void setMap(Context context,String key, Map<K,T> map){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(map);
        editor.putString(key,json);
        editor.commit();
    }

    /**
     * 获取map集合
     */
    public static  Map<String,Long> getMap(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Map<String,Long> map = new HashMap<>();
        String strJson = sp.getString(key,null);
        Log.d(TAG, "getMap: "+ strJson);
        if (strJson == null){
            return map;
        }
        Gson gson = new Gson();
        map = gson.fromJson(strJson,new TypeToken<Map<String,Long> >(){}.getType());
        return map;
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(Context context , String key, Object defaultObject){
        if (context == null) {
            return defaultObject;
        }
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if("String".equals(type)) {
            return sp.getString(key, (String)defaultObject);
        }

        else if("Integer".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }

        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }

        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }

        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }

        return null;
    }

    /**
     * 清除所有数据
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

    /**
     * 清除指定数据
     * @param context
     */
    public static void removeKey(Context context,String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key).apply();
    }


}