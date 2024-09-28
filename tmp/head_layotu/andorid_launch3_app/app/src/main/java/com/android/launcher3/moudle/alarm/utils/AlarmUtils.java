package com.android.launcher3.moudle.alarm.utils;

import static android.content.Context.ALARM_SERVICE;

import static com.android.launcher3.common.constant.SettingsConstant.ALARM_CLOCK;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.launcher3.common.db.alarm.AlarmDBEngine;
import com.android.launcher3.common.db.alarm.AlarmModel;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.android.launcher3.moudle.alarm.receiver.AlarmClockReceiver;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * 1.1闹钟响铃一分钟
 * 1.2闹钟响铃间隔5分钟
 * 1.3闹钟提醒次数为3次（包括设定的时间响铃）
 * <p>
 * 两个闹钟：
 * 1，每周都提醒
 * 2，闹钟提醒时，点击睡眠或者未执行操作时，延时5分钟开启的闹钟
 */
public class AlarmUtils {

    /**
     * 开启一次性闹钟  (只提醒一次)
     *
     * @param context    父类
     * @param timeStr    时分
     * @param alarmModel 标识
     */
    public static void startOneOffAlarm(Context context, String timeStr, AlarmModel alarmModel) {
        String[] split = timeStr.split(":");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());//获取当前时间

        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));//设置时区
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));//设置几点提醒
        c.set(Calendar.MINUTE, Integer.parseInt(split[1]));//设置几分提醒
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long timeInMillis = c.getTimeInMillis();

        if (timeInMillis <= System.currentTimeMillis()) {
            timeInMillis = timeInMillis + 24 * 60 * 60 * 1000;
        }

        startAlarm(context, 0, 0, 0, timeInMillis, alarmModel.getId() * 100, false, false, alarmModel);
    }

    /**
     * 开启闹钟  (每周提醒)
     *
     * @param context 父类
     * @param week    时分
     * @param model   标识
     */
    public static void startFirstAlarm(Context context, String week, AlarmModel model) {
        String[] split = model.getTime().split(":");
        if (TextUtils.isEmpty(week)) {
            startOneOffAlarm(context, model.getTime(), model);
        } else {
            startAlarm(context, Integer.parseInt(week), Integer.parseInt(split.length < 1 ? "00" : split[0]), Integer.parseInt(split.length < 2 ? "00" : split[1]), 0, Integer.parseInt(model.getId() + week), true, false, model);
        }
    }

    /**
     * 开启闹钟  (每周提醒)
     *
     * @param context 父类
     * @param model   标识
     */
    public static void startWeekAlarm(Context context, String week, String timeStr, int alarmId, AlarmModel model) {
        String[] split = timeStr.split(":");
        if (TextUtils.isEmpty(week)) {
            startOneOffAlarm(context, timeStr, model);
        } else {
            startAlarm(context, Integer.parseInt(week), Integer.parseInt(split.length < 1 ? "00" : split[0]), Integer.parseInt(split.length < 2 ? "00" : split[1]), 0, Integer.parseInt(alarmId + week), true, false, model);
        }
    }

    /**
     * 开启闹钟  (每周提醒)
     *
     * @param context 父类
     * @param id      标识
     */
    public static void startWeekAlarm(Context context, long time, int id, AlarmModel model) {
        startAlarm(context, 0, 0, 0, time + 7 * 24 * 60 * 60 * 1000, id, false, false, model);
    }

    /**
     * 五分钟后的延时闹钟
     *
     * @param context 父类
     * @param id      标识
     */
    public static void startDelay5Alarm(Context context, int id, AlarmModel model) {
        startAlarm(context, 0, 0, 0, System.currentTimeMillis() + 5 * 60 * 1000, id, false, true, model);
    }


    public static void startAlarm(Context context, int week, int hour, int minute, long time, int id, boolean isFirstAlarm, boolean isDelayAlarm, AlarmModel model) {
        try {
            LogUtil.d("开启闹钟:" + id + ", isFirst:" + isFirstAlarm + ", delay:" + isDelayAlarm, LogUtil.TYPE_RELEASE);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());//获取当前时间
            //获取当前毫秒值
            long systemTime = System.currentTimeMillis();
            c.setTimeZone(TimeZone.getTimeZone("GMT+8"));//设置时区
            if (isFirstAlarm) {
                c.set(Calendar.DAY_OF_WEEK, week == 7 ? 1 : week + 1);
                c.set(Calendar.HOUR_OF_DAY, hour);//设置几点提醒
                c.set(Calendar.MINUTE, minute);//设置几分提醒
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
            } else {
                c.setTimeInMillis(time);
            }

            LogUtil.d("标识：" + c.getTimeInMillis(), LogUtil.TYPE_RELEASE);

            //获取上面设置的时间
            long selectTime = c.getTimeInMillis();

            /* 闹钟时间到了的一个提醒类 */
            Intent intent = new Intent(context, AlarmClockReceiver.class);
            intent.setAction("android.intent.action.AlarmClockReceiver");
            intent.putExtra("id", id);
            intent.putExtra("time", selectTime);
            intent.putExtra("isDelay", isDelayAlarm);
            intent.putExtra("alarm", model.getId());

            //第二个参数一定要是唯一的，比如不同的ID之类的
            PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //得到AlarmManager实例
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            // 设置重复闹钟
            int alarmType = AlarmManager.RTC_WAKEUP; // 选择闹钟类型，RTC_WAKEUP会在设定时间唤醒设备

            LogUtil.d("系统时间：" + systemTime + ", 选择的时间：" + selectTime + ", alarmId:" + model.getId(), LogUtil.TYPE_RELEASE);

            // 设置重复闹钟
            if (systemTime > selectTime) {
                am.setExactAndAllowWhileIdle(alarmType, c.getTimeInMillis() + 1000 * 60 * 60 * 24 * 7, pi);
            } else {
                am.setExactAndAllowWhileIdle(alarmType, c.getTimeInMillis(), pi);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭多个闹钟提醒
     *
     * @param context 父类
     * @param id      标识
     */
    public static void stopMoreRemind(Context context, int id, String weekDays) {
        stopRemind(context, id * 100);
        stopRemind(context, Integer.parseInt(id * 100 + "5"));
        String weekDay = weekDays.replaceAll(" ", "");
        String[] weeks = weekDay.split(",");
        for (String week : weeks) {
            if (!TextUtils.isEmpty(week)) {
                stopRemind(context, Integer.parseInt(id + week));
                stopRemind(context, Integer.parseInt(id + week + "5"));
                SharedPreferencesUtils.removeKey(context, id + week + "5");
            }
        }
    }

    /**
     * 关闭单个闹钟提醒
     *
     * @param context 父类
     * @param id      标识
     */
    public static void stopRemind(Context context, int id) {
        try {
            LogUtil.d("关闭闹钟:" + id, LogUtil.TYPE_RELEASE);

            Intent intent = new Intent(context, AlarmClockReceiver.class);
            intent.setAction("android.intent.action.AlarmClockReceiver");
            intent.putExtra("id", id);
            PendingIntent pi = PendingIntent.getBroadcast(context, id,
                    intent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            //取消提醒
            am.cancel(pi);

            Intent intents = new Intent("android.intent.action.BOOKCASE_RESTART");
            PendingIntent pis = PendingIntent.getBroadcast(context, id,
                    intents, 0);
            AlarmManager ams = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            //取消提醒
            ams.cancel(pis);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //搜索本地数据重新开启闹钟
    public static void bootStartAlarm(Context context) {

        ThreadPoolUtils.getExecutorService().execute(() -> {

            List<AlarmModel> alarmModels = AlarmDBEngine.getInstance(context).quary_all_alarm();

            for (AlarmModel alarmModel : alarmModels) {
                LogUtil.d("闹钟model：" + alarmModel.toString(), LogUtil.TYPE_RELEASE);
                int state = alarmModel.getState();
                if (state == 1 || state == 2) {

                    String weekDays = alarmModel.getWeekDay();
                    if (TextUtils.isEmpty(weekDays)) {
                        startFirstAlarm(context, null, alarmModel);
                    } else {
                        String weekDay = weekDays.replaceAll(" ", "");
                        String[] weeks = weekDay.split(",");
                        for (String week : weeks) {
                            if (!TextUtils.isEmpty(week)) {
                                startFirstAlarm(context, week, alarmModel);
                            }
                        }
                    }
                }
            }
            AlarmUtils.setSystemData(context, alarmModels);
        });
    }

    //获取时间戳
    public static long getNewCalendar(Calendar calendar, String week, String hour, String minute) {

        calendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt(week));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));//设置几点提醒
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));//设置几分提醒
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * 存储闹钟数据
     *
     * @param context
     * @param alarmModels
     */
    public static void setSystemData(Context context, List<AlarmModel> alarmModels) {

        boolean isSettingAlarm = false;
        for (AlarmModel alarmModel : alarmModels) {
            if (alarmModel.getState() == 1 || alarmModel.getState() == 2) {
                isSettingAlarm = true;
                break;
            }
        }
        Settings.System.putInt(context.getContentResolver(), ALARM_CLOCK, isSettingAlarm ? 1 : 0); //1设置  0未设置
    }

    public static String getWeekName(int num) {
        String str = "";
        switch (num) {
            case 1:
                str = "周一";
                break;
            case 2:
                str = "周二";
                break;
            case 3:
                str = "周三";
                break;
            case 4:
                str = "周四";
                break;
            case 5:
                str = "周五";
                break;
            case 6:
                str = "周六";
                break;
            case 7:
                str = "周日";
                break;
        }
        return str;
    }

}
