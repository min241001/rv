package com.android.launcher3.moudle.calendar.calendarview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.launcher3.R;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日历布局
 */
@SuppressWarnings({"unused"})
public class CalendarView extends FrameLayout {


    private final CalendarViewDelegate mDelegate;

    private MonthViewPager mMonthPager;

    private View mWeekLine;

    private WeekBar mWeekBar;

    CalendarLayout mParentLayout;

    public CalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDelegate = new CalendarViewDelegate(context, attrs);
        init(context);
    }

    //初始化
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_calendar_view, this, true);
        FrameLayout frameContent = findViewById(R.id.frameContent);

        try {
            Constructor constructor = mDelegate.getWeekBarClass().getConstructor(Context.class);
            mWeekBar = (WeekBar) constructor.newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frameContent.addView(mWeekBar, 2);
        LayoutParams barParams = (LayoutParams) this.mWeekBar.getLayoutParams();
        barParams.setMargins(CalendarUtil.dipToPx(context, 10),
                0,
                CalendarUtil.dipToPx(context, 10),
                0);

        mWeekBar.setup(mDelegate);
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());

        this.mWeekLine = findViewById(R.id.line);
        this.mWeekLine.setBackgroundColor(mDelegate.getWeekLineBackground());
        LayoutParams lineParams = (LayoutParams) this.mWeekLine.getLayoutParams();
        lineParams.setMargins(mDelegate.getWeekLineMargin(),
                mDelegate.getWeekBarHeight() + CalendarUtil.dipToPx(context, 10),
                mDelegate.getWeekLineMargin(),
                0);
        this.mWeekLine.setLayoutParams(lineParams);

        this.mMonthPager = findViewById(R.id.vp_month);
        this.mMonthPager.mWeekBar = mWeekBar;
        LayoutParams params = (LayoutParams) this.mMonthPager.getLayoutParams();
        params.setMargins(0, mDelegate.getWeekBarHeight() + CalendarUtil.dipToPx(context, 16), 0, 0);

        mDelegate.mInnerListener = new OnInnerDateSelectedListener() {

            //月视图选择事件
            @Override
            public void onMonthDateSelected(Calendar calendar, boolean isClick) {

                if (calendar.getYear() == mDelegate.getCurrentDay().getYear() &&
                        calendar.getMonth() == mDelegate.getCurrentDay().getMonth()
                        && mMonthPager.getCurrentItem() != mDelegate.mCurrentMonthViewItem) {
                    return;
                }
                mDelegate.mIndexCalendar = calendar;
                if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick) {
                    mDelegate.mSelectedCalendar = calendar;
                }
                mMonthPager.updateSelected();
                if (mWeekBar != null &&
                        (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick)) {
                    mWeekBar.onDateSelected(calendar, mDelegate.getWeekStart(), isClick);
                }
            }

            //周视图选择事件
            @Override
            public void onWeekDateSelected(Calendar calendar, boolean isClick) {
                mDelegate.mIndexCalendar = calendar;
                if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick
                        || mDelegate.mIndexCalendar.equals(mDelegate.mSelectedCalendar)) {
                    mDelegate.mSelectedCalendar = calendar;
                }
                int y = calendar.getYear() - mDelegate.getMinYear();
                int position = 12 * y + mDelegate.mIndexCalendar.getMonth() - mDelegate.getMinYearMonth();
                mMonthPager.setCurrentItem(position, false);
                mMonthPager.updateSelected();
                if (mWeekBar != null &&
                        (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT
                                || isClick
                                || mDelegate.mIndexCalendar.equals(mDelegate.mSelectedCalendar))) {
                    mWeekBar.onDateSelected(calendar, mDelegate.getWeekStart(), isClick);
                }
            }
        };


        if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            if (isInRange(mDelegate.getCurrentDay())) {
                mDelegate.mSelectedCalendar = mDelegate.createCurrentDate();
            } else {
                mDelegate.mSelectedCalendar = mDelegate.getMinRangeCalendar();
            }
        } else {
            mDelegate.mSelectedCalendar = new Calendar();
        }

        mDelegate.mIndexCalendar = mDelegate.mSelectedCalendar;

        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, mDelegate.getWeekStart(), false);

        mMonthPager.setup(mDelegate);
        mMonthPager.setCurrentItem(mDelegate.mCurrentMonthViewItem);
    }


    //返回今天
    public int getCurDay() {
        return mDelegate.getCurrentDay().getDay();
    }

    //返回本月
    public int getCurMonth() {
        return mDelegate.getCurrentDay().getMonth();
    }

    //返回本年
    public int getCurYear() {
        return mDelegate.getCurrentDay().getYear();
    }

    public void showYearSelectLayout(final int year) {
        showSelectLayout(year);
    }

    private void showSelectLayout(final int year) {
        if (mParentLayout != null && mParentLayout.mContentView != null) {
            if (!mParentLayout.isExpand()) {
                mParentLayout.expand();
                //return;
            }
        }
        mDelegate.isShowYearSelectedLayout = true;
        if (mParentLayout != null) {
            mParentLayout.hideContentView();
        }
        mWeekBar.animate()
                .translationY(-mWeekBar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(260)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mWeekBar.setVisibility(GONE);
                        if (mParentLayout != null && mParentLayout.mContentView != null) {
                            mParentLayout.expand();
                        }
                    }
                });

        mMonthPager.animate()
                .scaleX(0)
                .scaleY(0)
                .setDuration(260)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (mDelegate.mYearViewChangeListener != null) {
                            mDelegate.mYearViewChangeListener.onYearViewChange(false);
                        }
                    }
                });
    }

    //关闭日历布局，同时会滚动到指定的位置
    private void closeSelectLayout(final int position) {
        mWeekBar.setVisibility(VISIBLE);
        if (position == mMonthPager.getCurrentItem()) {
            if (mDelegate.mCalendarSelectListener != null &&
                    mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_SINGLE) {
                mDelegate.mCalendarSelectListener.onCalendarSelect(mDelegate.mSelectedCalendar, false);
            }
        } else {
            mMonthPager.setCurrentItem(position, false);
        }
        mWeekBar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(280)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mWeekBar.setVisibility(VISIBLE);
                    }
                });
        mMonthPager.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(180)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (mDelegate.mYearViewChangeListener != null) {
                            mDelegate.mYearViewChangeListener.onYearViewChange(true);
                        }
                        if (mParentLayout != null) {
                            mParentLayout.showContentView();
                            if (mParentLayout.isExpand()) {
                                mMonthPager.setVisibility(VISIBLE);
                            } else {
                                mParentLayout.shrink();
                            }
                        } else {
                            mMonthPager.setVisibility(VISIBLE);
                        }
                        mMonthPager.clearAnimation();
                    }
                });
    }

    /**
     * 滚动到当前
     */
    public void scrollToCurrent() {
        scrollToCurrent(false);
    }

    //滚动到当前
    public void scrollToCurrent(boolean smoothScroll) {
        if (!isInRange(mDelegate.getCurrentDay())) {
            return;
        }
        Calendar calendar = mDelegate.createCurrentDate();
        if (mDelegate.mCalendarInterceptListener != null &&
                mDelegate.mCalendarInterceptListener.onCalendarIntercept(calendar)) {
            mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(calendar, false);
            return;
        }
        mDelegate.mSelectedCalendar = mDelegate.createCurrentDate();
        mDelegate.mIndexCalendar = mDelegate.mSelectedCalendar;
        mDelegate.updateSelectCalendarScheme();
        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, mDelegate.getWeekStart(), false);
        if (mMonthPager.getVisibility() == VISIBLE) {
            mMonthPager.scrollToCurrent(smoothScroll);
        }
    }


    // 滚动到选择的日历
    public void scrollToSelectCalendar() {
        if (!mDelegate.mSelectedCalendar.isAvailable()) {
            return;
        }
        scrollToCalendar(mDelegate.mSelectedCalendar.getYear(),
                mDelegate.mSelectedCalendar.getMonth(),
                mDelegate.mSelectedCalendar.getDay(),
                false,
                true);
    }

    //滚动到指定日期
    public void scrollToCalendar(int year, int month, int day) {
        scrollToCalendar(year, month, day, false, true);
    }

    //滚动到指定日期
    public void scrollToCalendar(int year, int month, int day, boolean smoothScroll) {
        scrollToCalendar(year, month, day, smoothScroll, true);
    }

    public void scrollToCalendar(int year, int month, int day, boolean smoothScroll, boolean invokeListener) {

        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        if (!calendar.isAvailable()) {
            return;
        }
        if (!isInRange(calendar)) {
            return;
        }
        if (mDelegate.mCalendarInterceptListener != null &&
                mDelegate.mCalendarInterceptListener.onCalendarIntercept(calendar)) {
            mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(calendar, false);
            return;
        }

        mMonthPager.scrollToCalendar(year, month, day, smoothScroll, invokeListener);
    }


    //设置月视图是否可滚动
    public final void setMonthViewScrollable(boolean monthViewScrollable) {
        mDelegate.setMonthViewScrollable(monthViewScrollable);
    }


    //设置周视图是否可滚动
    public final void setWeekViewScrollable(boolean weekViewScrollable) {
        mDelegate.setWeekViewScrollable(weekViewScrollable);
    }


    public final void setDefaultMonthViewSelectDay() {
        mDelegate.setDefaultCalendarSelectDay(CalendarViewDelegate.FIRST_DAY_OF_MONTH);
    }

    public final void setLastMonthViewSelectDay() {
        mDelegate.setDefaultCalendarSelectDay(CalendarViewDelegate.LAST_MONTH_VIEW_SELECT_DAY);
    }

    public final void setLastMonthViewSelectDayIgnoreCurrent() {
        mDelegate.setDefaultCalendarSelectDay(CalendarViewDelegate.LAST_MONTH_VIEW_SELECT_DAY_IGNORE_CURRENT);
    }

    public final void clearSelectRange() {
        mDelegate.clearSelectRange();
        mMonthPager.clearSelectRange();
    }


    public final void clearSingleSelect() {
        mDelegate.mSelectedCalendar = new Calendar();
        mMonthPager.clearSingleSelect();
    }


    public final void clearMultiSelect() {
        mDelegate.mSelectedCalendars.clear();
        mMonthPager.clearMultiSelect();
    }


    public final void putMultiSelect(Calendar... calendars) {
        if (calendars == null || calendars.length == 0) {
            return;
        }
        for (Calendar calendar : calendars) {
            if (calendar == null || mDelegate.mSelectedCalendars.containsKey(calendar.toString())) {
                continue;
            }
            mDelegate.mSelectedCalendars.put(calendar.toString(), calendar);
        }
        update();
    }


    @SuppressWarnings("RedundantCollectionOperation")
    public final void removeMultiSelect(Calendar... calendars) {
        if (calendars == null || calendars.length == 0) {
            return;
        }
        for (Calendar calendar : calendars) {
            if (calendar == null) {
                continue;
            }
            if (mDelegate.mSelectedCalendars.containsKey(calendar.toString())) {
                mDelegate.mSelectedCalendars.remove(calendar.toString());
            }
        }
        update();
    }

    public final List<Calendar> getMultiSelectCalendars() {
        List<Calendar> calendars = new ArrayList<>();
        if (mDelegate.mSelectedCalendars.size() == 0) {
            return calendars;
        }
        calendars.addAll(mDelegate.mSelectedCalendars.values());
        Collections.sort(calendars);
        return calendars;
    }


    public final void setCalendarItemHeight(int calendarItemHeight) {
        if (mDelegate.getCalendarItemHeight() == calendarItemHeight) {
            return;
        }
        mDelegate.setCalendarItemHeight(calendarItemHeight);
        mMonthPager.updateItemHeight();
        if (mParentLayout == null) {
            return;
        }
        mParentLayout.updateCalendarItemHeight();
    }

    public final void setMonthView(Class<?> cls) {
        if (cls == null) {
            return;
        }
        if (mDelegate.getMonthViewClass().equals(cls)) {
            return;
        }
        mDelegate.setMonthViewClass(cls);
        mMonthPager.updateMonthViewClass();
    }

    //设置周视图
    public final void setWeekView(Class<?> cls) {
        if (cls == null) {
            return;
        }
        if (mDelegate.getWeekBarClass().equals(cls)) {
            return;
        }
    }

    //设置周栏视图
    public final void setWeekBar(Class<?> cls) {
        if (cls == null) {
            return;
        }
        if (mDelegate.getWeekBarClass().equals(cls)) {
            return;
        }
        mDelegate.setWeekBarClass(cls);
        FrameLayout frameContent = findViewById(R.id.frameContent);
        frameContent.removeView(mWeekBar);

        try {
            Constructor constructor = cls.getConstructor(Context.class);
            mWeekBar = (WeekBar) constructor.newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frameContent.addView(mWeekBar, 2);
        mWeekBar.setup(mDelegate);
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());
        this.mMonthPager.mWeekBar = mWeekBar;
        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, mDelegate.getWeekStart(), false);
    }

    public final void setOnCalendarInterceptListener(OnCalendarInterceptListener listener) {
        if (listener == null) {
            mDelegate.mCalendarInterceptListener = null;
        }
        if (listener == null || mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            return;
        }
        mDelegate.mCalendarInterceptListener = listener;
        if (!listener.onCalendarIntercept(mDelegate.mSelectedCalendar)) {
            return;
        }
        mDelegate.mSelectedCalendar = new Calendar();
    }

    public final void setOnClickCalendarPaddingListener(OnClickCalendarPaddingListener listener) {
        if (listener == null) {
            mDelegate.mClickCalendarPaddingListener = null;
        }
        if (listener == null) {
            return;
        }
        mDelegate.mClickCalendarPaddingListener = listener;
    }


    public void setOnYearChangeListener(OnYearChangeListener listener) {
        this.mDelegate.mYearChangeListener = listener;
    }

    public void setOnMonthChangeListener(OnMonthChangeListener listener) {
        this.mDelegate.mMonthChangeListener = listener;
    }


    /**
     * 周视图切换监听
     *
     * @param listener listener
     */
    public void setOnWeekChangeListener(OnWeekChangeListener listener) {
        this.mDelegate.mWeekChangeListener = listener;
    }

    //日期选择事件
    public void setOnCalendarSelectListener(OnCalendarSelectListener listener) {
        this.mDelegate.mCalendarSelectListener = listener;
        if (mDelegate.mCalendarSelectListener == null) {
            return;
        }
        if (mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            return;
        }
        if (!isInRange(mDelegate.mSelectedCalendar)) {
            return;
        }
        mDelegate.updateSelectCalendarScheme();
    }

    //日期选择事件
    public final void setOnCalendarRangeSelectListener(OnCalendarRangeSelectListener listener) {
        this.mDelegate.mCalendarRangeSelectListener = listener;
    }


    public final void setSelectStartCalendar(int startYear, int startMonth, int startDay) {
        if (mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_RANGE) {
            return;
        }
        Calendar startCalendar = new Calendar();
        startCalendar.setYear(startYear);
        startCalendar.setMonth(startMonth);
        startCalendar.setDay(startDay);
        setSelectStartCalendar(startCalendar);
    }

    public final void setSelectStartCalendar(Calendar startCalendar) {
        if (mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_RANGE) {
            return;
        }
        if (startCalendar == null) {
            return;
        }
        if (!isInRange(startCalendar)) {
            if (mDelegate.mCalendarRangeSelectListener != null) {
                mDelegate.mCalendarRangeSelectListener.onSelectOutOfRange(startCalendar, true);
            }
            return;
        }
        if (onCalendarIntercept(startCalendar)) {
            if (mDelegate.mCalendarInterceptListener != null) {
                mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(startCalendar, false);
            }
            return;
        }
        mDelegate.mSelectedEndRangeCalendar = null;
        mDelegate.mSelectedStartRangeCalendar = startCalendar;
        scrollToCalendar(startCalendar.getYear(), startCalendar.getMonth(), startCalendar.getDay());
    }

    public final void setSelectEndCalendar(int endYear, int endMonth, int endDay) {
        if (mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_RANGE) {
            return;
        }
        if (mDelegate.mSelectedStartRangeCalendar == null) {
            return;
        }
        Calendar endCalendar = new Calendar();
        endCalendar.setYear(endYear);
        endCalendar.setMonth(endMonth);
        endCalendar.setDay(endDay);
        setSelectEndCalendar(endCalendar);
    }

    public final void setSelectEndCalendar(Calendar endCalendar) {
        if (mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_RANGE) {
            return;
        }
        if (mDelegate.mSelectedStartRangeCalendar == null) {
            return;
        }
        setSelectCalendarRange(mDelegate.mSelectedStartRangeCalendar, endCalendar);
    }

    /**
     * 直接指定选择范围，set select calendar range
     *
     * @param startYear  startYear
     * @param startMonth startMonth
     * @param startDay   startDay
     * @param endYear    endYear
     * @param endMonth   endMonth
     * @param endDay     endDay
     */
    public final void setSelectCalendarRange(int startYear, int startMonth, int startDay,
                                             int endYear, int endMonth, int endDay) {
        if (mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_RANGE) {
            return;
        }
        Calendar startCalendar = new Calendar();
        startCalendar.setYear(startYear);
        startCalendar.setMonth(startMonth);
        startCalendar.setDay(startDay);

        Calendar endCalendar = new Calendar();
        endCalendar.setYear(endYear);
        endCalendar.setMonth(endMonth);
        endCalendar.setDay(endDay);
        setSelectCalendarRange(startCalendar, endCalendar);
    }

    /**
     * 设置选择日期范围
     *
     * @param startCalendar startCalendar
     * @param endCalendar   endCalendar
     */
    public final void setSelectCalendarRange(Calendar startCalendar, Calendar endCalendar) {
        if (mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_RANGE) {
            return;
        }
        if (startCalendar == null || endCalendar == null) {
            return;
        }
        if (onCalendarIntercept(startCalendar)) {
            if (mDelegate.mCalendarInterceptListener != null) {
                mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(startCalendar, false);
            }
            return;
        }
        if (onCalendarIntercept(endCalendar)) {
            if (mDelegate.mCalendarInterceptListener != null) {
                mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(endCalendar, false);
            }
            return;
        }
        int minDiffer = endCalendar.differ(startCalendar);
        if (minDiffer < 0) {
            return;
        }
        if (!isInRange(startCalendar) || !isInRange(endCalendar)) {
            return;
        }


        //优先判断各种直接return的情况，减少代码深度
        if (mDelegate.getMinSelectRange() != -1 && mDelegate.getMinSelectRange() > minDiffer + 1) {
            if (mDelegate.mCalendarRangeSelectListener != null) {
                mDelegate.mCalendarRangeSelectListener.onSelectOutOfRange(endCalendar, true);
            }
            return;
        } else if (mDelegate.getMaxSelectRange() != -1 && mDelegate.getMaxSelectRange() <
                minDiffer + 1) {
            if (mDelegate.mCalendarRangeSelectListener != null) {
                mDelegate.mCalendarRangeSelectListener.onSelectOutOfRange(endCalendar, false);
            }
            return;
        }
        if (mDelegate.getMinSelectRange() == -1 && minDiffer == 0) {
            mDelegate.mSelectedStartRangeCalendar = startCalendar;
            mDelegate.mSelectedEndRangeCalendar = null;
            if (mDelegate.mCalendarRangeSelectListener != null) {
                mDelegate.mCalendarRangeSelectListener.onCalendarRangeSelect(startCalendar, false);
            }
            scrollToCalendar(startCalendar.getYear(), startCalendar.getMonth(), startCalendar.getDay());
            return;
        }

        mDelegate.mSelectedStartRangeCalendar = startCalendar;
        mDelegate.mSelectedEndRangeCalendar = endCalendar;
        if (mDelegate.mCalendarRangeSelectListener != null) {
            mDelegate.mCalendarRangeSelectListener.onCalendarRangeSelect(startCalendar, false);
            mDelegate.mCalendarRangeSelectListener.onCalendarRangeSelect(endCalendar, true);
        }
        scrollToCalendar(startCalendar.getYear(), startCalendar.getMonth(), startCalendar.getDay());
    }

    /**
     * 是否拦截日期，此设置续设置mCalendarInterceptListener
     *
     * @param calendar calendar
     * @return 是否拦截日期
     */
    protected final boolean onCalendarIntercept(Calendar calendar) {
        return mDelegate.mCalendarInterceptListener != null &&
                mDelegate.mCalendarInterceptListener.onCalendarIntercept(calendar);
    }


    /**
     * 获得最大多选数量
     *
     * @return 获得最大多选数量
     */
    public final int getMaxMultiSelectSize() {
        return mDelegate.getMaxMultiSelectSize();
    }

    /**
     * 设置最大多选数量
     *
     * @param maxMultiSelectSize 最大多选数量
     */
    public final void setMaxMultiSelectSize(int maxMultiSelectSize) {
        mDelegate.setMaxMultiSelectSize(maxMultiSelectSize);
    }


    public final int getMinSelectRange() {
        return mDelegate.getMinSelectRange();
    }


    public final int getMaxSelectRange() {
        return mDelegate.getMaxSelectRange();
    }


    public void setOnCalendarLongClickListener(OnCalendarLongClickListener listener) {
        this.mDelegate.mCalendarLongClickListener = listener;
    }

    //日期长按事件
    public void setOnCalendarLongClickListener(OnCalendarLongClickListener listener, boolean preventLongPressedSelect) {
        this.mDelegate.mCalendarLongClickListener = listener;
        this.mDelegate.setPreventLongPressedSelected(preventLongPressedSelect);
    }

    /**
     * 视图改变事件
     *
     * @param listener listener
     */
    public void setOnViewChangeListener(OnViewChangeListener listener) {
        this.mDelegate.mViewChangeListener = listener;
    }


    public void setOnYearViewChangeListener(OnYearViewChangeListener listener) {
        this.mDelegate.mYearViewChangeListener = listener;
    }

    /**
     * 保持状态
     *
     * @return 状态
     */
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        if (mDelegate == null) {
            return super.onSaveInstanceState();
        }
        Bundle bundle = new Bundle();
        Parcelable parcelable = super.onSaveInstanceState();
        bundle.putParcelable("super", parcelable);
        bundle.putSerializable("selected_calendar", mDelegate.mSelectedCalendar);
        bundle.putSerializable("index_calendar", mDelegate.mIndexCalendar);
        return bundle;
    }

    /**
     * 恢复状态
     *
     * @param state 状态
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superData = bundle.getParcelable("super");
        mDelegate.mSelectedCalendar = (Calendar) bundle.getSerializable("selected_calendar");
        mDelegate.mIndexCalendar = (Calendar) bundle.getSerializable("index_calendar");
        if (mDelegate.mCalendarSelectListener != null) {
            mDelegate.mCalendarSelectListener.onCalendarSelect(mDelegate.mSelectedCalendar, false);
        }
        if (mDelegate.mIndexCalendar != null) {
            scrollToCalendar(mDelegate.mIndexCalendar.getYear(),
                    mDelegate.mIndexCalendar.getMonth(),
                    mDelegate.mIndexCalendar.getDay());
        }
        update();
        super.onRestoreInstanceState(superData);
    }


    /**
     * 初始化时初始化日历卡默认选择位置
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getParent() != null && getParent() instanceof CalendarLayout) {
            mParentLayout = (CalendarLayout) getParent();
            mMonthPager.mParentLayout = mParentLayout;
            mParentLayout.mWeekBar = mWeekBar;
            mParentLayout.setup(mDelegate);
            mParentLayout.initStatus();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mDelegate == null ||
                !mDelegate.isFullScreenCalendar()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        setCalendarItemHeight((height -
                mDelegate.getWeekBarHeight()) / 6);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //标记哪些日期有事件
    public final void setSchemeDate(Map<String, Calendar> mSchemeDates) {
        this.mDelegate.mSchemeDatesMap = mSchemeDates;
        this.mDelegate.updateSelectCalendarScheme();
        this.mMonthPager.updateScheme();
    }

    //清空日期标记
    public final void clearSchemeDate() {
        this.mDelegate.mSchemeDatesMap = null;
        this.mDelegate.clearSelectedScheme();
        mMonthPager.updateScheme();
    }

    //添加事物标记
    public final void addSchemeDate(Calendar calendar) {
        if (calendar == null || !calendar.isAvailable()) {
            return;
        }
        if (mDelegate.mSchemeDatesMap == null) {
            mDelegate.mSchemeDatesMap = new HashMap<>();
        }
        mDelegate.mSchemeDatesMap.remove(calendar.toString());
        mDelegate.mSchemeDatesMap.put(calendar.toString(), calendar);
        this.mDelegate.updateSelectCalendarScheme();
        this.mMonthPager.updateScheme();
    }

    //添加事物标记
    public final void addSchemeDate(Map<String, Calendar> mSchemeDates) {
        if (this.mDelegate == null || mSchemeDates == null || mSchemeDates.size() == 0) {
            return;
        }
        if (this.mDelegate.mSchemeDatesMap == null) {
            this.mDelegate.mSchemeDatesMap = new HashMap<>();
        }
        this.mDelegate.addSchemes(mSchemeDates);
        this.mDelegate.updateSelectCalendarScheme();
        this.mMonthPager.updateScheme();
    }

    //移除某天的标记
    public final void removeSchemeDate(Calendar calendar) {
        if (calendar == null) {
            return;
        }
        if (mDelegate.mSchemeDatesMap == null || mDelegate.mSchemeDatesMap.size() == 0) {
            return;
        }
        mDelegate.mSchemeDatesMap.remove(calendar.toString());
        if (mDelegate.mSelectedCalendar.equals(calendar)) {
            mDelegate.clearSelectedScheme();
        }
        mMonthPager.updateScheme();
    }

    //设置背景色
    public void setBackground(int yearViewBackground, int weekBackground, int lineBg) {
        mWeekBar.setBackgroundColor(weekBackground);
        mWeekLine.setBackgroundColor(lineBg);
    }


    //设置文本颜色
    public void setTextColor(
            int currentDayTextColor,
            int curMonthTextColor,
            int otherMonthColor,
            int curMonthLunarTextColor,
            int otherMonthLunarTextColor) {
        if (mDelegate == null || mMonthPager == null) {
            return;
        }
        mDelegate.setTextColor(currentDayTextColor, curMonthTextColor,
                otherMonthColor, curMonthLunarTextColor, otherMonthLunarTextColor);
        mMonthPager.updateStyle();
    }

    public void setSelectedColor(int selectedThemeColor, int selectedTextColor, int selectedLunarTextColor) {
        if (mDelegate == null || mMonthPager == null) {
            return;
        }
        mDelegate.setSelectColor(selectedThemeColor, selectedTextColor, selectedLunarTextColor);
        mMonthPager.updateStyle();
    }

    public void setThemeColor(int selectedThemeColor, int schemeColor) {
        if (mDelegate == null || mMonthPager == null) {
            return;
        }
        mDelegate.setThemeColor(selectedThemeColor, schemeColor);
        mMonthPager.updateStyle();
    }

    public void setSchemeColor(int schemeColor, int schemeTextColor, int schemeLunarTextColor) {
        if (mDelegate == null || mMonthPager == null) {
            return;
        }
        mDelegate.setSchemeColor(schemeColor, schemeTextColor, schemeLunarTextColor);
        mMonthPager.updateStyle();
    }


    //设置星期栏的背景和字体颜色
    public void setWeeColor(int weekBackground, int weekTextColor, int weekendTextColor) {
        if (mWeekBar == null) {
            return;
        }
        mWeekBar.setBackgroundColor(weekBackground);
        mWeekBar.setTextColor(weekTextColor, weekendTextColor);
    }


    public void setCalendarPadding(int mCalendarPadding) {
        if (mDelegate == null) {
            return;
        }
        mDelegate.setCalendarPadding(mCalendarPadding);
        update();
    }


    public void setCalendarPaddingLeft(int mCalendarPaddingLeft) {
        if (mDelegate == null) {
            return;
        }
        mDelegate.setCalendarPaddingLeft(mCalendarPaddingLeft);
        update();
    }

    public void setCalendarPaddingRight(int mCalendarPaddingRight) {
        if (mDelegate == null) {
            return;
        }
        mDelegate.setCalendarPaddingRight(mCalendarPaddingRight);
        update();
    }

    public final void setSelectDefaultMode() {
        if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            return;
        }
        mDelegate.mSelectedCalendar = mDelegate.mIndexCalendar;
        mDelegate.setSelectMode(CalendarViewDelegate.SELECT_MODE_DEFAULT);
        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, mDelegate.getWeekStart(), false);
        mMonthPager.updateDefaultSelect();
    }

    public void setSelectSingleMode() {
        if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_SINGLE) {
            return;
        }
        mDelegate.setSelectMode(CalendarViewDelegate.SELECT_MODE_SINGLE);
        mMonthPager.updateSelected();
    }

    //设置星期日周起始
    public void setWeekStarWithSun() {
        setWeekStart(CalendarViewDelegate.WEEK_START_WITH_SUN);
    }

    //设置星期一周起始
    public void setWeekStarWithMon() {
        setWeekStart(CalendarViewDelegate.WEEK_START_WITH_MON);
    }


    private void setWeekStart(int weekStart) {
        if (weekStart != CalendarViewDelegate.WEEK_START_WITH_SUN &&
                weekStart != CalendarViewDelegate.WEEK_START_WITH_MON &&
                weekStart != CalendarViewDelegate.WEEK_START_WITH_SAT)
            return;
        if (weekStart == mDelegate.getWeekStart())
            return;
        mDelegate.setWeekStart(weekStart);
        mWeekBar.onWeekStartChange(weekStart);
        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, weekStart, false);
        mMonthPager.updateWeekStart();
    }


    public boolean isSingleSelectMode() {
        return mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_SINGLE;
    }

    //设置显示模式为全部
    public void setAllMode() {
        setShowMode(CalendarViewDelegate.MODE_ALL_MONTH);
    }

    //设置显示模式为仅当前月份
    public void setOnlyCurrentMode() {
        setShowMode(CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH);
    }


    public void setFixMode() {
        setShowMode(CalendarViewDelegate.MODE_FIT_MONTH);
    }


    private void setShowMode(int mode) {
        if (mode != CalendarViewDelegate.MODE_ALL_MONTH &&
                mode != CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH &&
                mode != CalendarViewDelegate.MODE_FIT_MONTH)
            return;
        if (mDelegate.getMonthViewShowMode() == mode)
            return;
        mDelegate.setMonthViewShowMode(mode);
        mMonthPager.updateShowMode();
    }


    public final void update() {
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());
        mMonthPager.updateScheme();
    }

    /**
     * 更新周视图
     */
    public void updateWeekBar() {
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());
    }


    //更新当前日期
    public final void updateCurrentDate() {
        if (mDelegate == null || mMonthPager == null) {
            return;
        }
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        if (getCurDay() == day) {
            return;
        }
        mDelegate.updateCurrentDay();
        mMonthPager.updateCurrentDate();
    }


    //获取当前月份日期
    public List<Calendar> getCurrentMonthCalendars() {
        return mMonthPager.getCurrentMonthCalendars();
    }

    public Calendar getSelectedCalendar() {
        return mDelegate.mSelectedCalendar;
    }

    public Calendar getMinRangeCalendar() {
        return mDelegate.getMinRangeCalendar();
    }

    public Calendar getMaxRangeCalendar() {
        return mDelegate.getMaxRangeCalendar();
    }


    public MonthViewPager getMonthViewPager() {
        return mMonthPager;
    }

    protected final boolean isInRange(Calendar calendar) {
        return mDelegate != null && CalendarUtil.isCalendarInRange(calendar, mDelegate);
    }

    public interface OnYearChangeListener {
        void onYearChange(int year);
    }

    //月份切换事件
    public interface OnMonthChangeListener {
        void onMonthChange(int year, int month);
    }

    public interface OnWeekChangeListener {
        void onWeekChange(List<Calendar> weekCalendars);
    }

    interface OnInnerDateSelectedListener {
        void onMonthDateSelected(Calendar calendar, boolean isClick);

        void onWeekDateSelected(Calendar calendar, boolean isClick);
    }

    public interface OnCalendarRangeSelectListener {

        void onCalendarSelectOutOfRange(Calendar calendar);

        void onSelectOutOfRange(Calendar calendar, boolean isOutOfMinRange);

        void onCalendarRangeSelect(Calendar calendar, boolean isEnd);
    }

    public interface OnCalendarMultiSelectListener {

        void onCalendarMultiSelectOutOfRange(Calendar calendar);

        void onMultiSelectOutOfSize(Calendar calendar, int maxSize);

        void onCalendarMultiSelect(Calendar calendar, int curSize, int maxSize);
    }

    public interface OnCalendarSelectListener {

        void onCalendarOutOfRange(Calendar calendar);

        //日期选择事件
        void onCalendarSelect(Calendar calendar, boolean isClick);
    }

    public interface OnCalendarLongClickListener {

        void onCalendarLongClickOutOfRange(Calendar calendar);

        //日期长按事件
        void onCalendarLongClick(Calendar calendar);
    }


    public interface OnViewChangeListener {
        void onViewChange(boolean isMonthView);
    }


    public interface OnYearViewChangeListener {
        void onYearViewChange(boolean isClose);
    }

    //拦截日期是否可用事件
    public interface OnCalendarInterceptListener {
        boolean onCalendarIntercept(Calendar calendar);

        void onCalendarInterceptClick(Calendar calendar, boolean isClick);
    }

    //点击Padding位置事件
    public interface OnClickCalendarPaddingListener {

        void onClickCalendarPadding(float x, float y, boolean isMonthView, Calendar adjacentCalendar, Object obj);
    }
}
