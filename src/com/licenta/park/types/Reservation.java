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
public class Reservation implements ParkTypes, Parcelable {

    private String mId;
   //private User user;
    private ParkingLot parkingLot;
    
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
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

    }

}
