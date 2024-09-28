package com.android.launcher3.moudle.meet.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.base.BaseMvpActivity;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.BaseActivityManager;
import com.android.launcher3.common.utils.BatteryUtil;
import com.android.launcher3.common.utils.DeviceStateSwitchUtil;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.moudle.meet.presenter.MakeFriendsPresenter;
import com.android.launcher3.moudle.shortcut.util.PhoneSIMCardUtil;
import com.baehug.lib.orginallocation.LocationHelper;
import com.baehug.lib.orginallocation.LocationInfoModel;

public class MakeFriendsActivity extends BaseMvpActivity<MakeFriendsView, MakeFriendsPresenter> implements MakeFriendsView {

    private ImageView makeFriendsImg;
    private boolean animFlag = false;
    private boolean exit = false;
    private String response;
    AnimatorSet animatorSet;
    private boolean locationFlag = false;

    @Override
    protected int getResourceId() {
        return R.layout.activity_make_friends;
    }

    @Override
    protected MakeFriendsPresenter createPresenter() {
        return new MakeFriendsPresenter();
    }

    @Override
    protected void initView() {
        TextView titleTxtView = findViewById(R.id.title_txt);
        makeFriendsImg = findViewById(R.id.make_friends_img);
        titleTxtView.setText(getString(R.string.make_friends));
    }

    private synchronized void location() {
        try {
            if (AppUtils.isNetworkAvailable(MakeFriendsActivity.this) || AppUtils.isWifiConnected(MakeFriendsActivity.this)) {
                if (locationFlag) {
                    return;
                }
                String deviceState = DeviceStateSwitchUtil.getHexString(DeviceStateSwitchUtil.DeviceStateConstant.UNKNOWN, DeviceStateSwitchUtil.DeviceStateConstant.UNKNOWN);
                if (deviceState.isEmpty()) {
                    return;
                }
                locationFlag = true;
                new Thread(() -> {
                    LocationInfoModel locationInfoModel = LocationHelper.getInstance().getLocationInfo(CommonApp.getInstance(), true, false, true);
                    response = locationInfoModel.formatToTcpAnswerInfo(BatteryUtil.getInstance().getBattery(), 0, PhoneSIMCardUtil.getInstance().getCellularSignalStrength(), deviceState);
                    locationFlag = false;
                }).start();
            } else {
                locationFlag = false;
                ToastUtils.show(com.android.launcher3.common.R.string.no_network);
            }
        } catch (Exception e) {
            locationFlag = false;
        }
    }

    @Override
    protected void initEvent() {
        makeFriendsImg.setOnClickListener(v -> {
            if (!animFlag) {
                // 创建一个旋转45度的动画
                ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(makeFriendsImg, "rotation", 0f, 45f);
                rotateAnimator.setDuration(500); // 设置动画持续时间，单位为毫秒
                rotateAnimator.setInterpolator(new LinearInterpolator()); // 设置动画插值器，这里使用线性插值器
                // 创建一个反向旋转45度的动画
                ObjectAnimator reverseRotateAnimator = ObjectAnimator.ofFloat(makeFriendsImg, "rotation", 45f, 0f);
                reverseRotateAnimator.setDuration(500); // 设置动画持续时间，单位为毫秒
                reverseRotateAnimator.setInterpolator(new LinearInterpolator()); // 设置动画插值器，这里使用线性插值器
                // 创建一个AnimatorSet来组合两个动画
                animatorSet = new AnimatorSet();
                animatorSet.playSequentially(rotateAnimator, reverseRotateAnimator); // 按顺序播放两个动画
                // 设置动画重复播放
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (response == null || response.isEmpty()) {
                            location();
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (BaseActivityManager.getInstance().getActivityList().contains(getActivity()) && !exit) {
                            if (response == null || response.isEmpty()) {
                                animatorSet.start();
                                return;
                            }
                            animFlag = false;
                            Intent intent = new Intent(MakeFriendsActivity.this, SearchedFriendsActivity.class);
                            intent.putExtra("type", "0");
                            intent.putExtra("response", response);
                            startActivity(intent);
                        }
                    }
                });
                // 启动动画
                animFlag = true;
                animatorSet.start();
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit = true;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

}
