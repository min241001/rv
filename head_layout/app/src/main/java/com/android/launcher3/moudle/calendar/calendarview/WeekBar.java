package com.android.launcher3.moudle.calendar.calendarview;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher3.R;

public class WeekBar extends LinearLayout {
    private CalendarViewDelegate mDelegate;

    public WeekBar(Context context) {
        super(context);
        if ("com.android.launcher3.moudle.calendar.calendarview.WeekBar".equals(getClass().getName())) {
            LayoutInflater.from(context).inflate(R.layout.week_bar, this, true);
        }
    }

    void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        if ("com.android.launcher3.moudle.calendar.calendarview.WeekBar".equalsIgnoreCase(getClass().getName())) {
            setTextSize(mDelegate.getWeekTextSize());
            setupWeek(delegate);
            setBackgroundColor(delegate.getWeekBackground());
            setPadding(delegate.getCalendarPaddingLeft(), 0, delegate.getCalendarPaddingRight(), 0);
        }
    }

    void setupWeek(final CalendarViewDelegate delegate) {
        for (int i = 0; i < getChildCount(); i++) {
            if (i == 0 || i == getChildCount() - 1) {
                ((TextView) getChildAt(i)).setTextColor(delegate.getWeekendTextColor());
            } else {
                ((TextView) getChildAt(i)).setTextColor(delegate.getWeekTextColor());
            }
        }
    }

    protected void setTextColor(int weekTextColor, int weekendTextColor) {
        for (int i = 0; i < getChildCount(); i++) {
            if (i == 0 || i == getChildCount() - 1) {
                ((TextView) getChildAt(i)).setTextColor(weekendTextColor);
            } else {
                ((TextView) getChildAt(i)).setTextColor(weekTextColor);
            }
        }
    }

    //设置文本大小
    protected void setTextSize(int size) {
        for (int i = 0; i < getChildCount(); i++) {
            ((TextView) getChildAt(i)).setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    protected void onDateSelected(Calendar calendar, int weekStart, boolean isClick) {

    }

    protected void onWeekStartChange(int weekStart) {
        if (!"com.android.launcher3.moudle.calendar.calendarview.WeekBar".equalsIgnoreCase(getClass().getName())) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            ((TextView) getChildAt(i)).setText(getWeekString(i, weekStart));
        }
    }

    private String getWeekString(int index, int weekStart) {
        String[] weeks = getContext().getResources().getStringArray(R.array.week_string_array);

        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return weeks[index];
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return weeks[index == 6 ? 0 : index + 1];
        }
        return weeks[index == 0 ? 6 : index - 1];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDelegate != null) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mDelegate.getWeekBarHeight(), MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(CalendarUtil.dipToPx(getContext(), 40), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
