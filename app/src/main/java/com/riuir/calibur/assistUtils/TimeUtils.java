package com.riuir.calibur.assistUtils;


import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public  class TimeUtils {


    public static int getYear() {
        Calendar c = Calendar.getInstance();
        int  year = c.get(Calendar.YEAR);
        return year;
    }

    public int getMonth() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        return month;
    }

    public int getDay() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        return day;
    }

    public int getHour() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public int getMinute() {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        return minute;
    }

    public int getSecond() {
        Calendar c = Calendar.getInstance();
        int second = c.get(Calendar.SECOND);
        return second;
    }

    public static String HowLongTimeForNow(String fromTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date_start = null;
        Date date_end = null;
        try {
            date_start = sdf.parse(fromTime);
            date_end = new Date(System.currentTimeMillis());
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return getGapCount(date_start,date_end);
    }

    /**
     * 获取两个日期之间的间隔天数
     * @return
     */
    public static String getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);

        int fromYear = fromCalendar.get(Calendar.YEAR);
        int fromMonth = fromCalendar.get(Calendar.MONTH)+1;
        int fromDay = fromCalendar.get(Calendar.DAY_OF_MONTH);
        int fromHour = fromCalendar.get(Calendar.HOUR_OF_DAY);
        int fromMinute = fromCalendar.get(Calendar.MINUTE);

        int toYear = toCalendar.get(Calendar.YEAR);
        int toMonth = toCalendar.get(Calendar.MONTH)+1;
        int toDay = toCalendar.get(Calendar.DAY_OF_MONTH);
        int toHour = toCalendar.get(Calendar.HOUR_OF_DAY);
        int toMinute = toCalendar.get(Calendar.MINUTE);

        if (fromYear<toYear){
            int year = toYear-fromYear;
            return year+"年前";
        }else{
            if (fromMonth<toMonth){
                int month = toMonth-fromMonth;
                return month+"月前";
            }else{
                if (fromDay < toDay){
                    int day = toDay-fromDay;
                    return day+"天前";
                }else{
                    if (fromHour<toHour){
                        int hour = toHour-fromHour;
                        return hour+"小时前";
                    }else {
                        if (fromMinute<toMinute){
                            int minute = toMinute-fromMinute;
                            return minute+"分钟前";
                        }else {
                            return "刚刚";
                        }
                    }
                }
            }
        }
    }

    public static long getCurTimeLong(){
        long time=System.currentTimeMillis();
        return time;
    }
    public static String getCurDate(String pattern){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new java.util.Date());
    }

    //时间戳转换成字符窜
    //时间格式类型 pattern
    public static String getTimestamp2Date(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
    //字符窜转换成时间戳
    //时间格式类型 pattern
    // "yyyy-MM-dd HH:mm:ss"
    // "yyyy-MM-dd"
    //"yyyy年MM月dd日 HH时mm分ss秒"
    //"yyyy年MM月dd日"
    public static long getDate2Timestamp(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

}
