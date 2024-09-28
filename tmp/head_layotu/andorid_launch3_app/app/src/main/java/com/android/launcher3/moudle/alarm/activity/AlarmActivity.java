package com.android.launcher3.moudle.alarm.activity;

import static com.android.launcher3.moudle.alarm.receiver.AlarmClockReceiver.listenerDialog;

import android.content.Intent;
import android.text.format.DateFormat;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.common.db.alarm.AlarmDBEngine;
import com.android.launcher3.common.db.alarm.AlarmModel;
import com.android.launcher3.common.utils.DateUtils;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.moudle.alarm.adapter.AlarmRecyclerAdapter;
import com.android.launcher3.moudle.alarm.utils.AlarmUtils;
import com.baehug.toputils.view.TitleRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends BaseActivity {

    private NestedScrollView nsv_view;
    private RecyclerView recyclerView;
    private TextView tv_bed_time;
    private TitleRelativeLayout view_top;
    private List<AlarmModel> alarmModels = new ArrayList<>();
    private AlarmRecyclerAdapter adapter;
    private AlarmModel defModel;
    private ArrayList<String> times = new ArrayList<>();

    @Override
    protected int getResourceId() {
        return R.layout.activity_alarm;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    protected void initView() {
        nsv_view = findViewById(R.id.nsv_view);
        tv_bed_time = findViewById(R.id.tv_bed_time);
        view_top = findViewById(R.id.view_top);
        recyclerView = findViewById(R.id.rv_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AlarmRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
        view_top.setScrollView(nsv_view);
    }

    @Override
    protected void initData() {

        ThreadPoolUtils.getExecutorService().execute(() -> {
            alarmModels.clear();
            alarmModels = AlarmDBEngine.getInstance(AlarmActivity.this).quary_all_alarm();
            AlarmUtils.setSystemData(this, alarmModels);
            if (alarmModels.isEmpty()) {
                defModel = null;
            }

            times.clear();
            for (AlarmModel alarmModel : alarmModels) {
                times.add(alarmModel.getTime());
                if (alarmModel.getState() == 2) {
                    defModel = alarmModel;
                }
            }
            if (defModel != null) {
                alarmModels.remove(defModel);
            }

            runOnUiThread(() -> {
                if (defModel == null) {
                    tv_bed_time.setText(getString(R.string.alarm_no));
                } else {
                    tv_bed_time.setText(DateFormat.is24HourFormat(this) ? defModel.getTime() : DateUtils.get12HourTime(defModel.getTime()));
                }
                adapter.setList(alarmModels);
                adapter.notifyDataSetChanged();
            });
        });

        if (listenerDialog != null) {
            listenerDialog.setCloseOnClick(this::initData);
        }
    }

    @Override
    protected void initEvent() {

        //跳转闹钟设置
        adapter.setOnItemClickListener((parent, view, position) -> {
            AlarmModel alarmModel = adapter.getList().get(position);
            Intent intent = new Intent(AlarmActivity.this, SettingActivity.class);
            intent.putExtra("alarm", alarmModel);
            intent.putStringArrayListExtra("times", times);
            startActivity(intent);
        });

        //添加闹钟
        findViewById(R.id.ib_add).setOnClickListener(v -> {
            if (alarmModels.size() >= 10) {
                ToastUtils.show(R.string.alarm_max_hint);
                return;
            }
            Intent intent = new Intent(AlarmActivity.this, TimeSettingActivity.class);
            intent.putStringArrayListExtra("times", times);
            startActivity(intent);
        });

        findViewById(R.id.ll_top).setOnClickListener(v -> {
            Intent intent;
            if (defModel == null) {
                intent = new Intent(AlarmActivity.this, TimeSettingActivity.class);
                intent.putExtra("isFirst", true);
            } else {
                intent = new Intent(AlarmActivity.this, SettingActivity.class);
                intent.putExtra("alarm", defModel);
            }
            intent.putStringArrayListExtra("times", times);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AlarmUtils.setSystemData(this, alarmModels);
    }
}