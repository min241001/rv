package com.android.launcher3.moudle.alarm.view;

import static com.android.launcher3.moudle.alarm.receiver.PhoneStateReceiver.CALL_STATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.db.alarm.AlarmDBEngine;
import com.android.launcher3.common.db.alarm.AlarmModel;
import com.android.launcher3.common.dialog.AlertDialog;
import com.android.launcher3.common.utils.DateUtils;
import com.android.launcher3.common.utils.LauncherAppManager;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.ScreenUtils;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.moudle.alarm.utils.AlarmManager;
import com.android.launcher3.moudle.alarm.utils.AlarmUtils;
import com.android.launcher3.moudle.record.base.RecordConstant;

import java.util.Calendar;

public class ListenerDialog {

    private Context mContext;
    private AlertDialog mAlertDialog = null;
    private AlarmManager alarmManager;
    private boolean isDelayAlarm = false;
    private int alarmId = 0;
    private AlarmModel alarmModel = null;
    private long alarmTime = System.currentTimeMillis();
    private final Handler handler = new Handler();
    private ScreenUtils screenUtils;

    public ListenerDialog(Context context) {
        this.mContext = context;
    }

    public void init() {
        // 延迟60秒后开始执行该任务
        handler.postDelayed(task, 60 * 1000);
        //闹钟铃声
        alarmManager = new AlarmManager(mContext);
        if (LauncherAppManager.isForbiddenInClass(mContext, "com.baehug.util")) {
            return;
        }
        alarmManager.startAlarm();
    }

    public void startProgress(Intent intent) {
        if (intent != null) {
            alarmId = intent.getIntExtra("id", 0);
            alarmTime = intent.getLongExtra("time", System.currentTimeMillis());
            isDelayAlarm = intent.getBooleanExtra("isDelay", false);
            alarmModel = (AlarmModel) intent.getSerializableExtra("alarmModel");
        }

        if (alarmModel == null) {
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(CALL_STATE);
        filter.addAction(RecordConstant.ACTION_WEILIAO_STATE_CHANGED);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_REBOOT);
        mContext.registerReceiver(phoneStateReceiver, filter);

        LogUtil.d("闹钟响起来了: id:" + alarmId + ", time:" + alarmTime + ", AlarmModel:" + alarmModel, LogUtil.TYPE_RELEASE);

        Calendar calendar = Calendar.getInstance();
        //星期几
        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));

        String minStr = minute.length() == 1 ? "0" + minute : minute;
        String hourStr = hour.length() == 1 ? "0" + hour : hour;
        String timeStr = hourStr + ":" + minStr;
        alarmTime = AlarmUtils.getNewCalendar(calendar, week, hour, minute);

        //闹钟重叠时，直接开启下周闹钟
        if (!isDelayAlarm && isShow()) {
            cancelBtnAction();
        }
        if (isDelayAlarm && isShow()) {
            SharedPreferencesUtils.removeKey(mContext, "Alarm_" + alarmId);
        }

        boolean chatCallState = AppLocalData.getInstance().getChatCallState();
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        int callState = telephonyManager.getCallState();
        if (callState == TelephonyManager.CALL_STATE_RINGING || callState == TelephonyManager.CALL_STATE_OFFHOOK || chatCallState) {
            if (alarmModel.isLaterAlert()) {
                sleepAlarm();
            } else {
                cancelBtnAction();
            }
            return;
        }

        if (isShow() || String.valueOf(alarmId).length() <= 1) {
            return;
        }

        init();

        screenUtils = new ScreenUtils(mContext);
        screenUtils.acquireScreenLock();
        AlertDialog.Builder mAlert = new AlertDialog.Builder(mContext);
        mAlert.setContentView(R.layout.activity_listener)
                .fullHeight()
                .fullWidth();
        mAlertDialog = mAlert.show();
        mAlertDialog.setCancelable(false);
        TextView tvTime = mAlertDialog.getView(R.id.tv_time);
        tvTime.setText(DateFormat.is24HourFormat(mContext) ? timeStr : DateUtils.get12HourTime(timeStr));
        TextView tv_tag = mAlertDialog.getView(R.id.tv_tag);
        if (alarmModel != null) {
            String tag = alarmModel.getTag();
            tv_tag.setText(TextUtils.isEmpty(tag) ? mContext.getString(R.string.widget_alarm) : tag);
        }
        Button btn_sleep = mAlertDialog.getView(R.id.btn_sleep);
        if (alarmModel != null) {
            btn_sleep.setVisibility(alarmModel.isLaterAlert() ? View.VISIBLE : View.GONE);
        }
        mAlertDialog.getView(R.id.btn_cancel).setOnClickListener(v -> {
            cancelAlarm();
        });
        mAlertDialog.getView(R.id.btn_sleep).setOnClickListener(v -> {
            sleepAlarm();
        });
    }

    // 关闭闹钟按钮操作
    private void cancelAlarm() {
        AlarmUtils.stopRemind(mContext, alarmId);
        cancelBtnAction();
        stopProgress();
    }

    // 睡眠闹钟操作
    private void sleepAlarm() {
        AlarmUtils.stopRemind(mContext, alarmId);
        openDelay(isDelayAlarm, 1);
        stopProgress();
    }

    // 关闭闹钟事件
    private void cancelBtnAction() {
        updateSqlData();
        if (!isDelayAlarm && !TextUtils.isEmpty(alarmModel.getWeekDay())) {
            AlarmUtils.startWeekAlarm(mContext, alarmTime, alarmId, alarmModel);
        }
        SharedPreferencesUtils.removeKey(mContext, "Alarm_" + alarmId + (isDelayAlarm ? "" : "5"));
    }

    // 更新数据库数据
    private void updateSqlData() {
        if (alarmModel.getState() == 2 && TextUtils.isEmpty(alarmModel.getWeekDay())) {
            ThreadPoolUtils.getExecutorService().execute(() -> {
                AlarmDBEngine.getInstance(mContext).delete_alarm(alarmModel.getId());
            });
        } else if (TextUtils.isEmpty(alarmModel.getWeekDay())) {
            alarmModel.setState(0);
            ThreadPoolUtils.getExecutorService().execute(() -> {
                AlarmDBEngine.getInstance(mContext).update_alarm(alarmModel);
            });
        }
    }

    // 一分钟后关闭闹钟
    Runnable task = () -> {
        AlarmUtils.stopRemind(mContext, alarmId);
        // 是否开启稍后提醒功能
        if (alarmModel.isLaterAlert()) {
            if (isDelayAlarm) {
                int param = (int) SharedPreferencesUtils.getParam(mContext, "Alarm_" + alarmId, 0);
                if (param >= 2) {
                    updateSqlData();
                    SharedPreferencesUtils.removeKey(mContext, "Alarm_" + alarmId);
                } else {
                    openDelay(true, ++param);
                }
            } else {
                openDelay(false, 1);
            }
        } else {
            cancelBtnAction();
        }
        stopProgress();
    };

    private void openDelay(boolean isAdd, int param) {
        if (isAdd) {
            AlarmUtils.startDelay5Alarm(mContext, alarmId, alarmModel);
            SharedPreferencesUtils.setParam(mContext, "Alarm_" + alarmId, param);
            return;
        }

        if (!TextUtils.isEmpty(alarmModel.getWeekDay())) {
            AlarmUtils.startWeekAlarm(mContext, alarmTime, alarmId, alarmModel);
        }
        AlarmUtils.startDelay5Alarm(mContext, Integer.parseInt(alarmId + "5"), alarmModel);
        SharedPreferencesUtils.setParam(mContext, "Alarm_" + alarmId + "5", param);
    }

    public void stopProgress() {
        if (alarmManager != null) {
            alarmManager.stopAlarm();
            alarmManager = null;
        }
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            handler.removeCallbacks(task);
            screenUtils.releaseScreenLock();
        }
        if (closeOnClick != null) {
            closeOnClick.closeClick();
        }
    }

    private boolean isShow() {
        if (mAlertDialog != null) {
            return mAlertDialog.isShowing();
        }
        return false;
    }

    private CloseOnClick closeOnClick = null;
    public interface CloseOnClick{
        void closeClick();
    }
    public void setCloseOnClick(CloseOnClick click) {
        this.closeOnClick = click;
    }

    private final BroadcastReceiver phoneStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (alarmModel.isLaterAlert()) {
                sleepAlarm();
            } else {
                cancelAlarm();
            }
        }
    };
}
