/**
 * 
 */
package com.licenta.park.types;


import com.licenta.park.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vladucu
 *
 */
public class Reservation implements ParkTypes, Parcelable {

    private String mId;
   //private User user;
    private ParkingLot mParkingLot;
    private String mStartTime;
    private String mEndTime;
    
    public Reservation(){        
    }
    
    public Reservation(Parcel in) {
        mId = ParcelUtils.readStringFromParcel(in);
        if (in.readInt() == 1) {
            mParkingLot = in.readParcelable(ParkingLot.class.getClassLoader());
        }
        mStartTime = ParcelUtils.readStringFromParcel(in);
        mEndTime = ParcelUtils.readStringFromParcel(in);
    }
    
    /*
     * Implement required public static field Creator wich will create a new object 
     * based on incoming Parcel.
     */
    public static final Parcelable.Creator<Reservation> CREATOR = new Parcelable.Creator<Reservation>() {
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }
        
        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };
    
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
    public void writeToParcel(Parcel out, int flags) {
        ParcelUtils.writeStringToParcel(out, mId);
        if (mParkingLot != null) {
            out.writeInt(1);
            out.writeParcelable(mParkingLot, flags);
        }
        else {
            out.writeInt(0);
        }
        ParcelUtils.writeStringToParcel(out, mStartTime);
        ParcelUtils.writeStringToParcel(out, mEndTime);
    }
    
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
    
    public void setParkingLot(ParkingLot parkingLot) {
        mParkingLot = parkingLot;
    }
    
    public ParkingLot getParkingLot() {
        return mParkingLot;        
    }
    
    public void setStartTime(String time) {
        mStartTime = time;
    }
    
    public String getStartTime() {
        return mStartTime;
    }
    
    public void setEndTime(String time) {
        mEndTime = time;
    }
    
    public String getEndTime() {
        return mEndTime;
    }

}
