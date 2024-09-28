package com.android.launcher3.moudle.alarm.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.widget.NestedScrollView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.common.db.alarm.AlarmDBEngine;
import com.android.launcher3.common.db.alarm.AlarmModel;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.moudle.alarm.utils.AlarmUtils;
import com.baehug.toputils.view.TitleRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class WeekSettingActivity extends BaseActivity implements View.OnClickListener {

    private CheckBox cb_7, cb_6, cb_5, cb_4, cb_3, cb_2, cb_1;
    private RadioGroup radioGroup;
    private RadioButton rb_work, rb_every, rb_week;
    private TitleRelativeLayout viewTop;
    private NestedScrollView nsView;
    private List<Integer> weekList = new ArrayList<>();
    private AlarmModel alarmModel = null;

    @Override
    protected int getResourceId() {
        return R.layout.activity_setting_week;
    }

    @Override
    protected void initView() {
        viewTop = findViewById(R.id.view_top);
        nsView = findViewById(R.id.ns_view);
        radioGroup = findViewById(R.id.radioGroup);
        rb_every = findViewById(R.id.rb_every);
        rb_week = findViewById(R.id.rb_week);
        rb_work = findViewById(R.id.rb_work);
        cb_7 = findViewById(R.id.cb_7);
        cb_6 = findViewById(R.id.cb_6);
        cb_5 = findViewById(R.id.cb_5);
        cb_4 = findViewById(R.id.cb_4);
        cb_3 = findViewById(R.id.cb_3);
        cb_2 = findViewById(R.id.cb_2);
        cb_1 = findViewById(R.id.cb_1);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            alarmModel = (AlarmModel) intent.getSerializableExtra("alarm");
        } else {
            alarmModel = null;
        }

        if (alarmModel != null && alarmModel.getWeekDay() != null) {
            String weekDay = alarmModel.getWeekDay().replaceAll(" ", "");
            cb_7.setChecked(weekDay.contains("7"));
            cb_1.setChecked(weekDay.contains("1"));
            cb_2.setChecked(weekDay.contains("2"));
            cb_3.setChecked(weekDay.contains("3"));
            cb_4.setChecked(weekDay.contains("4"));
            cb_5.setChecked(weekDay.contains("5"));
            cb_6.setChecked(weekDay.contains("6"));

            switch (weekDay) {
                case "1,2,3,4,5":
                    rb_work.setChecked(true);
                    break;
                case "1,2,3,4,5,6,7":
                    rb_every.setChecked(true);
                    break;
                case "6,7":
                    rb_week.setChecked(true);
                    break;
            }
        }
    }

    @Override
    protected void initEvent() {

        viewTop.setScrollView(nsView);
        viewTop.setOnLeftClickListener(new TitleRelativeLayout.OnLeftClickListener() {
            @Override
            public void willLeftClick() {
                onBackPressed();
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_every:
                    if (rb_every.isChecked()) {
                        setBtnCheck(1);
                    }
                    break;
                case R.id.rb_work:
                    if (rb_work.isChecked()) {
                        setBtnCheck(2);
                    }
                    break;
                case R.id.rb_week:
                    if (rb_week.isChecked()) {
                        setBtnCheck(3);
                    }
                    break;
            }
        });

        cb_1.setOnClickListener(this);
        cb_2.setOnClickListener(this);
        cb_3.setOnClickListener(this);
        cb_4.setOnClickListener(this);
        cb_5.setOnClickListener(this);
        cb_6.setOnClickListener(this);
        cb_7.setOnClickListener(this);
    }

    /**
     * 1:每天；2：工作日；3：周末
     *
     * @param type
     */
    private void setBtnCheck(int type) {
        cb_7.setChecked(type != 2);
        cb_1.setChecked(type != 3);
        cb_2.setChecked(type != 3);
        cb_3.setChecked(type != 3);
        cb_4.setChecked(type != 3);
        cb_5.setChecked(type != 3);
        cb_6.setChecked(type != 2);
    }

    @Override
    public void onClick(View v) {
        if (cb_1.isChecked() && cb_2.isChecked() && cb_3.isChecked() && cb_4.isChecked() && cb_5.isChecked() && cb_6.isChecked() && cb_7.isChecked()) {
            radioGroup.check(R.id.rb_every);
        } else if (cb_1.isChecked() && cb_2.isChecked() && cb_3.isChecked() && cb_4.isChecked() && cb_5.isChecked() && !cb_6.isChecked() && !cb_7.isChecked()) {
            radioGroup.check(R.id.rb_work);
        } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked() && !cb_5.isChecked() && cb_6.isChecked() && cb_7.isChecked()) {
            radioGroup.check(R.id.rb_week);
        } else {
            radioGroup.clearCheck();
        }
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }

    public void saveData() {

        setSelectWeek(cb_1, 1);
        setSelectWeek(cb_2, 2);
        setSelectWeek(cb_3, 3);
        setSelectWeek(cb_4, 4);
        setSelectWeek(cb_5, 5);
        setSelectWeek(cb_6, 6);
        setSelectWeek(cb_7, 7);

        weekList.sort((o1, o2) -> o1 > o2 ? o2 : o1);

        String list2String = weekList.toString();
        //使用replaceAll方法替换 中括号
        list2String = list2String.replaceAll("\\[|]", "").replaceAll(" ", "");

        Log.d("TAG", "选择的日期：" + list2String);

        if (alarmModel != null) {

            if (list2String.equals(alarmModel.getWeekDay())) {
                finish();
                return;
            }

            AlarmUtils.stopMoreRemind(WeekSettingActivity.this, alarmModel.getId(), alarmModel.getWeekDay());

            alarmModel.setWeekDay(list2String);

            ThreadPoolUtils.getExecutorService().execute(() -> {
                AlarmDBEngine.getInstance(WeekSettingActivity.this).update_alarm(alarmModel);
            });

            int state = alarmModel.getState();
            //开启所有闹钟
            if (state == 1 || state == 2) {
                for (Integer week : weekList) {
                    AlarmUtils.startFirstAlarm(WeekSettingActivity.this, String.valueOf(week), alarmModel);
                }
            }

            Intent intent = new Intent(WeekSettingActivity.this, SettingActivity.class);
            intent.putExtra("alarm", alarmModel);
            setResult(RESULT_OK, intent);
        }
    }

    private void setSelectWeek(CheckBox checkBox, int num) {
        if (checkBox.isChecked()) {
            weekList.add(num);
        } else {
            weekList.remove(Integer.valueOf(num));
        }
    }
}