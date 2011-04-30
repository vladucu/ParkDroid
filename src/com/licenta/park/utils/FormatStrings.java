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
    
}
