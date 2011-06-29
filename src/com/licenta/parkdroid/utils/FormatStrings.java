/**
 * 
 */
package com.licenta.parkdroid.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author vladucu
 *
 */
public class FormatStrings {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "HH:mm, EEE, dd MMM yy");
    
    //public static final DateFormat dataformat =  DateFormat.getDateInstance(DateFormat.LONG);
    public static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat(
    		"HH:mm, dd-MM-yyyy");
    
    
    public  static Date StringToDate(String time) {
        try {
            return (Date) DATE_FORMAT.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }
  
    public static boolean checkValidDates(String time1, String time2) {
    	Date date1 = StringToDate(time1);
    	Date date2 = StringToDate(time2);
    	if (date2.after(date1))
    		return true;
    	else return false;
    }
    
    
}
