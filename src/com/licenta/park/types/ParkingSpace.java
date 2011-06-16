package com.licenta.park.types;

import android.os.Parcel;
import android.os.Parcelable;

import com.licenta.park.utils.ParcelUtils;

public class ParkingSpace implements ParkTypes, Parcelable {

	//private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String address;
	private String geolat;
	private String geolong;
	private int spaces;  
	private int price;

    public ParkingSpace() {
    }

    public ParkingSpace(int id, String name, String address, String geolat, String geolong, int spaces, int price) {
    	super();
    	this.id = id;
    	this.name = name;
        this.address = address;
        this.geolat = geolat;
        this.geolong = geolong;
        this.spaces = spaces;
        this.price = price;        
    }
    
    private ParkingSpace(Parcel in) {
    	id = in.readInt();
    	name = ParcelUtils.readStringFromParcel(in);
        //mPhone = ParcelUtils.readStringFromParcel(in);
        address = ParcelUtils.readStringFromParcel(in);
        //mCity = ParcelUtils.readStringFromParcel(in);
        //mZip = ParcelUtils.readStringFromParcel(in);
        geolat = ParcelUtils.readStringFromParcel(in);
        geolong = ParcelUtils.readStringFromParcel(in);
        //mDistance = ParcelUtils.readStringFromParcel(in);
        spaces = in.readInt();
        price = in.readInt();
       /* if (in.readInt() == 1) {
        	user = new User();
        	user = in.readParcelable(User.class.getClassLoader());
        }*/
        //mTotalSpaces = ParcelUtils.readStringFromParcel(in);
        //mPrice = ParcelUtils.readStringFromParcel(in);
        //mOpenHours = ParcelUtils.readStringFromParcel(in);
        //mIconUrl = ParcelUtils.readStringFromParcel(in);
        //mHasReservation = in.readInt() == 1;
       /* if (in.readInt() == 1) {
            mReservation = new Reservation();
            mReservation = in.readParcelable(Reservation.class.getClassLoader());
        }*/
    }

    /*
     * Implement required public static field Creator wich will create a new object 
     * based on incoming Parcel.
     */
    public static final Parcelable.Creator<ParkingSpace> CREATOR = new Parcelable.Creator<ParkingSpace>() {
        public ParkingSpace createFromParcel(Parcel in) {
            return new ParkingSpace(in);
        }
        
        @Override
        public ParkingSpace[] newArray(int size) {
            return new ParkingSpace[size];
        }
    };
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGeoLat() {
        return geolat;
    }

    public void setGeoLat(String geolat) {
        this.geolat = geolat;
    }    

    public String getGeoLong() {
        return geolong;
    }

    public void setGeoLong(String geolong) {
        this.geolong = geolong;
    }  
    
    public int getSpaces() {
        return spaces;
    }

    public void setSpaces(int spaces) {
        this.spaces = spaces;
    }
    
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
        ParcelUtils.writeStringToParcel(out, name);
        //ParcelUtils.writeStringToParcel(out, mPhone);
        ParcelUtils.writeStringToParcel(out, address);
        //ParcelUtils.writeStringToParcel(out, mCity);
        //ParcelUtils.writeStringToParcel(out, mZip);
        ParcelUtils.writeStringToParcel(out, geolat);
        ParcelUtils.writeStringToParcel(out, geolong);
        //ParcelUtils.writeStringToParcel(out, mDistance);
        out.writeInt(spaces);
        out.writeInt(price);
       /* if (user != null) {
        	out.writeInt(1);
        	out.writeParcelable(user, flags);        	
        }
        else {
        	out.writeInt(0);
        }*/
        ///ParcelUtils.writeStringToParcel(out, mTotalSpaces);
        //ParcelUtils.writeStringToParcel(out, mPrice);
        ///ParcelUtils.writeStringToParcel(out, mOpenHours);
        ///ParcelUtils.writeStringToParcel(out, mIconUrl);
        //out.writeInt(mHasReservation ? 1 : 0);
        /*if (mReservation != null ) {
            out.writeInt(1);
            out.writeParcelable(mReservation, flags);
        }
        else {
            out.writeInt(0);
        }*/
		
	}

}