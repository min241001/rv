package com.android.launcher3.moudle.touchup.utils;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.moudle.touchup.activity.AppListActivity;
import com.android.launcher3.moudle.touchup.adapter.FavoriteAdapter;
import com.android.launcher3.moudle.touchup.bean.AppInfoBean;
import com.android.launcher3.moudle.touchup.event.ItemEventListener;
import com.android.launcher3.moudle.touchup.opreator.DefaultAppsOpreator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

/**
 * Create by pengmin on 2024/9/6 .
 */
public class CommonUtil {
    private static final String TAG = " CommonUtil";

    public static FavoriteAdapter InitFavRecyclerView(RecyclerView rv, List<AppInfoBean> fav_beans, ActivityResultLauncher launcher, Context context, Handler handler) {
        FavoriteAdapter favoriteAdapter = new FavoriteAdapter(context, fav_beans, rv);
        //rv.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.HORIZONTAL, DpUtil.dip2px(context, 1), 0xffe5e5e5));
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(RecyclerView.HORIZONTAL);
        rv.setLayoutManager(llm);
        rv.setAdapter(favoriteAdapter);
        CommonUtil.SetRVProperties(rv,10);
        favoriteAdapter.setItemClickListener(new FavoriteAdapter.OnItemClickListener() {
            @Override
            public void onItemClik(View view, int position) {
                AppInfoBean bean = fav_beans.get(position);
                //Toast.makeText(context, "edit_mode:"+bean.getEditMode()+":type:"+bean.getType(), Toast.LENGTH_SHORT).show();
                if ((!bean.getEditMode()) && (bean.getType() == 0)) {
                    AppUtil.LauncherActivity(context, fav_beans.get(position).getPackage_name());
                } else {
                    //Toast.makeText(getActivity(), "menu", Toast.LENGTH_SHORT).show();
                    //context.sendBroadcast(new Intent(Constants.SET_VIEW_PAGER_ACTION));
                    if (launcher != null) {
                        Intent intent = new Intent(context, AppListActivity.class);
                        intent.putExtra("source_position", position);
                        launcher.launch(intent);
                        //Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        favoriteAdapter.setItemLongClickListener(new ItemEventListener.OnItemLongClickListener() {
            @Override
            public void onItemLongClik(View view, int position) {
                DefaultAppsOpreator.SendMessage(position, Constants.msgWhat.OPEN_EDITMODE, handler);
            }
        });
        return favoriteAdapter;
    }

    public static void SetBlur(Activity a, BlurView v) {
        float radius = 12f;
        View decorView = a.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        ColorDrawable drawable = new ColorDrawable(a.getResources().getColor(R.color.colorOverlay2));
        ColorDrawable drawable2 = new ColorDrawable(a.getResources().getColor(R.color.colorOverlay3));
        //Drawable d = a.getResources().getDrawable(android:R.drawable.tR.drawable.blur_background);
        v.setupWith(rootView, new RenderScriptBlur(a)) // or RenderEffectBlur
                .setFrameClearDrawable(drawable) // Optional
                .setBlurRadius(radius)
                .setFrameClearDrawable(drawable2)
        //.setOverlayColor(a.getResources().getColor(R.color.colorOverlay2))
        ;
    }

    public static void SetBackResult(Activity a, List<AppInfoBean> beans, int type, int source_position, int targetPosition) {
        if (beans.size() >= targetPosition) {
            Intent intent = new Intent();//源，目标Source, target
            intent.putExtra("type", type);
            intent.putExtra("target_position", beans.size() - targetPosition);
            intent.putExtra("source_position", source_position);
            a.setResult(RESULT_OK, intent);
            a.finish();
            //AppUtil.LauncherActivity(getActivity(), beans.get(0).getPackage_name());
        }
    }

    public static void SaveAppsData(Context context,Gson gson) {
        JSONArray js = new JSONArray();
        for (int i = 0; i < AppUtil.system_apps.size(); i++) {
            js.put(StringUtil.BeanToString(gson, AppUtil.system_apps.get(i)));
        }
        LogUtil.i(Constants.temp_tag1, "save js:" + js.toString());
        SharedPreferencesUtils.setParam(context,"system_apps", js.toString());
    }

    public static void InitAppsData(Context context,Gson gson) {
        String ss = (String)SharedPreferencesUtils.getParam(context,"system_apps", "");
        try {
            if (!TextUtils.isEmpty(ss)) {
                AppUtil.system_apps.clear();
                LogUtil.i(Constants.temp_tag1, "get ss:" + ss);
                //StringUtil.i(TAG, "get ds:" + ds);
                //StringUtil.i(TAG, "get fs:" + fs);

                JSONArray js = new JSONArray(ss);
                LogUtil.i(Constants.temp_tag1, "get ss len:" + js.length());
                for (int i = 0; i < js.length(); i++) {
                    AppUtil.system_apps.add(StringUtil.StringToBean(gson, js.optString(i)));
                }
            }
        } catch (JSONException e) {
            LogUtil.e(TAG, "InitAppsData e:" + e, 1);
        }
    }
    public static void SetRVProperties(RecyclerView rv,int cacheSize){
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(cacheSize);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rv.setItemAnimator(null);
    }
}
