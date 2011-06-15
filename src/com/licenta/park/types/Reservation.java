package com.licenta.park.types;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import com.licenta.park.utils.ParcelUtils;


public class Reservation implements ParkTypes, Parcelable {

	private int reservationId;
	private String startTime;
	private String endTime;
	private int cost;	
	private ParkingSpace parkingSpace;
	private User user;

    public Reservation() {
    }

    public Reservation(int id, String start_time, String end_time, int cost, ParkingSpace parkingSpace, User user) {
    	this.reservationId = id;
    	this.startTime = start_time;
        this.endTime = end_time;
        this.cost = cost;
        this.parkingSpace = parkingSpace;
        this.user = user;
    }

    public Reservation(Parcel in) {
        reservationId = in.readInt();       
        startTime = ParcelUtils.readStringFromParcel(in);
        endTime = ParcelUtils.readStringFromParcel(in);
        cost = in.readInt();
        if (in.readInt() == 1) {
        	parkingSpace = in.readParcelable(ParkingSpace.class.getClassLoader());
        }
        if (in.readInt() == 1) {
        	user = in.readParcelable(User.class.getClassLoader());
        }
        
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
        out.writeInt(reservationId);
        ParcelUtils.writeStringToParcel(out, startTime);
        ParcelUtils.writeStringToParcel(out, endTime);
        out.writeInt(cost);        
        if (parkingSpace != null) {
            out.writeInt(1);
            out.writeParcelable(parkingSpace, flags);
        }
        else {
            out.writeInt(0);
        }
        if (user != null) {
            out.writeInt(1);
            out.writeParcelable(user, flags);
        }
        else {
            out.writeInt(0);
        }
    }
    
    public int getId() {
        return reservationId;
    }

    public void setId(int id) {
        this.reservationId = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String start_time) {
        this.startTime = start_time;
    }
    
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String end_time) {
        this.endTime = end_time;
    }
    
    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public ParkingSpace getParkingSpace() {
        return parkingSpace;
    }

    public void setParkingSpace(ParkingSpace parkingSpace) {
        this.parkingSpace = parkingSpace;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}