package com.android.launcher3.moudle.calendar.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.android.launcher3.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

final class CalendarViewDelegate {

    static final int WEEK_START_WITH_SUN = 1;

    static final int WEEK_START_WITH_MON = 2;

    static final int WEEK_START_WITH_SAT = 7;

    static final int FIRST_DAY_OF_MONTH = 0;

    static final int LAST_MONTH_VIEW_SELECT_DAY = 1;

    static final int LAST_MONTH_VIEW_SELECT_DAY_IGNORE_CURRENT = 2;

    private int mDefaultCalendarSelectDay;

    private int mWeekStart;

    static final int MODE_ALL_MONTH = 0;
    static final int MODE_ONLY_CURRENT_MONTH = 1;

    static final int MODE_FIT_MONTH = 2;

    private int mMonthViewShowMode;


    static final int SELECT_MODE_DEFAULT = 0;

    static final int SELECT_MODE_SINGLE = 1;

    static final int SELECT_MODE_RANGE = 2;

    private int mSelectMode;

    static final int MIN_YEAR = 1900;

    private static final int MAX_YEAR = 2099;

    private int mCurDayTextColor,
            mCurrentDayThemeColor,
            mCurDayLunarTextColor,
            mWeekTextColor,
            mWeekendTextColor,
            mSchemeTextColor,
            mSchemeLunarTextColor,
            mOtherMonthTextColor,
            mCurrentMonthTextColor,
            mCurrentMonthWeekendTextColor,
            mSelectedTextColor,
            mSelectedLunarTextColor,
            mCurMonthLunarTextColor,
            mOtherMonthLunarTextColor;

    private boolean preventLongPressedSelected;

    private int
            mYearViewPadding,
            mYearViewPaddingLeft,
            mYearViewPaddingRight;

    private int
            mYearViewMonthPaddingLeft,
            mYearViewMonthPaddingRight,
            mYearViewMonthPaddingTop,
            mYearViewMonthPaddingBottom;

    private int mCalendarPadding;

    private int mCalendarPaddingLeft;

    private int mCalendarPaddingRight;

    private int mYearViewMonthTextSize,
            mYearViewDayTextSize,
            mYearViewWeekTextSize;

    private int mYearViewMonthHeight,
            mYearViewWeekHeight;

    private int mYearViewMonthTextColor,
            mYearViewDayTextColor,
            mYearViewSchemeTextColor,
            mYearViewSelectTextColor,
            mYearViewCurDayTextColor,
            mYearViewWeekTextColor;

    private int mWeekLineBackground,
            mYearViewBackground,
            mWeekBackground;

    private int mWeekLineMargin;

    private int mWeekTextSize;

    private int mSchemeThemeColor, mSelectedThemeColor;

    private String mMonthViewClassPath;

    private Class<?> mMonthViewClass;

    private String mYearViewClassPath;

    private String mWeekBarClassPath;

    private Class<?> mWeekBarClass;

    boolean isShowYearSelectedLayout;

    private String mSchemeText;

    private int mMinYear, mMaxYear;

    private int mMinYearMonth, mMaxYearMonth;

    private int mMinYearDay, mMaxYearDay;

    private int mDayTextSize, mLunarTextSize;

    private int mCalendarItemHeight;

    private boolean isFullScreenCalendar;

    private int mWeekBarHeight;

    private Calendar mCurrentDate;


    private boolean mMonthViewScrollable,
            mWeekViewScrollable,
            mYearViewScrollable;

    int mCurrentMonthViewItem;

    Map<String, Calendar> mSchemeDatesMap;

    CalendarView.OnClickCalendarPaddingListener mClickCalendarPaddingListener;

    CalendarView.OnCalendarInterceptListener mCalendarInterceptListener;

    CalendarView.OnCalendarSelectListener mCalendarSelectListener;

    CalendarView.OnCalendarRangeSelectListener mCalendarRangeSelectListener;

    CalendarView.OnCalendarLongClickListener mCalendarLongClickListener;

    CalendarView.OnInnerDateSelectedListener mInnerListener;

    CalendarView.OnYearChangeListener mYearChangeListener;

    CalendarView.OnMonthChangeListener mMonthChangeListener;

    CalendarView.OnWeekChangeListener mWeekChangeListener;

    CalendarView.OnViewChangeListener mViewChangeListener;

    CalendarView.OnYearViewChangeListener mYearViewChangeListener;

    Calendar mSelectedCalendar;

    Calendar mIndexCalendar;

    Map<String, Calendar> mSelectedCalendars = new HashMap<>();

    private int mMaxMultiSelectSize;

    Calendar mSelectedStartRangeCalendar, mSelectedEndRangeCalendar;

    private int mMinSelectRange, mMaxSelectRange;

    CalendarViewDelegate(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        LunarCalendar.init(context);

        mCalendarPadding = (int) array.getDimension(R.styleable.CalendarView_calendar_padding, 0);
        mCalendarPaddingLeft = (int) array.getDimension(R.styleable.CalendarView_calendar_padding_left, 0);
        mCalendarPaddingRight = (int) array.getDimension(R.styleable.CalendarView_calendar_padding_right, 0);

        if (mCalendarPadding != 0) {
            mCalendarPaddingLeft = mCalendarPadding;
            mCalendarPaddingRight = mCalendarPadding;
        }

        mSchemeTextColor = array.getColor(R.styleable.CalendarView_scheme_text_color, 0xFFFFFFFF);
        mSchemeLunarTextColor = array.getColor(R.styleable.CalendarView_scheme_lunar_text_color, 0xFFe1e1e1);
        mSchemeThemeColor = array.getColor(R.styleable.CalendarView_scheme_theme_color, 0x50CFCFCF);
        mMonthViewClassPath = array.getString(R.styleable.CalendarView_month_view);
        mYearViewClassPath = array.getString(R.styleable.CalendarView_year_view);
        mWeekBarClassPath = array.getString(R.styleable.CalendarView_week_bar_view);
        mWeekTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_week_text_size,
                CalendarUtil.dipToPx(context, 12));
        mWeekBarHeight = (int) array.getDimension(R.styleable.CalendarView_week_bar_height,
                CalendarUtil.dipToPx(context, 40));
        mWeekLineMargin = (int) array.getDimension(R.styleable.CalendarView_week_line_margin,
                CalendarUtil.dipToPx(context, 0));

        mSchemeText = array.getString(R.styleable.CalendarView_scheme_text);
        if (TextUtils.isEmpty(mSchemeText)) {
            mSchemeText = "记";
        }

        mMonthViewScrollable = array.getBoolean(R.styleable.CalendarView_month_view_scrollable, true);
        mWeekViewScrollable = array.getBoolean(R.styleable.CalendarView_week_view_scrollable, true);
        mYearViewScrollable = array.getBoolean(R.styleable.CalendarView_year_view_scrollable, true);

        mDefaultCalendarSelectDay = array.getInt(R.styleable.CalendarView_month_view_auto_select_day,
                FIRST_DAY_OF_MONTH);

        mMonthViewShowMode = array.getInt(R.styleable.CalendarView_month_view_show_mode, MODE_ALL_MONTH);
        mWeekStart = array.getInt(R.styleable.CalendarView_week_start_with, WEEK_START_WITH_SUN);
        mSelectMode = array.getInt(R.styleable.CalendarView_select_mode, SELECT_MODE_DEFAULT);
        mMaxMultiSelectSize = array.getInt(R.styleable.CalendarView_max_multi_select_size, Integer.MAX_VALUE);
        mMinSelectRange = array.getInt(R.styleable.CalendarView_min_select_range, -1);
        mMaxSelectRange = array.getInt(R.styleable.CalendarView_max_select_range, -1);
        setSelectRange(mMinSelectRange, mMaxSelectRange);

        mWeekBackground = array.getColor(R.styleable.CalendarView_week_background, Color.WHITE);
        mWeekLineBackground = array.getColor(R.styleable.CalendarView_week_line_background, Color.TRANSPARENT);
        mYearViewBackground = array.getColor(R.styleable.CalendarView_year_view_background, Color.WHITE);
        mWeekTextColor = array.getColor(R.styleable.CalendarView_week_text_color, 0xFF333333);
        mWeekendTextColor = array.getColor(R.styleable.CalendarView_weekend_text_color, 0xFF333333);

        mCurDayTextColor = array.getColor(R.styleable.CalendarView_current_day_text_color, Color.RED);
        mCurrentDayThemeColor = array.getColor(R.styleable.CalendarView_current_day_theme_color, 0x50CFCFCF);

        mCurDayLunarTextColor = array.getColor(R.styleable.CalendarView_current_day_lunar_text_color, Color.RED);

        mSelectedThemeColor = array.getColor(R.styleable.CalendarView_selected_theme_color, 0x50CFCFCF);
        mSelectedTextColor = array.getColor(R.styleable.CalendarView_selected_text_color, 0xFF111111);

        mSelectedLunarTextColor = array.getColor(R.styleable.CalendarView_selected_lunar_text_color, 0xFF111111);
        mCurrentMonthTextColor = array.getColor(R.styleable.CalendarView_current_month_text_color, 0xFF111111);
        mCurrentMonthWeekendTextColor = array.getColor(R.styleable.CalendarView_current_month_weekend_text_color, 0xFF111111);


        mOtherMonthTextColor = array.getColor(R.styleable.CalendarView_other_month_text_color, 0xFFe1e1e1);

        mCurMonthLunarTextColor = array.getColor(R.styleable.CalendarView_current_month_lunar_text_color, 0xffe1e1e1);
        mOtherMonthLunarTextColor = array.getColor(R.styleable.CalendarView_other_month_lunar_text_color, 0xffe1e1e1);
        mMinYear = array.getInt(R.styleable.CalendarView_min_year, 1971);
        mMaxYear = array.getInt(R.styleable.CalendarView_max_year, 2055);
        mMinYearMonth = array.getInt(R.styleable.CalendarView_min_year_month, 1);
        mMaxYearMonth = array.getInt(R.styleable.CalendarView_max_year_month, 12);
        mMinYearDay = array.getInt(R.styleable.CalendarView_min_year_day, 1);
        mMaxYearDay = array.getInt(R.styleable.CalendarView_max_year_day, -1);

        mDayTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_day_text_size,
                CalendarUtil.dipToPx(context, 16));
        mLunarTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_lunar_text_size,
                CalendarUtil.dipToPx(context, 10));
        mCalendarItemHeight = (int) array.getDimension(R.styleable.CalendarView_calendar_height,
                CalendarUtil.dipToPx(context, 56));
        isFullScreenCalendar = array.getBoolean(R.styleable.CalendarView_calendar_match_parent, false);

        mYearViewMonthTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_month_text_size,
                CalendarUtil.dipToPx(context, 18));
        mYearViewDayTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_day_text_size,
                CalendarUtil.dipToPx(context, 7));
        mYearViewMonthTextColor = array.getColor(R.styleable.CalendarView_year_view_month_text_color, 0xFF111111);
        mYearViewDayTextColor = array.getColor(R.styleable.CalendarView_year_view_day_text_color, 0xFF111111);
        mYearViewSchemeTextColor = array.getColor(R.styleable.CalendarView_year_view_scheme_color, mSchemeThemeColor);
        mYearViewWeekTextColor = array.getColor(R.styleable.CalendarView_year_view_week_text_color, 0xFF333333);
        mYearViewCurDayTextColor = array.getColor(R.styleable.CalendarView_year_view_current_day_text_color, mCurDayTextColor);
        mYearViewSelectTextColor = array.getColor(R.styleable.CalendarView_year_view_select_text_color, 0xFF333333);
        mYearViewWeekTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_week_text_size,
                CalendarUtil.dipToPx(context, 8));
        mYearViewMonthHeight = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_month_height,
                CalendarUtil.dipToPx(context, 32));
        mYearViewWeekHeight = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_week_height,
                CalendarUtil.dipToPx(context, 0));

        mYearViewPadding = (int) array.getDimension(R.styleable.CalendarView_year_view_padding,
                CalendarUtil.dipToPx(context, 12));
        mYearViewPaddingLeft = (int) array.getDimension(R.styleable.CalendarView_year_view_padding_left,
                CalendarUtil.dipToPx(context, 12));
        mYearViewPaddingRight = (int) array.getDimension(R.styleable.CalendarView_year_view_padding_right,
                CalendarUtil.dipToPx(context, 12));

        if (mYearViewPadding != 0) {
            mYearViewPaddingLeft = mYearViewPadding;
            mYearViewPaddingRight = mYearViewPadding;
        }

        mYearViewMonthPaddingTop = (int) array.getDimension(R.styleable.CalendarView_year_view_month_padding_top,
                CalendarUtil.dipToPx(context, 4));
        mYearViewMonthPaddingBottom = (int) array.getDimension(R.styleable.CalendarView_year_view_month_padding_bottom,
                CalendarUtil.dipToPx(context, 4));

        mYearViewMonthPaddingLeft = (int) array.getDimension(R.styleable.CalendarView_year_view_month_padding_left,
                CalendarUtil.dipToPx(context, 4));
        mYearViewMonthPaddingRight = (int) array.getDimension(R.styleable.CalendarView_year_view_month_padding_right,
                CalendarUtil.dipToPx(context, 4));
        if (!(mMinYear == -1 && mMaxYear == -1)) {
            if (mMinYear <= MIN_YEAR) mMinYear = MIN_YEAR;
            if (mMaxYear >= MAX_YEAR) mMaxYear = MAX_YEAR;
        }
        array.recycle();
        init();
    }

    private void init() {
        mCurrentDate = new Calendar();
        Date d = new Date();
        mCurrentDate.setYear(CalendarUtil.getDate("yyyy", d));
        mCurrentDate.setMonth(CalendarUtil.getDate("MM", d));
        mCurrentDate.setDay(CalendarUtil.getDate("dd", d));
        mCurrentDate.setCurrentDay(true);
        LunarCalendar.setupLunarCalendar(mCurrentDate);
        if (mMinYear == -1 && mMaxYear == -1) {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int year = calendar.get(java.util.Calendar.YEAR);
            mMinYear = year - 2;
            mMaxYear = year + 2;
        }
        setRange(mMinYear, mMinYearMonth, mMaxYear, mMaxYearMonth);

        try {
            mWeekBarClass = TextUtils.isEmpty(mWeekBarClassPath) ?
                    mWeekBarClass = WeekBar.class : Class.forName(mWeekBarClassPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mMonthViewClass = TextUtils.isEmpty(mMonthViewClassPath) ?
                    DefaultMonthView.class : Class.forName(mMonthViewClassPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setRange(int minYear, int minYearMonth,
                          int maxYear, int maxYearMonth) {
        this.mMinYear = minYear;
        this.mMinYearMonth = minYearMonth;
        this.mMaxYear = maxYear;
        this.mMaxYearMonth = maxYearMonth;
        if (this.mMaxYear < mCurrentDate.getYear()) {
            this.mMaxYear = mCurrentDate.getYear();
        }
        if (this.mMaxYearDay == -1) {
            this.mMaxYearDay = CalendarUtil.getMonthDaysCount(this.mMaxYear, mMaxYearMonth);
        }
        int y = mCurrentDate.getYear() - this.mMinYear;
        mCurrentMonthViewItem = 12 * y + mCurrentDate.getMonth() - this.mMinYearMonth;
    }


    String getSchemeText() {
        return mSchemeText;
    }

    int getCurDayTextColor() {
        return mCurDayTextColor;
    }

    int getCurDayLunarTextColor() {
        return mCurDayLunarTextColor;
    }

    int getWeekTextColor() {
        return mWeekTextColor;
    }

    int getWeekendTextColor() {
        return mWeekendTextColor;
    }

    public int getmCurrentDayThemeColor() {
        return mCurrentDayThemeColor;
    }

    int getSchemeTextColor() {
        return mSchemeTextColor;
    }

    int getSchemeLunarTextColor() {
        return mSchemeLunarTextColor;
    }

    int getOtherMonthTextColor() {
        return mOtherMonthTextColor;
    }

    int getCurrentMonthTextColor() {
        return mCurrentMonthTextColor;
    }

    public int getCurrentMonthWeekendTextColor() {
        return mCurrentMonthWeekendTextColor;
    }

    int getSelectedTextColor() {
        return mSelectedTextColor;
    }

    int getSelectedLunarTextColor() {
        return mSelectedLunarTextColor;
    }

    int getCurrentMonthLunarTextColor() {
        return mCurMonthLunarTextColor;
    }

    int getOtherMonthLunarTextColor() {
        return mOtherMonthLunarTextColor;
    }

    int getSchemeThemeColor() {
        return mSchemeThemeColor;
    }

    int getSelectedThemeColor() {
        return mSelectedThemeColor;
    }

    int getWeekBackground() {
        return mWeekBackground;
    }

    int getWeekLineBackground() {
        return mWeekLineBackground;
    }

    int getWeekLineMargin() {
        return mWeekLineMargin;
    }

    Class<?> getMonthViewClass() {
        return mMonthViewClass;
    }


    Class<?> getWeekBarClass() {
        return mWeekBarClass;
    }


    int getWeekBarHeight() {
        return mWeekBarHeight;
    }

    int getMinYear() {
        return mMinYear;
    }

    int getMaxYear() {
        return mMaxYear;
    }

    int getDayTextSize() {
        return mDayTextSize;
    }

    int getLunarTextSize() {
        return mLunarTextSize;
    }

    int getCalendarItemHeight() {
        return mCalendarItemHeight;
    }

    void setCalendarItemHeight(int height) {
        mCalendarItemHeight = height;
    }

    int getMinYearMonth() {
        return mMinYearMonth;
    }

    int getMaxYearMonth() {
        return mMaxYearMonth;
    }

    int getMonthViewShowMode() {
        return mMonthViewShowMode;
    }

    void setMonthViewShowMode(int monthViewShowMode) {
        this.mMonthViewShowMode = monthViewShowMode;
    }

    void setTextColor(int curDayTextColor, int curMonthTextColor, int otherMonthTextColor, int curMonthLunarTextColor, int otherMonthLunarTextColor) {
        mCurDayTextColor = curDayTextColor;
        mOtherMonthTextColor = otherMonthTextColor;
        mCurrentMonthTextColor = curMonthTextColor;
        mCurMonthLunarTextColor = curMonthLunarTextColor;
        mOtherMonthLunarTextColor = otherMonthLunarTextColor;
    }

    void setSchemeColor(int schemeColor, int schemeTextColor, int schemeLunarTextColor) {
        this.mSchemeThemeColor = schemeColor;
        this.mSchemeTextColor = schemeTextColor;
        this.mSchemeLunarTextColor = schemeLunarTextColor;
    }


    void setSelectColor(int selectedColor, int selectedTextColor, int selectedLunarTextColor) {
        this.mSelectedThemeColor = selectedColor;
        this.mSelectedTextColor = selectedTextColor;
        this.mSelectedLunarTextColor = selectedLunarTextColor;
    }

    void setThemeColor(int selectedThemeColor, int schemeColor) {
        this.mSelectedThemeColor = selectedThemeColor;
        this.mSchemeThemeColor = schemeColor;
    }

    boolean isMonthViewScrollable() {
        return mMonthViewScrollable;
    }

    void setMonthViewScrollable(boolean monthViewScrollable) {
        this.mMonthViewScrollable = monthViewScrollable;
    }

    void setWeekViewScrollable(boolean weekViewScrollable) {
        this.mWeekViewScrollable = weekViewScrollable;
    }

    int getWeekStart() {
        return mWeekStart;
    }

    void setWeekStart(int mWeekStart) {
        this.mWeekStart = mWeekStart;
    }

    void setDefaultCalendarSelectDay(int defaultCalendarSelect) {
        this.mDefaultCalendarSelectDay = defaultCalendarSelect;
    }

    int getDefaultCalendarSelectDay() {
        return mDefaultCalendarSelectDay;
    }

    int getWeekTextSize() {
        return mWeekTextSize;
    }


    int getSelectMode() {
        return mSelectMode;
    }


    void setSelectMode(int mSelectMode) {
        this.mSelectMode = mSelectMode;
    }

    int getMinSelectRange() {
        return mMinSelectRange;
    }

    int getMaxSelectRange() {
        return mMaxSelectRange;
    }

    int getMaxMultiSelectSize() {
        return mMaxMultiSelectSize;
    }

    void setMaxMultiSelectSize(int maxMultiSelectSize) {
        this.mMaxMultiSelectSize = maxMultiSelectSize;
    }

    final void setSelectRange(int minRange, int maxRange) {
        if (minRange > maxRange && maxRange > 0) {
            mMaxSelectRange = minRange;
            mMinSelectRange = minRange;
            return;
        }
        if (minRange <= 0) {
            mMinSelectRange = -1;
        } else {
            mMinSelectRange = minRange;
        }
        if (maxRange <= 0) {
            mMaxSelectRange = -1;
        } else {
            mMaxSelectRange = maxRange;
        }
    }

    Calendar getCurrentDay() {
        return mCurrentDate;
    }

    void updateCurrentDay() {
        Date d = new Date();
        mCurrentDate.setYear(CalendarUtil.getDate("yyyy", d));
        mCurrentDate.setMonth(CalendarUtil.getDate("MM", d));
        mCurrentDate.setDay(CalendarUtil.getDate("dd", d));
        LunarCalendar.setupLunarCalendar(mCurrentDate);
    }

    @SuppressWarnings("unused")
    int getCalendarPadding() {
        return mCalendarPadding;
    }

    void setCalendarPadding(int mCalendarPadding) {
        this.mCalendarPadding = mCalendarPadding;
        mCalendarPaddingLeft = mCalendarPadding;
        mCalendarPaddingRight = mCalendarPadding;
    }

    int getCalendarPaddingLeft() {
        return mCalendarPaddingLeft;
    }

    void setCalendarPaddingLeft(int mCalendarPaddingLeft) {
        this.mCalendarPaddingLeft = mCalendarPaddingLeft;
    }

    int getCalendarPaddingRight() {
        return mCalendarPaddingRight;
    }

    void setCalendarPaddingRight(int mCalendarPaddingRight) {
        this.mCalendarPaddingRight = mCalendarPaddingRight;
    }

    void setPreventLongPressedSelected(boolean preventLongPressedSelected) {
        this.preventLongPressedSelected = preventLongPressedSelected;
    }

    void setMonthViewClass(Class<?> monthViewClass) {
        this.mMonthViewClass = monthViewClass;
    }

    void setWeekBarClass(Class<?> weekBarClass) {
        this.mWeekBarClass = weekBarClass;
    }


    boolean isPreventLongPressedSelected() {
        return preventLongPressedSelected;
    }

    void clearSelectedScheme() {
        mSelectedCalendar.clearScheme();
    }

    int getMinYearDay() {
        return mMinYearDay;
    }

    int getMaxYearDay() {
        return mMaxYearDay;
    }

    boolean isFullScreenCalendar() {
        return isFullScreenCalendar;
    }

    final void updateSelectCalendarScheme() {
        if (mSchemeDatesMap != null && mSchemeDatesMap.size() > 0) {
            String key = mSelectedCalendar.toString();
            if (mSchemeDatesMap.containsKey(key)) {
                Calendar d = mSchemeDatesMap.get(key);
                mSelectedCalendar.mergeScheme(d, getSchemeText());
            }
        } else {
            clearSelectedScheme();
        }
    }


    Calendar createCurrentDate() {
        Calendar calendar = new Calendar();
        calendar.setYear(mCurrentDate.getYear());
        calendar.setWeek(mCurrentDate.getWeek());
        calendar.setMonth(mCurrentDate.getMonth());
        calendar.setDay(mCurrentDate.getDay());
        calendar.setCurrentDay(true);
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }

    final Calendar getMinRangeCalendar() {
        Calendar calendar = new Calendar();
        calendar.setYear(mMinYear);
        calendar.setMonth(mMinYearMonth);
        calendar.setDay(mMinYearDay);
        calendar.setCurrentDay(calendar.equals(mCurrentDate));
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }

    @SuppressWarnings("unused")
    final Calendar getMaxRangeCalendar() {
        Calendar calendar = new Calendar();
        calendar.setYear(mMaxYear);
        calendar.setMonth(mMaxYearMonth);
        calendar.setDay(mMaxYearDay);
        calendar.setCurrentDay(calendar.equals(mCurrentDate));
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }


    final void addSchemes(Map<String, Calendar> mSchemeDates) {
        if (mSchemeDates == null || mSchemeDates.size() == 0) {
            return;
        }
        if (this.mSchemeDatesMap == null) {
            this.mSchemeDatesMap = new HashMap<>();
        }
        for (String key : mSchemeDates.keySet()) {
            this.mSchemeDatesMap.remove(key);
            Calendar calendar = mSchemeDates.get(key);
            if (calendar == null) {
                continue;
            }
            this.mSchemeDatesMap.put(key, calendar);
        }
    }

    final void clearSelectRange() {
        mSelectedStartRangeCalendar = null;
        mSelectedEndRangeCalendar = null;
    }

}
