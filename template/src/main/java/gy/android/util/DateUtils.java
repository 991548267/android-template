package gy.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chron on 2018/1/11.
 */

public class DateUtils {

    public static Date getTodayNow() {
        return new Date(System.currentTimeMillis());
    }

    public static Date getTodayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getTodayEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(getTodayStartTime());
        calendar.add(Calendar.DATE, 1);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }

    public static Date getThisWeekStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    public static Date getThisWeekEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(getThisWeekStartTime());
        calendar.add(Calendar.DATE, 7);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }

    public static Date getThisMonthStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date getThisMonthEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }

    public static Date getThisQuarterStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int month = calendar.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                calendar.set(Calendar.MONTH, Calendar.APRIL);
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                calendar.set(Calendar.MONTH, Calendar.JULY);
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                break;
            default:
                return null;
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getThisQuarterEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(getThisQuarterStartTime());
        calendar.add(Calendar.MONTH, 3);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }

    public static Date getThisYearStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.YEAR, 0);
        calendar.set(Calendar.DAY_OF_YEAR, 1);//设置为1号,当前日期既为本年第一天
        return calendar.getTime();
    }

    public static Date getThisYearEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(getThisYearStartTime());
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }

    public static String format(Date date) {
        return format(date, 100);
    }

    public static String format(long time) {
        return format(new Date(time), 100);
    }

    public static String format(long time, int type) {
        return format(new Date(time), type);
    }

    public static String format(Date date, int type) {
        SimpleDateFormat format;
        switch (type) {
            case 0:
                format = new SimpleDateFormat("yyyy年MM月dd日");
                break;
            case 1:
                format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                break;
            case 2:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case 3:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
            case 4:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                break;
            case 5:
                format = new SimpleDateFormat("HH:mm:ss");
                break;
            default:
                format = new SimpleDateFormat("yyyy-MM-dd");
                break;
        }
        return format.format(date);
    }

    public static Date parse(String source, int type) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format;
        switch (type) {
            case 0:
                format = new SimpleDateFormat("yyyy年MM月dd日");
                break;
            case 1:
                format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                break;
            case 2:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            default:
                format = new SimpleDateFormat("yyyy-MM-dd");
                break;
        }
        try {
            calendar.setTime(format.parse(source));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return calendar.getTime();
    }
}
