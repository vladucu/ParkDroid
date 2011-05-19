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
public class AddReservationResult implements ParkTypes, Parcelable {

	private String mId;
	
	 public AddReservationResult(){        
	    }
	    
	 public AddReservationResult(Parcel in) {
	        mId = ParcelUtils.readStringFromParcel(in);	        
	    }
	
	 /*
     * Implement required public static field Creator wich will create a new object 
     * based on incoming Parcel.
     */
    public static final Parcelable.Creator<AddReservationResult> CREATOR = new Parcelable.Creator<AddReservationResult>() {
        public AddReservationResult createFromParcel(Parcel in) {
            return new AddReservationResult(in);
        }
        
        @Override
        public AddReservationResult[] newArray(int size) {
            return new AddReservationResult[size];
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
    	ParcelUtils.writeStringToParcel(out, mId);
        
    }
    
	public void setId(String id) {
		mId = id;
	}
	
	public String getId() {
		return mId;
	}
}
