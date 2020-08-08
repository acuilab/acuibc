/**
 *
 */
package com.acuilab.bc.main.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 */
public class DateUtil {

    private DateUtil() {
    }

    public static String commonDateFormat(Date date, String pattern) {
        if (date != null) {
            return new SimpleDateFormat(pattern).format(date);
        }

        return "";
    }

    public static Date commonDateParse(String date, String pattern)
            throws ParseException {
        return new SimpleDateFormat(pattern).parse(date);
    }

    public static Date commonDateParseWithoutException(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Date commonDateParseWithoutException(String date, String... patterns) {
        
        for(int i=0; i<patterns.length; i++) {
            Date d = commonDateParseWithoutException(date, patterns[i]);
            if(d != null) {
                return d;
            }
        }
        
        return null;
    }

    // 获得某个日期的下一天
    public static Date getNextDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);

        return c.getTime();
    }

    // 两个日期间隔天数
    public static int getDaysBetween(Date startDate, Date endDate) {

        if (startDate == null || endDate == null) {
            // null无法比较
            return 0;
        }
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int)((toCalendar.getTimeInMillis() - fromCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24));
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }

        return weekDays[w];
    }

}
