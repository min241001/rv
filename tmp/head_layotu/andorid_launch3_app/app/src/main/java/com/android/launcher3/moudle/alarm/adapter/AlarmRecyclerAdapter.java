package com.android.launcher3.moudle.alarm.adapter;

import static com.android.launcher3.common.constant.SettingsConstant.ALARM_CLOCK;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;

import androidx.appcompat.widget.SwitchCompat;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.BaseRecyclerHolder;
import com.android.launcher3.common.db.alarm.AlarmDBEngine;
import com.android.launcher3.common.db.alarm.AlarmModel;
import com.android.launcher3.common.utils.DateUtils;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.moudle.alarm.utils.AlarmUtils;

import java.util.ArrayList;

public class AlarmRecyclerAdapter extends BaseRecyclerAdapter<AlarmModel> {

    private Context context;

    public AlarmRecyclerAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.item_alarm);
        this.context = context;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, AlarmModel item, int position, boolean isScrolling) {

        holder.setText(R.id.tv_title, TextUtils.isEmpty(item.getTag()) ? context.getString(R.string.widget_alarm) : item.getTag());
        String time = item.getTime();
        if (!DateFormat.is24HourFormat(context)) {
            time = DateUtils.get12HourTime(time);
        }

        holder.setText(R.id.tv_time, time);
        SwitchCompat switchCompat = holder.getView(R.id.switch_compat);
        switchCompat.setChecked(item.getState() == 1);

        switchCompat.setOnCheckedChangeListener((compoundButton, isChecked) -> {

            item.setState(switchCompat.isChecked() ? 1 : 0);

            if (switchCompat.isChecked()) {
                Settings.System.putInt(context.getContentResolver(), ALARM_CLOCK, 1); //1设置  0未设置
            }

            ThreadPoolUtils.getExecutorService().execute(() -> {
                AlarmDBEngine.getInstance(getContext()).update_alarm(item);
            });

            String weekDay = item.getWeekDay();

            if (TextUtils.isEmpty(weekDay)) {
                if (switchCompat.isChecked()) {
                    AlarmUtils.startFirstAlarm(context, null, item);
                } else {
                    AlarmUtils.stopMoreRemind(context, item.getId(), weekDay);
                    SharedPreferencesUtils.removeKey(context, item.getId() * 100 + "5");
                }
                return;
            }

            for (int i=1; i<=7; i++) {
                if (weekDay.contains(String.valueOf(i))) {
                    setAlarmValue(switchCompat.isChecked(), item, i);
                }
            }
        });
    }

    private void setAlarmValue(boolean isSelect, AlarmModel item, int week) {
        if (isSelect) {
            AlarmUtils.startFirstAlarm(context, String.valueOf(week), item);
        } else {
            AlarmUtils.stopRemind(context, Integer.parseInt(item.getId() + String.valueOf(week)));
            AlarmUtils.stopRemind(context, Integer.parseInt(item.getId() + String.valueOf(week) + "5"));
            SharedPreferencesUtils.removeKey(context, item.getId() + String.valueOf(week) + "5");
        }
    }
}
