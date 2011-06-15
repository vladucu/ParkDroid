package com.licenta.park.utils;

import java.util.Date;

import android.os.Parcel;


/**
 * @author vladucu
 *
 */
public class ParcelUtils {

    public static void writeStringToParcel(Parcel out, String str) {
        if (str != null) {
            out.writeInt(1);
            out.writeString(str);
        } else {
            out.writeInt(0);
        }
    }

    public static String readStringFromParcel(Parcel in) {
        int flag = in.readInt();
        if (flag == 1) {
            return in.readString();
        } else {
            return null;
        }
    }
    
    public static void writeDateToParcel(Parcel out, Date date) {
    	if (date != null) {
    		out.writeInt(1);
    		out.writeString(date.toString());
    	}
    	else {
    		out.writeInt(0);    		
    	}
    }
    
    public static Date readDateFromParcel(Parcel in) {
        int flag = in.readInt();
        if (flag == 1) {
        	String x = in.readString();
        	System.out.println();
            return FormatStrings.stringDateToUnits(x);
        } else {
            return null;
        }
    }
}
