/**
 * 
 */
package com.licenta.park.types;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vladucu
 *
 */
public class User implements Parcelable, ParkTypes {


    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        // TODO Auto-generated method stub

    }

}
