package com.android.launcher3.moudle.alarm.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.common.db.alarm.AlarmDBEngine;
import com.android.launcher3.common.db.alarm.AlarmModel;
import com.android.launcher3.common.utils.DateUtils;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.moudle.alarm.utils.AlarmUtils;
import com.android.launcher3.moudle.alarm.view.EditDialog;
import com.baehug.toputils.view.TitleRelativeLayout;

import java.util.ArrayList;

public class SettingActivity extends BaseActivity {

    public static final int Result_success = 1;
    private TextView tvChange, tvRepetition, tvTag;
    private LinearLayout ll_tag;
    private TitleRelativeLayout view_top;
    private SwitchCompat switchCompat;
    private AlarmModel alarmModel;
    private ArrayList<String> times = new ArrayList<>();
    private final EditDialog editDialog = EditDialog.Companion.init(CommonApp.getInstance());

    @Override
    protected int getResourceId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        tvChange = findViewById(R.id.tv_change);
        tvRepetition = findViewById(R.id.tv_repetition);
        ll_tag = findViewById(R.id.ll_tag);
        tvTag = findViewById(R.id.tv_tag);
        switchCompat = findViewById(R.id.switch_compat);
        view_top = findViewById(R.id.view_top);
        NestedScrollView nsv_view = findViewById(R.id.nsv_view);
        view_top.setScrollView(nsv_view);

        Intent intent = getIntent();
        if (intent != null) {
            alarmModel = (AlarmModel) intent.getSerializableExtra("alarm");
            times = intent.getStringArrayListExtra("times");
        }
    }

    @Override
    protected void initData() {
        if (alarmModel != null && alarmModel.getState() == 2) {
            ll_tag.setVisibility(View.GONE);
        } else {
            tvTag.setText((alarmModel == null ? getString(R.string.widget_alarm) : TextUtils.isEmpty(alarmModel.getTag()) ? getString(R.string.widget_alarm) : alarmModel.getTag()));
        }

        tvChange.setText(alarmModel == null ? "00:00" : DateFormat.is24HourFormat(this) ? alarmModel.getTime() : DateUtils.get12HourTime(alarmModel.getTime()));

        String weeks = "";
        if (alarmModel != null) {
            switchCompat.setChecked(alarmModel.isLaterAlert());
            String weekDay = alarmModel.getWeekDay();
            if (weekDay.equals("1,2,3,4,5")) {
                weeks = getString(R.string.alarm_workday);
            } else if (weekDay.equals("1,2,3,4,5,6,7")) {
                weeks = getString(R.string.alarm_everyday);
            } else if (weekDay.equals("6,7")) {
                weeks = getString(R.string.alarm_weekends);
            } else if (TextUtils.isEmpty(weekDay)) {
                weeks = getString(R.string.alarm_only_one);
            } else {
                for (int i=1; i<=7; i++) {
                    if (weekDay.contains(String.valueOf(i))) {
                        weeks = weeks + (TextUtils.isEmpty(weeks) ? "" : ",") + AlarmUtils.getWeekName(i);
                    }
                }
            }
        }
        tvRepetition.setText(weeks);
    }

    @Override
    protected void initEvent() {

        findViewById(R.id.ll_change).setOnClickListener(v -> {
            // 更改时间
            Intent intent = new Intent(SettingActivity.this, TimeSettingActivity.class);
            intent.putExtra("alarm", alarmModel);
            intent.putStringArrayListExtra("times", times);
            startActivityForResult(intent, Result_success);
        });

        findViewById(R.id.ll_repetition).setOnClickListener(v -> {
            // 重复次数
            Intent intent = new Intent(SettingActivity.this, WeekSettingActivity.class);
            intent.putExtra("alarm", alarmModel);
            startActivityForResult(intent, Result_success);
        });

        findViewById(R.id.ll_tag).setOnClickListener(v -> {
            // 标签
            editDialog.startProgress(tvTag.getText().toString());
        });

        editDialog.setCloseOnClick(new EditDialog.CloseOnClick() {
            @Override
            public void closeClick(@NonNull String str) {
                tvTag.setText(str);
                if (alarmModel != null) {
                    alarmModel.setTag(str);
                    restartAlarm();
                    ThreadPoolUtils.getExecutorService().execute(() -> {
                        AlarmDBEngine.getInstance(SettingActivity.this).update_alarm(alarmModel);
                    });
                }
            }
        });

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (alarmModel != null) {
                alarmModel.setLaterAlert(isChecked);
                restartAlarm();
                ThreadPoolUtils.getExecutorService().execute(() -> {
                    AlarmDBEngine.getInstance(SettingActivity.this).update_alarm(alarmModel);
                });
            }
        });

        findViewById(R.id.btn_del).setOnClickListener(v -> {
            if (alarmModel != null) {
                AlarmUtils.stopMoreRemind(SettingActivity.this, alarmModel.getId(), alarmModel.getWeekDay());
                ThreadPoolUtils.getExecutorService().execute(() -> {
                    AlarmDBEngine.getInstance(SettingActivity.this).delete_alarm(alarmModel.getId());
                });
                finish();
            }
        });

        view_top.setOnLeftClickListener(this::onBackPressed);
    }

    private void restartAlarm() {
        String weekDay = alarmModel.getWeekDay();
        AlarmUtils.stopMoreRemind(SettingActivity.this, alarmModel.getId(), weekDay);
        if (TextUtils.isEmpty(weekDay)) {
            AlarmUtils.startFirstAlarm(SettingActivity.this, null, alarmModel);
        } else {
            String[] weeks = weekDay.replaceAll(" ", "").split(",");
            for (String week : weeks) {
                AlarmUtils.startFirstAlarm(SettingActivity.this, week, alarmModel);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Result_success) {
            if (data != null) {
                alarmModel = (AlarmModel) data.getSerializableExtra("alarm");
                initData();
            }
        }
    }
}