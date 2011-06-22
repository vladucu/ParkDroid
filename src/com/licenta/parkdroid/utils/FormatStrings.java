/**
 * 
 */
package com.licenta.parkdroid.utils;

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
            "HH:mm, EEE, dd MMM yy");
    
    //public static final DateFormat dataformat =  DateFormat.getDateInstance(DateFormat.LONG);
    public static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat(
    		"HH:mm, dd-MM-yyyy");
    
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
    
    public  static Date StringToDate(String time) {
        try {
            return (Date) DATE_FORMAT.parse(time);
        } catch (ParseException e) {
            return null;
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
    
    public static boolean checkValidDates(String time1, String time2) {
    	try {
			Date x = DATE_FORMAT2.parse(time1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
    }
    
    
}
