package com.android.launcher3.moudle.launcher.presenter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.android.launcher3.App;
import com.android.launcher3.R;
import com.android.launcher3.common.anim.DefaultTransformer;
import com.android.launcher3.common.anim.DrawerTransformer;
import com.android.launcher3.common.anim.RotateTransformer;
import com.android.launcher3.common.anim.StackTransformer;
import com.android.launcher3.common.anim.ZoomTransformer;
import com.android.launcher3.common.base.BasePresenter;
import com.android.launcher3.common.constant.Constants;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.mode.FaceMode;
import com.android.launcher3.common.mode.WorkspaceMode;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.MobileDataUtil;
import com.android.launcher3.moudle.launcher.view.LauncherView;
import com.android.launcher3.moudle.notification.view.NotificationFragment;
import com.android.launcher3.moudle.touchup.fragment.TouchUpEventFragment;
import com.android.launcher3.moudle.touchup.fragment.TouchUpEventFragment;
import com.android.launcher3.moudle.shortcut.util.AirplaneModeUtil;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.android.launcher3.moudle.shortcut.view.ShortCutMenuFragment;
import com.android.launcher3.utils.FragmentUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LauncherPresenter extends BasePresenter<LauncherView> implements ILauncherPresenter {

    private FaceMode faceMode;
    private WorkspaceMode workspaceMode;
    private Activity activity;

    public LauncherPresenter(Activity activity) {
        // 创建数据
        LogUtil.d("创建数据", LogUtil.TYPE_RELEASE);
        this.activity = activity;
        faceMode = new FaceMode(App.getInstance().getApplicationContext());
        workspaceMode = new WorkspaceMode(App.getInstance().getApplicationContext());


    }

    private List<Fragment> mContainerFragments = Arrays.asList(
            ShortCutMenuFragment.newInstance(null),
            NotificationFragment.newInstance(null),
            TouchUpEventFragment.newInstance(null)

    );

    private List<Fragment> mChildFragmentList = new ArrayList<>();

    public List<Fragment> getFragmentList() {
        return mChildFragmentList;
    }

    @Override
    public void initData() {
        boolean power = AppLocalData.getInstance().getPower();
        if (power){
            try {
                loadPowerClass();
            } catch (Exception e) {
                AppLocalData.getInstance().setPower(false);
                loadApp();
            }
        }else {
            loadApp();
        }
    }

    @Override
    public void loadFragments(@NonNull FragmentManager fragmentManager) {
        // 下拉通知栏
        addQSPanelFragment(fragmentManager);
        // 上拉消息页
        addNotificationFragment(fragmentManager);
        //上拉组件
        addTouchUpEventFragment(fragmentManager);
        // 隐藏所有页面
        hideAllFragment(fragmentManager);
    }

    @Override
    public void hideAllFragment(FragmentManager fragmentManager) {
        if (mContainerFragments != null && mContainerFragments.size() > 0) {
            for (Fragment target : mContainerFragments) {
                FragmentUtils.hideFragment(fragmentManager, target);
            }
        }
    }

    @Override
    public void showShortcutMenuFragment(FragmentManager fragmentManager) {
        LogUtil.d("显示快捷菜单界面", LogUtil.TYPE_RELEASE);
        // 隐藏所有页面
        hideAllFragment(fragmentManager);
        // 显示当前页面
        FragmentUtils.showFragment(fragmentManager, mContainerFragments.get(0), R.id.fragment_qspanel);
    }

    @Override
    public void showNotificationFragment(FragmentManager fragmentManager) {
        LogUtil.d("显示通知界面", LogUtil.TYPE_RELEASE);
        // 隐藏所有页面
        hideAllFragment(fragmentManager);
        // 显示当前页面
        FragmentUtils.showFragment(fragmentManager, mContainerFragments.get(1), R.id.fragment_notification);
    }

    @Override
    public void showTouchUpEventFragment(FragmentManager fragmentManager) {
        LogUtil.d("显示通知界面", LogUtil.TYPE_RELEASE);
        // 隐藏所有页面
        hideAllFragment(fragmentManager);
        // 显示当前页面
        FragmentUtils.showFragment(fragmentManager, mContainerFragments.get(2), R.id.fragment_pulldown);
    }


    /**
     * 加载主题
     */
    private void loadFaceClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> faceClass = faceMode.getDefaultClassById();
        Fragment faceFragment = (Fragment) faceClass.newInstance();
        LogUtil.d("faceFragment = " + faceFragment.getClass().getSimpleName(), LogUtil.TYPE_RELEASE);
        if (mChildFragmentList != null && mChildFragmentList.size() > 0) {
            mChildFragmentList.set(0, faceFragment);
        } else {
            mChildFragmentList.add(faceFragment);
        }
    }

    private void loadDefaultFaceClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> faceClass = faceMode.getClassById();
        Fragment faceFragment = (Fragment) faceClass.newInstance();
        if (mChildFragmentList != null && mChildFragmentList.size() > 0) {
            mChildFragmentList.set(0, faceFragment);
        } else {
            mChildFragmentList.add(faceFragment);
        }
    }
    public void loadWorkspace(){
        //loadApp();
        try {
            //mChildFragmentList.remove(1);
            loadWorkspaceClass();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            if (isViewAttached()) {
                getView().showLoadWorkspaceError();
            }
        }
    }
    /**
     * 加载桌面风格
     */
    private void loadWorkspaceClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> workspaceClass = workspaceMode.getDefaultClassById();
        Fragment workspaceFragment = (Fragment) workspaceClass.newInstance();
        LogUtil.d("workspaceFragment = " + workspaceFragment.getClass().getSimpleName(), LogUtil.TYPE_RELEASE);
        LogUtil.i(Constants.ws, workspaceFragment.getClass().getSimpleName());
        if (mChildFragmentList != null && mChildFragmentList.size() > 1) {
            mChildFragmentList.set(1, workspaceFragment);
        } else {
            mChildFragmentList.add(workspaceFragment);
        }
    }


    private void loadPowerClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> powerClass = Class.forName("com.android.launcher3.moudle.launcher.fragment.PowerFragment");
        Fragment powerFragment = (Fragment) powerClass.newInstance();
        if (mChildFragmentList != null && mChildFragmentList.size() > 1) {
            mChildFragmentList.set(0, powerFragment);
        } else {
            mChildFragmentList.add(powerFragment);
        }
    }

    private void addQSPanelFragment(@NonNull FragmentManager fragmentManager) {
        FragmentUtils.addFragment(fragmentManager, mContainerFragments.get(0), R.id.fragment_qspanel);
    }

    private void addNotificationFragment(@NonNull FragmentManager fragmentManager) {
        FragmentUtils.addFragment(fragmentManager, mContainerFragments.get(1), R.id.fragment_notification);
    }
    private void addTouchUpEventFragment(@NonNull FragmentManager fragmentManager) {
        FragmentUtils.addFragment(fragmentManager, mContainerFragments.get(2), R.id.fragment_pulldown);
    }

    private void loadApp(){
        if (AppUtils.isThemeDial()){
            loadDial();
        }else {
            loadSystemDial();
        }

        try {
            loadWorkspaceClass();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            if (isViewAttached()) {
                getView().showLoadWorkspaceError();
            }
        }
    }

    private void loadDial(){
        try {
            ClassLoader classLoader = activity.getClassLoader();
            Fragment faceFragment = (Fragment) classLoader.loadClass(AppLocalData.getInstance().getFaceClassName()).newInstance();
            LogUtil.d("faceFragment = " + faceFragment.getClass().getSimpleName(), LogUtil.TYPE_RELEASE);
            if (mChildFragmentList != null && mChildFragmentList.size() > 0) {
                mChildFragmentList.set(0, faceFragment);
            } else {
                mChildFragmentList.add(faceFragment);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            if (isViewAttached()) {
                getView().showLoadFaceError();
            }
            if (Build.VERSION.SDK_INT <  Build.VERSION_CODES.O){
                AppLocalData.getInstance().setFaceDefaultId(1);
                loadSystemDial();
            }else {
                try {
                    loadDefaultFaceClass();
                } catch (Exception ex) {
                  e.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }

    @Override
    public void setPageTransformer(ViewPager viewPager) {
        int mode = AppLocalData.getInstance().getAnimMode();
        switch (mode){

            case 1:
                viewPager.setPageTransformer(true,new RotateTransformer());
                break;

            case 2:
                viewPager.setPageTransformer(true,new ZoomTransformer());
                break;

            case 3:
                viewPager.setPageTransformer(true,new DrawerTransformer());
                break;

            case 4:
                viewPager.setPageTransformer(true,new StackTransformer());
                break;

            default:
                viewPager.setPageTransformer(true,new DefaultTransformer());
                break;
        }
    }


    private void loadSystemDial(){
        try {
            loadFaceClass();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            if (isViewAttached()) {
                getView().showLoadFaceError();
            }
        }
    }

    public void clearTask(Context context){
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> recentTasks = am.getAppTasks();
            for (ActivityManager.AppTask task : recentTasks) {
                ActivityManager.RecentTaskInfo taskInfo = task.getTaskInfo();
                if (taskInfo.baseIntent.getComponent().getPackageName().equals(context.getPackageName())) {
                    task.setExcludeFromRecents(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断移动网络是否选中状态
     *
     * @return 是否被打开
     */
    private boolean mobileSelect() {
        if (activity == null) {
            return false;
        }
        if (!PhoneSIMCardUtil.getInstance().isSIMCardInserted() || AirplaneModeUtil.isAirplaneMode(activity)) {
            LogUtil.i("mobileSelect: sim " + PhoneSIMCardUtil.getInstance().isSIMCardInserted()
                    + " AirplaneMode " + AirplaneModeUtil.isAirplaneMode(activity), LogUtil.TYPE_RELEASE);
            return false;
        } else {
            LogUtil.i("mobileSelect: " + MobileDataUtil.INSTANCE.getDataEnabled(activity), LogUtil.TYPE_RELEASE);
            return MobileDataUtil.INSTANCE.getDataEnabled(activity);
        }
    }
}