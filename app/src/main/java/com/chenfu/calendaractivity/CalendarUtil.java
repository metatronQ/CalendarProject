package com.chenfu.calendaractivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shubo on 16/8/29.
 */
public class CalendarUtil {

    public static String getFormattedDate(Calendar calendar) {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
            return "今天 " + getWeekDays(calendar);
        } else {
            return (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日 " + getWeekDays(calendar);
        }
    }

    public static String getWeekDays(Calendar calendar) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "周一";
            case Calendar.TUESDAY:
                return "周二";
            case Calendar.WEDNESDAY:
                return "周三";
            case Calendar.THURSDAY:
                return "周四";
            case Calendar.FRIDAY:
                return "周五";
            case Calendar.SATURDAY:
                return "周六";
            case Calendar.SUNDAY:
                return "周日";
            default:
                return "";
        }
    }

    public static String getFormattedCourseTime(Calendar calendar, float lastInHour) {
        return (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日 " +
                calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " 时长:" + lastInHour + "小时";
    }

    public static String getStartAndEndTimeByTS(long start, long end) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        String text = format.format(new Date(start));
        SimpleDateFormat format1 = new SimpleDateFormat("-HH:mm");
        text += format1.format(new Date(end));
        return text;
    }

    /**
     *格式化时间（单位为秒）
     * @param second
     * @return
     */
    public static String getFormatTimeLength(long second){
        if(second < 0){
            return "";
        }
        return formatNumber((int) (second / 3600)) + ":" + formatNumber((int) (second % 3600 / 60)) + ":" + formatNumber((int) (second % 3600 % 60));
    }

    private static String formatNumber(int number){
        return number >= 10 ? String.valueOf(number) : "0" + number;
    }

    private int maxEnableClickDate = 31;
    private boolean isCurrentMonth = true;

    public List<DateInfo> initMonthData(Date initialDate) {
        List<DateInfo> dateList = new ArrayList<>();
        // 获取China区Calendar实例，实际是GregorianCalendar的一个实例
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        // 初始化日期
        calendar.setTime(initialDate);

        int yearValue = calendar.get(Calendar.YEAR);
        int monthValue = calendar.get(Calendar.MONTH) + 1;

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(System.currentTimeMillis());
        int nowYearValue = nowCalendar.get(Calendar.YEAR);
        int nowMonthValue = nowCalendar.get(Calendar.MONTH) + 1;

        if (yearValue == nowYearValue && monthValue == nowMonthValue) {
            isCurrentMonth = true;
            maxEnableClickDate = nowCalendar.get(Calendar.DAY_OF_MONTH);
        } else {
            isCurrentMonth = false;
        }

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  //获得当前日期所在月份有多少天（或者说day的最大值)，用于后面的计算
        Calendar calendarClone = (Calendar) calendar.clone(); //克隆一个Calendar再进行操作，避免造成混乱
        calendarClone.set(Calendar.DAY_OF_MONTH, 1);  //将日期调到当前月份的第一天
        int startDayOfWeek = calendarClone.get(Calendar.DAY_OF_WEEK); //获得当前日期所在月份的第一天是星期几
        calendarClone.set(Calendar.DAY_OF_MONTH, maxDay); //将日期调到当前月份的最后一天
        int endDayOfWeek = calendarClone.get(Calendar.DAY_OF_WEEK); //获得当前日期所在月份的最后一天是星期几

        /**
         * 计算上一个月在本月日历页出现的那几天.
         * 比如，startDayOfWeek = 3，表示当月第一天是星期二，所以日历向前会空出2天的位置，那么让上月的最后两天显示在星期日和星期一的位置上.
         */
        int startEmptyCount = startDayOfWeek - 1; //上月在本月日历页因该出现的天数。
        Calendar preCalendar = (Calendar) calendar.clone();  //克隆一份再操作
        preCalendar.set(Calendar.DAY_OF_MONTH, 1); //将日期调到当月第一天
        preCalendar.add(Calendar.DAY_OF_MONTH, -startEmptyCount); //向前推移startEmptyCount天
        for (int i = 0; i < startEmptyCount; i++) {
            DateInfo dateInfo = new DateInfo(); //使用DateInfo来储存所需的相关信息
            dateInfo.setDate(preCalendar.getTime());
            dateInfo.setType(DateInfo.PRE_MONTH); //标记日期信息的类型为上个月
            dateList.add(dateInfo); //将日期添加到数组中
            preCalendar.add(Calendar.DAY_OF_MONTH, 1); //向后推移一天
        }

        /**
         * 计算当月的每一天日期
         */
        calendar.set(Calendar.DAY_OF_MONTH, 1); //由于是获取当月日期信息，所以直接操作当月Calendar即可。将日期调为当月第一天
        for (int i = 0; i < maxDay; i++) {
            DateInfo dateInfo = new DateInfo();
            dateInfo.setDate(calendar.getTime());
            if (isCurrentMonth) {
                if (i + 1 <= maxEnableClickDate) {
                    dateInfo.setType(DateInfo.CURRENT_MONTH);  //标记日期信息的类型为当月
                } else {
                    dateInfo.setType(DateInfo.CURRENT_MONTH_LAST);  //标记日期信息的类型为当月
                }
            } else {
                dateInfo.setType(DateInfo.CURRENT_MONTH);  //标记日期信息的类型为当月
            }
            dateList.add(dateInfo);
            calendar.add(Calendar.DAY_OF_MONTH, 1); //向后推移一天
        }

        /**
         * 计算下月在本月日历页出现的那几天。
         * 比如，endDayOfWeek = 6，表示当月第二天是星期五，所以日历向后会空出1天的位置，那么让下月的第一天显示在星期六的位置上。
         */
        int endEmptyCount = 7 - endDayOfWeek; //下月在本月日历页上因该出现的天数
        Calendar afterCalendar = (Calendar) calendar.clone(); //同样，克隆一份在操作
        for (int i = 0; i < endEmptyCount; i++) {
            DateInfo dateInfo = new DateInfo();
            dateInfo.setDate(afterCalendar.getTime());
            dateInfo.setType(DateInfo.AFTER_MONTH); //将DateInfo类型标记为下个月
            dateList.add(dateInfo);
            afterCalendar.add(Calendar.DAY_OF_MONTH, 1); //向后推移一天
        }
        return dateList;
    }

    public List<DateInfo> initWeekData(Date initialDate) {
        List<DateInfo> dateList = new ArrayList<>();
        // 获取China区Calendar实例，实际是GregorianCalendar的一个实例
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        // 初始化日期
        calendar.setTime(initialDate);
        // 获取当前选择的year和month，用来区分当前周包含的上月或下月
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH);
        // 获取当前周
        int curDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (curDayOfWeek != 1) calendar.add(Calendar.DAY_OF_MONTH, -(curDayOfWeek - 1));
        for (int i = 0;i < 7;i++) {
            DateInfo dateInfo = new DateInfo();
            dateInfo.setDate(calendar.getTime());
            int type = DateInfo.CURRENT_MONTH;
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            if (year < curYear) {
                type = DateInfo.PRE_MONTH;
            } else if (year > curYear) {
                type = DateInfo.AFTER_MONTH;
            } else {
                if (month < curMonth) {
                    type = DateInfo.PRE_MONTH;
                } else if (month > curMonth) {
                    type = DateInfo.AFTER_MONTH;
                }
            }
            dateInfo.setType(type);
            dateList.add(dateInfo);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dateList;
    }

    public static class DateInfo {
        public static final int PRE_MONTH = 1;
        public static final int CURRENT_MONTH = PRE_MONTH + 1;
        public static final int AFTER_MONTH = CURRENT_MONTH + 1;
        public static final int WEEK_TITLE = AFTER_MONTH + 1;
        public static final int CURRENT_MONTH_LAST = WEEK_TITLE + 1;
        private Date date;
        private int type;
        private String weekTitle;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getWeekTitle() {
            return weekTitle;
        }

        public void setWeekTitle(String weekTitle) {
            this.weekTitle = weekTitle;
        }
    }

    public static boolean isSameDay(Date firstDate, Date lastDate) {

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTime(firstDate);

        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTime(lastDate);

        return firstCalendar.get(Calendar.YEAR) == lastCalendar.get(Calendar.YEAR) && firstCalendar.get(Calendar.MONTH) == lastCalendar.get(Calendar.MONTH) && firstCalendar.get(Calendar.DAY_OF_MONTH) == lastCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isToday(Date date){
        return isSameDay(date, Calendar.getInstance().getTime());
    }

    public static long convertToTimeStamp(String time){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

}
