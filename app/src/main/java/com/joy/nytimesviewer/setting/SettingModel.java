package com.joy.nytimesviewer.setting;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by joy0520 on 2017/2/25.
 */

@org.parceler.Parcel
public class SettingModel {
    public static final int SORTED_BY_OLDEST = 0;
    public static final int SORTED_BY_NEWEST = 1;

    public Date date;
    public int sorted_by = SORTED_BY_NEWEST;
    public List<String> news_desk;

    public SettingModel() {
        date = new GregorianCalendar(2016, 0, 1).getTime();
        news_desk = new ArrayList<>();
    }

    @Override
    public String toString() {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        StringBuilder builder = new StringBuilder();
        builder.append(c.get(GregorianCalendar.YEAR)).append("/")
                .append(c.get(GregorianCalendar.MONTH)).append("/")
                .append(c.get(GregorianCalendar.DAY_OF_MONTH))
                .append(", sorted_by : ").append(sorted_by)
                .append(", news_desk : ").append(news_desk);
        return builder.toString();
    }

    public static String toDisplayString(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(GregorianCalendar.YEAR)).append("/")
                .append(calendar.get(GregorianCalendar.MONTH) + 1).append("/") // Since month is 0-based
                .append(calendar.get(GregorianCalendar.DAY_OF_MONTH));
        return builder.toString();
    }

    public static String toQueryString(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(GregorianCalendar.YEAR))
                .append(toTwoDigit(calendar.get(GregorianCalendar.MONTH)))
                .append(toTwoDigit(calendar.get(GregorianCalendar.DAY_OF_MONTH)));
        return builder.toString();
    }

    private static String toTwoDigit(int input) {
        if (input / 100 != 0) return Integer.toString(input / 100);
        else if (input / 10 != 0) return Integer.toString(input);
        else return "0" + Integer.toString(input);
    }

    public static String newsDeskToQueryString(final List<String> foo) {
        StringBuilder builder = new StringBuilder();
        builder.append("news_desk:(");
        for (int i = 0; i < foo.size(); i++) {
            builder.append(foo.get(i));
            if (i != foo.size()) {
                builder.append(" ");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
