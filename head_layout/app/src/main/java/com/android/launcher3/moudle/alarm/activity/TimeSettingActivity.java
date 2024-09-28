package com.android.launcher3.moudle.alarm.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.common.db.alarm.AlarmDBEngine;
import com.android.launcher3.common.db.alarm.AlarmModel;
import com.android.launcher3.common.utils.DateUtils;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.moudle.alarm.utils.AlarmUtils;
import com.android.launcher3.moudle.alarm.view.CircularSliderView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 设置的时间不能一致
 */
public class TimeSettingActivity extends BaseActivity {

    private AlarmModel alarmModel = null;
    private List<String> times = new ArrayList<>();
    private CircularSliderView viewClock;
    private Button btn_h, btn_m, btn_am, btn_pm;
    private ImageView iv_bg;
    private boolean isFirst = false;

    @Override
    protected int getResourceId() {
        return R.layout.activity_setting_time;
    }

    @Override
    protected void initView() {
        viewClock = findViewById(R.id.view_clock);
        btn_h = findViewById(R.id.btn_h);
        btn_m = findViewById(R.id.btn_m);
        btn_am = findViewById(R.id.btn_am);
        btn_pm = findViewById(R.id.btn_pm);
        iv_bg = findViewById(R.id.iv_bg);
        btn_h.setSelected(true);
        btn_m.setSelected(false);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            alarmModel = (AlarmModel) intent.getSerializableExtra("alarm");
            isFirst = intent.getBooleanExtra("isFirst", false);
            times = intent.getStringArrayListExtra("times");
        }

        String hour;
        String minute;
        if (alarmModel != null) {
            String modelTime = alarmModel.getTime();
            String[] split = modelTime.split(":");
            hour = split.length < 1 ? "00" : split[0];
            if (!DateFormat.is24HourFormat(this)) {
                boolean isPm = DateUtils.getTodayFlag(modelTime).equals(getString(R.string.alarm_time_pm));
                setHour12(isPm);
                String s = DateUtils.convert24To12(modelTime);
                hour = s.split(":")[0].length() < 1 ? "00" : s.split(":")[0];
            }
            minute = split.length < 2 ? "00" : split[1];
        } else {
            Calendar calendars = Calendar.getInstance();
            int hourDay = calendars.get(Calendar.HOUR_OF_DAY);
            minute = String.valueOf(calendars.get(Calendar.MINUTE));
            if (!DateFormat.is24HourFormat(this)) {
                int amPm = calendars.get(Calendar.AM_PM);
                setHour12(amPm == Calendar.PM);
                hour = String.valueOf(calendars.get(Calendar.HOUR));
            } else {
                hour = String.valueOf(hourDay);
            }
        }
        btn_h.setText(hour.length() == 1 ? "0" + hour : hour);
        btn_m.setText(minute.length() == 1 ? "0" + minute : minute);
        if (DateFormat.is24HourFormat(this)) {
            btn_am.setBackgroundResource(com.android.launcher3.dial2.R.color.transparent);
            btn_am.setTextColor(getColor(R.color.color_808080));
            btn_am.setText(getString(R.string.alarm_hour_24));
            btn_pm.setVisibility(View.GONE);
            btn_am.setEnabled(false);
        }
        hourBtnAction();
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            finish();
        });

        btn_h.setOnClickListener(v -> {
            if (!btn_h.isSelected()) {
                hourBtnAction();
            }
        });

        btn_m.setOnClickListener(v -> {
            if (!btn_m.isSelected()) {
                btn_m.setSelected(true);
                btn_h.setSelected(false);
                iv_bg.setBackgroundResource(R.mipmap.ic_alarm_clock_m);
                viewClock.setStartPosition(Integer.parseInt(btn_m.getText().toString().trim()), true);
            }
        });

        btn_am.setOnClickListener(v -> {
            if (!btn_am.isSelected()) {
                setpmam(true);
            }
        });

        btn_pm.setOnClickListener(v -> {
            if (!btn_pm.isSelected()) {
                setpmam(false);
            }
        });

        viewClock.setOnMoveAction(() -> {
            if (btn_h.isSelected()) {
                String hour = String.valueOf(viewClock.getHour());
                btn_h.setText(hour.length() == 1 ? "0" + hour : hour);
            } else {
                String minute = String.valueOf(viewClock.getMinute());
                btn_m.setText(minute.length() == 1 ? "0" + minute : minute);
            }
        });

        findViewById(R.id.btn_ok).setOnClickListener(v -> {
            String hourValues = btn_h.getText().toString().trim();
            hourValues = hourValues.equals("24") ? "00" : hourValues;
            hourValues = hourValues.length() == 1 ? "0" + hourValues : hourValues;
            String minuteValues = btn_m.getText().toString().trim();
            minuteValues = minuteValues.length() == 1 ? "0" + minuteValues : minuteValues;

            String time;
            if (!DateFormat.is24HourFormat(this)) {
                time = DateUtils.convert12To24((btn_am.isSelected() ? getString(R.string.alarm_time_am) : getString(R.string.alarm_time_pm)) + hourValues + ":" + minuteValues);
            } else {
                time = hourValues + ":" + minuteValues;
            }
            if (times != null) {
                for (String string : times) {
                    if (time.equals(string) && !(alarmModel != null && time.equals(alarmModel.getTime()))) {
                        ToastUtils.show(getString(R.string.alarm_repetition_hint));
                        return;
                    }
                }
            }

            if (alarmModel != null) {
                if (time.equals(alarmModel.getTime())) {
                    finish();
                    return;
                }
                //修改时间后
                AlarmUtils.stopMoreRemind(TimeSettingActivity.this, alarmModel.getId(), alarmModel.getWeekDay());
                alarmModel.setTime(time);
                ThreadPoolUtils.getExecutorService().execute(() -> {
                    AlarmDBEngine.getInstance(TimeSettingActivity.this).update_alarm(alarmModel);
                });
                String[] weeks = alarmModel.getWeekDay().replaceAll(" ", "").split(",");
                int state = alarmModel.getState();
                if (state == 1 || state == 2) {
                    if (weeks.length == 0) {
                        AlarmUtils.startFirstAlarm(TimeSettingActivity.this, "", alarmModel);
                    } else {
                        for (String week : weeks) {
                            AlarmUtils.startFirstAlarm(TimeSettingActivity.this, week, alarmModel);
                        }
                    }
                }
                Intent intent = new Intent(TimeSettingActivity.this, SettingActivity.class);
                intent.putExtra("alarm", alarmModel);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
            //添加闹钟默认开启
            AlarmModel model = new AlarmModel(time, isFirst ? "1,2,3,4,5" : "", isFirst ? getString(R.string.alarm_getup) : getString(R.string.widget_alarm), true, isFirst ? 2 : 1);
            ThreadPoolUtils.getExecutorService().execute(() -> {
                AlarmDBEngine.getInstance(TimeSettingActivity.this).insert_alarm(model);
                List<AlarmModel> alarmModels = AlarmDBEngine.getInstance(TimeSettingActivity.this).quary_all_alarm();
                AlarmModel model1 = alarmModels.get(alarmModels.size() - 1);
                if (model1 != null && (model1.getState() == 1 || model1.getState() == 2)) {
                    String weekDay = model1.getWeekDay();
                    if (TextUtils.isEmpty(weekDay)) {
                        AlarmUtils.startFirstAlarm(TimeSettingActivity.this, weekDay, model1);
                    } else {
                        String[] weeks = weekDay.replaceAll(" ", "").split(",");
                        for (String week : weeks) {
                            AlarmUtils.startFirstAlarm(TimeSettingActivity.this, week, model1);
                        }
                    }
                }
            });
            finish();
        });
    }

    private void hourBtnAction() {
        btn_h.setSelected(true);
        btn_m.setSelected(false);
        if (DateFormat.is24HourFormat(this)) {
            iv_bg.setBackgroundResource(R.mipmap.ic_alarm_clock_24);
        } else {
            iv_bg.setBackgroundResource(R.mipmap.ic_alarm_clock_12);
        }
        viewClock.post(() -> viewClock.setStartPosition(Integer.parseInt(btn_h.getText().toString().trim()), false));
    }

    private void setHour12(boolean isPM) {
        btn_am.setTextColor(getColor(isPM ? R.color.color_502e09 : R.color.color_808080));
        btn_pm.setTextColor(getColor(!isPM ? R.color.color_502e09 : R.color.color_808080));
        btn_pm.setSelected(isPM);
        btn_am.setSelected(!isPM);
    }

    private void setpmam(boolean isAm) {
        btn_am.setSelected(isAm);
        btn_pm.setSelected(!isAm);
        btn_am.setTextColor(isAm ? getColor(R.color.black) : getColor(R.color.color_502e09));
        btn_pm.setTextColor(isAm ? getColor(R.color.color_502e09) : getColor(R.color.black));
    }
}