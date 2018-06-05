package com.riuir.calibur.assistUtils;


import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
}
