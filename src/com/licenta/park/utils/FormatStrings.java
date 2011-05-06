/**
 * 
 */
package com.licenta.park.utils;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author vladucu
 *
 */
public class FormatStrings {
    private static final String TAG = "FormatStrings";
    private static boolean DEBUG = true;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "EEE, dd MMM yy HH:mm:ss Z");
    
    //format hour as 24-based, like "18:06"
    public static final SimpleDateFormat DATE_FORMAT_HOUR = new SimpleDateFormat(
            "k:mm");

    //format date as "Sat June 25"
    public static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat(
            "EE MMM d");
    
    public static CharSequence getRelativeTimeString(String time) {
        if (DEBUG) Log.d(TAG, "getRelativeTimeString()");
        try {
            return DateUtils.getRelativeTimeSpanString(DATE_FORMAT.parse(time).getTime(),
                    new Date().getTime(), DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);
        } catch (ParseException e) {
            return time;
        }
    }
    
    public  static CharSequence StringToDate(String time) {
        try {
            return (CharSequence) DATE_FORMAT.parse(time);
        } catch (ParseException e) {
            return time;
        }
    }
    
    /*
     * Return hour in 24-based format
     */
    public static String getHourString(String time) {
        try {
            return DATE_FORMAT_HOUR.format(DATE_FORMAT.parse(time));
        } catch (ParseException e) {
            return time;
        }
    }
    
    /*
     * Return date as "Sat June 25"
     */
    public static String getDayString(String time) {
        try {
            return DATE_FORMAT_DAY.format(DATE_FORMAT.parse(time));
        } catch (ParseException e) {
            return time;
        }
    }
}
