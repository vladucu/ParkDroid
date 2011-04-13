/**
 * 
 */
package com.licenta.parkdroid;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vladucu
 *
 */
public class User implements Parcelable {
    
    private String mCreated;
    private String mId;
    private String mEmail;
    private String mFirstname;
    private String mLastname;
    private String mPhone;

    public User() {        
    }
    
    private User(Parcel in) {
        mCreated = readStringFromParcel(in);
        mEmail = readStringFromParcel(in);
        mFirstname = readStringFromParcel(in);
        mId = readStringFromParcel(in);
        mLastname = readStringFromParcel(in);
        mPhone = readStringFromParcel(in);   
    }
    
    /**
     * Typical implementation of required static field CREATOR
     */
    public static final User.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
   
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO Auto-generated method stub
          
        writeStringToParcel(out, mCreated);
        writeStringToParcel(out, mEmail);
        writeStringToParcel(out, mFirstname);
        writeStringToParcel(out, mId);
        writeStringToParcel(out, mLastname);
        writeStringToParcel(out, mPhone);     
    }
    
    private static void writeStringToParcel(Parcel out, String str) {
        if (str != null) {
            out.writeInt(1);
            out.writeString(str);
        } else {
            out.writeInt(0);
        }
    }
    
    private static String readStringFromParcel(Parcel in) {
        int flag = in.readInt();
        if (flag == 1) {
            return in.readString();
        } else {
            return null;
        }
    }
}
